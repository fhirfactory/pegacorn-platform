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

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * This class (bean) is to be injected into the flow of an Egress Only WUP Implementation
 * (i.e. Egress Messaging, RESTful.POST, RESTful.PUT, RESTful.DELETE client). It provides the
 * Petasos pseudo finalisation Sequence of the Transaction/Messaging flow - including logging
 * the initial Audit-Trail entry.
 *
 * The method registerActivityEnd is invoked AFTER the call to the forwarding framework
 * system with a +ve/-ve response.
 *
 */

@ApplicationScoped
public class EgressActivityFinalisationRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(EgressActivityFinalisationRegistration.class);

    @Inject
    TopologyIM topologyProxy;

    @Inject
    PetasosMOAServicesBroker servicesBroker;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    public UoW registerActivityFinishAndFinalisation(UoW theUoW, Exchange camelExchange, String wupInstanceKey){
        LOG.debug(".registerActivityFinishAndFinalisation(): Entry, payload --> {}, wupInstanceKey --> {}", theUoW, wupInstanceKey);
        LOG.trace(".ingresGatekeeper(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        LOG.trace(".registerActivityFinishAndFinalisation(): Get Job Card and Status Element from Exchange for extraction by the WUP Egress Conduit");
        PetasosTaskOld wupTransportPacket = camelExchange.getProperty(PetasosPropertyConstants.WUP_TRANSPORT_PACKET_EXCHANGE_PROPERTY_NAME, PetasosTaskOld.class);
        PetasosTaskJobCard jobCard = wupTransportPacket.getCurrentJobCard();
        TaskStatusType statusElement = wupTransportPacket.getCurrentParcelStatus();
        metricsAgent.touchLastActivityInstant(node.getComponentID());
        LOG.trace(".registerActivityFinishAndFinalisation(): Extract the UoW");
        switch(theUoW.getProcessingOutcome()){
            case UOW_OUTCOME_SUCCESS:{
                jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                servicesBroker.notifyFinishOfWorkUnitActivity(jobCard, theUoW);
                servicesBroker.notifyFinalisationOfWorkUnitActivity(jobCard);
                metricsAgent.incrementFinishedTasks(node.getComponentID());
                metricsAgent.touchActivityFinishInstant(node.getComponentID());
                break;
            }
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_FAILED:{
                jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                servicesBroker.notifyFailureOfWorkUnitActivity(jobCard, theUoW);
                metricsAgent.incrementFailedTasks(node.getComponentID());
                metricsAgent.touchActivityFinishInstant(node.getComponentID());
            }
        }
        LOG.debug(".registerActivityFinishAndFinalisation(): exit, my work is done!");
        return(theUoW);
    }
}
