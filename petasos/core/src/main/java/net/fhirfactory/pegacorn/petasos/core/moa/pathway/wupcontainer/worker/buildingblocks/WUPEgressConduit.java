/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

/**
 * @author Mark A. Hunter
 * @since 2020-07-05
 */
@Dependent
public class WUPEgressConduit {
    private static final Logger LOG = LoggerFactory.getLogger(WUPEgressConduit.class);
    protected Logger getLogger(){
        return(LOG);
    }
    
    @Inject
    TopologyIM topologyProxy;

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;
    
    /**
     * This function reconstitutes the WorkUnitTransportPacket by extracting the WUPJobCard and ParcelStatusElement
     * from the Camel Exchange, and injecting them plus the UoW into it.
     *
     * @param incomingUoW   The Unit of Work (UoW) received as output from the actual Work Unit Processor (Business Logic)
     * @param camelExchange The Apache Camel Exchange object, for extracting the WUPJobCard & ParcelStatusElement from
     * @return A WorkUnitTransportPacket object for relay to the other
     */
    public WorkUnitTransportPacket receiveFromWUP(UoW incomingUoW, Exchange camelExchange) {
        getLogger().debug(".receiveFromWUP(): Entry, incomingUoW->{}", incomingUoW);
        // Get my Petasos Context
        if( topologyProxy == null ) {
        	getLogger().error(".receiveFromWUP(): Guru Software Meditation Error: topologyProxy is null");
        }
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        getLogger().trace(".receiveFromWUP(): Node Element retrieved --> {}", node);
        TopologyNodeFunctionFDNToken wupFunctionToken = node.getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".receiveFromWUP(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken); 
        // Now, continue with business logic
        RouteElementNames elementNames = new RouteElementNames(node.getNodeFDN().getToken());
        // Retrieve the information from the CamelExchange
        WUPJobCard jobCard = camelExchange.getProperty(PetasosPropertyConstants.WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME, WUPJobCard.class);
        ParcelStatusElement statusElement = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME, ParcelStatusElement.class);
        // Now process incoming content
        WorkUnitTransportPacket transportPacket = new WorkUnitTransportPacket(jobCard.getActivityID(), Date.from(Instant.now()), incomingUoW);
        getLogger().trace(".receiveFromWUP(): We only want to check if the UoW was successful and modify the JobCard/StatusElement accordingly.");
        getLogger().trace(".receiveFromWUP(): All detailed checking of the Cluster/SiteWide details is done in the WUPContainerEgressProcessor");
        switch (incomingUoW.getProcessingOutcome()) {
            case UOW_OUTCOME_SUCCESS:
                getLogger().trace(".receiveFromWUP(): UoW was processed successfully - updating JobCard/StatusElement to FINISHED!");
                jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
                statusElement.setEntryDate(Date.from(Instant.now()));
                break;
            case UOW_OUTCOME_NO_PROCESSING_REQUIRED:
                getLogger().trace(".receiveFromWUP(): UoW was processed with no actions required - updating JobCard/StatusElement to FINISHED!");
                jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                jobCard.setIsToBeDiscarded(true);
                statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
                statusElement.setEntryDate(Date.from(Instant.now()));
                break;
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_FAILED:
            default:
                getLogger().trace(".receiveFromWUP(): UoW was not processed or processing failed - updating JobCard/StatusElement to FAILED!");
                jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
                statusElement.setEntryDate(Date.from(Instant.now()));
                break;
        }
        transportPacket.setCurrentJobCard(jobCard);
        transportPacket.setCurrentParcelStatus(statusElement);
        return (transportPacket);
    }
}
