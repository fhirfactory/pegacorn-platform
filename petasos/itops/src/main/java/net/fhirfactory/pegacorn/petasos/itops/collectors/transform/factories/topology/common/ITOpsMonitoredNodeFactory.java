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
package net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.common;

import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.common.ITOpsMonitoredNode;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.valuesets.ITOpsMonitoredNodeTypeEnum;
import org.slf4j.Logger;

public abstract class ITOpsMonitoredNodeFactory {

    abstract protected Logger getLogger();

    protected ITOpsMonitoredNode newITOpsMonitoredNode(ITOpsMonitoredNode monitoredNode, TopologyNode topologyNode){
        getLogger().debug(".newITOpsMonitoredNode(): Entry, monitoredNode->{}, topologyNode->{}", monitoredNode, topologyNode);
        monitoredNode.setComponentID(topologyNode.getComponentType());
        monitoredNode.setComponentName(topologyNode.getNodeRDN().getNodeName());
        ITOpsMonitoredNodeTypeEnum nodeTypeEnum = ITOpsMonitoredNodeTypeEnum.nodeTypeFromTopologyNodeType(topologyNode.getComponentType());
        monitoredNode.setNodeType(nodeTypeEnum);
        monitoredNode.setNodeVersion(topologyNode.getNodeRDN().getNodeVersion());
        if(topologyNode.getConcurrencyMode() != null) {
            monitoredNode.setConcurrencyMode(topologyNode.getConcurrencyMode().getDisplayName());
        }
        if(topologyNode.getResilienceMode() != null) {
            monitoredNode.setResilienceMode(topologyNode.getResilienceMode().getDisplayName());
        }
        getLogger().debug(".newITOpsMonitoredNode(): Exit, monitoredNode->{}", monitoredNode);
        return(monitoredNode);
    }
}
