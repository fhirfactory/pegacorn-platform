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
package net.fhirfactory.pegacorn.petasos.itops.caches;

import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.ITOpsMetric;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.ITOpsMetricsSet;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ITOpsMetricsLocalDM extends ITOpsLocalDMRefreshBase {

    private Map<String, ITOpsMetricsSet> metricsSetMap;
    private Object metricsSetMapLock;

    public ITOpsMetricsLocalDM(){
        this.metricsSetMap = new ConcurrentHashMap<>();
        this.metricsSetMapLock = new Object();
    }

    public Map<String, ITOpsMetricsSet> getMetricsSetMap() {
        return metricsSetMap;
    }

    public void setMetricsSetMap(Map<String, ITOpsMetricsSet> metricsSetMap) {
        this.metricsSetMap = metricsSetMap;
    }

    public void addMetricSet(ITOpsMetricsSet metricSet){
        synchronized (metricsSetMapLock) {
            if (metricsSetMap.containsKey(metricSet.getComponentID())) {
                metricsSetMap.remove(metricSet.getComponentID());
            }
            metricsSetMap.put(metricSet.getComponentID(), metricSet);
            refreshCurrentStateUpdateInstant();
        }
    }

    public void addMetric(String componentID, ITOpsMetric metric){
        synchronized (metricsSetMapLock) {
            if (metricsSetMap.containsKey(componentID)) {
                metricsSetMap.get(componentID).addMetric(metric);
            } else {
                ITOpsMetricsSet newSet = new ITOpsMetricsSet();
                newSet.addMetric(metric);
                newSet.setComponentID(componentID);
                metricsSetMap.put(componentID, newSet);
            }
            refreshCurrentStateUpdateInstant();
        }
    }

    public List<ITOpsMetricsSet> getAllMetricsSets(){
        List<ITOpsMetricsSet> metricsSetList = new ArrayList<>();
        synchronized (metricsSetMapLock){
            metricsSetList.addAll(metricsSetMap.values());
        }
        return(metricsSetList);
    }
}
