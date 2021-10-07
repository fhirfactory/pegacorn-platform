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

public class ProcessingPlantNodeMetrics extends NodeMetricsBase {

    public static final String PROCESSING_PLANT_METRICS_TYPE = "ProcessingPlantBasedMetrics";

    private String resilienceCacheStatus;

    //
    // Constructors
    //

    public ProcessingPlantNodeMetrics(){
        super();
        this.resilienceCacheStatus = null;
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
    }

    public ProcessingPlantNodeMetrics(String componentID){
        super(componentID);
        this.resilienceCacheStatus = null;
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
    }

    //
    // Getters and Setters
    //

    public String getResilienceCacheStatus() {
        return resilienceCacheStatus;
    }

    public void setResilienceCacheStatus(String resilienceCacheStatus) {
        this.resilienceCacheStatus = resilienceCacheStatus;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "ProcessingPlantNodeMetrics{" +
                "resilienceCacheStatus=" + resilienceCacheStatus +
                ", componentID=" + getComponentID() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", nodeStartupInstant=" + getNodeStartupInstant() +
                ", nodeStatus=" + getNodeStatus() +
                ", metricsType=" + getMetricsType() +
                '}';
    }
}
