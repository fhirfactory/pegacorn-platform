/*
 * Copyright (c) 2020 MAHun
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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
@Dependent
public class WUPContainerEgressProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerEgressProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    RouteElementNames elementNames = null;

    @Inject
    PetasosMOAServicesBroker petasosMOAServicesBroker;

    @Inject
    TopologyIM topologyProxy;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    public PetasosTaskOld egressContentProcessor(PetasosTaskOld wupTransportPacket, Exchange camelExchange) {
      	getLogger().debug(".egressContentProcessor(): Entry, wupTransportPacket (WorkUnitTransportPacket) --> {}, wupNodeFDNTokenValue (String) --> {}", wupTransportPacket);
        // Get my Petasos Context
        getLogger().trace(".egressContentProcessor(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        getLogger().trace(".egressContentProcessor(): Retrieved the WUPTopologyNode, value->{}", node);
        TopologyNodeFunctionFDNToken wupFunctionToken = node.getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".egressContentProcessor(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);
        // Now, continue with business logic
        switch (node.getResilienceMode()) {
            case RESILIENCE_MODE_MULTISITE:
            case RESILIENCE_MODE_KUBERNETES_MULTISITE:
                getLogger().trace(".egressContentProcessor(): Deployment Mode --> PETASOS_MODE_MULTISITE");
            case RESILIENCE_MODE_CLUSTERED:
            case RESILIENCE_MODE_KUBERNETES_CLUSTERED:
                getLogger().trace(".egressContentProcessor(): Deployment Mode --> PETASOS_MODE_CLUSTERED");
            case RESILIENCE_MODE_STANDALONE:
            case RESILIENCE_MODE_KUBERNETES_STANDALONE:
                getLogger().trace(".egressContentProcessor(): Deployment Mode --> PETASOS_MODE_STANDALONE");
                standaloneDeploymentModeECP(wupTransportPacket, camelExchange,node);
        }
		getLogger().debug(".egressContentProcessor(): Exit");
        return (wupTransportPacket);
    }

    private void standaloneDeploymentModeECP(PetasosTaskOld wupTransportPacket, Exchange camelExchange, WorkUnitProcessorTopologyNode wupNode) {
       	getLogger().debug(".standaloneDeploymentModeECP(): Entry, wupTransportPacket (WorkUnitTransportPacket) --> {}, wupNode (NodeElement) --> {}", wupTransportPacket, wupNode);
        elementNames = new RouteElementNames(wupNode.getNodeFDN().getToken());
        getLogger().trace(".standaloneDeploymentModeECP(): Now, extract WUPJobCard from wupTransportPacket (WorkUnitTransportPacket)");
        PetasosTaskJobCard jobCard = wupTransportPacket.getCurrentJobCard();
        getLogger().trace(".standaloneDeploymentModeECP(): Now, extract ParcelStatusElement from wupTransportPacket (WorkUnitTransportPacket)");
        TaskStatusType statusElement = wupTransportPacket.getCurrentParcelStatus();
        getLogger().trace(".standaloneDeploymentModeECP(): Now, extract UoW from wupTransportPacket (WorkUnitTransportPacket)");
        UoW uow = wupTransportPacket.getPayload();
		getLogger().debug(".standaloneDeploymentModeECP(): uow (UoW) --> {}", uow);
		getLogger().trace(".standaloneDeploymentModeECP(): Now, continue processing based on the ParcelStatusElement.getParcelStatus() (ResilienceParcelProcessingStatusEnum)");
        FulfillmentExecutionStatusEnum parcelProcessingStatusEnum = statusElement.getFulfillmentExecutionStatus();
        switch (parcelProcessingStatusEnum) {
            case PARCEL_STATUS_FINISHED:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
                petasosMOAServicesBroker.notifyFinishOfWorkUnitActivity(jobCard, uow);
                metricsAgent.updateWorkUnitProcessorStatus(wupNode.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED.toString());
                metricsAgent.incrementFinishedTasks(wupNode.getComponentID());
                break;
            case PARCEL_STATUS_ACTIVE_ELSEWHERE:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE_ELSEWHERE);
            case PARCEL_STATUS_FINISHED_ELSEWHERE:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED_ELSEWHERE);
            case PARCEL_STATUS_FINALISED_ELSEWHERE:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED_ELSEWHERE);
            case PARCEL_STATUS_REGISTERED:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_REGISTERED);
            case PARCEL_STATUS_INITIATED:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_INITIATED);
            case PARCEL_STATUS_ACTIVE:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
            case PARCEL_STATUS_FINALISED:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
            case PARCEL_STATUS_FAILED:
            	getLogger().trace(".standaloneDeploymentModeECP(): ParcelStatus (ResilienceParcelProcessingStatusEnum) --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
            default:
                petasosMOAServicesBroker.notifyFailureOfWorkUnitActivity(jobCard, uow);
                metricsAgent.updateWorkUnitProcessorStatus(wupNode.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED.toString());
                metricsAgent.incrementFailedTasks(wupNode.getComponentID());
        }
        metricsAgent.touchLastActivityInstant(wupNode.getComponentID());
        metricsAgent.touchActivityFinishInstant(wupNode.getComponentID());
    }
}