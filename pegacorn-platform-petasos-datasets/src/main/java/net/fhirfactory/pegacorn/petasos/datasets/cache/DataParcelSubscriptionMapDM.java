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

import ca.uhn.fhir.rest.annotation.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.crypto.Data;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscriber;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;

@ApplicationScoped
public class DataParcelSubscriptionMapDM {
	private static final Logger LOG = LoggerFactory.getLogger(DataParcelSubscriptionMapDM.class);
	
	private ConcurrentHashMap<DataParcelTypeDescriptor, List<PubSubSubscription>> distributionList;
	private Object distributionListUpdateLock;
	
    public DataParcelSubscriptionMapDM(){
        this.distributionList = new ConcurrentHashMap<DataParcelTypeDescriptor, List<PubSubSubscription>>();
        this.distributionListUpdateLock = new Object();
    }

    /**
     * This function retrieves the list (FDNTokenSet) of WUPs that are interested in 
     * receiving the identified uowPayloadTopicID (FDNToken).
     * 
     * @param parcelDescriptor The FDNToken representing the UoW (Ingres) Payload Topic that we want to know which WUPs are interested in
     * @return The set of WUPs wanting to receive this payload type.
     */

    public List<PubSubSubscription> getSubsciberList(DataParcelTypeDescriptor parcelDescriptor){
		LOG.debug(".getSubsciberList(): Entry, parcelDescriptor->{}", parcelDescriptor);
		List<PubSubSubscription> subscriptionList = this.distributionList.get(parcelDescriptor);
		if(subscriptionList == null ) {
			LOG.debug(".getSubsciberList(): Couldn't find any associated PubSubSubscriber elements (i.e. couldn't find any interested WUPs), returning an empty set");
			return (new ArrayList<>());
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug(".getSubsciberList(): Exit, returning associated FDNSet of the WUPs interested:");
			int count = 0;
			for(PubSubSubscription currentSubscription : subscriptionList){
				PubSubSubscriber currentSubscriber = currentSubscription.getSubscriber();
				LOG.debug(".getSubsciberList(): Subscriber[{}]->{}", count, currentSubscriber);
				count++;
			}
		}
		LOG.debug(".getSubsciberList(): Exit, subscriptionList->{}", subscriptionList);
		return (subscriptionList);

	}
    
    /**
     * This function establishes a link between a Payload Type and a WUP that is interested in
     * processing/using it.
     * 
     * @param parcelManifest The contentTopicID (FDNToken) of the payload we have received from a WUP
     * @param subscriber The NodeElement of the WUP that is interested in the payload type.
     */
    public void addSubscriber(DataParcelManifest parcelManifest, PubSubSubscriber subscriber) {
    	LOG.debug(".addSubscriber(): Entry, parcelManifest->{}, subscriber->{}", parcelManifest, subscriber);
    	if((parcelManifest==null) || (subscriber==null)) {
    		throw(new IllegalArgumentException(".addSubscriber(): parcelManifest or subscriberInstanceID is null"));
    	}
		DataParcelTypeDescriptor contentDescriptor = null;
    	if(parcelManifest.hasContentDescriptor()) {
			contentDescriptor = parcelManifest.getContentDescriptor();
		} else {
			if (parcelManifest.hasContainerDescriptor()) {
				contentDescriptor = parcelManifest.getContainerDescriptor();
			}
		}
    	if(contentDescriptor == null){
			throw(new IllegalArgumentException(".addSubscriber(): parcelManifest does not contain suitable contentDescriptor or containerDescriptor"));
		}
		List<PubSubSubscription> subscriptionList = this.distributionList.get(contentDescriptor);
    	synchronized (this.distributionListUpdateLock) {
			if (subscriptionList != null) {
				LOG.trace(".addSubscriber(): Adding subscriber to existing map for parcelManifest --> {}", parcelManifest);
				PubSubSubscription existingSubscription = null;
				for(PubSubSubscription currentSubscription: subscriptionList){
					if(currentSubscription.getSubscriber().equals(subscriber)){
						if(currentSubscription.getParcelManifest().getContainerDescriptor().equals(parcelManifest.getContainerDescriptor())){
							existingSubscription = currentSubscription;
							break;
						}
					}
				}
				if(existingSubscription != null){
					subscriptionList.remove(existingSubscription);
				}
				PubSubSubscription newSubscription = new PubSubSubscription(parcelManifest, subscriber);
				subscriptionList.add(newSubscription);
			} else {
				LOG.trace(".addSubscriber(): Topic Subscription Map: Created new Distribution List and Added Subscriber");
				PubSubSubscription newSubscription = new PubSubSubscription(parcelManifest, subscriber);
				subscriptionList = new ArrayList<PubSubSubscription>();
				subscriptionList.add(newSubscription);
				this.distributionList.put(contentDescriptor, subscriptionList);
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(".addSubscriber(): Exit, here is the Subscription list for the Topic:");
			int count = 0;
			for(PubSubSubscription currentSubscription : subscriptionList){
				PubSubSubscriber currentSubscriber = currentSubscription.getSubscriber();
				LOG.debug(".addSubscriber(): Subscriber[{}]->{}", count, currentSubscriber);
				count++;
			}
		}
    }

    public void addSubscriber(DataParcelTypeDescriptor contentDescriptor, TopologyNodeFDNToken localSubscriberWUP){
		LOG.debug(".addSubscriber(): Entry, contentDescriptor->{}, localSubscriberWUP->{}", contentDescriptor, localSubscriberWUP);
		if((contentDescriptor==null) || (localSubscriberWUP==null)) {
			throw(new IllegalArgumentException(".addSubscriber(): payloadTopic or localSubscriberWUP is null"));
		}
		DataParcelManifest descriptor = new DataParcelManifest(contentDescriptor);
		PubSubSubscriber subscriber = new PubSubSubscriber(localSubscriberWUP);
		addSubscriber(descriptor, subscriber);
	}
    
    /**
     * Remove a Subscriber from the Topic Subscription list
     * 
     * @param parcelManifest The DataParcelManifest of the Topic we want to unsubscribe from.
     * @param subscriberInstanceID  The subscriber we are removing from the subscription list.
     */
    public void removeSubscriber(DataParcelManifest parcelManifest, PubSubSubscriber subscriberInstanceID) {
    	LOG.debug(".removeSubscriber(): Entry, parcelManifest --> {}, subscriberInstanceID --> {}", parcelManifest, subscriberInstanceID);
    	if((parcelManifest==null) || (subscriberInstanceID==null)) {
    		throw(new IllegalArgumentException(".removeSubscriber(): topic or subscriberInstanceID is null"));
    	}
		boolean found = false;
		DataParcelTypeDescriptor currentToken = null;
		DataParcelTypeDescriptor contentDescriptor = null;
		if(parcelManifest.hasContentDescriptor()) {
			contentDescriptor = parcelManifest.getContentDescriptor();
		} else {
			if (parcelManifest.hasContainerDescriptor()) {
				contentDescriptor = parcelManifest.getContainerDescriptor();
			}
		}
		Enumeration<DataParcelTypeDescriptor> topicEnumerator = distributionList.keys();
		while(topicEnumerator.hasMoreElements()){
			currentToken = topicEnumerator.nextElement();
			if(currentToken.equals(contentDescriptor)){
				LOG.trace(".removeSubscriber(): Found Topic in Subscription Cache");
				found = true;
				break;
			}
		}
		if(found) {
    		LOG.trace(".removeSubscriber(): Removing Subscriber from contentDescriptor --> {}", contentDescriptor);
    		synchronized (this.distributionListUpdateLock) {
				List<PubSubSubscription> subscriptionList = this.distributionList.get(currentToken);
				for(PubSubSubscription currentSubscription: subscriptionList){
					boolean sameSubscriber = currentSubscription.getSubscriber().equals(subscriberInstanceID);
					boolean sameParcelManifest = currentSubscription.getParcelManifest().equals(parcelManifest);
					if (sameParcelManifest && sameSubscriber) {
						LOG.trace(".removeSubscriber(): Found Subscriber in Subscription List, removing");
						subscriptionList.remove(currentSubscription);
						LOG.debug(".removeSubscriber(): Exit, removed the subscriberInstanceID from the topic");
						LOG.trace("Topic Subscription Map: (Remove Subscriber) Topic [{}] <-- Subscriber [{}]", currentToken, subscriberInstanceID);
						break;
					}
				}
			}
    	} else {
    		LOG.debug(".removeSubscriber(): Exit, Could not find Subscriber in Subscriber Cache for Topic");
    		return;
    	}
		LOG.debug(".removeSubscriber(): Exit, Could not find Topic in Subscriber Cache");
    }

    public void printAllSubscriptionSets(){
    	if(!LOG.isDebugEnabled()){
    		return;
		}
    	Enumeration<DataParcelTypeDescriptor> topicEnumerator = distributionList.keys();
    	LOG.debug(".printAllSubscriptionSets(): Printing ALL Subscription Lists");
    	while(topicEnumerator.hasMoreElements()){
			DataParcelTypeDescriptor currentToken = topicEnumerator.nextElement();
    		LOG.debug(".printAllSubscriptionSets(): Topic (TopicToken) --> {}", currentToken);
			List<PubSubSubscription> subscriptionList = getSubsciberList(currentToken);
			if(subscriptionList != null){
				for(PubSubSubscription currentSubscription: subscriptionList){
					PubSubSubscriber currentSubscriber = currentSubscription.getSubscriber();
					LOG.debug(".printAllSubscriptionSets(): Subscriber --> {}", currentSubscriber);
				}
			}

		}
	}

	//
	// More sophisticated SubscriberList derivation
	//

	public List<PubSubSubscriber> deriveSubscriberList(DataParcelManifest parcelManifest){
		LOG.info(".deriveSubscriberList(): Entry, parcelManifest->{}", parcelManifest);
		DataParcelTypeDescriptor parcelDescriptor = parcelManifest.getContentDescriptor();
		List<PubSubSubscription> subscriberList = this.distributionList.get(parcelDescriptor);
		if(subscriberList == null ){
			LOG.info(".getSubscriberList(): Couldn't find any associated PubSubSubscriber elements (i.e. couldn't find any interested WUPs), returning an empty set");
			return(new ArrayList<>());
		}
		List<PubSubSubscriber> derivedSubscriberList = new ArrayList<>();
		for(PubSubSubscription currentRegisteredSubscription: subscriberList){
			LOG.info(".getSubscriberList(): Checking for equivalence/match is subscription");
			DataParcelManifest subscriberRequestedManifest = currentRegisteredSubscription.getParcelManifest();
			LOG.info(".getSubscriberList(): Checking for equivalence/match is subscription, currentSubscriberRequestedManifest->{}, availableManifest->{}", currentRegisteredSubscription, parcelManifest);
			boolean containerIsEqual = containerIsEqual(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: containerIsEqual->{}",containerIsEqual);
			boolean contentIsEqual = contentIsEqual(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: contentIsEqual->{}",contentIsEqual);
			boolean containerOnlyIsEqual = containerOnlyEqual(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: containerOnlyIsEqual->{}",containerOnlyIsEqual);
			boolean matchedNormalisation = normalisationMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedNormalisation->{}",matchedNormalisation);
			boolean matchedValidation = validationMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedValidation->{}",matchedValidation);
			boolean matchedManifestType = manifestTypeMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedManifestType->{}",matchedManifestType);
			boolean matchedSource = sourceSystemMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedSource->{}",matchedSource);
			boolean matchedTarget = targetSystemMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedTarget->{}",matchedTarget);
			boolean matchedPEStatus = enforcementPointApprovalStatusMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedPEStatus->{}",matchedPEStatus);
			boolean matchedDistributionStatus = isDistributableMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedDistributionStatus->{}",matchedDistributionStatus);
			boolean matchedDirection = parcelFlowDirectionMatches(parcelManifest, subscriberRequestedManifest);
			LOG.info(".getSubscriberList(): Checking for equivalence/match: matchedDirection->{}",matchedDirection);
			boolean goodEnoughMatch = containerIsEqual
					&& contentIsEqual
					&& matchedNormalisation
					&& matchedValidation
					&& matchedManifestType
					&& matchedSource
					&& matchedTarget
					&& matchedPEStatus
					&& matchedDirection
					&& matchedDistributionStatus;
			LOG.info(".getSubscriberList(): Checking for equivalence/match: goodEnoughMatch->{}",goodEnoughMatch);
			boolean containerBasedOKMatch = containerOnlyIsEqual
					&& matchedNormalisation
					&& matchedValidation
					&& matchedManifestType
					&& matchedSource
					&& matchedTarget
					&& matchedPEStatus
					&& matchedDirection
					&& matchedDistributionStatus;
			LOG.info(".getSubscriberList(): Checking for equivalence/match: containerBasedOKMatch->{}",containerBasedOKMatch);
			if(goodEnoughMatch || containerBasedOKMatch){
				LOG.info(".getSubscriberList(): Adding entry!");
				derivedSubscriberList.add(currentRegisteredSubscription.getSubscriber());
			}

		}
		return(derivedSubscriberList);
	}

	private boolean containerIsEqual(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
    	if(testManifest == null || subscribedManifest == null){
    		return(false);
		}
		if(!(testManifest.hasContainerDescriptor() && subscribedManifest.hasContainerDescriptor()))
		{
			return(true);
		}
    	if(testManifest.hasContainerDescriptor() && subscribedManifest.hasContentDescriptor()) {
			if (testManifest.getContainerDescriptor().equals(subscribedManifest.getContainerDescriptor())) {
				return (true);
			}
		}
    	return(false);
	}

	private boolean contentIsEqual(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
		LOG.debug(".contentIsEqual(): Entry");
    	if(testManifest == null || subscribedManifest == null){
    		return(false);
		}
		LOG.trace(".contentIsEqual(): testManifest & subscribedManifest are bot NOT null");
		LOG.trace(".contentIsEqual(): checking to see if testManifest has a contentDescriptor");
    	boolean testManifestHasContentDescriptor = testManifest.hasContentDescriptor();
		LOG.trace(".contentIsEqual(): checking to see if subscribedManifest has a contentDescriptor");
    	boolean subscribedManifestHasContentDescriptor = subscribedManifest.hasContentDescriptor();
		if(!testManifestHasContentDescriptor ) {
			LOG.debug(".contentIsEqual(): Exit, testManifest has not contentDescriptor, returning -false-");
			return(false);
		}
		if(!subscribedManifestHasContentDescriptor){
			LOG.debug(".contentIsEqual(): Exit, subscribedManifest has not contentDescriptor, returning -false-");
			return(false);
		}
		if (testManifest.getContentDescriptor().equals(subscribedManifest.getContentDescriptor())) {
			LOG.debug(".contentIsEqual(): Exit, descriptors are equal, returning -true-");
			return (true);
		} else {
			LOG.debug(".contentIsEqual(): Exit, descriptors are not equal, returning -false-");
			return (false);
		}
	}

	private boolean containerOnlyEqual(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
		if(testManifest == null || subscribedManifest == null){
			return(false);
		}
		if(testManifest.hasContentDescriptor() && subscribedManifest.hasContentDescriptor()){
			return(contentIsEqual(testManifest, subscribedManifest));
		}
		if(subscribedManifest.hasContentDescriptor()){
			return(false);
		}
		if(testManifest.hasContainerDescriptor() && subscribedManifest.hasContainerDescriptor()){
			return(containerIsEqual(testManifest, subscribedManifest));
		}
		return(false);
	}

	private boolean normalisationMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
		if(testManifest == null || subscribedManifest == null){
			return(false);
		}
		if(subscribedManifest.getNormalisationStatus().equals(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY)){
			return(true);
		}
		boolean normalisationStatusIsEqual = subscribedManifest.getNormalisationStatus().equals(testManifest.getNormalisationStatus());
		return(normalisationStatusIsEqual);
	}

	private boolean validationMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		if(subscribedManifest.getValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY)){
			return(true);
		}
		boolean validationStatusIsEqual = subscribedManifest.getValidationStatus().equals(testManifest.getValidationStatus());
		return(validationStatusIsEqual);
	}

	private boolean manifestTypeMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		boolean manifestTypeMatches = subscribedManifest.getDataParcelType().equals(testManifest.getDataParcelType());
		return(manifestTypeMatches);
	}

	private boolean sourceSystemMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null && subscribedManifest == null) {
			return (false);
		}
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		if(!testManifest.hasSourceSystem() && !subscribedManifest.hasSourceSystem()){
			return(true);
		}
		if(!testManifest.hasContainerDescriptor() || !subscribedManifest.hasSourceSystem()){
			return(false);
		}
		if (testManifest.hasSourceSystem() && subscribedManifest.hasSourceSystem()) {
			boolean sourceIsSame = testManifest.getSourceSystem().contentEquals(subscribedManifest.getSourceSystem());
			return (sourceIsSame);
		}
		return(false);
	}

	private boolean targetSystemMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null && subscribedManifest == null) {
			return (false);
		}
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		if(!testManifest.hasSourceSystem() && !subscribedManifest.hasSourceSystem()){
			return(true);
		}
		if(!testManifest.hasSourceSystem() || !subscribedManifest.hasSourceSystem()){
			return(false);
		}
		if (testManifest.hasIntendedTargetSystem() && subscribedManifest.hasIntendedTargetSystem()) {
			boolean targetIsSame = testManifest.getIntendedTargetSystem().contentEquals(subscribedManifest.getIntendedTargetSystem());
			return (targetIsSame);
		}
		if(!subscribedManifest.hasIntendedTargetSystem()){
			return(true);
		}
		return(false);
	}

	private boolean enforcementPointApprovalStatusMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		if (subscribedManifest.getEnforcementPointApprovalStatus().equals(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY)) {
			return (true);
		}
		boolean approvalStatusMatch = subscribedManifest.getEnforcementPointApprovalStatus().equals(testManifest.getEnforcementPointApprovalStatus());
		return (approvalStatusMatch);
	}

	private boolean isDistributableMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest) {
		if (testManifest == null || subscribedManifest == null) {
			return (false);
		}
		return (testManifest.isInterSubsystemDistributable() == subscribedManifest.isInterSubsystemDistributable());
	}

	private boolean parcelFlowDirectionMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
    	if(testManifest == null || subscribedManifest == null){
    		return(false);
		}
    	boolean directionMatches = testManifest.getDataParcelFlowDirection() == subscribedManifest.getDataParcelFlowDirection();
    	return(directionMatches);
	}
}
