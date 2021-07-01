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
package net.fhirfactory.pegacorn.petasos.model.pubsub;

import net.fhirfactory.pegacorn.common.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DistributedPubSubSubscriber extends DistributedPubSubParticipant implements Serializable {

    private List<DataParcelToken> subscriptionTopics;
    private EffectivePeriod subscriptionEffectiveDate;

    public DistributedPubSubSubscriber(){
        super();
        this.subscriptionTopics = new ArrayList<>();
        this.subscriptionEffectiveDate = null;
    }

    public DistributedPubSubSubscriber(DistributedPubSubSubscriber ori){
        super(ori);
        this.subscriptionTopics = new ArrayList<>();
        this.subscriptionTopics.addAll(ori.getSubscriptionTopics());
        this.subscriptionEffectiveDate = ori.getSubscriptionEffectiveDate();
    }

    public List<DataParcelToken> getSubscriptionTopics() {
        return subscriptionTopics;
    }

    public void setSubscriptionTopics(List<DataParcelToken> subscriptionTopics) {
        this.subscriptionTopics = subscriptionTopics;
    }

    public EffectivePeriod getSubscriptionEffectiveDate() {
        return subscriptionEffectiveDate;
    }

    public void setSubscriptionEffectiveDate(EffectivePeriod subscriptionEffectiveDate) {
        this.subscriptionEffectiveDate = subscriptionEffectiveDate;
    }

    @Override
    public String toString() {
        return "RemoteSubscriber{" +
                "identifier=" + getIdentifier() +
                ", membershipStatus=" + getMembershipStatus() +
                ", membershipPeriod=" + getMembershipPeriod() +
                ", subscriptionTopics=" + getSubscriptionTopics() +
                ", subscriptionEffectiveDate=" + getSubscriptionEffectiveDate() +
                '}';}
}
