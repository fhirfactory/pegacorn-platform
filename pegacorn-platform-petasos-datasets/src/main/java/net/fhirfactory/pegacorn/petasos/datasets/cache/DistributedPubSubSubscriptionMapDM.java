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
package net.fhirfactory.pegacorn.petasos.datasets.cache;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DistributedPubSubSubscriptionMapDM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedPubSubSubscriptionMapDM.class);

    // ConcurrentHashMap<publisherServiceInstanceName, publisherRegistration>
    private ConcurrentHashMap<String, InterSubsystemPubSubPublisherRegistration> publisherMap;
    private Object publisherMapLock;

    // ConcurrentHashMap<publisherServiceName, List<publisherServiceInstanceName>>
    private ConcurrentHashMap<String, List<String>> publisherServiceFulfillmentMap;
    private Object publisherServiceFulfillmentMapLock;

    // ConcurrentHashMap<publisherServiceName, subscriptionRegistration>
    private ConcurrentHashMap<String, InterSubsystemPubSubPublisherSubscriptionRegistration> publisherServiceSubscriptionMap;
    private Object publisherSubscriptionMapLock;

    public DistributedPubSubSubscriptionMapDM(){
        this.publisherMap = new ConcurrentHashMap<>();
        this.publisherMapLock = new Object();
        this.publisherServiceSubscriptionMap = new ConcurrentHashMap<>();
        this.publisherSubscriptionMapLock = new Object();
        this.publisherServiceFulfillmentMap = new ConcurrentHashMap<>();
        this.publisherServiceFulfillmentMapLock = new Object();
    }

    //
    // Publisher Traceability
    //

    /**
     *
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration registerPublisherInstance(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".registerPublisherInstance(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = new InterSubsystemPubSubPublisherRegistration();
        LOG.trace(".registerPublisherInstance(): First, we check the content of the passed-in parameter");
        if(publisher == null){
            LOG.debug("registerPublisherInstance(): Exit, publisher is null, returning -null-");
            return(null);
        }
        if(publisher.getEndpointID() == null){
            LOG.debug("registerPublisherInstance(): Exit, publisher identifier is null, return -null");
            return(null);
        }
        if(publisher.getEndpointServiceName() == null || publisher.getEndpointID().getEndpointName() == null){
            LOG.debug("registerPublisherInstance(): Exit, publisher name or instance name is null, returning -null-");
            return(null);
        }
        LOG.warn(".registerPublisherInstance(): Now, check to see if publisher (instance) is already cached and, if so, do nothing!");
        if(publisherMap.containsKey(publisher.getEndpointID().getEndpointName())){
            registration = publisherMap.get(publisher.getEndpointID().getEndpointName());
            LOG.warn("registerPublisherInstance(): Exit, publisher already registered, registration->{}", registration);
            return(registration);
        } else {
            LOG.trace(".registerPublisherInstance(): Publisher is not in Map, so add it!");
            registration.setPublisher(publisher);
            registration.setRegistrationCommentary("Publisher Registered");
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setLastActivityDate(Date.from(Instant.now()));
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED);
            synchronized (this.publisherMapLock) {
                publisherMap.put(publisher.getEndpointID().getEndpointName(), registration);
            }
            addPublisherServiceProviderInstance(publisher);
            LOG.warn(".registerPublisherInstance(): Exit, registration->{}", registration);
            return (registration);
        }
    }

    public void unregisterPublisherInstance(String publisherInstanceName){
        LOG.debug(".unregisterPublisherInstance(): Entry, publisherInstanceName->{}", publisherInstanceName);
        InterSubsystemPubSubPublisherRegistration registration = null;
        if(StringUtils.isEmpty(publisherInstanceName)){
            registration = new InterSubsystemPubSubPublisherRegistration();
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_UTILISED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.trace("unregisterPublisherInstance(): Exit, publisher is null, registration->{}", registration);
        }
        LOG.trace(".unregisterPublisherInstance(): publisherInstanceName is not null");
        if(publisherMap.containsKey(publisherInstanceName)){
            InterSubsystemPubSubPublisherRegistration publisherRegistration = publisherMap.get(publisherInstanceName);
            synchronized (this.publisherMapLock) {
                registration = publisherMap.remove(publisherInstanceName);
            }
            LOG.trace(".unregisterPublisherInstance(): have removed publisher registration, now clean up ServiceFulfillmentMap");
            String serviceName = publisherRegistration.getPublisher().getEndpointServiceName();
            List<String> publisherList = this.publisherServiceFulfillmentMap.get(serviceName);
            if(publisherList != null){
                LOG.trace(".unregisterPublisherInstance(): There was a publisherList in the ServiceFulfillmentMap, removing instance");
                if(publisherList.contains(publisherInstanceName)){
                    publisherList.remove(publisherInstanceName);
                }
                if(publisherList.isEmpty()){
                    this.publisherServiceFulfillmentMap.remove(serviceName);
                }
            }
            LOG.debug(".unregisterPublisherInstance(): Invoking checkAProviderIsAvailable() with serviceName->{}", serviceName);
            checkAProviderIsAvailable(serviceName);
        }
    }

    /**
     *
     * @param publisher
     * @return
     */
    public void unregisterPublisherInstance(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".unregisterPublisherInstance(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = new InterSubsystemPubSubPublisherRegistration();
        LOG.trace(".unregisterPublisherInstance(): First, we check the content of the passed-in parameter");
        if(publisher == null){
            LOG.debug(".unregisterPublisherInstance(): Exit, publisher is null");
            return;
        }
        if(publisher.getEndpointID() == null){
            LOG.debug(".unregisterPublisherInstance(): Exit, endpointID is null");
            return;
        }
        if(StringUtils.isEmpty(publisher.getEndpointID().getEndpointName())){
            LOG.debug(".unregisterPublisherInstance(): Exit, endpoint name is null");
            return;
        }
        String publisherInstanceName = publisher.getEndpointID().getEndpointName();
        LOG.debug(".unregisterPublisherInstance(): Calling unregisterPublisherInstance() with publisherInstanceName->{}", publisherInstanceName);
        unregisterPublisherInstance(publisherInstanceName);
    }

    /**
     *
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration getPublisherInstanceRegistration(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".getPublisherInstanceRegistration(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = new InterSubsystemPubSubPublisherRegistration();
        LOG.trace(".getPublisherInstanceRegistration(): First, we check the content of the passed-in parameter");
        if(publisher == null){
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher is null, returning null");
            return(null);
        }
        if(publisher.getEndpointID() == null){

            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher identifier is null, returning null");
            return(null);
        }
        if(publisher.getEndpointServiceName() == null || publisher.getEndpointID().getEndpointName() == null){
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher name or instance name is null, returning null");
            return(null);
        }
        LOG.trace(".getPublisherInstanceRegistration(): Now, check to see if publisher (instance) is in the cache and, if so, return detail!");
        if(publisherMap.containsKey(publisher.getEndpointID().getEndpointName())){
            registration = publisherMap.get(publisher.getEndpointID().getEndpointName());
            LOG.trace("getPublisherInstanceRegistration(): Exit, publisher found, registration->{}", registration);
            return(registration);
        } else {
            LOG.debug("getPublisherInstanceRegistration(): Exit, Could not find registration, returning null");
            return (registration);
        }
    }

    public InterSubsystemPubSubPublisherRegistration getPublisherInstanceRegistration(String publisherInstanceName){
        LOG.debug(".getPublisherInstanceRegistration(): Entry, publisherInstanceName->{}", publisherInstanceName);
        if(StringUtils.isEmpty(publisherInstanceName)){
            LOG.debug(".getPublisherInstanceRegistration(): Entry, publisherInstanceName is empty, returning -null-");
            return(null);
        }
        if(publisherMap.containsKey(publisherInstanceName)){
            InterSubsystemPubSubPublisherRegistration registration = publisherMap.get(publisherInstanceName);
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher found, registration->{}", registration);
            return(registration);
        } else {
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher not registered, return -null-");
            return (null);
        }
    }

    //
    // Publisher Instance Traceability
    //

    private void addPublisherServiceProviderInstance(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".addPublisherServiceProviderInstance(): Entry, publisher->{}", publisher);
        if(publisher == null){
            LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisher is null");
            return;
        }
        if(publisher.getEndpointID() == null){
            LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisher.getIdentifier() is null");
            return;
        }
        String publisherInstanceName = publisher.getEndpointID().getEndpointName();
        String publisherServiceName = publisher.getEndpointServiceName();
        if(publisherServiceName == null || publisherInstanceName == null){
            LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisherServiceName or publisherInstanceName is null");
            return;
        }
        synchronized (publisherServiceFulfillmentMapLock) {
            List<String> publisherInstanceList = publisherServiceFulfillmentMap.get(publisherServiceName);
            if (publisherInstanceList == null) {
                LOG.trace(".addPublisherServiceProviderInstance(): No map entry exists for service ({}), so creating it", publisherServiceName);
                publisherInstanceList = new ArrayList<>();
                publisherInstanceList.add(publisherInstanceName);
                publisherServiceFulfillmentMap.put(publisherServiceName, publisherInstanceList);
            } else {
                LOG.trace(".addPublisherServiceProviderInstance(): No map entry for service ({}), so just adding list-entry", publisherServiceName);
                if (publisherInstanceList.contains(publisherInstanceName)) {
                    // do nothing
                } else {
                    publisherInstanceList.add(publisherInstanceName);
                }
            }
        }
        LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisher instance added");
    }

    private void removePublisherServiceProviderInstance(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".removePublisherServiceProviderInstance(): Entry, publisher->{}", publisher);
        if(publisher == null){
            LOG.debug(".removePublisherServiceProviderInstance(): Exit, publisher is null");
            return;
        }
        if(publisher.getEndpointID() == null){
            LOG.debug(".removePublisherServiceProviderInstance(): Exit, publisher.getIdentifier() is null");
            return;
        }
        String publisherInstanceName = publisher.getEndpointID().getEndpointName();
        String publisherServiceName = publisher.getEndpointServiceName();
        if(publisherServiceName == null || publisherInstanceName == null){
            LOG.debug(".removePublisherServiceProviderInstance(): Exit, publisherName or publisherInstanceName is null");
            return;
        }
        synchronized (publisherServiceFulfillmentMapLock) {
            List<String> publisherInstanceList = publisherServiceFulfillmentMap.get(publisherServiceName);
            if (publisherInstanceList != null) {
                if (publisherInstanceList.contains(publisherInstanceName)) {
                    publisherInstanceList.remove(publisherInstanceName);
                }
            }
            if(publisherInstanceList.isEmpty()){
                publisherServiceFulfillmentMap.remove(publisherServiceName);
            }
        }
        LOG.debug(".removePublisherServiceProviderInstance(): Exit, publisher removed added");
    }

    public List<InterSubsystemPubSubParticipant> getPublisherServiceProviderInstances(String publisherServiceName){
        LOG.debug(".getPublisherServiceProviderInstances(): Entry, publisherServiceName->{}", publisherServiceName);
        List<InterSubsystemPubSubParticipant> publisherDetailList = new ArrayList<>();
        if(StringUtils.isBlank(publisherServiceName)){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, publisherServiceName is empty/null");
            return(publisherDetailList);
        }
        List<String> publisherList = publisherServiceFulfillmentMap.get(publisherServiceName);
        if(publisherList == null){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, no publisher (list) for provided publisherServiceName");
            return(publisherDetailList);
        }
        if(publisherList.isEmpty()){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, empty publisher (list) for provided publisherServiceName");
            return(publisherDetailList);
        }
        for(String currentPublisherInstanceName: publisherList){
            InterSubsystemPubSubPublisherRegistration publisherRegistration = publisherMap.get(currentPublisherInstanceName);
            if(publisherRegistration != null){
                InterSubsystemPubSubParticipant currentPublisher = publisherRegistration.getPublisher();
                if(currentPublisher != null){
                    publisherDetailList.add(currentPublisher);
                }
            }
        }
        LOG.debug(".getPublisherServiceProviderInstances(): Exit, returning list");
        return(publisherDetailList);
    }

    public List<InterSubsystemPubSubPublisherRegistration> getPublisherServiceProviderInstanceRegistrations(String publisherServiceName){
        LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Entry, publisherServiceName->{}", publisherServiceName);
        if(StringUtils.isBlank(publisherServiceName)){
            LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Exit, publisherServiceName is empty/null");
            return(new ArrayList<>());
        }
        List<String> publisherList = publisherServiceFulfillmentMap.get(publisherServiceName);
        if(publisherList == null){
            LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Exit, no publisher (list) for provided publisherServiceName");
            return(new ArrayList<>());
        }
        if(publisherList.isEmpty()){
            LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Exit, empty publisher (list) for provided publisherServiceName");
            return(new ArrayList<>());
        }
        List<InterSubsystemPubSubPublisherRegistration> publisherDetailList = new ArrayList<>();
        LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Creating publisherDetailList");
        for(String currentPublisherInstanceName: publisherList){
            LOG.trace(".getPublisherServiceProviderInstanceRegistrations(): processing=>{}", currentPublisherInstanceName);
            InterSubsystemPubSubPublisherRegistration publisherRegistration = publisherMap.get(currentPublisherInstanceName);
            LOG.trace(".getPublisherServiceProviderInstanceRegistrations(): Registration=>{}", publisherRegistration);
            if(publisherRegistration != null){
                LOG.trace(".getPublisherServiceProviderInstanceRegistrations(): adding entry to the publisherDetailList");
                publisherDetailList.add(publisherRegistration);
            }
        }
        LOG.debug(".getPublisherServiceProviderInstanceRegistrations(): Exit, returning list");
        return(publisherDetailList);
    }

    public boolean isPublisherRegistered(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".isPublisherRegistered(): Entry, publisher->{}", publisher);
        if(publisher == null){
            LOG.debug(".isPublisherRegistered(): Exit, publisher is null, return -false-");
            return(false);
        }
        if(publisher.getEndpointID() == null){
            LOG.debug(".isPublisherRegistered(): Exit, publisher.getIdentifier() is null, return -false-");
            return(false);
        }
        String publisherInstanceName = publisher.getEndpointID().getEndpointName();
        String publisherServiceName = publisher.getEndpointServiceName();
        if(publisherServiceName == null || publisherInstanceName == null){
            LOG.debug(".isPublisherRegistered(): Exit, publisherServiceName or publisherInstanceName is null, return -false-");
            return(false);
        }
        List<InterSubsystemPubSubParticipant> publisherList = getPublisherServiceProviderInstances(publisherServiceName);
        if(publisherList.isEmpty()){
            LOG.debug(".isPublisherRegistered(): Exit, No publishers registered for the specified publisherService, return -false-");
            return(false);
        }
        for(InterSubsystemPubSubParticipant currentPublisher: publisherList){
            if(currentPublisher.equals(publisher)){
                LOG.debug(".isPublisherRegistered(): Exit, Publisher registered, return -true-");
                return(true);
            }
        }
        LOG.debug(".isPublisherRegistered(): Exit, Publisher Service registered, but specific publisher Instance is not, return -false-");
        return(false);
    }

    //
    // Publisher Subscription Traceability
    //

    public InterSubsystemPubSubPublisherSubscriptionRegistration addSubscriptionToPublisher(List<DataParcelManifest> subscriptionList, String publisherServiceName){
        LOG.debug(".addSubscriptionToPublisher(): Entry, publisherServiceName->{}", publisherServiceName);
        LOG.trace(".addSubscriptionToPublisher(): First, we check the content of the passed-in parameter, tedious, but good defensive programming");
        if(StringUtils.isEmpty(publisherServiceName)){
            LOG.debug(".addSubscriptionToPublisher(): Exit, publisherServiceName is null");
            return(null);
        }
        LOG.trace(".addSubscriptionToPublisher(): publisher parameter is good!");
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = null;
        synchronized (this.publisherSubscriptionMapLock) {
            registration = publisherServiceSubscriptionMap.get(publisherServiceName);
            if (registration != null) {
                registration.addSubscriptionList(subscriptionList);
            } else {
                registration = new InterSubsystemPubSubPublisherSubscriptionRegistration();
                registration.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
                registration.setSubscriptionList(subscriptionList);
                registration.setPublisherServiceName(publisherServiceName);
                registration.setRegistrationDate(Date.from(Instant.now()));
                publisherServiceSubscriptionMap.put(publisherServiceName, registration);
            }
        }
        LOG.debug("addSubscriptionToPublisher(): Exit, subscription registered, registration->{}", registration);
        return(registration);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration getPublisherServiceSubscription(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".getSubscriptionToPublisher(): Entry, publisher->{}", publisher);
        LOG.trace(".getSubscriptionToPublisher(): First, we check the content of the passed-in parameter, tedious, but good defensive programming");
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = new InterSubsystemPubSubPublisherSubscriptionRegistration();
        if(publisher == null){
            LOG.debug(".getSubscriptionToPublisher(): Exit, publisher is null");
            return(null);
        }
        if(publisher.getEndpointID() == null){
            LOG.debug(".getSubscriptionToPublisher(): Exit, publisher.getIdentifier() is null");
            return(null);
        }
        if(StringUtils.isEmpty(publisher.getEndpointServiceName())){
            LOG.debug(".getSubscriptionToPublisher(): Exit, publisherServiceName is null");
            return(null);
        }
        registration = publisherServiceSubscriptionMap.get(publisher.getEndpointServiceName());
        if(registration != null){
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration found, value->{}", registration);

        } else {
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration no found");
        }
        return(registration);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration getPublisherServiceSubscription(String publisherServiceName){
        LOG.debug(".getSubscriptionToPublisher(): Entry, publisherServiceName->{}", publisherServiceName);
        if(StringUtils.isEmpty(publisherServiceName)){
            LOG.debug(".getSubscriptionToPublisher(): Exit, publisherServiceName is null, returning -null-");
            return(null);
        }
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = publisherServiceSubscriptionMap.get(publisherServiceName);
        registration = publisherServiceSubscriptionMap.get(publisherServiceName);
        if(registration != null){
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration found, value->{}", registration);

        } else {
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration no found");
        }
        return(registration);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration checkAProviderIsAvailable(String publisherServiceName){
        LOG.debug(".checkAProviderIsAvailable(): Entry, publisherServiceName->{}", publisherServiceName);
        InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = publisherServiceSubscriptionMap.get(publisherServiceName);
        if(subscriptionRegistration != null) {
            List<InterSubsystemPubSubParticipant> publisherServiceProviderInstances = getPublisherServiceProviderInstances(publisherServiceName);
            if (publisherServiceProviderInstances.isEmpty()) {
                subscriptionRegistration.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
                LOG.debug(".checkAProviderIsAvailable(): Exit, no provider now available for service, subscriptionRegistration->{}", subscriptionRegistration);
                return (subscriptionRegistration);
            } else {
                LOG.debug(".checkAProviderIsAvailable(): Exit, there are still providers providing service, subscriptionRegistration->{}", subscriptionRegistration);
                return(subscriptionRegistration);
            }
        } else {
            LOG.debug(".checkAProviderIsAvailable(): Exit, there are no subscribers consuming this publisher service");
            return (null);
        }
    }

    public List<InterSubsystemPubSubPublisherSubscriptionRegistration> getAllPublisherServiceSubscriptions(){
        LOG.debug(".getAllPublisherServiceSubscriptions(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionList = new ArrayList<>();
        if(publisherServiceSubscriptionMap.isEmpty()){
            LOG.debug(".getAllPublisherServiceSubscriptions(): Exit, Returning empty list");
            return(subscriptionList);
        }
        Enumeration<String> publisherServiceNameEnumeration = publisherServiceSubscriptionMap.keys();
        while(publisherServiceNameEnumeration.hasMoreElements()){
            String currentPublisherServiceName = publisherServiceNameEnumeration.nextElement();
            InterSubsystemPubSubPublisherSubscriptionRegistration currentRegistration = publisherServiceSubscriptionMap.get(currentPublisherServiceName);
            subscriptionList.add(currentRegistration);
        }
        LOG.debug(".getAllPublisherServiceSubscriptions(): Returning list");
        return(subscriptionList);
    }

    public List<String> getAllPublishers(){
        LOG.debug(".getAllPublishers(): Entry");
        List<String> participantList = new ArrayList<>();
        synchronized(publisherSubscriptionMapLock) {
            Enumeration<String> publisherServiceNames = this.publisherServiceFulfillmentMap.keys();
            while (publisherServiceNames.hasMoreElements()) {
                String currentPublisherServiceName = publisherServiceNames.nextElement();
                List<String> currentPublisherInstanceNames = this.publisherServiceFulfillmentMap.get(currentPublisherServiceName);
                if (currentPublisherInstanceNames != null) {
                    participantList.addAll(currentPublisherInstanceNames);
                }
            }
        }
        LOG.debug(".getAllPublishers(): Exit, participantList->{}", participantList);
        return(participantList);
    }
}
