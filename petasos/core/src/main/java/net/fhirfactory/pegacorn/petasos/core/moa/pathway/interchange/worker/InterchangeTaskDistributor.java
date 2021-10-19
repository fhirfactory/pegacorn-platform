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
package net.fhirfactory.pegacorn.petasos.core.moa.pathway.interchange.worker;

import net.fhirfactory.pegacorn.common.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.finalisation.datatypes.TaskFinalisationStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.tasks.operations.cluster.im.PetasosActionableTaskIM;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.im.PetasosOversightTaskIM;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.pubsub.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.apache.camel.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Mark A. Hunter
 */

@Dependent
public class InterchangeTaskDistributor {

    private static final Logger LOG = LoggerFactory.getLogger(InterchangeTaskDistributor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    DataParcelSubscriptionMapIM topicServer;

    @Inject
    TopologyIM topologyProxy;

    @Inject
    private PetasosActionableTaskIM actionableTaskIM;

    @Inject
    WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    @Produce
    private ProducerTemplate template;

    @Inject
    PetasosOversightTaskIM activityServicesController;

    /**
     * Essentially, we get the set of WUPs subscribing to a particular UoW type,
     * create a property within the CamelExchange and then we use that Property
     * as a mechanism of keeping track of who we have already forwarded the UoW
     * to. Once we've cycled through all the targets (subscribers), we return
     * null.
     *
     * @param actionableTask Incoming PetasosActionableTask that will be distributed to all
     * Subscribed WUPs
     * @param camelExchange The Apache Camel Exchange instance associated with
     * this route.
     * @return An endpoint (name) for a recipient for the incoming UoW
     */
    @RecipientList
    public List<String> distributeTasksToWorkUnitProcessors(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".forwardUoW2WUPs(): Entry, wupTransportPacket (WorkUnitTransportPacket)->{}", actionableTask);

        // Get my Petasos Context
        getLogger().trace(".forwardUoW2WUPs(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        DataParcelManifest uowTopicID = null;
        if (!actionableTask.getTaskWorkItem().hasIngresContent()) {
            getLogger().debug(".forwardUoW2WUPs(): Exit, there's no payload (UoW), so return an empty list (and end this route).");
            metricsAgent.touchEventDistributionFinishInstant(node.getComponentId().getId());
            return (new ArrayList<String>());
        }
        //
        // We have content, so let's now work out who is interested!
        //
        uowTopicID = actionableTask.getTaskWorkItem().getIngresContent().getPayloadManifest();
        getLogger().trace(".forwardUoW2WUPs(): uowTopicId --> {}", uowTopicID);
        // We know the topic, so let's see the registered subscribers
        getLogger().trace(".forwardUoW2WUPs(): Getting the set of subscribers for the given topic (calling the topicServer)");
        getLogger().trace(".forwardUoW2WUPs(): Looking for Subscribers To->{}:", uowTopicID);
        List<PubSubParticipant> subscriberSet = topicServer.getSubscriberSet(uowTopicID);
        getLogger().trace(".forwardUoW2WUPs(): Before we do a general routing attempt, let's see if the message is directed somewhere specific");
        //
        // Because auditing is not running yet
        // Remove once Auditing is in place
        //
        if(getLogger().isWarnEnabled()) {
            int subscriberSetSize = 0;
            if (subscriberSet != null) {
                subscriberSetSize = subscriberSet.size();
            }
            getLogger().warn("Number of Subscribers->{}", subscriberSetSize);
        }
        //
        //
        //
        String alreadySentTo = "";
        if(!StringUtils.isEmpty(uowTopicID.getIntendedTargetSystem())){
            getLogger().trace(".forwardUoW2WUPs(): It's not empty, so let's see if the appropriate downstream system is registered");
            for(PubSubParticipant currentSubscriber: subscriberSet){
                if(hasRemoteServiceName(currentSubscriber)) {
                    String subscriberName = currentSubscriber.getInterSubsystemParticipant().getEndpointServiceName();
                    if (subscriberName.contentEquals(uowTopicID.getIntendedTargetSystem())) {
                        distributeTask(node.getComponentId(), currentSubscriber, actionableTask);
                        metricsAgent.incrementDistributedMessageCount(node.getComponentId().getId());
                        alreadySentTo = subscriberName;
                    }
                }
            }
        }

        getLogger().trace(".forwardUoW2WUPs(): Iterate through the subscribers");
        if(getLogger().isDebugEnabled()){
            getLogger().debug(".forwardUoW2WUPs(): number of subscribers to this UoW->{}", subscriberSet.size());
        }
        if (subscriberSet != null) {
            if(!subscriberSet.isEmpty()) {
                getLogger().trace(".forwardUoW2WUPs(): Iterating through....");
                for (PubSubParticipant currentSubscriber : subscriberSet) {
                    getLogger().trace(".forwardUoW2WUPs(): Iterating, currentSubscriber->{}", currentSubscriber);
                    boolean dontSendAgain = false;
                    if (hasRemoteServiceName(currentSubscriber)) {
                        getLogger().trace(".forwardUoW2WUPs(): has Inter-Subsystem element");
                        if (currentSubscriber.getInterSubsystemParticipant().getEndpointServiceName().contentEquals(alreadySentTo)) {
                            dontSendAgain = true;
                        }
                    }
                    if (!dontSendAgain) {
                        getLogger().trace(".forwardUoW2WUPs(): does not have Inter-Subsystem element");
                        distributeTask(node.getComponentId(), currentSubscriber, actionableTask);
                    }
                }
            }
        }
        // Updated Metrics Associated with Distributed Message Count
        metricsAgent.touchEventDistributionFinishInstant(node.getComponentId().getId());
        getLogger().debug(".forwardUoW2WUPs(): Exiting");
        List<String> targetSubscriberSet = new ArrayList<String>();
        return (targetSubscriberSet);
    }

    private boolean hasRemoteServiceName(PubSubParticipant subscriber){
        if(subscriber == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant() == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant().getEndpointID() == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant().getEndpointServiceName() == null){
            return(false);
        }
        return(true);
    }

    private boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(StringUtils.isEmpty(parcelManifest.getIntendedTargetSystem())){
            return(false);
        }
        return(true);
    }

    private void distributeTask(ComponentIdType componentId, PubSubParticipant subscriber, PetasosActionableTask actionableTask){
        getLogger().debug(".forwardUoW2WUPs(): Subscriber --> {}", subscriber);
        IntraSubsystemPubSubParticipantIdentifier localSubscriberIdentifier = subscriber.getIntraSubsystemParticipant().getIdentifier();
        getLogger().trace(".forwardUoW2WUPs(): The (LocalSubscriber aspect) Identifier->{}", localSubscriberIdentifier);
        WorkUnitProcessorTopologyNode targetWUPNode = (WorkUnitProcessorTopologyNode)topologyProxy.getNode(localSubscriberIdentifier);
        getLogger().trace(".forwardUoW2WUPs(): The TopologyNode for the target subscriber->{}", targetWUPNode);
        RouteElementNames routeName = new RouteElementNames(componentId);

        //
        // Get Current ActionableTask
        TaskIdType upstreamTaskId = actionableTask.getTaskTraceability().getUpstreamTaskId();
        if(upstreamTaskId == null){
            return;
        }
        PetasosActionableTask upstreamActionableTask = actionableTaskIM.getActionableTask(upstreamTaskId);
        if(upstreamActionableTask == null){
            return;
        }

        // Clone and Inject Message into Target Route
        PetasosActionableTask clonedActionableTask = SerializationUtils.clone(actionableTask);

        //
        // Everything we do here should be against the clonedActionableTask, so be careful....
        //

        //
        // Now check if the Subscriber is actually a remote one! If so, ensure it has a proper "IntendedTarget" entry
        if(hasRemoteServiceName(subscriber)){
            getLogger().trace(".forwardPacket(): Has Remote Service as Target");
            DataParcelManifest payloadTopicID = clonedActionableTask.getTaskWorkItem().getPayloadTopicID();
            boolean hasEmptyIntendedTarget = StringUtils.isEmpty(payloadTopicID.getIntendedTargetSystem());
            boolean hasWildcardTarget = false;
            if(hasIntendedTarget(payloadTopicID)){
                hasWildcardTarget = payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER);
            }
            boolean hasRemoteElement = hasRemoteServiceName(subscriber);
            getLogger().trace(".forwardPacket(): hasEmptyIntendedTarget->{}, hasWildcardTarget->{}, hasRemoteElement->{} ", hasEmptyIntendedTarget, hasWildcardTarget, hasRemoteElement);
            if((hasEmptyIntendedTarget || hasWildcardTarget) && hasRemoteElement){
                clonedActionableTask.getTaskWorkItem().getPayloadTopicID().setIntendedTargetSystem(subscriber.getInterSubsystemParticipant().getEndpointServiceName());
                getLogger().trace(".forwardPacket(): Setting the intendedTargetSystem->{}", subscriber.getInterSubsystemParticipant().getEndpointServiceName());
            }
        }
        //
        // OK, now send the actionableTask onto its "next great adventure"...
        template.sendBody(routeName.getEndPointWUPContainerIngresProcessorIngres(), ExchangePattern.InOnly, clonedActionableTask);
        //
        // Metrics
        metricsAgent.incrementDistributedMessageCount(componentId.getId());
        metricsAgent.incrementDistributedMessageEndpointCount(componentId.getId(), subscriber.getIntraSubsystemParticipant().getComponentID());
        //
        // Now add the downstream WUPFunction to the Parcel Finalisation Registry
        if(!upstreamActionableTask.hasTaskFinalisation()){
            synchronized (upstreamActionableTask.getTaskFinalisationLock()) {
                TaskFinalisationStatusType finalisationStatus = new TaskFinalisationStatusType();
                upstreamActionableTask.setTaskFinalisation(finalisationStatus);
            }
        }
        synchronized (upstreamActionableTask.getTaskFinalisationLock()){
            upstreamActionableTask.getTaskFinalisation().addDownstreamTask(clonedActionableTask.getTaskId());
        }
    }

    private void tracePrintSubscribedWUPSet(Set<WorkUnitProcessorTopologyNode> wupSet) {
        getLogger().trace(".tracePrintSubscribedWUPSet(): Subscribed WUP Set --> {}", wupSet.size());
        Iterator<WorkUnitProcessorTopologyNode> tokenIterator = wupSet.iterator();
        while (tokenIterator.hasNext()) {
            getLogger().trace(".forwardUoW2WUPs(): Subscribed WUP Ingres Point --> {}", tokenIterator.next());
        }
    }
}
