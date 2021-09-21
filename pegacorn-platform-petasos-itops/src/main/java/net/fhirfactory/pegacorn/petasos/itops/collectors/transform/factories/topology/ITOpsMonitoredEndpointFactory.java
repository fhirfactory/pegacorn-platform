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

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCClusteredServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.interact.StandardInteractClientTopologyEndpointPort;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.common.ITOpsMonitoredNodeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ITOpsMonitoredEndpointFactory  extends ITOpsMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMonitoredEndpointFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ITOpsMonitoredEndpoint newEndpoint(IPCTopologyEndpoint endpointTopologyNode){
        getLogger().debug(".newEndpoint(): Entry, endpointTopologyNode->{}", endpointTopologyNode);
        if(endpointTopologyNode == null){
            return(null);
        }
        ITOpsMonitoredEndpoint endpoint = new ITOpsMonitoredEndpoint();
        endpoint = (ITOpsMonitoredEndpoint) newITOpsMonitoredNode(endpoint, endpointTopologyNode);
        endpoint.setEndpointType(endpointTopologyNode.getEndpointType());
        endpoint.setEncrypted(endpointTopologyNode.isEncrypted());
        endpoint.setActualHostIP(endpointTopologyNode.getActualHostIP());
        endpoint.setActualPodIP(endpointTopologyNode.getActualPodIP());
        switch(endpointTopologyNode.getEndpointType()){
            case JGROUPS_INTRAZONE_SERVICE:
            case JGROUPS_INTERZONE_SERVICE:
            case JGROUPS_INTERSITE_SERVICE:{
                IPCServerTopologyEndpoint jgroupsEndpoint = (IPCServerTopologyEndpoint)endpointTopologyNode;
                endpoint.setLocalPort(Integer.toString(jgroupsEndpoint.getPortValue()));
                endpoint.setLocalDNSEntry(jgroupsEndpoint.getHostDNSName());
                break;
            }
            case MLLP_SERVER: {
                IPCClusteredServerTopologyEndpoint mllpServerEndpoint = (IPCClusteredServerTopologyEndpoint)endpointTopologyNode;
                endpoint.setLocalPort(Integer.toString(mllpServerEndpoint.getPortValue()));
                endpoint.setLocalDNSEntry(mllpServerEndpoint.getHostDNSName());
                endpoint.setRemoteSystemName(mllpServerEndpoint.getConnectedSystemName());
                endpoint.setLocalServicePort(mllpServerEndpoint.getServicePortName());
                endpoint.setLocalServiceDNSEntry(mllpServerEndpoint.getServiceDNSName());
                break;
            }
            case MLLP_CLIENT: {
                StandardInteractClientTopologyEndpointPort mllpClientEndpoint = (StandardInteractClientTopologyEndpointPort)endpointTopologyNode;
                endpoint.setRemoteSystemName(mllpClientEndpoint.getConnectedSystemName());
                endpoint.setRemoteDNSEntry(mllpClientEndpoint.getTargetSystem().getTargetPorts().get(0).getTargetPortDNSName());
                endpoint.setRemotePort(Integer.toString(mllpClientEndpoint.getTargetSystem().getTargetPorts().get(0).getTargetPortValue()));
                break;
            }
            case HTTP_API_SERVER:
                break;
            case HTTP_API_CLIENT:
                break;
            case SQL_SERVER:
                break;
            case SQL_CLIENT:
                break;
            case OTHER_API_SERVER:
                break;
            case OTHER_API_CLIENT:
                break;
            case OTHER_SERVER:
                break;
        }
        getLogger().debug(".newEndpoint(): Exit, endpoint->{}", endpoint);
        return(endpoint);
    }
}
