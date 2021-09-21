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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ITOpsTopologyGraph {
    private String deploymentName;
    private Map<String, ITOpsMonitoredProcessingPlant> processingPlants;

    public ITOpsTopologyGraph(){
        processingPlants = new ConcurrentHashMap<>();
    }

    public Map<String, ITOpsMonitoredProcessingPlant> getProcessingPlants() {
        return processingPlants;
    }

    public void setProcessingPlants(Map<String, ITOpsMonitoredProcessingPlant> processingPlants) {
        this.processingPlants = processingPlants;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public void addProcessingPlant(ITOpsMonitoredProcessingPlant processingPlant){
        removeProcessingPlant(processingPlant.getComponentID());
        processingPlants.put(processingPlant.getComponentID(), processingPlant);
    }

    public void removeProcessingPlant(String componentID){
        if(processingPlants.containsKey(componentID)){
            processingPlants.remove(componentID);
        }
    }

    @Override
    public String toString() {
        return "ITOpsTopologyMap{" +
                "deploymentName='" + deploymentName + '\'' +
                ", processingPlants=" + processingPlants +
                '}';
    }
}
