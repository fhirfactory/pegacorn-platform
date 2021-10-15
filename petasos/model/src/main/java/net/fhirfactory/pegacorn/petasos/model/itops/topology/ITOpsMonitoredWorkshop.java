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

public class ITOpsMonitoredWorkshop extends ITOpsMonitoredNode {
    private Map<String, ITOpsMonitoredWUP> workUnitProcessors;

    public ITOpsMonitoredWorkshop(){
        this.workUnitProcessors = new ConcurrentHashMap<>();
    }

    public Map<String, ITOpsMonitoredWUP> getWorkUnitProcessors() {
        return workUnitProcessors;
    }

    public void setWorkUnitProcessors(Map<String, ITOpsMonitoredWUP> workUnitProcessors) {
        this.workUnitProcessors = workUnitProcessors;
    }

    public void removeWorkUnitProcessor(String componentID){
        if(workUnitProcessors.containsKey(componentID)){
            workUnitProcessors.remove(componentID);
        }
    }

    public void addWorkUnitProcessor(ITOpsMonitoredWUP wup){
        removeWorkUnitProcessor(wup.getComponentID());
        workUnitProcessors.put(wup.getComponentID(), wup);
    }

    @Override
    public String toString() {
        return "ITOpsMonitoredWorkshop{" +
                "workUnitProcessors=" + workUnitProcessors +
                ", nodeName='" + getComponentID() + '\'' +
                ", nodeVersion='" + getNodeVersion() + '\'' +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode='" + getConcurrencyMode() + '\'' +
                ", resilienceMode='" + getResilienceMode() + '\'' +
                '}';
    }
}