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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessingPlantSubscriptionSummary implements Serializable {
    private Map<String, SubscriberSubscriptionSummary> asSubscriber;
    private Map<String, PublisherSubscriptionSummary> asPublisher;
    private String componentID;

    public ProcessingPlantSubscriptionSummary(){
        asPublisher = new HashMap<>();
        asSubscriber = new HashMap<>();
        componentID = "Unknown";
    }

    public Map<String, SubscriberSubscriptionSummary> getAsSubscriber() {
        return asSubscriber;
    }

    public void setAsSubscriber(Map<String, SubscriberSubscriptionSummary> asSubscriber) {
        this.asSubscriber = asSubscriber;
    }

    public Map<String, PublisherSubscriptionSummary> getAsPublisher() {
        return asPublisher;
    }

    public void setAsPublisher(Map<String, PublisherSubscriptionSummary> asPublisher) {
        this.asPublisher = asPublisher;
    }

    public void addPublisherSummary(PublisherSubscriptionSummary publisherSubscriptionSummary){
        if(asPublisher.containsKey(publisherSubscriptionSummary.getSubscriber())){
            asPublisher.remove(publisherSubscriptionSummary.getSubscriber());
        }
        asPublisher.put(publisherSubscriptionSummary.getSubscriber(), publisherSubscriptionSummary);
    }

    public void addSubscriberSummary(SubscriberSubscriptionSummary subscriberSubscriptionSummary){
        if(asSubscriber.containsKey(subscriberSubscriptionSummary.getPublisher())){
            asSubscriber.remove(subscriberSubscriptionSummary.getPublisher());
        }
        asSubscriber.put(subscriberSubscriptionSummary.getPublisher(), subscriberSubscriptionSummary);
    }

    public boolean addSubscriptionForExistingSubscriber(String componentID, String topic){
        if(asPublisher.containsKey(componentID)){
            asPublisher.get(componentID).addTopic(topic);
            return(true);
        } else {
            return(false);
        }
    }

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    @Override
    public String toString() {
        return "ProcessingPlantSubscriptionSummary{" +
                "asSubscriber=" + asSubscriber +
                ", asPublisher=" + asPublisher +
                ", componentID='" + componentID + '\'' +
                '}';
    }
}
