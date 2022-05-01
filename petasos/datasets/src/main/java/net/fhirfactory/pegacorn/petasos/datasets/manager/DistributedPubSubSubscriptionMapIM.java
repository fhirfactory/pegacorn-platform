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
package net.fhirfactory.pegacorn.petasos.datasets.manager;

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.datasets.cache.DistributedPubSubSubscriptionMapDM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DistributedPubSubSubscriptionMapIM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedPubSubSubscriptionMapIM.class);

    @Inject
    private DistributedPubSubSubscriptionMapDM registrationMapDM;

    /**
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration registerPublisherInstance(InterSubsystemPubSubParticipant publisher) {
        LOG.debug(".registerPublisherInstance(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = registrationMapDM.registerPublisherInstance(publisher);
        LOG.debug(".registerPublisherInstance(): Exit, registration->{}", registration);
        return (registration);
    }

    /**
     * @param publisher
     * @return
     */
    public void unregisterPublisherInstance(InterSubsystemPubSubParticipant publisher) {
        LOG.debug(".unregisterPublisherInstance(): Entry, publisher->{}", publisher);
        registrationMapDM.unregisterPublisherInstance(publisher);
        LOG.debug(".unregisterPublisherInstance(): Exit");
    }

    public void unregisterPublisherInstance(String publisherInstanceName) {
        LOG.debug(".unregisterPublisherInstance(): Entry, publisher->{}", publisherInstanceName);
        registrationMapDM.unregisterPublisherInstance(publisherInstanceName);
        LOG.debug(".unregisterPublisherInstance(): Exit");
    }

    /**
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration getPublisherInstanceRegistration(InterSubsystemPubSubParticipant publisher) {
        LOG.debug(".getPublisherInstanceRegistration(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = registrationMapDM.getPublisherInstanceRegistration(publisher);
        LOG.debug(".getPublisherInstanceRegistration(): Exit, registration->{}", registration);
        return (registration);
    }

    public boolean isPublisherRegistered(InterSubsystemPubSubParticipant publisher) {
        LOG.debug(".isPublisherRegistered(): Entry, publisher->{}", publisher);
        boolean isAlreadyRegistered = registrationMapDM.isPublisherRegistered(publisher);
        if (isAlreadyRegistered) {
            LOG.debug(".isPublisherRegistered(): Exit, publisher found & has Status of PUBLISHER_REGISTERED, returning->{}", true);
            return (true);
        } else {
            LOG.debug(".isPublisherRegistered(): Exit, publisher not found & has Status not equal to PUBLISHER_REGISTERED, returning->{}", false);
            return (false);
        }
    }

    public List<InterSubsystemPubSubParticipant> getPublisherServiceProviderInstances(String publisherServiceName) {
        LOG.debug(".getPublisherServiceProviderInstances(): Entry, publisherServiceName->{}", publisherServiceName);
        List<InterSubsystemPubSubParticipant> publisherList = registrationMapDM.getPublisherServiceProviderInstances(publisherServiceName);
        LOG.debug(".getPublisherServiceProviderInstances(): Exit");
        return (publisherList);
    }

    public List<InterSubsystemPubSubPublisherRegistration> getPublisherServiceProviderInstanceRegistrations(String publisherServiceName) {
        LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Entry, publisherServiceName->{}", publisherServiceName);
        List<InterSubsystemPubSubPublisherRegistration> publisherRegistrationList = registrationMapDM.getPublisherServiceProviderInstanceRegistrations(publisherServiceName);
        LOG.debug(".getPublisherServiceProviderInstances(): Exit");
        return (publisherRegistrationList);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration addSubscriptionToPublisher(List<DataParcelManifest> subscriptionList, String publisherServiceName) {
        LOG.debug(".addSubscriptionToPublisher(): Entry, publisherServiceName->{}", publisherServiceName);
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = registrationMapDM.addSubscriptionToPublisher(subscriptionList, publisherServiceName);
        LOG.debug(".addSubscriptionToPublisher(): Exit, registration->{}", registration);
        return (registration);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration getPublisherServiceSubscription(InterSubsystemPubSubParticipant publisher) {
        LOG.debug(".getSubscriptionToPublisher(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = registrationMapDM.getPublisherServiceSubscription(publisher);
        LOG.debug(".getSubscriptionToPublisher(): Exit, registration->{}", registration);
        return (registration);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration getPublisherServiceSubscription(String publisherServiceName){
        LOG.debug(".getSubscriptionToPublisher(): Entry, publisherServiceName->{}", publisherServiceName);
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = registrationMapDM.getPublisherServiceSubscription(publisherServiceName);
        LOG.debug(".getSubscriptionToPublisher(): Exit, registration->{}", registration);
        return (registration);
    }

    public List<InterSubsystemPubSubPublisherSubscriptionRegistration> getAllPublisherServiceSubscriptions() {
        LOG.debug(".getAllPublisherServiceSubscriptions(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> registrationList = registrationMapDM.getAllPublisherServiceSubscriptions();
        LOG.debug(".getAllPublisherServiceSubscriptions(): Exit");
        return(registrationList);
    }

    public List<String> getAllPublishers(){
        LOG.debug(".getAllPublishers(): Entry");
        List<String> publisherList = registrationMapDM.getAllPublishers();
        LOG.debug(".getAllPublishers(): Exit");
        return(publisherList);
    }

    public InterSubsystemPubSubPublisherRegistration getPublisherInstanceRegistration(String publisherInstanceName) {
        LOG.debug(".getPublisherInstanceRegistration(): Entry, publisherInstanceName->{}", publisherInstanceName);
        InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = registrationMapDM.getPublisherInstanceRegistration(publisherInstanceName);
        LOG.debug("getPublisherInstanceRegistration(): Exit, publisherInstanceRegistration->{}", publisherInstanceRegistration);
        return (publisherInstanceRegistration);
    }
}
