/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.common.ITOpsMonitoredNodeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredEndpoint;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ITOpsMonitoredWUPFactory extends ITOpsMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMonitoredWUPFactory.class);

    @Inject
    private ITOpsMonitoredEndpointFactory endpointFactory;

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ITOpsMonitoredWUP newWorkUnitProcessor(WorkUnitProcessorTopologyNode wupTopologyNode){
        getLogger().debug(".newWorkUnitProcessor(): wupTopologyNode->{}", wupTopologyNode);
        ITOpsMonitoredWUP wup = new ITOpsMonitoredWUP();
        wup = (ITOpsMonitoredWUP) newITOpsMonitoredNode(wup, wupTopologyNode);
        for(TopologyNodeFDN currentEndpointFDN: wupTopologyNode.getEndpoints()){
            getLogger().trace(".newWorkUnitProcessor(): currentEndpointFDN->{}", currentEndpointFDN);
            IPCTopologyEndpoint endpointTopologyNode = (IPCTopologyEndpoint) topologyIM.getNode(currentEndpointFDN);
            getLogger().trace(".newWorkUnitProcessor(): endpointTopologyNode->{}", endpointTopologyNode);
            ITOpsMonitoredEndpoint currentEndpoint = endpointFactory.newEndpoint(endpointTopologyNode);
            if(currentEndpoint != null) {
                wup.addEndpoint(currentEndpoint);
            }
        }
        getLogger().debug(".newWorkUnitProcessor(): wup->{}", wup);
        return(wup);
    }
}
