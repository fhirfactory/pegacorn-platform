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
package net.fhirfactory.pegacorn.petasos.itops.collectors.metrics;

import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.common.ITOpsMetricsCollectionAgentBase;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.ProcessingPlantNodeMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessingPlantMetricsCollectionAgent extends ITOpsMetricsCollectionAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantMetricsCollectionAgent.class);


    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Actual Metrics Functions

    //
    public ProcessingPlantNodeMetrics getNodeMetrics(String componentID){
        if(componentID == null){
            throw(new IllegalArgumentException("componentID is null"));
        }
        ProcessingPlantNodeMetrics existingMetrics = (ProcessingPlantNodeMetrics)getMetricsDM().getNodeMetrics(componentID);
        if(existingMetrics == null){
            Object lockObject = new Object();
            ProcessingPlantNodeMetrics metrics = new ProcessingPlantNodeMetrics(componentID);
            synchronized (getMetricsDM().getMetricsSetMapLock()) {
                getMetricsDM().getNodeMetricsLockMap().put(componentID, lockObject);
                getMetricsDM().getNodeMetricsMap().put(componentID, metrics);
            }
            return(metrics);
        } else {
            return(existingMetrics);
        }
    }

    public void updateResilienceCacheMetrics(String componentID, String metricsString){
        getLogger().debug(".updateResilienceCacheMetrics(): Entry, componentID->{}, metricsString->{}", componentID, metricsString);
        ProcessingPlantNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.setResilienceCacheStatus(metricsString);
        }
        getLogger().debug(".updateResilienceCacheMetrics(): Exit");
    }
}
