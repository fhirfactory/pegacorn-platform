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
package net.fhirfactory.pegacorn.petasos.core.moa.pathway.interchange.worker;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.manager.ProcessingPlantResilienceActivityServicesController;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DataParcelSubscriptionIM;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.pubsub.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPFunctionToken;
import org.apache.camel.*;
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
public class InterchangeTargetWUPTypeRouter {

    private static final Logger LOG = LoggerFactory.getLogger(InterchangeTargetWUPTypeRouter.class);

    @Inject
    DataParcelSubscriptionIM topicServer;

    @Inject
    TopologyIM topologyProxy;

    @Produce
    private ProducerTemplate template;

    @Inject
    ProcessingPlantResilienceActivityServicesController activityServicesController;

    /**
     * Essentially, we get the set of WUPs subscribing to a particular UoW type,
     * create a property within the CamelExchange and then we use that Property
     * as a mechanism of keeping track of who we have already forwarded the UoW
     * to. Once we've cycled through all the targets (subscribers), we return
     * null.
     *
     * @param ingresPacket Incoming WorkUnitTransportPacket that will be distributed to all
     * Subscribed WUPs
     * @param camelExchange The Apache Camel Exchange instance associated with
     * this route.
     * @return An endpoint (name) for a recipient for the incoming UoW
     */
    @RecipientList
    public List<String> forwardUoW2WUPs(WorkUnitTransportPacket ingresPacket, Exchange camelExchange) {
        LOG.debug(".forwardUoW2WUPs(): Entry, ingresPacket (WorkUnitTransportPacket)->{}", ingresPacket);

        // Get my Petasos Context
        LOG.trace(".forwardUoW2WUPs(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        DataParcelManifest uowTopicID = null;
        if (ingresPacket.getPayload().hasIngresContent()) {
            uowTopicID = ingresPacket.getPayload().getIngresContent().getPayloadManifest();
            LOG.trace(".forwardUoW2WUPs(): uowTopicId --> {}", uowTopicID);
        } else {
            LOG.debug(".forwardUoW2WUPs(): Exit, there's no payload (UoW), so return an empty list (and end this route).");
            return (new ArrayList<String>());
        }
        LOG.trace(".forwardUoW2WUPs(): Getting the set of subscribers for the given topic (calling the topicServer)");
        LOG.info(".forwardUoW2WUPs(): Looking for Subscribers To->{}:", uowTopicID);
        List<PubSubParticipant> subscriberSet = topicServer.getSubscriberSet(uowTopicID);
        LOG.trace(".forwardUoW2WUPs(): Before we do a general routing attempt, let's see if the message is directed somewhere specific");
        //
        // Because auditing is not running yet
        // Remove once Auditing is in place
        //
        int subscriberSetSize = 0;
        if(subscriberSet != null) {
            subscriberSetSize = subscriberSet.size();
        }
        LOG.info("Number of Subscribers->{}", subscriberSetSize);
        //
        //
        //
        String alreadySentTo = "";
        if(!StringUtils.isEmpty(uowTopicID.getIntendedTargetSystem())){
            LOG.trace(".forwardUoW2WUPs(): It's not empty, so let's see if the appropriate downstream system is registered");
            for(PubSubParticipant currentSubscriber: subscriberSet){
                if(hasRemoteServiceName(currentSubscriber)) {
                    String subscriberName = currentSubscriber.getInterSubsystemParticipant().getIdentifier().getServiceName();
                    if (subscriberName.contentEquals(uowTopicID.getIntendedTargetSystem())) {
                        forwardPacket(currentSubscriber, ingresPacket);
                        alreadySentTo = subscriberName;
                    }
                }
            }
        }

        LOG.trace(".forwardUoW2WUPs(): Iterate through the subscribers");
        if(LOG.isDebugEnabled()){
            LOG.debug(".forwardUoW2WUPs(): number of subscribers to this UoW->{}", subscriberSet.size());
        }
        if (subscriberSet != null) {
            if(!subscriberSet.isEmpty()) {
                LOG.trace(".forwardUoW2WUPs(): Iterating through....");
                for (PubSubParticipant currentSubscriber : subscriberSet) {
                    LOG.trace(".forwardUoW2WUPs(): Iterating, currentSubscriber->{}", currentSubscriber);
                    boolean dontSendAgain = false;
                    if (hasRemoteServiceName(currentSubscriber)) {
                        LOG.info(".forwardUoW2WUPs(): has Inter-Subsystem element");
                        if (currentSubscriber.getInterSubsystemParticipant().getIdentifier().getServiceName().contentEquals(alreadySentTo)) {
                            dontSendAgain = true;
                        }
                    }
                    if (!dontSendAgain) {
                        LOG.trace(".forwardUoW2WUPs(): does not have Inter-Subsystem element");
                        forwardPacket(currentSubscriber, ingresPacket);
                    }
                }
            }
        }
        LOG.debug(".forwardUoW2WUPs(): Exiting");
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
        if(subscriber.getInterSubsystemParticipant().getIdentifier() == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant().getIdentifier().getServiceName() == null){
            return(false);
        }
        return(true);
    }

    private boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(parcelManifest.getIntendedTargetSystem() == null){
            return(false);
        }
        return(true);
    }

    private void forwardPacket(PubSubParticipant subscriber, WorkUnitTransportPacket packet){
        LOG.debug(".forwardUoW2WUPs(): Subscriber --> {}", subscriber);
        IntraSubsystemPubSubParticipantIdentifier localSubscriberIdentifier = subscriber.getIntraSubsystemParticipant().getIdentifier();
        LOG.trace(".forwardUoW2WUPs(): The (LocalSubscriber aspect) Identifier->{}", localSubscriberIdentifier);
        WorkUnitProcessorTopologyNode currentNodeElement = (WorkUnitProcessorTopologyNode)topologyProxy.getNode(localSubscriberIdentifier);
        LOG.trace(".forwardUoW2WUPs(): The TopologyNode for the target subscriber->{}", currentNodeElement);
        TopologyNodeFDNToken currentNodeToken = currentNodeElement.getNodeFDN().getToken();
        LOG.trace(".forwardUoW2WUPs(): The WUPToken for the target subscriber->{}", currentNodeElement);
        RouteElementNames routeName = new RouteElementNames(currentNodeToken);
        // Clone and Inject Message into Target Route
        WorkUnitTransportPacket clonedPacket = packet.deepClone();
        // Now check if the Subscriber is actually a remote one! If so, ensure it has a proper "IntendedTarget" entry
        if(hasRemoteServiceName(subscriber)){
            DataParcelManifest payloadTopicID = packet.getPayload().getPayloadTopicID();
            boolean hasEmptyIntendedTarget = StringUtils.isEmpty(payloadTopicID.getIntendedTargetSystem());
            boolean hasWildcardTarget = false;
            if(hasIntendedTarget(payloadTopicID)){
                hasWildcardTarget = payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER);
            }
            boolean hasRemoteElement = hasRemoteServiceName(subscriber);
            if((hasEmptyIntendedTarget || hasWildcardTarget) && hasRemoteElement){
                clonedPacket.getPayload().getPayloadTopicID().setIntendedTargetSystem(subscriber.getInterSubsystemParticipant().getIdentifier().getServiceName());
            }
        }
        template.sendBody(routeName.getEndPointWUPContainerIngresProcessorIngres(), ExchangePattern.InOnly, clonedPacket);
        // targetSubscriberSet.add(routeName.getEndPointWUPContainerIngresProcessorIngres());
        // Now add the downstream WUPFunction to the Parcel Finalisation Registry
        WUPFunctionToken functionToken = new WUPFunctionToken(currentNodeElement.getNodeFunctionFDN().getFunctionToken());
        activityServicesController.registerWUAEpisodeDownstreamWUPInterest(packet.getPacketID().getPresentEpisodeIdentifier(), functionToken);
    }

    private void tracePrintSubscribedWUPSet(Set<WorkUnitProcessorTopologyNode> wupSet) {
        LOG.trace(".tracePrintSubscribedWUPSet(): Subscribed WUP Set --> {}", wupSet.size());
        Iterator<WorkUnitProcessorTopologyNode> tokenIterator = wupSet.iterator();
        while (tokenIterator.hasNext()) {
            LOG.trace(".forwardUoW2WUPs(): Subscribed WUP Ingres Point --> {}", tokenIterator.next());
        }
    }

    private void updateServiceModuleMap(FDNToken associatedWUP, FDNToken targetIngresPoint) {
    	// TODO
    }
}
