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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupframework.worker.buildingblocks;

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
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
    public PetasosTaskOld receiveFromWUP(UoW incomingUoW, Exchange camelExchange) {
        getLogger().debug(".receiveFromWUP(): Entry, incomingUoW->{}", incomingUoW);
        // Get my Petasos Context
        if( topologyProxy == null ) {
        	getLogger().error(".receiveFromWUP(): Guru Software Meditation Error: topologyProxy is null");
        }
        // Retrieve the information from the CamelExchange
        PetasosTaskOld wupTP = camelExchange.getProperty(PetasosPropertyConstants.WUP_TRANSPORT_PACKET_EXCHANGE_PROPERTY_NAME, PetasosTaskOld.class);
        wupTP.setPayload(incomingUoW);
        getLogger().trace(".receiveFromWUP(): We only want to check if the UoW was successful and modify the JobCard/StatusElement accordingly.");
        getLogger().trace(".receiveFromWUP(): All detailed checking of the Cluster/SiteWide details is done in the WUPContainerEgressProcessor");
        switch (incomingUoW.getProcessingOutcome()) {
            case UOW_OUTCOME_SUCCESS:
                getLogger().trace(".receiveFromWUP(): UoW was processed successfully - updating JobCard/StatusElement to FINISHED!");
                wupTP.getCurrentJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                wupTP.getCurrentJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                wupTP.getCurrentParcelStatus().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
                wupTP.getCurrentParcelStatus().setEntryDate(Date.from(Instant.now()));
                break;
            case UOW_OUTCOME_NO_PROCESSING_REQUIRED:
                getLogger().trace(".receiveFromWUP(): UoW was processed with no actions required - updating JobCard/StatusElement to FINISHED!");
                wupTP.getCurrentJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                wupTP.getCurrentJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                wupTP.getCurrentJobCard().setIsToBeDiscarded(true);
                wupTP.getCurrentParcelStatus().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
                wupTP.getCurrentParcelStatus().setEntryDate(Date.from(Instant.now()));
                break;
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_FAILED:
            default:
                getLogger().trace(".receiveFromWUP(): UoW was not processed or processing failed - updating JobCard/StatusElement to FAILED!");
                wupTP.getCurrentJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                wupTP.getCurrentJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                wupTP.getCurrentParcelStatus().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
                wupTP.getCurrentParcelStatus().setEntryDate(Date.from(Instant.now()));
                break;
        }
        return (wupTP);
    }
}
