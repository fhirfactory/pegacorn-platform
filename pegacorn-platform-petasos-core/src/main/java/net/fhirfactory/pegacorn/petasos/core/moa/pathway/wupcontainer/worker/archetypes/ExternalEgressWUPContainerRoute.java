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
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerIngresGatekeeper;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerIngresProcessor;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPIngresConduit;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark A. Hunter
 * @since 2020-07-1
 */

public class ExternalEgressWUPContainerRoute extends BaseRouteBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(ExternalEgressWUPContainerRoute.class);
    protected Logger getLogger(){
        return(LOG);
    }

	private WorkUnitProcessorTopologyNode wupTopologyNode;
	private RouteElementNames nameSet;

	public ExternalEgressWUPContainerRoute(CamelContext camelCTX, WorkUnitProcessorTopologyNode wupNode) {
		super(camelCTX);
		getLogger().debug(".StandardWUPContainerRoute(): Entry, context --> ###, wupNode --> {}", wupNode);
		this.wupTopologyNode = wupNode;
		nameSet = new RouteElementNames(wupNode.getNodeFDN().getToken());
	}

	@Override
	public void configure() {
		getLogger().debug(".configure(): Entry!, for wupNode --> {}", this.wupTopologyNode);
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPContainerIngresProcessorIngres --> {}", nameSet.getEndPointWUPContainerIngresProcessorIngres());
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPContainerIngresProcessorEgress --> {}", nameSet.getEndPointWUPContainerIngresProcessorEgress());
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPContainerIngresGatekeeperIngres --> {}", nameSet.getEndPointWUPContainerIngresGatekeeperIngres());
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPIngresConduitIngres --> {}", nameSet.getEndPointWUPIngresConduitIngres());
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPIngres --> {}", nameSet.getEndPointWUPIngres());
		getLogger().info("StandardWUPContainerRoute :: EndPointWUPEgress --> {}", nameSet.getEndPointWUPEgress());

		ExternalEgressWUPContainerRoute.NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresProcessorIngres())
				.routeId(nameSet.getRouteWUPContainerIngressProcessor())
				.process(nodeDetailInjector)
				.bean(WUPContainerIngresProcessor.class, "ingresContentProcessor(*, Exchange)")
				.to(nameSet.getEndPointWUPContainerIngresProcessorEgress());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresProcessorEgress())
				.routeId(nameSet.getRouteIngresProcessorEgress2IngresGatekeeperIngres())
				.to(nameSet.getEndPointWUPContainerIngresGatekeeperIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresGatekeeperIngres())
				.routeId(nameSet.getRouteWUPContainerIngresGateway())
				.process(nodeDetailInjector)
				.bean(WUPContainerIngresGatekeeper.class, "ingresGatekeeper(*, Exchange)");

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresGatekeeperEgress())
				.routeId(nameSet.getRouteIngresGatekeeperEgress2IngresConduitIngres())
				.to(ExchangePattern.InOnly, nameSet.getEndPointWUPIngresConduitIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPIngresConduitIngres())
				.routeId(nameSet.getRouteIngresConduitIngres2WUPIngres())
				.process(nodeDetailInjector)
				.bean(WUPIngresConduit.class, "forwardIntoWUP(*, Exchange)")
				.to(nameSet.getEndPointWUPIngres());
	}

	protected class NodeDetailInjector implements Processor {
		@Override
		public void process(Exchange exchange) throws Exception {
			getLogger().debug("NodeDetailInjector.process(): Entry");
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
