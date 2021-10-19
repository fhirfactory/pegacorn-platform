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
import net.fhirfactory.pegacorn.components.auditing.AuditEventCaptureLevelEnum;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.coordinator.ProcessingPlantTaskCoordinator;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;

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
    private MOAServicesAuditBroker auditBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private ProcessingPlantTaskCoordinator taskCoordinator;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    public PetasosFulfillmentTask egressContentProcessor(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
      	getLogger().debug(".egressContentProcessor(): Entry, fulfillmentTask (WorkUnitTransportPacket) --> {}, wupNodeFDNTokenValue (String) --> {}", fulfillmentTask);
        // Get my Petasos Context
        getLogger().trace(".egressContentProcessor(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        getLogger().trace(".egressContentProcessor(): Retrieved the WUPTopologyNode, value->{}", node);
        // Now, continue with business logic
        elementNames = new RouteElementNames(node.getComponentId());
        // Implement StateMachine
		getLogger().trace(".egressContentProcessor(): Now, continue processing based on the ParcelStatusElement.getParcelStatus() (ResilienceParcelProcessingStatusEnum)");
		Instant now = Instant.now();
        getLogger().trace(".egressContentProcessor(): fulfillmentTask.getTaskFulfillment().getStatus()->{}",fulfillmentTask.getTaskFulfillment().getStatus());
        switch (fulfillmentTask.getTaskFulfillment().getStatus()) {
            case FULFILLMENT_EXECUTION_STATUS_FINISHED: {
                taskCoordinator.notifyExecutionFinish(fulfillmentTask.getTaskJobCard());
                //
                // Metrics
                metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED.toString());
                metricsAgent.incrementFinishedTasks(node.getComponentId().getId());
                break;
            }
            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:{
                taskCoordinator.notifyExecutionFinish(fulfillmentTask.getTaskJobCard());
                metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED.toString());
                metricsAgent.incrementCancelledTasks(node.getComponentId().getId());
                break;
            }

            case FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED: {
                taskCoordinator.notifyExecutionFinish(fulfillmentTask.getTaskJobCard());
                metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED.toString());
                metricsAgent.incrementFinishedTasks(node.getComponentId().getId());
                break;
            }
            default:
                taskCoordinator.notifyExecutionFailure(fulfillmentTask.getTaskJobCard());
                metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED.toString());
                metricsAgent.incrementFailedTasks(node.getComponentId().getId());
        }
        metricsAgent.touchLastActivityInstant(node.getComponentId().getId());
        metricsAgent.touchActivityFinishInstant(node.getComponentId().getId());
        //
        // Audit Trail
        if(processingPlant.getAuditingLevel().getAuditLevel() >= AuditEventCaptureLevelEnum.LEVEL_4_WUP_ALL.getAuditLevel()){
            auditBroker.logActivity(fulfillmentTask, false);
        }
        //
        // We're done
        return(fulfillmentTask);
    }
}
