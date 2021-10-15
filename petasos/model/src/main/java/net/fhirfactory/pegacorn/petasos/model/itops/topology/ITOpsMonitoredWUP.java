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
package net.fhirfactory.pegacorn.petasos.model.itops.topology;

import net.fhirfactory.pegacorn.petasos.model.itops.topology.common.ITOpsMonitoredNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ITOpsMonitoredWUP extends ITOpsMonitoredNode {
    Map<String, ITOpsMonitoredEndpoint> endpoints;

    public ITOpsMonitoredWUP(){
        this.endpoints = new ConcurrentHashMap<>();
    }

    public Map<String, ITOpsMonitoredEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, ITOpsMonitoredEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public void removeEndpoint(String componentID){
        if(endpoints.containsKey(componentID)){
            endpoints.remove(componentID);
        }
    }

    public void addEndpoint(ITOpsMonitoredEndpoint endpoint){
        removeEndpoint(endpoint.getComponentID());
        endpoints.put(endpoint.getComponentID(), endpoint);
    }

    @Override
    public String toString() {
        return "ITOpsMonitoredWUP{" +
                "endpoints=" + endpoints +
                ", nodeName='" + getComponentID() + '\'' +
                ", nodeVersion='" + getNodeVersion() + '\'' +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode='" + getConcurrencyMode() + '\'' +
                ", resilienceMode='" + getResilienceMode() + '\'' +
                '}';
    }
}
