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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.archetypes;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerEgressGatekeeper;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerEgressProcessor;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPEgressConduit;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalIngresWUPContainerRoute extends BaseRouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalIngresWUPContainerRoute.class);

    private WorkUnitProcessorTopologyNode wupTopologyNode;
    private RouteElementNames nameSet;

    public ExternalIngresWUPContainerRoute( CamelContext camelCTX, WorkUnitProcessorTopologyNode wupNode) {
        super(camelCTX);
        LOG.debug(".ExternalIngresWUPContainerRoute(): Entry, context --> ###, wupNode --> {}", wupNode );
        this.wupTopologyNode = wupNode;
        nameSet = new RouteElementNames(wupTopologyNode.getNodeFunctionFDN().getFunctionToken());
    }

    @Override
    public void configure() {
        LOG.debug(".configure(): Entry!, for wupNodeElement --> {}", this.wupTopologyNode);
        LOG.debug("ExternalIngresWUPContainerRoute :: EndPointWUPIngres --> Per Implementation Specified");
        LOG.debug("ExternalIngresWUPContainerRoute :: EndPointWUPEgress --> {}", nameSet.getEndPointWUPEgress() );
        LOG.debug("ExternalIngresWUPContainerRoute :: EndPointWUPEgressConduitEgress --> {}", nameSet.getEndPointWUPEgressConduitEgress());
        LOG.debug("ExternalIngresWUPContainerRoute :: EndPointWUPContainerEgressProcessorEgress --> {}", nameSet.getEndPointWUPContainerEgressProcessorEgress());

        ExternalIngresWUPContainerRoute.NodeDetailInjector nodeDetailInjector = new ExternalIngresWUPContainerRoute.NodeDetailInjector();
      
        fromWithStandardExceptionHandling(nameSet.getEndPointWUPEgress())
        		.log(LoggingLevel.DEBUG, "from(nameSet.getEndPointWUPEgress()) --> ${body}")
                .process(nodeDetailInjector)
                .routeId(nameSet.getRouteWUPEgress2WUPEgressConduitEgress())
                .bean(WUPEgressConduit.class, "receiveFromWUP(*, Exchange," + this.wupTopologyNode.getNodeFDN().getToken().getTokenValue() + ")")
                .to(nameSet.getEndPointWUPEgressConduitEgress());

        fromWithStandardExceptionHandling(nameSet.getEndPointWUPEgressConduitEgress())
                .routeId(nameSet.getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres())
                .to(nameSet.getEndPointWUPContainerEgressProcessorIngres());

        fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressProcessorIngres())
                .routeId(nameSet.getRouteWUPContainerEgressProcessor())
                .process(nodeDetailInjector)
                .bean(WUPContainerEgressProcessor.class, "egressContentProcessor(*, Exchange," + this.wupTopologyNode.getNodeFDN().getToken().getTokenValue() + ")")
                .to(nameSet.getEndPointWUPContainerEgressProcessorEgress());

        fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressProcessorEgress())
                .routeId(nameSet.getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres())
                .to(nameSet.getEndPointWUPContainerEgressGatekeeperIngres());

        fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressGatekeeperIngres())
                .routeId(nameSet.getRouteWUPContainerEgressGateway())
                .process(nodeDetailInjector)
                .bean(WUPContainerEgressGatekeeper.class, "egressGatekeeper(*, Exchange," + this.wupTopologyNode.getNodeFDN().getToken().getTokenValue() + ")");

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
