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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

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
    PetasosMOAServicesBroker petasosMOAServicesBroker;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    @Inject
    TopologyIM topologyProxy;
    
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
     * @param transportPacket The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange The Apache Camel Exchange object, used to store a Semaphors and Attributes
     * @return Should return a WorkUnitTransportPacket that is forwarding onto the WUP Ingres Gatekeeper.
     */
    public PetasosTaskOld ingresContentProcessor(PetasosTaskOld transportPacket, Exchange camelExchange) {
        getLogger().debug(".ingresContentProcessor(): Enter, transportPacket (WorkUnitTransportPacket)->{}", transportPacket );
        //
        // 1st, lets resolve our WorkUnitProcessorTopologyNode from the incoming Exchange (camelExchange)
        //
        getLogger().trace(".ingresContentProcessor(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        //
        // Now let's report some metrics
        //
        metricsAgent.incrementIngresMessageCount(node.getComponentID());
        metricsAgent.touchLastActivityInstant(node.getComponentID());
        metricsAgent.touchActivityStartInstant(node.getComponentID());
        metricsAgent.incrementRegisteredTasks(node.getComponentID());
        //
        // Now do the actual processing of the transport packet and content
        //
        TopologyNodeFunctionFDNToken wupFunctionToken = node.getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".ingresContentProcessor(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);
        // Now, continue with business logic
        getLogger().trace(".ingresContentProcessor(): Now, check if this the 1st time the associated UoW has been (attempted to be) processed");
        PetasosTaskOld newTransportPacket;
        if (transportPacket.getIsARetry()) {
            getLogger().trace(".ingresContentProcessor(): This is a recovery or retry iteration of processing this UoW, so send to .alternativeIngresContentProcessor()");
            newTransportPacket = alternativeIngresContentProcessor(transportPacket, camelExchange, wupFunctionToken, node.getNodeFDN().getToken());
        } else {
            getLogger().trace(".ingresContentProcessor(): This is the 1st time this UoW is being processed, so send to .standardIngresContentProcessor()");
            newTransportPacket = standardIngresContentProcessor(transportPacket, camelExchange, node);
        }
        //
        //
        //
        long waitTime = PetasosPropertyConstants.WUP_SLEEP_INTERVAL_MILLISECONDS;
        boolean waitState = true;
        PetasosTaskJobCard jobCard = newTransportPacket.getCurrentJobCard();
        TaskStatusType statusElement = newTransportPacket.getCurrentParcelStatus();
        metricsAgent.updateCurrentEpisode(node.getComponentID(), statusElement.getActivityID().getPresentEpisodeIdentifier());
        if(statusElement.getActivityID().hasPreviousEpisodeIdentifier()) {
            metricsAgent.updatePreviousEpisodeID(node.getComponentID(), statusElement.getActivityID().getPreviousEpisodeIdentifier());
        }
        metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(),jobCard.getCurrentStatus().toString());
        while (waitState) {
            getLogger().trace(".ingresContentProcessor(): jobCard.getCurrentStatus->{}", jobCard.getCurrentStatus());
            switch (jobCard.getCurrentStatus()) {
                case WUP_ACTIVITY_STATUS_WAITING:
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING.toString());
                    getLogger().trace(".ingresContentProcessor(): jobCard.getCurrentStatus --> {}", PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING );
                    jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                    petasosMOAServicesBroker.synchroniseJobCard(jobCard);
                    if (jobCard.getGrantedStatus() == PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING) {
                        jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                        statusElement.setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
                        petasosMOAServicesBroker.notifyStartOfWorkUnitActivity(jobCard);
                        metricsAgent.touchLastActivityInstant(node.getComponentID());
                        metricsAgent.incrementStartedTasks(node.getComponentID());
                        metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING.toString());
                        getLogger().trace(".ingresContentProcessor(): We've been granted execution privileges!");
                        waitState = false;
                    }
                    break;
                case WUP_ACTIVITY_STATUS_EXECUTING:{
                    jobCard.setIsToBeDiscarded(true);
                    waitState = false;
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED.toString());
                    metricsAgent.incrementFailedTasks(node.getComponentID());
                    statusElement.setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
                    jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                    jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                }
                case WUP_ACTIVITY_STATUS_FINISHED:
                case WUP_ACTIVITY_STATUS_FAILED:
                    jobCard.setIsToBeDiscarded(true);
                    waitState = false;
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED.toString());
                    metricsAgent.incrementFailedTasks(node.getComponentID());
                    statusElement.setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
                    jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                    jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                    break;
                case WUP_ACTIVITY_STATUS_CANCELED:
                default:
                    jobCard.setIsToBeDiscarded(true);
                    waitState = false;
                    jobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                    jobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                    statusElement.setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_CANCELLED);
                    metricsAgent.touchLastActivityInstant(node.getComponentID());
                    metricsAgent.incrementCancelledTasks(node.getComponentID());
                    metricsAgent.updateWorkUnitProcessorStatus(node.getComponentID(), PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED.toString());

            }
            if (waitState) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    getLogger().trace(".ingresContentProcessor(): Something interrupted my nap! reason --> {}", e.getMessage());
                }
            }
        }
        if (jobCard.getIsToBeDiscarded()) {
            TaskStatusType currentParcelStatus = newTransportPacket.getCurrentParcelStatus();
            currentParcelStatus.setRequiresRetry(true);
        }
        getLogger().debug(".ingresContentProcessor(): Exit, newTransportPacket --> {}", newTransportPacket);
        return (newTransportPacket);
    }

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
}
