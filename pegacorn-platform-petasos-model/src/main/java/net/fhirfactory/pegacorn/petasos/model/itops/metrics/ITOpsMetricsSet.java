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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITOpsMetricsSet implements Serializable {
    private Instant timestamp;
    private String componentID;
    private Map<String, ITOpsMetric> metrics;

    public ITOpsMetricsSet(){
        this.timestamp = Instant.now();
        this.metrics = new HashMap<>();
        this.componentID = null;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, ITOpsMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, ITOpsMetric> metrics) {
        this.metrics = metrics;
    }

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    @JsonIgnore
    public void addMetric(ITOpsMetric newMetric){
        removeMetric(newMetric);
        this.getMetrics().put(newMetric.getName().getDisplayName(), newMetric);
    }

    @JsonIgnore
    public void removeMetric(ITOpsMetric oldMetric){
        if(getMetrics().containsKey(oldMetric.getName())){
            getMetrics().remove(oldMetric.getName());
        }
    }
}
