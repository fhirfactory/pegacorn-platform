/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.itops.collectors;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsPubSubMapLocalDM;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.subscriptions.TopicSummaryFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.interfaces.ITOpsPubSubCollectionAgentInterface;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.ProcessingPlantSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.PublisherSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.SubscriberSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.WorkUnitProcessorSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.valuesets.SubscriptionSummaryType;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ITOpsPubSubCollectionAgent implements ITOpsPubSubCollectionAgentInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsPubSubCollectionAgent.class);

    @Inject
    private ITOpsPubSubMapLocalDM itopsPubSubMapDM;

    @Inject
    private DataParcelSubscriptionMapIM subscriptionMapIM;

    @Inject
    private DistributedPubSubSubscriptionMapIM distributedSubscriptionMapIM;

    @Inject
    private TopicSummaryFactory topicSummaryFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Override
    public void refreshLocalProcessingPlantPubSubMap(){
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Entry");
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Get all InterSubsystemPubSubPublisherSubscriptionRegistration(s)");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> allPublisherServiceSubscriptions = distributedSubscriptionMapIM.getAllPublisherServiceSubscriptions();
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Create a ProcessingPlantSubscriptionSummary skeleton");
        ProcessingPlantSubscriptionSummary processingPlantSubscriptionSummary = new ProcessingPlantSubscriptionSummary();
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Assign the ProcessintPlant ComponentID (processingPlantNode->{})", processingPlant.getProcessingPlantNode());
        processingPlantSubscriptionSummary.setComponentID(processingPlant.getProcessingPlantNode().getComponentID());
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Check if there are any subscriptions, if not, exit out");
        if(allPublisherServiceSubscriptions.isEmpty()){
            LOG.info(".refreshLocalProcessingPlantPubSubMap(): Exit, publisher service subscriptions is empty");
            return;
        }
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Iterate Through Registrations");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentRegistration: allPublisherServiceSubscriptions){
            List<InterSubsystemPubSubPublisherRegistration> publisherServiceProviderInstanceRegistrations = distributedSubscriptionMapIM.getPublisherServiceProviderInstanceRegistrations(currentRegistration.getPublisherServiceName());
            for(InterSubsystemPubSubPublisherRegistration currentPublisherRegistration: publisherServiceProviderInstanceRegistrations) {
                SubscriberSubscriptionSummary publisherSubscriptionSummary = new SubscriberSubscriptionSummary();
                publisherSubscriptionSummary.setPublisherServiceName(currentRegistration.getPublisherServiceName());
                publisherSubscriptionSummary.setTimestamp(Instant.from(currentRegistration.getRegistrationDate().toInstant()));
                publisherSubscriptionSummary.setSummaryType(SubscriptionSummaryType.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                publisherSubscriptionSummary.setPublisher(currentPublisherRegistration.getPublisher().getEndpointID().getProcessingPlantComponentID());
                for(DataParcelManifest currentManifest: currentRegistration.getSubscriptionList()){
                    publisherSubscriptionSummary.getSubscribedTopics().add(topicSummaryFactory.transformToSimpleTopicName(currentManifest));
                }
                processingPlantSubscriptionSummary.addSubscriberSummary(publisherSubscriptionSummary);
            }
        }
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Create Summary Set");
        List<PubSubSubscription> allSubscriptions = subscriptionMapIM.getAllSubscriptions();
        for(PubSubSubscription currentSubscription: allSubscriptions){
            if(currentSubscription.getSubscriber().getInterSubsystemParticipant() != null){
                String processingPlantID = currentSubscription.getSubscriber().getInterSubsystemParticipant().getEndpointID().getProcessingPlantComponentID();
                String topic = topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest());
                boolean added = processingPlantSubscriptionSummary.addSubscriptionForExistingSubscriber(processingPlantID, topic);
                if(!added){
                    PublisherSubscriptionSummary subscriberSummary = new PublisherSubscriptionSummary();
                    subscriberSummary.setSubscriber(processingPlantID);
                    subscriberSummary.setSummaryType(SubscriptionSummaryType.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                    subscriberSummary.setTimestamp(currentSubscription.getRegistrationInstant());
                    subscriberSummary.setSubscriberServiceName(currentSubscription.getSubscriber().getInterSubsystemParticipant().getEndpointServiceName());
                    subscriberSummary.addTopic(topic);
                    subscriberSummary.setComponentID(processingPlantID);
                    processingPlantSubscriptionSummary.addPublisherSummary(subscriberSummary);
                }
            }
        }
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Add Summary Set to the ProcessingPlant cache");
        itopsPubSubMapDM.addProcessingPlantSubscriptionSummary(processingPlantSubscriptionSummary);
        LOG.info(".refreshLocalProcessingPlantPubSubMap(): Exit");
    }

    @Override
    public void refreshWorkUnitProcessorPubSubMap(){
        LOG.info(".refreshWorkUnitProcessorPubSubMap(): Entry");
        List<PubSubSubscription> allSubscriptions = subscriptionMapIM.getAllSubscriptions();
        Map<String, WorkUnitProcessorSubscriptionSummary> summaries = new HashMap<>();
        for(PubSubSubscription currentSubscription: allSubscriptions){
            if(summaries.containsKey(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getComponentID())){
                WorkUnitProcessorSubscriptionSummary currentSummary = summaries.get(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getComponentID());
                currentSummary.addTopic(topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest()));
            } else {
                WorkUnitProcessorSubscriptionSummary currentSummary = new WorkUnitProcessorSubscriptionSummary();
                currentSummary.setSummaryType(SubscriptionSummaryType.WORK_UNIT_PROCESSOR_SUMMARY);
                currentSummary.setSubscriber(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getComponentID());
                currentSummary.setTimestamp(currentSubscription.getRegistrationInstant());
                currentSummary.addTopic(topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest()));
                currentSummary.setComponentID(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getComponentID());
                summaries.put(currentSummary.getSubscriber(), currentSummary);
            }
        }
        if(!summaries.isEmpty()){
            for(WorkUnitProcessorSubscriptionSummary currentSummary: summaries.values()){
                itopsPubSubMapDM.addWorkUnitProcessorSubscriptionSummary(currentSummary);
            }
        }
        LOG.info(".refreshWorkUnitProcessorPubSubMap(): Exit");
    }
}
