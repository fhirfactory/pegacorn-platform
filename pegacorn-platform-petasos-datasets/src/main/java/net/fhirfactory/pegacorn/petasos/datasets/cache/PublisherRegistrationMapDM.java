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
public class PublisherRegistrationMapDM {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherRegistrationMapDM.class);

    // ConcurrentHashMap<publisherServiceInstanceName, publisherRegistration>
    private ConcurrentHashMap<String, InterSubsystemPubSubPublisherRegistration> publisherMap;
    private Object publisherMapLock;

    // ConcurrentHashMap<publisherServiceName, List<publisherServiceInstanceName>>
    private ConcurrentHashMap<String, List<String>> publisherServiceFulfillmentMap;
    private Object publisherServiceFulfillmentMapLock;

    // ConcurrentHashMap<publisherServiceName, subscriptionRegistration>
    private ConcurrentHashMap<String, InterSubsystemPubSubPublisherSubscriptionRegistration> publisherSubscriptionMap;
    private Object publisherSubscriptionMapLock;

    public PublisherRegistrationMapDM(){
        this.publisherMap = new ConcurrentHashMap<>();
        this.publisherMapLock = new Object();
        this.publisherSubscriptionMap = new ConcurrentHashMap<>();
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
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.debug("registerPublisherInstance(): Exit, publisher is null, registration->{}", registration);
            return(registration);
        }
        if(publisher.getIdentifier() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (No Identifier)");
            LOG.debug("registerPublisherInstance(): Exit, publisher identifier is null, registration->{}", registration);
            return(registration);
        }
        if(publisher.getIdentifier().getServiceName() == null || publisher.getIdentifier().getServiceInstanceName() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher SubsystemName or SubsystemInstanceName (== null)");
            LOG.debug("registerPublisherInstance(): Exit, publisher name or instance name is null, registration->{}", registration);
            return(registration);
        }
        LOG.trace(".registerPublisherInstance(): Now, check to see if publisher (instance) is already cached and, if so, do nothing!");
        if(publisherMap.containsKey(publisher.getIdentifier().getServiceInstanceName())){
            registration = publisherMap.get(publisher.getIdentifier().getServiceInstanceName());
            LOG.debug("registerPublisherInstance(): Exit, publisher already registered, registration->{}", registration);
        } else {
            LOG.trace(".registerPublisherInstance(): Publisher is not in Map, so add it!");
            registration.setPublisher(publisher);
            registration.setRegistrationCommentary("Publisher Registered");
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setLastActivityDate(Date.from(Instant.now()));
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED);
            synchronized (this.publisherMapLock) {
                publisherMap.put(publisher.getIdentifier().getServiceInstanceName(), registration);
            }
            LOG.debug(".registerPublisherInstance(): Exit, registration->{}", registration);
            return (registration);
        }
        addPublisherServiceProviderInstance(publisher);
        return(registration);
    }

    public InterSubsystemPubSubPublisherRegistration unregisterPublisherInstance(String publisherInstanceName){
        LOG.debug(".unregisterPublisherInstance(): Entry, publisherInstanceName->{}", publisherInstanceName);
        InterSubsystemPubSubPublisherRegistration registration = null;
        if(StringUtils.isEmpty(publisherInstanceName)){
            registration = new InterSubsystemPubSubPublisherRegistration();
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher is null, registration->{}", registration);
        }
        if(publisherMap.containsKey(publisherInstanceName)){
            synchronized (this.publisherMapLock) {
                registration = publisherMap.remove(publisherInstanceName);
            }
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED);
            registration.setLastActivityDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Publisher removed");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher removed, registration->{}", registration);
            return(registration);
        } else {
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setLastActivityDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Publisher not registered");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher not registered, registration->{}", registration);
            return (registration);
        }
    }

    /**
     *
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration unregisterPublisherInstance(InterSubsystemPubSubParticipant publisher){
        LOG.debug(".unregisterPublisherInstance(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = new InterSubsystemPubSubPublisherRegistration();
        LOG.trace(".unregisterPublisherInstance(): First, we check the content of the passed-in parameter");
        if(publisher == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher is null, registration->{}", registration);
        }
        if(publisher.getIdentifier() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (No Identifier)");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher identifier is null, registration->{}", registration);
        }
        if(publisher.getIdentifier().getServiceName() == null || publisher.getIdentifier().getServiceInstanceName() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher SubsystemName or SubsystemInstanceName (== null)");
            LOG.debug("unregisterPublisherInstance(): Exit, publisher name or instance name is null, registration->{}", registration);
        }
        LOG.trace(".unregisterPublisherInstance(): Now, check to see if publisher (instance) is in the cache and, if so, remove it!");
        registration = unregisterPublisherInstance(publisher.getIdentifier().getServiceInstanceName());
        LOG.debug("unregisterPublisherInstance(): Exit, registration->{}", registration);
        return (registration);
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
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher is null, registration->{}", registration);
            return(registration);
        }
        if(publisher.getIdentifier() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (No Identifier)");
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher identifier is null, registration->{}", registration);
            return(registration);
        }
        if(publisher.getIdentifier().getServiceName() == null || publisher.getIdentifier().getServiceInstanceName() == null){
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher SubsystemName or SubsystemInstanceName (== null)");
            LOG.debug("getPublisherInstanceRegistration(): Exit, publisher name or instance name is null, registration->{}", registration);
            return(registration);
        }
        LOG.trace(".getPublisherInstanceRegistration(): Now, check to see if publisher (instance) is in the cache and, if so, return detail!");
        unregisterPublisherInstance(publisher);
        if(publisherMap.containsKey(publisher.getIdentifier().getServiceInstanceName())){
            registration = publisherMap.get(publisher.getIdentifier().getServiceInstanceName());
            LOG.trace("getPublisherInstanceRegistration(): Exit, publisher found, registration->{}", registration);
            return(registration);
        } else {
            LOG.trace(".getPublisherInstanceRegistration(): Exit, registration->{}", registration);
            registration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_REGISTERED);
            registration.setLastActivityDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Publisher not registered");
        }
        LOG.debug("getPublisherInstanceRegistration(): Exit, registration->{}", registration);
        return (registration);

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
        if(publisher.getIdentifier() == null){
            LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisher.getIdentifier() is null");
            return;
        }
        String publisherInstanceName = publisher.getIdentifier().getServiceInstanceName();
        String publisherServiceName = publisher.getIdentifier().getServiceName();
        if(publisherServiceName == null || publisherInstanceName == null){
            LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisherServiceName or publisherInstanceName is null");
            return;
        }
        synchronized (publisherServiceFulfillmentMapLock) {
            List<String> publisherInstanceList = publisherServiceFulfillmentMap.get(publisherServiceName);
            if (publisherInstanceList == null) {
                publisherInstanceList = new ArrayList<>();
                publisherInstanceList.add(publisherInstanceName);
                publisherServiceFulfillmentMap.put(publisherServiceName, publisherInstanceList);
            } else {
                if (!publisherInstanceList.contains(publisherInstanceName)) {
                    publisherInstanceList.add(publisherInstanceName);
                }
            }
        }
        LOG.debug(".addPublisherServiceProviderInstance(): Exit, publisher instance added");
    }

    private void removePublisherServiceProviderInstance(InterSubsystemPubSubParticipant publisher){
        LOG.info(".removePublisherServiceProviderInstance(): Entry, publisher->{}", publisher);
        if(publisher == null){
            LOG.info(".removePublisherServiceProviderInstance(): Exit, publisher is null");
            return;
        }
        if(publisher.getIdentifier() == null){
            LOG.info(".removePublisherServiceProviderInstance(): Exit, publisher.getIdentifier() is null");
            return;
        }
        String publisherInstanceName = publisher.getIdentifier().getServiceInstanceName();
        String publisherServiceName = publisher.getIdentifier().getServiceName();
        if(publisherServiceName == null || publisherInstanceName == null){
            LOG.info(".removePublisherServiceProviderInstance(): Exit, publisherName or publisherInstanceName is null");
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
        LOG.info(".removePublisherServiceProviderInstance(): Exit, publisher removed added");
    }

    public List<InterSubsystemPubSubParticipant> getPublisherServiceProviderInstances(String publisherServiceName){
        LOG.debug(".getPublisherServiceProviderInstances(): Entry, publisherServiceName->{}", publisherServiceName);
        if(StringUtils.isBlank(publisherServiceName)){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, publisherServiceName is empty/null");
            return(new ArrayList<>());
        }
        List<String> publisherList = publisherServiceFulfillmentMap.get(publisherServiceName);
        if(publisherList == null){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, no publisher (list) for provided publisherServiceName");
            return(new ArrayList<>());
        }
        if(publisherList.isEmpty()){
            LOG.debug(".getPublisherServiceProviderInstances(): Exit, empty publisher (list) for provided publisherServiceName");
            return(new ArrayList<>());
        }
        List<InterSubsystemPubSubParticipant> publisherDetailList = new ArrayList<>();
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
        for(String currentPublisherInstanceName: publisherList){
            InterSubsystemPubSubPublisherRegistration publisherRegistration = publisherMap.get(currentPublisherInstanceName);
            if(publisherRegistration != null){
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
        if(publisher.getIdentifier() == null){
            LOG.debug(".isPublisherRegistered(): Exit, publisher.getIdentifier() is null, return -false-");
            return(false);
        }
        String publisherInstanceName = publisher.getIdentifier().getServiceInstanceName();
        String publisherServiceName = publisher.getIdentifier().getServiceName();
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

    public InterSubsystemPubSubPublisherSubscriptionRegistration addSubscriptionToPublisher(List<DataParcelManifest> subscriptionList, InterSubsystemPubSubParticipant publisher){
        LOG.debug(".addSubscriptionToPublisher(): Entry, publisher->{}", publisher);
        LOG.trace(".addSubscriptionToPublisher(): First, we check the content of the passed-in parameter, tedious, but good defensive programming");
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = new InterSubsystemPubSubPublisherSubscriptionRegistration();
        if(publisher == null){
            registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_FAILED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.debug(".addSubscriptionToPublisher(): Exit, publisher is null, registration->{}", registration);
        }
        if(publisher.getIdentifier().getServiceName() == null ){
            registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_FAILED);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher publisherServiceName (== null)");
            LOG.debug(".addSubscriptionToPublisher(): Exit, publisher Service Name, registration->{}", registration);
        }
        LOG.trace(".addSubscriptionToPublisher(): publisher parameter is good!");
        String publisherServiceName = publisher.getIdentifier().getServiceName();
        synchronized (this.publisherSubscriptionMapLock) {
            registration = publisherSubscriptionMap.get(publisherServiceName);
            if (registration != null) {
                registration.addSubscriptionList(subscriptionList);
            } else {
                registration = new InterSubsystemPubSubPublisherSubscriptionRegistration();
                registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                registration.setSubscriptionList(subscriptionList);
                registration.setPublisherServiceName(publisherServiceName);
                publisherSubscriptionMap.put(publisherServiceName, registration);
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
            registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_NOT_PRESENT);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Detail (NULL)");
            LOG.trace(".getSubscriptionToPublisher(): Exit, publisher is null, registration->{}", registration);
        }
        if(publisher.getIdentifier().getServiceName() == null ){
            registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_NOT_PRESENT);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Invalid Publisher Service Name (== null)");
            LOG.trace(".getSubscriptionToPublisher(): Exit, publisherServiceName is null, registration->{}", registration);
        }
        registration = publisherSubscriptionMap.get(publisher.getIdentifier().getServiceName());
        if(registration != null){
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration found, value->{}", registration);
            return(registration);
        } else {
            registration = new InterSubsystemPubSubPublisherSubscriptionRegistration();
            registration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_NOT_PRESENT);
            registration.setRegistrationDate(Date.from(Instant.now()));
            registration.setRegistrationCommentary("Registration not found!");
            LOG.debug(".getSubscriptionToPublisher(): Exit, registration not found, registration->{}", registration);
            return(registration);
        }
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration checkAProviderIsAvailable(String publisherServiceName){
        LOG.info(".checkAProviderIsAvailable(): Entry, publisherServiceName->{}", publisherServiceName);
        List<InterSubsystemPubSubParticipant> publisherServiceProviderInstances = getPublisherServiceProviderInstances(publisherServiceName);
        if(publisherServiceProviderInstances.isEmpty()){
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = publisherSubscriptionMap.get(publisherServiceName);
            if(subscriptionRegistration != null){
                subscriptionRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS);
                LOG.info(".checkAProviderIsAvailable(): Exit, provider available, subscriptionRegistration->{}", subscriptionRegistration);
                return(subscriptionRegistration);
            }
        }
        LOG.info(".checkAProviderIsAvailable(): Exit, no provider available");
        return(null);
    }

    public List<InterSubsystemPubSubPublisherSubscriptionRegistration> getAllPublisherServiceSubscriptions(){
        LOG.debug(".getAllPublisherServiceSubscriptions(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionList = new ArrayList<>();
        if(publisherSubscriptionMap.isEmpty()){
            LOG.debug(".getAllPublisherServiceSubscriptions(): Exit, Returning empty list");
            return(subscriptionList);
        }
        Enumeration<String> publisherServiceNameEnumeration = publisherSubscriptionMap.keys();
        while(publisherServiceNameEnumeration.hasMoreElements()){
            String currentPublisherServiceName = publisherServiceNameEnumeration.nextElement();
            InterSubsystemPubSubPublisherSubscriptionRegistration currentRegistration = publisherSubscriptionMap.get(currentPublisherServiceName);
            subscriptionList.add(currentRegistration);
        }
        LOG.debug(".getAllPublisherServiceSubscriptions(): Returning list");
        return(subscriptionList);
    }

    public List<String> getAllPublishers(){
        LOG.info(".getAllPublishers(): Entry");
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
        LOG.info(".getAllPublishers(): Exit, participantList->{}", participantList);
        return(participantList);
    }
}
