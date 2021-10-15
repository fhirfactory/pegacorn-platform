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

package net.fhirfactory.pegacorn.petasos.wup.helper;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

/**
 * This class (bean) is to be injected into the flow of an Ingres Only WUP Implementation
 * (i.e. Ingres Messaging, RESTful.POST, RESTful.PUT, RESTful.DELETE). It provides the
 * Petasos Initialisation Sequence of the Transaction/Messaging flow - including logging
 * the initial Audit-Trail entry.
 *
 * The method registerActivityStart must be invoked PRIOR to responding to the source (external)
 * system with a +ve/-ve response.
 *
 */

@ApplicationScoped
public class IngresActivityBeginRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(IngresActivityBeginRegistration.class);

    @Inject
    TopologyIM topologyProxy;

    @Inject
    PetasosMOAServicesBroker servicesBroker;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    public UoW registerActivityStart(UoW theUoW, Exchange camelExchange){
        LOG.debug(".registerActivityStart(): Entry, payload --> {}", theUoW);
        LOG.trace(".registerActivityStart(): reconstituted token, now attempting to retrieve NodeElement");
        WorkUnitProcessorTopologyNode wup = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);

//        TopologyNodeFDNToken nodeFDNToken = new TopologyNodeFDNToken(wupInstanceKey);
        LOG.trace(".registerActivityStart(): Node Element retrieved --> {}", wup);
        TopologyNodeFunctionFDNToken wupFunctionToken = wup.getNodeFunctionFDN().getFunctionToken();
        LOG.trace(".registerActivityStart(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);        
        LOG.trace(".registerActivityStart(): Building the ActivityID for this activity");
        WUPIdentifier wupID = new WUPIdentifier(wup.getNodeFDN().getToken());
        TaskFulfillmentType newPetasosTaskFulfillment = new TaskFulfillmentType();
        newPetasosTaskFulfillment.setPresentWUPFunctionToken(wupFunctionToken);
        newPetasosTaskFulfillment.setFulfillerComponentId(wupID);
        LOG.trace(".registerActivityStart(): newActivityID (ActivityID) --> {}", newPetasosTaskFulfillment);
        LOG.trace(".registerActivityStart(): Creating new JobCard");
        PetasosTaskJobCard activityJobCard = new PetasosTaskJobCard(newPetasosTaskFulfillment, PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING, PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING, wup.getConcurrencyMode(), wup.getResilienceMode(),  Date.from(Instant.now()));
        LOG.trace(".registerActivityStart(): Registering the Work Unit Activity using the activityJobCard --> {} and UoW --> {}", activityJobCard, theUoW);
        String portType = camelExchange.getProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_TYPE, String.class);
        String portValue = camelExchange.getProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_VALUE, String.class);
        TaskStatusType statusElement;
        if(portType != null && portValue != null) {
            statusElement = servicesBroker.registerSystemEdgeWorkUnitActivity(activityJobCard, theUoW, portType, portValue);
        } else {
            statusElement = servicesBroker.registerSystemEdgeWorkUnitActivity(activityJobCard, theUoW);
        }
        LOG.trace(".registerActivityStart(): Updated metrics");
        metricsAgent.touchActivityStartInstant(wup.getComponentID());
        metricsAgent.touchLastActivityInstant(wup.getComponentID());
        metricsAgent.incrementIngresMessageCount(wup.getComponentID());
        metricsAgent.incrementRegisteredTasks(wup.getComponentID());
        metricsAgent.incrementStartedTasks(wup.getComponentID());
        LOG.trace(".registerActivityStart(): Registration aftermath: statusElement --> {}", statusElement);
        // Now we have to Inject some details into the Exchange so that the WUPEgressConduit can extract them as per standard practice
        LOG.trace(".registerActivityStart(): Injecting Job Card and Status Element into Exchange for extraction by the WUP Egress Conduit");
        PetasosTaskOld wupTP = new PetasosTaskOld(activityJobCard.getActivityID(), Date.from(Instant.now()), theUoW);
        wupTP.setCurrentJobCard(activityJobCard);
        wupTP.setCurrentParcelStatus(statusElement);
        camelExchange.setProperty(PetasosPropertyConstants.WUP_TRANSPORT_PACKET_EXCHANGE_PROPERTY_NAME, wupTP);
        LOG.debug(".registerActivityStart(): exit, my work is done!");
        return(theUoW);
    }
}
