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
package net.fhirfactory.pegacorn.petasos.model.itops.subscriptions;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ITOpsPubSubReport implements Serializable {
    private Instant timestamp;
    private String processingPlantComponentID;
    private Map<String, ProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet;
    private Map<String, WorkUnitProcessorSubscriptionSummary> wupSubscriptionSummarySet;

    public ITOpsPubSubReport(){
        this.timestamp = Instant.now();
        wupSubscriptionSummarySet = new HashMap<>();
        processingPlantSubscriptionSummarySet = new HashMap<>();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getProcessingPlantComponentID() {
        return processingPlantComponentID;
    }

    public void setProcessingPlantComponentID(String processingPlantComponentID) {
        this.processingPlantComponentID = processingPlantComponentID;
    }

    public Map<String, ProcessingPlantSubscriptionSummary> getProcessingPlantSubscriptionSummarySet() {
        return processingPlantSubscriptionSummarySet;
    }

    public void setProcessingPlantSubscriptionSummarySet(Map<String, ProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet) {
        this.processingPlantSubscriptionSummarySet = processingPlantSubscriptionSummarySet;
    }

    public Map<String, WorkUnitProcessorSubscriptionSummary> getWupSubscriptionSummarySet() {
        return wupSubscriptionSummarySet;
    }

    public void setWupSubscriptionSummarySet(Map<String, WorkUnitProcessorSubscriptionSummary> wupSubscriptionSummarySet) {
        this.wupSubscriptionSummarySet = wupSubscriptionSummarySet;
    }

    public void addWorkUnitProcessorSubscriptionSummary(WorkUnitProcessorSubscriptionSummary wupSummary){
        if(wupSubscriptionSummarySet.containsKey(wupSummary.getComponentID())){
            wupSubscriptionSummarySet.remove(wupSummary.getComponentID());
        }
        wupSubscriptionSummarySet.put(wupSummary.getComponentID(), wupSummary);
    }

    public void addProcessingPlantSubscriptionSummary(ProcessingPlantSubscriptionSummary summary){
        if(processingPlantSubscriptionSummarySet.containsKey(summary.getComponentID())){
            processingPlantSubscriptionSummarySet.remove(summary.getComponentID());
        }
        processingPlantSubscriptionSummarySet.put(summary.getComponentID(), summary);
    }
}
