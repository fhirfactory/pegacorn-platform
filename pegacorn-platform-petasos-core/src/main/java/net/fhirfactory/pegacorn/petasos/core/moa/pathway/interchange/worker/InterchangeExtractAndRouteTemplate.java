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

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterchangeExtractAndRouteTemplate extends BaseRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InterchangeExtractAndRouteTemplate.class);

    private WorkUnitProcessorTopologyNode wupTopologyNode;
    private RouteElementNames nameSet;

    public InterchangeExtractAndRouteTemplate(CamelContext context, WorkUnitProcessorTopologyNode nodeElement) {
        super(context);
        LOG.debug(".InterchangeExtractAndRouteTemplate(): Entry, context --> ###, nodeElement --> {}", nodeElement);
        this.wupTopologyNode = nodeElement;
        nameSet = new RouteElementNames(wupTopologyNode.getNodeFDN().getToken());
    }

    @Override
    public void configure() {
        LOG.debug(".configure(): Entry!, for wupNodeElement --> {}", this.wupTopologyNode);
        LOG.debug("InterchangeExtractAndRouteTemplate :: EndPointInterchangePayloadTransformerIngres --> {}", nameSet.getEndPointInterchangePayloadTransformerIngres());
        LOG.debug("InterchangeExtractAndRouteTemplate :: EndPointInterchangeRouterIngres --> {}", nameSet.getEndPointInterchangeRouterIngres());

        NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();

        fromWithStandardExceptionHandling(nameSet.getEndPointInterchangePayloadTransformerIngres())
                .routeId(nameSet.getRouteInterchangePayloadTransformer())
                .process(nodeDetailInjector)
                .split().method(InterchangeUoWPayload2NewUoWProcessor.class, "extractUoWPayloadAndCreateNewUoWSet(*, Exchange)")
                .to(nameSet.getEndPointInterchangePayloadTransformerEgress());

        fromWithStandardExceptionHandling(nameSet.getEndPointInterchangePayloadTransformerEgress())
                .routeId(nameSet.getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres())
                .to(nameSet.getEndPointInterchangeRouterIngres());

        fromWithStandardExceptionHandling(nameSet.getEndPointInterchangeRouterIngres())
                .routeId(nameSet.getRouteInterchangeRouter())
                .process(nodeDetailInjector)
                .bean(InterchangeTargetWUPTypeRouter.class, "forwardUoW2WUPs(*, Exchange)");
    }

    protected class NodeDetailInjector implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            LOG.debug("NodeDetailInjector.process(): Entry");
            boolean alreadyInPlace = false;
            if(exchange.hasProperties()) {
                WorkUnitProcessorTopologyNode wupTN = exchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
                if (wupTN != null) {
                    alreadyInPlace = true;
                }
            }
            if(!alreadyInPlace) {
                exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getWupTopologyNode());
            }
        }
    }

    public WorkUnitProcessorTopologyNode getWupTopologyNode() {
        return wupTopologyNode;
    }
}
