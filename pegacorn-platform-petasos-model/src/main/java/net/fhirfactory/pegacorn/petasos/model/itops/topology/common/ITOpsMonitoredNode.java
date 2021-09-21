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
package net.fhirfactory.pegacorn.petasos.model.itops.topology.common;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.datatypes.ITOpsRoutingEndpoint;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.valuesets.ITOpsMonitoredNodeTypeEnum;

import java.io.Serializable;

public class ITOpsMonitoredNode implements Serializable {
    private String componentID;
    private String componentName;
    private String nodeVersion;
    private String concurrencyMode;
    private String resilienceMode;
    private ITOpsRoutingEndpoint routing;
    private TopologyNodeFDN topologyNodeFDN;

    public ITOpsRoutingEndpoint getRouting() {
        return routing;
    }

    public void setRouting(ITOpsRoutingEndpoint routing) {
        this.routing = routing;
    }

    public TopologyNodeFDN getTopologyNodeFDN() {
        return topologyNodeFDN;
    }

    public void setTopologyNodeFDN(TopologyNodeFDN topologyNodeFDN) {
        this.topologyNodeFDN = topologyNodeFDN;
    }

    private ITOpsMonitoredNodeTypeEnum nodeType;

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public String getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(String nodeVersion) {
        this.nodeVersion = nodeVersion;
    }

    public ITOpsMonitoredNodeTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(ITOpsMonitoredNodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public String getConcurrencyMode() {
        return concurrencyMode;
    }

    public void setConcurrencyMode(String concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
    }

    public String getResilienceMode() {
        return resilienceMode;
    }

    public void setResilienceMode(String resilienceMode) {
        this.resilienceMode = resilienceMode;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String toString() {
        return "ITOpsMonitoredNode{" +
                "componentID='" + componentID + '\'' +
                ", componentName='" + componentName + '\'' +
                ", nodeVersion='" + nodeVersion + '\'' +
                ", concurrencyMode='" + concurrencyMode + '\'' +
                ", resilienceMode='" + resilienceMode + '\'' +
                ", routing=" + routing +
                ", topologyNodeFDN=" + topologyNodeFDN +
                ", nodeType=" + nodeType +
                '}';
    }
}
