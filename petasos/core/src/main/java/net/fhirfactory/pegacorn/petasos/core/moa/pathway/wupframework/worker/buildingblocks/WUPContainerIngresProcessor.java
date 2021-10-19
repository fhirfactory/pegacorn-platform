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

import net.fhirfactory.pegacorn.components.auditing.AuditEventCaptureLevelEnum;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.coordinator.ProcessingPlantTaskCoordinator;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;

/**
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
@Dependent
public class WUPContainerIngresProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerIngresProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private ProcessingPlantTaskCoordinator taskCoordinator;

    @Inject
    private MOAServicesAuditBroker auditBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    
    /**
     * This class/method is used as the injection point into the WUP Processing Framework for the specific WUP Type/Instance in question.
     * It registers the following:
     *      - A ResilienceParcel for the UoW (registered with the SystemModule Parcel Cache: via the PetasosServiceBroker)
     *      - A WUPJobCard for the associated Work Unit Activity (registered into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *      - A ParcelStatusElement for the ResilienceParcel (again, register into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *
     * The function handles both new UoW or UoW instances that are being re-tried.
     *
     * It performs checks on the Status (WUPJobCard.currentStatus & ParcelStatusElement.hasClusterFocus) to determine if this WUP-Thread should
     * actually perform the Processing of the UoW via the WUP.
     *
     * It also checks on / assigns values to the Status (ParcelStatusElement.parcelStatus) if there are issues with the parcel. If there are, it may also
     * assign a "failed" status to both the WUPJobCard and ParcelStatusElement, and trigger a discard of this Parcel (for a retry) via setting the
     * WUPJobCard.isToBeDiscarded attribute to true.
     *
     * Finally, if all is going OK, but this WUP-Thread does not have the Cluster Focus (or SystemWide Focus), it waits in a sleep/loop until a condition
     * changes.
     *
     * @param actionableTask The PetasosActionableTask that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange The Apache Camel Exchange object, used to store a Semaphors and Attributes
     * @return Should return a WorkUnitTransportPacket that is forwarding onto the WUP Ingres Gatekeeper.
     */
    public PetasosFulfillmentTask ingresContentProcessor(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".ingresContentProcessor(): Enter, actionableTask->{}", actionableTask );
        //
        // Resolve our WorkUnitProcessorTopologyNode from the incoming Exchange (camelExchange)
        getLogger().trace(".ingresContentProcessor(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        //
        // Report some metrics
        metricsAgent.incrementIngresMessageCount(node.getComponentId().getId());
        metricsAgent.touchLastActivityInstant(node.getComponentId().getId());
        metricsAgent.touchActivityStartInstant(node.getComponentId().getId());
        metricsAgent.incrementRegisteredTasks(node.getComponentId().getId());
        //
        // Create the PetasosFulfillmentTask
        PetasosFulfillmentTask fulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(actionableTask, node);
        //
        // Create the JobCard
        PetasosTaskJobCard jobCard = new PetasosTaskJobCard();
        jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING);
        jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);
        jobCard.setActionableTaskIdentifier(fulfillmentTask.getActionableTaskId());
        jobCard.setClusterMode(node.getConcurrencyMode());
        jobCard.setSystemMode(node.getResilienceMode());
        jobCard.setFulfillmentTaskIdentifier(fulfillmentTask.getTaskId());
        jobCard.setLocalUpdateInstant(Instant.now());
        fulfillmentTask.setTaskJobCard(jobCard);
        //
        // Register the FulfillmentTask
        PetasosFulfillmentTask registeredTask = taskCoordinator.registerFulfillmentTask(fulfillmentTask);
        //
        // Report some more metrics
        metricsAgent.updateCurrentActionableTask(node.getComponentId().getId(), fulfillmentTask.getActionableTaskId());
        if(fulfillmentTask.hasTaskTraceability()){
            if(!fulfillmentTask.getTaskTraceability().getTaskJourney().isEmpty()){
                int lastEntry = fulfillmentTask.getTaskTraceability().getTaskJourney().size();
                TaskTraceabilityElementType taskTraceabilityElementType = fulfillmentTask.getTaskTraceability().getTaskJourney().get(lastEntry);
                if(taskTraceabilityElementType != null){
                    TaskIdType taskId = taskTraceabilityElementType.getActionableTaskId();
                    metricsAgent.updatePreviousEpisodeID(node.getComponentId().getId(),taskId);
                }
            }
        }
        metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(),jobCard.getCurrentStatus().toString());
        //
        // Perform Status Check and Wait if Required
        long waitTime = PetasosPropertyConstants.WUP_SLEEP_INTERVAL_MILLISECONDS;
        boolean waitState = true;
        while (waitState) {
            getLogger().trace(".ingresContentProcessor(): jobCard.getCurrentStatus->{}", jobCard.getCurrentStatus());
            Instant now = Instant.now();
            switch (registeredTask.getTaskJobCard().getCurrentStatus()) {
                case PETASOS_TASK_ACTIVITY_STATUS_WAITING: {
                    getLogger().trace(".ingresContentProcessor(): jobCard.getCurrentStatus --> {}", PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING);
                    //
                    // Metrics
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING.toString());
                    //
                    // State-Machine for Next Action
                    boolean tryStateUpdate = false;
                    switch (registeredTask.getTaskJobCard().getLocalFulfillmentStatus()) {
                        case FULFILLMENT_EXECUTION_STATUS_REGISTERED: {
                            registeredTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);
                            taskCoordinator.requestExecutionStatusChange(registeredTask.getTaskJobCard());
                            break;
                        }
                        case FULFILLMENT_EXECUTION_STATUS_ACTIVE_ELSEWHERE:
                            break;
                        case FULFILLMENT_EXECUTION_STATUS_FINALISED_ELSEWHERE:
                        case FULFILLMENT_EXECUTION_STATUS_FINISHED_ELSEWHERE:
                        case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
                        case FULFILLMENT_EXECUTION_STATUS_FINALISED:
                        case FULFILLMENT_EXECUTION_STATUS_UNREGISTERED: {
                            registeredTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELED);
                            registeredTask.getTaskFulfillment().setStatus(registeredTask.getTaskJobCard().getLocalFulfillmentStatus());
                            registeredTask.getTaskFulfillment().setStartInstant(now);
                            registeredTask.getTaskFulfillment().setFinishInstant(now);
                            registeredTask.getTaskFulfillment().setLastCheckedInstant(now);
                            taskCoordinator.notifyExecutionCancellation(registeredTask.getTaskJobCard());
                            waitState = false;
                            break;
                        }
                        case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                        case FULFILLMENT_EXECUTION_STATUS_ACTIVE:
                        case FULFILLMENT_EXECUTION_STATUS_INITIATED: {
                            registeredTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);
                            registeredTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
                            registeredTask.getTaskFulfillment().setStartInstant(now);
                            registeredTask.getTaskFulfillment().setFinishInstant(now);
                            registeredTask.getTaskFulfillment().setLastCheckedInstant(now);
                            taskCoordinator.notifyExecutionFailure(registeredTask.getTaskJobCard());
                            break;
                        }
                    }

                    if (registeredTask.getTaskJobCard().getGrantedStatus() == PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING) {
                        registeredTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);
                        registeredTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
                        registeredTask.getTaskFulfillment().setStartInstant(now);
                        registeredTask.getTaskFulfillment().setLastCheckedInstant(now);
                        taskCoordinator.notifyExecutionStart(registeredTask.getTaskJobCard());
                        //
                        // Metrics
                        metricsAgent.touchLastActivityInstant(node.getComponentId().getId());
                        metricsAgent.incrementStartedTasks(node.getComponentId().getId());
                        metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING.toString());
                        getLogger().trace(".ingresContentProcessor(): We've been granted execution privileges!");
                        waitState = false;
                    }
                    break;
                }
                case PETASOS_TASK_ACTIVITY_STATUS_EXECUTING:
                case PETASOS_TASK_ACTIVITY_STATUS_FINISHED:
                case PETASOS_TASK_ACTIVITY_STATUS_FAILED:{
                    //
                    // Metrics
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED.toString());
                    metricsAgent.incrementFailedTasks(node.getComponentId().getId());
                    //
                    // Register Changed status
                    registeredTask.getTaskJobCard().setToBeDiscarded(true);
                    waitState = false;
                    registeredTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
                    registeredTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);
                    registeredTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);
                    registeredTask.getTaskFulfillment().setStartInstant(now);
                    registeredTask.getTaskFulfillment().setFinishInstant(now);
                    registeredTask.getTaskFulfillment().setLastCheckedInstant(now);
                    taskCoordinator.notifyExecutionFailure(registeredTask.getTaskJobCard());
                    registeredTask.getTaskJobCard().setToBeDiscarded(true);
                    waitState = false;
                    break;
                }
                case PETASOS_TASK_ACTIVITY_STATUS_CANCELED:
                default: {
                    registeredTask.getTaskJobCard().setToBeDiscarded(true);
                    waitState = false;
                    registeredTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELED);
                    registeredTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELED);
                    registeredTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
                    registeredTask.getTaskFulfillment().setStartInstant(now);
                    registeredTask.getTaskFulfillment().setFinishInstant(now);
                    registeredTask.getTaskFulfillment().setLastCheckedInstant(now);
                    taskCoordinator.notifyExecutionCancellation(registeredTask.getTaskJobCard());
                    //
                    // Metrics
                    metricsAgent.touchLastActivityInstant(node.getComponentId().getId());
                    metricsAgent.incrementCancelledTasks(node.getComponentId().getId());
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentId().getId(), PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELED.toString());
                    waitState = false;
                }
            }
            if (waitState) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    getLogger().trace(".ingresContentProcessor(): Something interrupted my nap! reason --> {}", e.getMessage());
                }
            }
        }
        //
        // Audit Trail
        if(processingPlant.getAuditingLevel().getAuditLevel() >= AuditEventCaptureLevelEnum.LEVEL_4_WUP_ALL.getAuditLevel()){
            auditBroker.logActivity(fulfillmentTask, false);
        }
        //
        // We're done
        getLogger().debug(".ingresContentProcessor(): Exit, registeredTask --> {}", registeredTask);
        return (registeredTask);
    }

    /*
    public PetasosTaskOld standardIngresContentProcessor(PetasosTaskOld transportPacket, Exchange camelExchange, WorkUnitProcessorTopologyNode wupNode) {
        getLogger().debug(".standardIngresContentProcessor(): Enter, transportPacket->{}, wupNode->{}", transportPacket, wupNode );
        // The transportPacket was already cloned prior to us receiving it, so we don't need to that.
        UoW theUoW = transportPacket.getPayload();

        // Create a new ActivityID
        getLogger().trace(".standardIngresContentProcessor(): [ActivityID Creation] Start");
        WUPIdentifier wupID = new WUPIdentifier(wupNode.getNodeFDN().getToken());
        TopologyNodeFunctionFDNToken wupFunctionToken = wupNode.getNodeFunctionFDN().getFunctionToken();
        TaskFulfillmentType oldPetasosTaskFulfillment = transportPacket.getPacketID();
        TaskFulfillmentType newPetasosTaskFulfillment = new TaskFulfillmentType();
        FulfillmentTrackingIdType previousPresentParcelInstanceID = SerializationUtils.clone(oldPetasosTaskFulfillment.getPresentParcelIdentifier());
        PetasosEpisodeIdentifier previousPresentEpisodeID =  SerializationUtils.clone(oldPetasosTaskFulfillment.getPresentEpisodeIdentifier());
        WUPIdentifier previousPresentWUPInstanceID =  SerializationUtils.clone(oldPetasosTaskFulfillment.getFulfillerComponentId());
        TopologyNodeFunctionFDNToken previousPresentWUPTypeID =  SerializationUtils.clone(oldPetasosTaskFulfillment.getPresentWUPFunctionToken());
        newPetasosTaskFulfillment.setPreviousParcelIdentifier(previousPresentParcelInstanceID);
        newPetasosTaskFulfillment.setPreviousEpisodeIdentifier(previousPresentEpisodeID);
        newPetasosTaskFulfillment.setPreviousWUPIdentifier(previousPresentWUPInstanceID);
        newPetasosTaskFulfillment.setPreviousWUPFunctionToken(previousPresentWUPTypeID);
        newPetasosTaskFulfillment.setPresentWUPFunctionToken(wupFunctionToken);
        newPetasosTaskFulfillment.setFulfillerComponentId(wupID);
        transportPacket.setPacketID(newPetasosTaskFulfillment);
        getLogger().trace(".standardIngresContentProcessor(): [ActivityID Creation] Finish");

        // Create a new JobCard
        getLogger().trace(".standardIngresContentProcessor(): [WUPJobCard Creation] Start");
        PetasosTaskJobCard activityJobCard = new PetasosTaskJobCard(
                newPetasosTaskFulfillment,
                PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING,
                PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING,
                ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE,
                ResilienceModeEnum.RESILIENCE_MODE_STANDALONE,
                Date.from(Instant.now()));
        transportPacket.setCurrentJobCard(activityJobCard);
        getLogger().trace(".standardIngresContentProcessor(): [WUPJobCard Creation] Finish");

        // Register a new PetasosResilienceParcel / WorkUnitActivity
        getLogger().trace(".standardIngresContentProcessor(): [Registering Work Unit Activity] Start");
        getLogger().trace(".standardIngresContentProcessor(): [Registering Work Unit Activity] ActivityID->{} and UoW ->{}", newPetasosTaskFulfillment, theUoW);
        TaskStatusType statusElement = petasosMOAServicesBroker.registerStandardWorkUnitActivity(activityJobCard, theUoW);
        transportPacket.setCurrentParcelStatus(statusElement);
        getLogger().trace(".standardIngresContentProcessor(): [Registering Work Unit Activity] Finish");

        // Perform a status check (remembering we are presently at the start of the WUP Framework) using status
        // received back when we registered the WUA above
        getLogger().trace(".standardIngresContentProcessor(): Let's check the status of everything");
        switch (statusElement.getFulfillmentExecutionStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE_ELSEWHERE:
                getLogger().trace(".standardIngresContentProcessor(): The Parcel is either Registered or Active_Elsewhere - both are acceptable at this point");
                break;
            case PARCEL_STATUS_FAILED: {
                statusElement.setRequiresRetry(true);
                activityJobCard.setIsToBeDiscarded(true);
                activityJobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                break;
            }
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:{
                activityJobCard.setIsToBeDiscarded(false);
                activityJobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
                break;
            }
            case PARCEL_STATUS_FINALISED_ELSEWHERE:
            case PARCEL_STATUS_FINALISED:
            case PARCEL_STATUS_FINISHED_ELSEWHERE:
            case PARCEL_STATUS_FINISHED:{
                activityJobCard.setIsToBeDiscarded(false);
                activityJobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                break;
            }
            default:
                getLogger().trace(".standardIngresContentProcessor(): The Parcel is doing something odd, none of the above states should be in-play, so cancel");
        }
        getLogger().debug(".ingresContentProcessor(): Exit, newTransportPacket --> {}", transportPacket);
        return (transportPacket);
    }

    public PetasosTaskOld alternativeIngresContentProcessor(PetasosTaskOld ingresPacket, Exchange camelExchange, TopologyNodeFunctionFDNToken wupFunctionToken, TopologyNodeFDNToken wupInstanceID) {
        getLogger().debug(".alternativeIngresContentProcessor(): Enter, ingresPacket --> {}, wupFunctionToken --> {}, wupInstanceID --> {}", ingresPacket, wupFunctionToken, wupInstanceID);
        // TODO Implement alternate flow for ingressContentProcessor functionality (retry functionality).
        return (ingresPacket);
    }

     */
}
