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
package net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.datatypes.TopicSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.valuesets.SubscriptionSummaryType;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionSummaryBase implements Serializable {
    private SubscriptionSummaryType summaryType;
    private String componentID;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssNNN", timezone = "UTC")
    private Instant timestamp;
    private List<String> subscribedTopics;

    public SubscriptionSummaryBase(){
        subscribedTopics = new ArrayList<>();
    }

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public List<String> getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(List<String> subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }

    @JsonIgnore
    public void addTopic(String topic){
        boolean topicExists = false;
        if(!subscribedTopics.contains(topic)){
            subscribedTopics.add(topic);
        }
    }

    public SubscriptionSummaryType getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(SubscriptionSummaryType summaryType) {
        this.summaryType = summaryType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
