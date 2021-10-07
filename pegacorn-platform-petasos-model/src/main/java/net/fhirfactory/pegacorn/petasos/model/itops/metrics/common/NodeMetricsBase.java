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
package net.fhirfactory.pegacorn.petasos.model.itops.metrics.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;

public class NodeMetricsBase implements Serializable {
    private String componentID;
    private String metricsType;
    private Instant lastActivityInstant;
    private Instant nodeStartupInstant;
    private String nodeStatus;

    public NodeMetricsBase(){
        this.componentID = null;
        this.lastActivityInstant = Instant.EPOCH;
        this.nodeStatus = null;
        this.nodeStartupInstant = Instant.now();
        this.metricsType = null;
    }

    public NodeMetricsBase(String componentID){
        this.componentID = componentID;
        this.lastActivityInstant = Instant.EPOCH;
        this.nodeStatus = null;
        this.nodeStartupInstant = Instant.now();
        this.metricsType = null;
    }

    //
    // Some Helper Methods
    //

    @JsonIgnore
    public void touchLastActivityInstant(){
        this.lastActivityInstant = Instant.now();
    }

    //
    // Getters and Setters
    //


    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    public Instant getNodeStartupInstant() {
        return nodeStartupInstant;
    }

    public void setNodeStartupInstant(Instant nodeStartupInstant) {
        this.nodeStartupInstant = nodeStartupInstant;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricsType) {
        this.metricsType = metricsType;
    }

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.petasos.model.itops.metrics.common.NodeMetricsBase{" +
                "componentID='" + componentID + '\'' +
                ", metricsType='" + metricsType + '\'' +
                ", lastActivityInstant=" + lastActivityInstant +
                ", nodeStartupInstant=" + nodeStartupInstant +
                ", nodeStatus='" + nodeStatus + '\'' +
                '}';
    }
}
