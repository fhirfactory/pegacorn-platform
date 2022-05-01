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

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.IntraSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipantUtilisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscription;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
				PubSubParticipant currentSubscriber = currentSubscription.getSubscriber();
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
    public void addSubscriber(DataParcelManifest parcelManifest, PubSubParticipant subscriber) {
    	LOG.debug(".addSubscriber(): Entry, parcelManifest->{}, subscriber->{}", parcelManifest, subscriber);
    	if((parcelManifest==null) || (subscriber==null)) {
    		throw(new IllegalArgumentException(".addSubscriber(): parcelManifest or subscriberInstanceID is null"));
    	}
		DataParcelTypeDescriptor contentDescriptor = parcelManifest.getContentDescriptor();
    	DataParcelTypeDescriptor containerDescriptor = parcelManifest.getContainerDescriptor();
    	DataParcelTypeDescriptor descriptorToRegister = null;
    	if(contentDescriptor != null) {
			LOG.trace(".addSubscriber(): contentDescriptor is not null");
			descriptorToRegister = contentDescriptor;
		}
		if((descriptorToRegister == null) && (containerDescriptor != null)){
			LOG.trace(".addSubscriber(): contentDescriptor was null and containerDescriptor is not null");
			descriptorToRegister = containerDescriptor;
		}
    	if(descriptorToRegister == null){
			throw(new IllegalArgumentException(".addSubscriber(): parcelManifest does not contain suitable contentDescriptor or containerDescriptor"));
		}
		List<PubSubSubscription> subscriptionList = this.distributionList.get(descriptorToRegister);
    	synchronized (this.distributionListUpdateLock) {
			if (subscriptionList != null) {
				LOG.trace(".addSubscriber(): Topic Subscription Map: Adding subscriber to existing map for parcelManifest --> {}", parcelManifest);
				PubSubSubscription existingSubscription = null;
				for(PubSubSubscription currentSubscription: subscriptionList){
					if(currentSubscription.getSubscriber().getIntraSubsystemParticipant().equals(subscriber.getIntraSubsystemParticipant())){
						if(isSameRemoteEndpointSubscriber(currentSubscription.getSubscriber(), subscriber)){
							existingSubscription = currentSubscription;
							break;
						}
					}
				}
				if(existingSubscription == null) {
					PubSubSubscription newSubscription = new PubSubSubscription(parcelManifest, subscriber);
					subscriptionList.add(newSubscription);
				}
				if(subscriber.getInterSubsystemParticipant() != null) {
					subscriber.getInterSubsystemParticipant().setUtilisationStatus(PubSubParticipantUtilisationStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
				}
			} else {
				LOG.trace(".addSubscriber(): Topic Subscription Map: Created new Distribution List and Added Subscriber");
				PubSubSubscription newSubscription = new PubSubSubscription(parcelManifest, subscriber);
				LOG.trace(".addSubscriber(): Topic Subscription Map: Created new PubSubSubscription, adding to a newly created List");
				subscriptionList = new ArrayList<PubSubSubscription>();
				subscriptionList.add(newSubscription);
				LOG.trace(".addSubscriber(): Topic Subscription Map: PubSubSubscription List created, adding it to the distribution map");
				this.distributionList.put(descriptorToRegister, subscriptionList);
				LOG.trace(".addSubscriber(): Topic Subscription Map: Added PubSubSubscription List to the distribution map");
				if(subscriber.getInterSubsystemParticipant() != null) {
					subscriber.getInterSubsystemParticipant().setUtilisationStatus(PubSubParticipantUtilisationStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
				}
			}
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace(".addSubscriber(): Exit, here is the Subscription list for the Topic:");
			int count = 0;
			for(PubSubSubscription currentSubscription : subscriptionList){
				PubSubParticipant currentSubscriber = currentSubscription.getSubscriber();
				LOG.trace(".addSubscriber(): Subscriber[{}]->{}", count, currentSubscriber);
				count++;
			}
		}
		printAllSubscriptionSets();
    }

    private boolean isSameRemoteEndpointSubscriber(PubSubParticipant currentRegisteredParticipant, PubSubParticipant testParticipant){
		if(currentRegisteredParticipant.getIntraSubsystemParticipant().equals(testParticipant.getIntraSubsystemParticipant())) {
			boolean currentRegisteredParticipantHasRemoteSubscriber = currentRegisteredParticipant.getInterSubsystemParticipant() != null;
			boolean testParticipantHasRemoteSubscriber = testParticipant.getInterSubsystemParticipant() != null;
			if (currentRegisteredParticipantHasRemoteSubscriber && testParticipantHasRemoteSubscriber) {
				boolean currentHasServiceName = !(StringUtils.isEmpty(currentRegisteredParticipant.getInterSubsystemParticipant().getEndpointServiceName()));
				boolean testHasServiceName = !(StringUtils.isEmpty(testParticipant.getInterSubsystemParticipant().getEndpointServiceName()));
				if (currentHasServiceName && testHasServiceName) {
					String currentParticipantServiceName = currentRegisteredParticipant.getInterSubsystemParticipant().getEndpointServiceName();
					String testParticipantServiceName = testParticipant.getInterSubsystemParticipant().getEndpointServiceName();
					if (currentParticipantServiceName != null && testParticipantServiceName != null) {
						if (currentParticipantServiceName.contentEquals(testParticipantServiceName)) {
							return (true);
						}
					}
				}
			}
			if(!currentRegisteredParticipantHasRemoteSubscriber && !testParticipantHasRemoteSubscriber){
				return(true);
			} else {
				return(false);
			}
		} else {
			return(false);
		}
	}

    public void addSubscriber(DataParcelTypeDescriptor contentDescriptor, TopologyNodeFDNToken localSubscriberWUP){
		LOG.debug(".addSubscriber(): Entry, contentDescriptor->{}, localSubscriberWUP->{}", contentDescriptor, localSubscriberWUP);
		if((contentDescriptor==null) || (localSubscriberWUP==null)) {
			throw(new IllegalArgumentException(".addSubscriber(): payloadTopic or localSubscriberWUP is null"));
		}
		DataParcelManifest descriptor = new DataParcelManifest(contentDescriptor);
		IntraSubsystemPubSubParticipant intraSubsystemParticipant = new IntraSubsystemPubSubParticipant(localSubscriberWUP);
		PubSubParticipant subscriber = new PubSubParticipant(intraSubsystemParticipant);
		addSubscriber(descriptor, subscriber);
	}
    
    /**
     * Remove a Subscriber from the Topic Subscription list
     * 
     * @param parcelManifest The DataParcelManifest of the Topic we want to unsubscribe from.
     * @param subscriberInstanceID  The subscriber we are removing from the subscription list.
     */
    public void removeSubscriber(DataParcelManifest parcelManifest, PubSubParticipant subscriberInstanceID) {
    	LOG.debug(".removeSubscriber(): Entry, parcelManifest --> {}, subscriberInstanceID --> {}", parcelManifest, subscriberInstanceID);
    	if((parcelManifest==null) || (subscriberInstanceID==null)) {
    		throw(new IllegalArgumentException(".removeSubscriber(): topic or subscriberInstanceID is null"));
    	}
		boolean found = false;
		DataParcelTypeDescriptor currentToken = null;
		DataParcelTypeDescriptor contentDescriptor = parcelManifest.getContentDescriptor();
		DataParcelTypeDescriptor containerDescriptor = parcelManifest.getContainerDescriptor();
		DataParcelTypeDescriptor descriptorToTest = null;
		if(contentDescriptor != null) {
			descriptorToTest = contentDescriptor;
		}
		if((descriptorToTest == null) && (containerDescriptor != null)){
			descriptorToTest = containerDescriptor;
		}
		if(descriptorToTest == null){
			throw(new IllegalArgumentException(".addSubscriber(): parcelManifest does not contain suitable contentDescriptor or containerDescriptor"));
		}
		Enumeration<DataParcelTypeDescriptor> topicEnumerator = distributionList.keys();
		while(topicEnumerator.hasMoreElements()){
			currentToken = topicEnumerator.nextElement();
			if(currentToken.equals(descriptorToTest)){
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
    	if(!LOG.isTraceEnabled()){
    		return;
		}
    	Enumeration<DataParcelTypeDescriptor> topicEnumerator = distributionList.keys();
    	LOG.trace(".printAllSubscriptionSets(): Printing ALL Subscription Lists");
    	while(topicEnumerator.hasMoreElements()){
			DataParcelTypeDescriptor currentToken = topicEnumerator.nextElement();
    		LOG.trace(".printAllSubscriptionSets(): Topic (TopicToken) --> {}", currentToken);
			List<PubSubSubscription> subscriptionList = getSubsciberList(currentToken);
			if(subscriptionList != null){
				for(PubSubSubscription currentSubscription: subscriptionList){
					PubSubParticipant currentSubscriber = currentSubscription.getSubscriber();
					String remoteSubscriberShortName = "()";
					if(currentSubscriber.getInterSubsystemParticipant() != null) {
						remoteSubscriberShortName = "("+currentSubscriber.getInterSubsystemParticipant().getEndpointServiceName()+")";
					}
					String localSubscriberName = "("+currentSubscriber.getIntraSubsystemParticipant().getIdentifier()+")";
					String currentSubscriberName = remoteSubscriberShortName+localSubscriberName;
					LOG.trace(".printAllSubscriptionSets(): Subscriber --> {}", currentSubscriberName);
				}
			}

		}
	}

	private List<PubSubSubscription> getPossibleSubscriptionMatches(DataParcelTypeDescriptor testDescriptor){
    	List<DataParcelTypeDescriptor> possibleList = new ArrayList<>();
    	List<PubSubSubscription> possibleSubscriptionList = new ArrayList<>();
    	if(this.distributionList.isEmpty()){
    		return(possibleSubscriptionList);
		}
		Enumeration<DataParcelTypeDescriptor> descriptorEnumeration = this.distributionList.keys();
    	while(descriptorEnumeration.hasMoreElements()){
    		DataParcelTypeDescriptor subscribedDescriptor = descriptorEnumeration.nextElement();
			boolean parcelDefinerIsEqual = StringUtils.equals(testDescriptor.getDataParcelDefiner(),subscribedDescriptor.getDataParcelDefiner());
			boolean parcelDefinedHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDefiner(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelDefinerIsGoodEnoughMatch = parcelDefinerIsEqual || parcelDefinedHasWildcard;
			boolean parcelCategoryIsEqual = StringUtils.equals(testDescriptor.getDataParcelCategory(),subscribedDescriptor.getDataParcelCategory());
			boolean parcelCategoryHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelCategory(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelCategoryIsGoodEnoughMatch = parcelCategoryIsEqual || parcelCategoryHasWildcard;
			boolean parcelSubcategoryIsEqual = StringUtils.equals(testDescriptor.getDataParcelSubCategory(),subscribedDescriptor.getDataParcelSubCategory());
			boolean parcelSubcategoryHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelSubCategory(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelSubcategoryIsGoodEnoughMatch = parcelSubcategoryIsEqual || parcelSubcategoryHasWildcard;
			boolean parcelResourceIsEqual = StringUtils.equals(testDescriptor.getDataParcelResource(),subscribedDescriptor.getDataParcelResource());
			boolean parcelResourceHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelResource(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelResourceIsGoodEnoughMatch = parcelResourceIsEqual || parcelResourceHasWildcard;
			boolean parcelSegmentIsEqual = StringUtils.equals(testDescriptor.getDataParcelSegment(),subscribedDescriptor.getDataParcelSegment());
			boolean parcelSegmentHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelSegment(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelSegmentIsGoodEnoughMatch = parcelSegmentIsEqual || parcelSegmentHasWildcard;
			boolean parcelAttributeIsEqual = StringUtils.equals(testDescriptor.getDataParcelAttribute(),subscribedDescriptor.getDataParcelAttribute());
			boolean parcelAttributeHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelAttribute(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelAttributeIsGoodEnoughMatch = parcelAttributeIsEqual || parcelAttributeHasWildcard;
			boolean parcelDiscriminatorTypeIsEqual = StringUtils.equals(testDescriptor.getDataParcelDiscriminatorType(),subscribedDescriptor.getDataParcelDiscriminatorType());
			boolean parcelDiscriminatorTypeHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDiscriminatorType(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelDiscriminatorTypeIsGoodEnoughMatch = parcelDiscriminatorTypeIsEqual || parcelDiscriminatorTypeHasWildcard;
			boolean parcelDiscriminatorValueIsEqual = StringUtils.equals(testDescriptor.getDataParcelDiscriminatorValue(),subscribedDescriptor.getDataParcelDiscriminatorValue());
			boolean parcelDiscriminatorValueHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDiscriminatorValue(),DataParcelManifest.WILDCARD_CHARACTER);
			boolean parcelDiscriminatorValueIsGoodEnoughMatch = parcelDiscriminatorValueIsEqual || parcelDiscriminatorValueHasWildcard;
			if(		parcelDefinerIsGoodEnoughMatch
					&& parcelCategoryIsGoodEnoughMatch
					&& parcelSubcategoryIsGoodEnoughMatch
					&& parcelResourceIsGoodEnoughMatch
					&& parcelSegmentIsGoodEnoughMatch
					&& parcelAttributeIsGoodEnoughMatch
					&& parcelDiscriminatorTypeIsGoodEnoughMatch
					&& parcelDiscriminatorValueIsGoodEnoughMatch){
				possibleList.add(subscribedDescriptor);
			}
		}
    	if(!possibleList.isEmpty()){ ;
			for(DataParcelTypeDescriptor possibleDescriptor: possibleList){
				List<PubSubSubscription> currentSubscriptionSet = this.distributionList.get(possibleDescriptor);
				if(currentSubscriptionSet != null){
					possibleSubscriptionList.addAll(currentSubscriptionSet);
				}
			}
		}
    	return(possibleSubscriptionList);
	}

	//
	// More sophisticated SubscriberList derivation
	//

	public List<PubSubParticipant> deriveSubscriberList(DataParcelManifest parcelManifest){
		LOG.debug(".deriveSubscriberList(): Entry, parcelManifest->{}", parcelManifest);
		printAllSubscriptionSets();
		if(LOG.isInfoEnabled()){
			if(parcelManifest.hasContentDescriptor()){
				String messageToken = parcelManifest.getContentDescriptor().toFDN().getToken().toTag();
				LOG.trace(".deriveSubscriberList(): parcel.ContentDescriptor->{}", messageToken);
			}
		}
		DataParcelTypeDescriptor parcelContentDescriptor = parcelManifest.getContentDescriptor();
		DataParcelTypeDescriptor parcelContainerDescriptor = parcelManifest.getContainerDescriptor();
		List<PubSubSubscription> contentBasedSubscriberList = new ArrayList<>();
		List<PubSubSubscription> containerBasedSubscriptionList = new ArrayList<>();
		if(parcelContentDescriptor != null ){
			LOG.trace(".deriveSubscriberList(): parcelContentDescriptor is not null");
			contentBasedSubscriberList.addAll(getPossibleSubscriptionMatches(parcelContentDescriptor));
			LOG.trace(".deriveSubscriberList(): contentBasedSubscriberList->{}", contentBasedSubscriberList);
		}
		if(parcelContainerDescriptor != null){
			LOG.trace(".deriveSubscriberList(): parcelContainerDescriptor is not null");
			containerBasedSubscriptionList.addAll(getPossibleSubscriptionMatches(parcelContainerDescriptor));
			LOG.trace(".deriveSubscriberList(): containerBasedSubscriptionList->{}", containerBasedSubscriptionList);
		}

		boolean contentListIsEmpty = contentBasedSubscriberList.isEmpty();
		boolean containerListEmpty = containerBasedSubscriptionList.isEmpty();

		if(contentListIsEmpty && containerListEmpty) {
			LOG.trace(".deriveSubscriberList(): Couldn't find any associated PubSubSubscriber elements [empty lists or nulls] (i.e. couldn't find any interested WUPs), returning an empty set");
			return (new ArrayList<>());
		}
		List<PubSubSubscription> retrievedSubscriberList = new ArrayList<>();
		if(!contentListIsEmpty){
			LOG.trace(".deriveSubscriberList(): contentBasedSubscriberList contains something");
			retrievedSubscriberList.addAll(contentBasedSubscriberList);
		}
		if(!containerListEmpty){
			LOG.trace(".deriveSubscriberList(): containerBasedSubscriptionList contains something");
			retrievedSubscriberList.addAll(containerBasedSubscriptionList);
		}
		if(retrievedSubscriberList.isEmpty()){
			LOG.trace(".deriveSubscriberList(): Couldn't find any associated PubSubSubscriber elements [empty aggregate list] (i.e. couldn't find any interested WUPs), returning an empty set");
			return (new ArrayList<>());
		}
		List<PubSubParticipant> derivedSubscriberList = new ArrayList<>();
		for(PubSubSubscription currentRegisteredSubscription: retrievedSubscriberList){
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match in subscription");
			DataParcelManifest subscriberRequestedManifest = currentRegisteredSubscription.getParcelManifest();
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match in subscription, currentSubscriberRequestedManifest->{}, availableManifest->{}", currentRegisteredSubscription, parcelManifest);
//			LOG.trace(".getSubscriberList(): Checking subscriber->{}", currentRegisteredSubscription.getSubscriber());
//			LOG.trace(".getSubscriberList(): Subscriber Manifest (container)->{}", subscriberRequestedManifest.getContainerDescriptor());
//			LOG.trace(".getSubscriberList(): Publisher  Manifest (container)->{}", parcelManifest.getContainerDescriptor());
//			LOG.trace(".getSubscriberList(): Subscriber Manifest (content)->{}", subscriberRequestedManifest.getContentDescriptor());
//			LOG.trace(".getSubscriberList(): Publisher  Manifest (content)->{}", parcelManifest.getContentDescriptor());
			boolean containerIsEqual = containerIsEqual(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: containerIsEqual->{}",containerIsEqual);
			boolean contentIsEqual = contentIsEqual(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: contentIsEqual->{}",contentIsEqual);
			boolean containerOnlyIsEqual = containerOnlyEqual(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: containerOnlyIsEqual->{}",containerOnlyIsEqual);
			boolean matchedNormalisation = normalisationMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedNormalisation->{}",matchedNormalisation);
			boolean matchedValidation = validationMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedValidation->{}",matchedValidation);
			boolean matchedManifestType = manifestTypeMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedManifestType->{}",matchedManifestType);
			boolean matchedSource = sourceSystemMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedSource->{}",matchedSource);
			boolean matchedTarget = targetSystemMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedTarget->{}",matchedTarget);
			boolean matchedPEPStatus = enforcementPointApprovalStatusMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedPEPStatus->{}",matchedPEPStatus);
			boolean matchedDistributionStatus = isDistributableMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedDistributionStatus->{}",matchedDistributionStatus);
			boolean matchedDirection = parcelFlowDirectionMatches(parcelManifest, subscriberRequestedManifest);
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: matchedDirection->{}",matchedDirection);
			boolean goodEnoughMatch = containerIsEqual
					&& contentIsEqual
					&& matchedNormalisation
					&& matchedValidation
					&& matchedManifestType
					&& matchedSource
					&& matchedTarget
					&& matchedPEPStatus
					&& matchedDirection
					&& matchedDistributionStatus;
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: goodEnoughMatch->{}",goodEnoughMatch);
			boolean containerBasedOKMatch = containerOnlyIsEqual
					&& matchedNormalisation
					&& matchedValidation
					&& matchedManifestType
					&& matchedSource
					&& matchedTarget
					&& matchedPEPStatus
					&& matchedDirection
					&& matchedDistributionStatus;
			LOG.trace(".deriveSubscriberList(): Checking for equivalence/match: containerBasedOKMatch->{}",containerBasedOKMatch);
			if(goodEnoughMatch || containerBasedOKMatch){
				if(LOG.isWarnEnabled()) {
					String subscriber = currentRegisteredSubscription.getSubscriber().getIntraSubsystemParticipant().getIdentifier().toVersionBasedFDNToken().toTag();
					LOG.trace(".deriveSubscriberList(): subscriber->{}", subscriber);
				}
				derivedSubscriberList.add(currentRegisteredSubscription.getSubscriber());
			}
		}
		LOG.debug(".getSubscriberList(): Exit!");
		return(derivedSubscriberList);
	}

	private boolean containerIsEqual(DataParcelManifest publisherManifest, DataParcelManifest subscribedManifest){
		LOG.debug(".containerIsEqual(): Entry");
    	if(publisherManifest == null || subscribedManifest == null){
			LOG.debug(".containerIsEqual(): publisherManifest or subscribedManifest is null, return -false-");
    		return(false);
		}
		LOG.trace(".containerIsEqual(): publisherManifest & subscribedManifest are bot NOT null");
		LOG.trace(".containerIsEqual(): checking to see if publisherManifest has a containerDescriptor");
		boolean testManifestHasContainerDescriptor = publisherManifest.hasContainerDescriptor();
		LOG.trace(".containerIsEqual(): checking to see if subscribedManifest has a containerDescriptor");
		boolean subscribedManifestHasContainerDescriptor = subscribedManifest.hasContainerDescriptor();
		if(!testManifestHasContainerDescriptor && !subscribedManifestHasContainerDescriptor) {
			LOG.debug(".contentIsEqual(): Exit, neither publisherManifest or subscribedManifest has a containerDescriptor, returning -true-");
			return(true);
		}
		if(!subscribedManifestHasContainerDescriptor){
			LOG.debug(".containerIsEqual(): Exit, subscribedManifest has no containerDescriptor, but publisherManifest does, returning -false-");
			return(false);
		}
		if(!testManifestHasContainerDescriptor ) {
			LOG.debug(".containerIsEqual(): Exit, publisherManifest has no containerDescriptor, but subscribedManifest does, returning -false-");
			return(false);
		}
		LOG.trace(".containerIsEqual(): publisherManifest and subscribedManifest both have containerDescriptors, now testing for equality");

		boolean containersAreEqual = publisherManifest.getContainerDescriptor().equals(subscribedManifest.getContainerDescriptor());
		LOG.debug(".containerIsEqual(): Exit, publisherManifest and subscribedManifest containerDescriptor comparison yielded->{}", containersAreEqual);
		return(containersAreEqual);
	}

	private boolean contentIsEqual(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
		LOG.debug(".contentIsEqual(): Entry");
    	if(testManifest == null || subscribedManifest == null){
			LOG.debug(".contentIsEqual(): testManifest or subscribedManifest is null, return -false-");
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

		DataParcelTypeDescriptor testDescriptor = testManifest.getContentDescriptor();
		DataParcelTypeDescriptor subscribedDescriptor = subscribedManifest.getContentDescriptor();

		boolean parcelDefinerIsEqual = StringUtils.equals(testDescriptor.getDataParcelDefiner(),subscribedDescriptor.getDataParcelDefiner());
		boolean parcelDefinedHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDefiner(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelDefinerIsGoodEnoughMatch = parcelDefinerIsEqual || parcelDefinedHasWildcard;
		if (!parcelDefinerIsGoodEnoughMatch) {
			return (false);
		}
		boolean parcelCategoryIsEqual = StringUtils.equals(testDescriptor.getDataParcelCategory(),subscribedDescriptor.getDataParcelCategory());
		boolean parcelCategoryHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelCategory(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelCategoryIsGoodEnoughMatch = parcelCategoryIsEqual || parcelCategoryHasWildcard;
		if(!parcelCategoryIsGoodEnoughMatch){
			return(false);
		}
		boolean parcelSubcategoryIsEqual = StringUtils.equals(testDescriptor.getDataParcelSubCategory(),subscribedDescriptor.getDataParcelSubCategory());
		boolean parcelSubcategoryHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelSubCategory(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelSubcategoryIsGoodEnoughMatch = parcelSubcategoryIsEqual || parcelSubcategoryHasWildcard;
		if(!parcelSubcategoryIsGoodEnoughMatch){
			return(false);
		}
		boolean parcelResourceIsEqual = StringUtils.equals(testDescriptor.getDataParcelResource(),subscribedDescriptor.getDataParcelResource());
		boolean parcelResourceHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelResource(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelResourceIsGoodEnoughMatch = parcelResourceIsEqual || parcelResourceHasWildcard;
		if (!parcelResourceIsGoodEnoughMatch) {
			return(false);
		}
		boolean parcelSegmentIsEqual = StringUtils.equals(testDescriptor.getDataParcelSegment(),subscribedDescriptor.getDataParcelSegment());
		boolean parcelSegmentHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelSegment(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelSegmentIsGoodEnoughMatch = parcelSegmentIsEqual || parcelSegmentHasWildcard;
		if (!parcelSegmentIsGoodEnoughMatch) {
			return(false);
		}
		boolean parcelAttributeIsEqual = StringUtils.equals(testDescriptor.getDataParcelAttribute(),subscribedDescriptor.getDataParcelAttribute());
		boolean parcelAttributeHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelAttribute(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelAttributeIsGoodEnoughMatch = parcelAttributeIsEqual || parcelAttributeHasWildcard;
		if(!parcelAttributeIsGoodEnoughMatch){
			return(false);
		}
		boolean parcelDiscriminatorTypeIsEqual = StringUtils.equals(testDescriptor.getDataParcelDiscriminatorType(),subscribedDescriptor.getDataParcelDiscriminatorType());
		boolean parcelDiscriminatorTypeHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDiscriminatorType(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelDiscriminatorTypeIsGoodEnoughMatch = parcelDiscriminatorTypeIsEqual || parcelDiscriminatorTypeHasWildcard;
		if(!parcelDiscriminatorTypeIsGoodEnoughMatch){
			return(false);
		}
		boolean parcelDiscriminatorValueIsEqual = StringUtils.equals(testDescriptor.getDataParcelDiscriminatorValue(),subscribedDescriptor.getDataParcelDiscriminatorValue());
		boolean parcelDiscriminatorValueHasWildcard = StringUtils.equals(subscribedDescriptor.getDataParcelDiscriminatorValue(),DataParcelManifest.WILDCARD_CHARACTER);
		boolean parcelDiscriminatorValueIsGoodEnoughMatch = parcelDiscriminatorValueIsEqual || parcelDiscriminatorValueHasWildcard;
		if(!parcelDiscriminatorValueIsGoodEnoughMatch){
			return(false);
		}
		LOG.debug(".contentIsEqual(): Exit, -true-");
		return (true);
	}

	private boolean containerOnlyEqual(DataParcelManifest publisherManifest, DataParcelManifest subscribedManifest){
		LOG.debug(".containerOnlyEqual(): Entry");
		if(publisherManifest == null || subscribedManifest == null){
			LOG.debug(".containerOnlyEqual(): testManifest or subscribedManifest is null, return -false-");
			return(false);
		}
		LOG.trace(".containerOnlyEqual(): testManifest & subscribedManifest are bot NOT null");
		LOG.trace(".containerOnlyEqual(): checking to see if subscribedManifest has a contentDescriptor && containerDescriptor");
		if(subscribedManifest.hasContainerDescriptor() && subscribedManifest.hasContentDescriptor()){
			LOG.trace(".containerOnlyEqual(): subscribedManifest has both contentDescriptor && containerDescriptor, checking to see if they are the same");
			if(!subscribedManifest.getContainerDescriptor().equals(subscribedManifest.getContentDescriptor())){
				LOG.debug(".containerOnlyEqual(): contentDescriptor && containerDescriptor are different, so this subscriberManifest is after specific content, returning -false-");
				return(false);
			}
		}
		LOG.trace(".containerOnlyEqual(): subscribedManifest does not have a ContentDescriber!, checking comparisons of the container only");
		if(publisherManifest.hasContainerDescriptor() && subscribedManifest.hasContainerDescriptor()){
			LOG.trace(".containerOnlyEqual(): publisherManifest cotnains a ContainerDescriptor, so comparing");
			boolean containerIsEqual = containerIsEqual(publisherManifest, subscribedManifest);
			LOG.debug(".containerOnlyEqual(): Comparison of ContainerContent is ->{}, returning it!", containerIsEqual);
			return(containerIsEqual);
		}
		LOG.debug(".containerOnlyEqual(): Publisher does not have a ContainerDescriptor, returning -false-");
		return(false);
	}

	private boolean normalisationMatches(DataParcelManifest testManifest, DataParcelManifest subscribedManifest){
		LOG.debug(".normalisationMatches(): Entry");
		if(testManifest == null || subscribedManifest == null){
			LOG.debug(".normalisationMatches(): Exit, either testManifest or subscribedManifest are null, returning -false-");
			return(false);
		}
		LOG.trace(".normalisationMatches(): subscribedManifest.getNormalisationStatus()->{}", subscribedManifest.getNormalisationStatus());
		if(subscribedManifest.getNormalisationStatus().equals(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY)){
			LOG.debug(".normalisationMatches(): Exit, subscribedManifest has requested 'ANY', returning -true-");
			return(true);
		}
		boolean normalisationStatusIsEqual = subscribedManifest.getNormalisationStatus().equals(testManifest.getNormalisationStatus());
		LOG.debug(".normalisationMatches(): Exit, returning comparison result->{}", normalisationStatusIsEqual);
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
		if(subscribedManifest.hasSourceSystem()){
			if(subscribedManifest.getSourceSystem().contentEquals("*")){
				return(true);
			}
		}
		if(!testManifest.hasSourceSystem() && !subscribedManifest.hasSourceSystem()){
			return(true);
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
		if(subscribedManifest.hasIntendedTargetSystem()){
			if(subscribedManifest.getIntendedTargetSystem().contentEquals("*")){
				return(true);
			}
		}
		if(!testManifest.hasIntendedTargetSystem() && !subscribedManifest.hasIntendedTargetSystem()){
			return(true);
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

	private boolean enforcementPointApprovalStatusMatches(DataParcelManifest publishedManifest, DataParcelManifest subscribedManifest) {
    	LOG.debug(".enforcementPointApprovalStatusMatches(): Entry");
		if (publishedManifest == null || subscribedManifest == null) {
			LOG.debug(".enforcementPointApprovalStatusMatches(): Exit, either publishedManifest or subscribedManifest are null, returning -false-");
			return (false);
		}
		if (subscribedManifest.getEnforcementPointApprovalStatus().equals(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY)) {
			LOG.debug(".enforcementPointApprovalStatusMatches(): Exit, subscribedManifest is set to 'ANY', returning -true-");
			return (true);
		}
		LOG.trace(".enforcementPointApprovalStatusMatches(): publishedManifest PEP Status->{}", publishedManifest.getEnforcementPointApprovalStatus());
		LOG.trace(".enforcementPointApprovalStatusMatches(): subscribedManifest PEP Status->{}", subscribedManifest.getEnforcementPointApprovalStatus());
		boolean approvalStatusMatch = subscribedManifest.getEnforcementPointApprovalStatus().equals(publishedManifest.getEnforcementPointApprovalStatus());
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
