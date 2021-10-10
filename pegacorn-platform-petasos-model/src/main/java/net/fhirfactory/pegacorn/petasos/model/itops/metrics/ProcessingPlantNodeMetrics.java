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
package net.fhirfactory.pegacorn.petasos.model.itops.metrics;

import net.fhirfactory.pegacorn.petasos.model.itops.metrics.common.NodeMetricsBase;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.HashMap;

public class ProcessingPlantNodeMetrics extends NodeMetricsBase {

    public static final String PROCESSING_PLANT_METRICS_TYPE = "ProcessingPlantBasedMetrics";

    private HashMap<String, String> localCacheStatusMap;
    private int synchronousAuditEventsWritten;
    private int asynchronousAuditEventsWritten;
    private int asynchronousAuditEventsQueued;
    private Instant lastAsynchronousAuditEventWrite;

    //
    // Constructors
    //

    public ProcessingPlantNodeMetrics(){
        super();
        this.localCacheStatusMap = new HashMap<>();
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.lastAsynchronousAuditEventWrite = Instant.EPOCH;
    }

    public ProcessingPlantNodeMetrics(String componentID){
        super(componentID);
        this.localCacheStatusMap = new HashMap<>();
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.lastAsynchronousAuditEventWrite = Instant.EPOCH;
    }

    //
    // Helpers
    //

    public void updateLocalCacheStatus(String cacheName, String cacheStatus){
        if(StringUtils.isEmpty(cacheName) || StringUtils.isEmpty(cacheStatus)){
            return;
        }
        if(this.localCacheStatusMap.containsKey(cacheName)){
            this.localCacheStatusMap.remove(cacheName);
        }
        this.localCacheStatusMap.put(cacheName, cacheStatus);
    }

    public void incrementSynchronousAuditEventWritten(){
        this.synchronousAuditEventsWritten += 1;
    }

    public void incrementAsynchronousAuditEventWritten(){
        this.asynchronousAuditEventsWritten += 1;
    }

    public void touchAsynchronousAuditEventWrite(){
        this.lastAsynchronousAuditEventWrite = Instant.now();
    }

    //
    // Getters and Setters
    //


    public HashMap<String, String> getLocalCacheStatusMap() {
        return localCacheStatusMap;
    }

    public void setLocalCacheStatusMap(HashMap<String, String> localCacheStatusMap) {
        this.localCacheStatusMap = localCacheStatusMap;
    }

    public int getSynchronousAuditEventsWritten() {
        return synchronousAuditEventsWritten;
    }

    public void setSynchronousAuditEventsWritten(int synchronousAuditEventsWritten) {
        this.synchronousAuditEventsWritten = synchronousAuditEventsWritten;
    }

    public int getAsynchronousAuditEventsWritten() {
        return asynchronousAuditEventsWritten;
    }

    public void setAsynchronousAuditEventsWritten(int asynchronousAuditEventsWritten) {
        this.asynchronousAuditEventsWritten = asynchronousAuditEventsWritten;
    }

    public int getAsynchronousAuditEventsQueued() {
        return asynchronousAuditEventsQueued;
    }

    public void setAsynchronousAuditEventsQueued(int asynchronousAuditEventsQueued) {
        this.asynchronousAuditEventsQueued = asynchronousAuditEventsQueued;
    }

    public Instant getLastAsynchronousAuditEventWrite() {
        return lastAsynchronousAuditEventWrite;
    }

    public void setLastAsynchronousAuditEventWrite(Instant lastAsynchronousAuditEventWrite) {
        this.lastAsynchronousAuditEventWrite = lastAsynchronousAuditEventWrite;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "ProcessingPlantNodeMetrics{" +
                "localCacheStatusMap=" + localCacheStatusMap +
                ", synchronousAuditEventsWritten=" + synchronousAuditEventsWritten +
                ", asynchronousAuditEventsWritten=" + asynchronousAuditEventsWritten +
                ", asynchronousAuditEventsQueued=" + asynchronousAuditEventsQueued +
                ", lastAsynchronousAuditEventWrite=" + lastAsynchronousAuditEventWrite +
                ", componentID='" + getComponentID() + '\'' +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", nodeStartupInstant=" + getNodeStartupInstant() +
                ", nodeStatus='" + getNodeStatus() + '\'' +
                ", metricsType='" + getMetricsType() + '\'' +
                '}';
    }
}
