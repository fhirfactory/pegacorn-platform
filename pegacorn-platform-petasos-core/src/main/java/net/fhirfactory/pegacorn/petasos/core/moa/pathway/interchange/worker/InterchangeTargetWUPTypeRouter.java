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
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.manager.ProcessingPlantResilienceActivityServicesController;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DataParcelSubscriptionIM;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPFunctionToken;
import org.apache.camel.*;
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
    public List<String> forwardUoW2WUPs(WorkUnitTransportPacket ingresPacket, Exchange camelExchange, String wupInstanceKey) {
        LOG.debug(".forwardUoW2WUPs(): Entry, ingresPacket (WorkUnitTransportPacket) --> {}, wupInstanceKey (String) --> {}", ingresPacket, wupInstanceKey);

        // Get my Petasos Context
        TopologyNodeFDNToken nodeFDNToken = new TopologyNodeFDNToken(wupInstanceKey);
        WorkUnitProcessorTopologyNode node = (WorkUnitProcessorTopologyNode)topologyProxy.getNode(nodeFDNToken);
        DataParcelToken uowTopicID = null;
        if (ingresPacket.getPayload().hasIngresContent()) {
            uowTopicID = ingresPacket.getPayload().getIngresContent().getPayloadTopicID();
            LOG.trace(".forwardUoW2WUPs(): uowTopicId --> {}", uowTopicID);
        } else {
            LOG.debug(".forwardUoW2WUPs(): Exit, there's no payload (UoW), so return an empty list (and end this route).");
            return (new ArrayList<String>());
        }
        Set<TopologyNodeFDNToken> nodeSet = topicServer.getSubscriberSet(uowTopicID);
        if (nodeSet != null) {
            Iterator<TopologyNodeFDNToken> nodeIterator = nodeSet.iterator();
            while (nodeIterator.hasNext()) {
                TopologyNodeFDNToken currentNodeIdentifier = nodeIterator.next();
                LOG.trace(".forwardUoW2WUPs(): Subscriber --> {}", currentNodeIdentifier);
                WorkUnitProcessorTopologyNode currentNodeElement = (WorkUnitProcessorTopologyNode)topologyProxy.getNode(currentNodeIdentifier);
                TopologyNodeFunctionFDNToken currentNodeFunctionToken = currentNodeElement.getNodeFunctionFDN().getFunctionToken();
                RouteElementNames routeName = new RouteElementNames(currentNodeFunctionToken);
                // Clone and Inject Message into Target Route
                WorkUnitTransportPacket clonedPacket = ingresPacket.deepClone();
                template.sendBody(routeName.getEndPointWUPContainerIngresProcessorIngres(), ExchangePattern.InOnly, clonedPacket);
                // targetSubscriberSet.add(routeName.getEndPointWUPContainerIngresProcessorIngres());
                // Now add the downstream WUPFunction to the Parcel Finalisation Registry
                WUPFunctionToken functionToken = new WUPFunctionToken(currentNodeFunctionToken);
                activityServicesController.registerWUAEpisodeDownstreamWUPInterest(ingresPacket.getPacketID().getPresentEpisodeIdentifier(), functionToken);
            }
        }
        LOG.debug(".forwardUoW2WUPs(): Exiting");
        List<String> targetSubscriberSet = new ArrayList<String>();
        return (targetSubscriberSet);
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
