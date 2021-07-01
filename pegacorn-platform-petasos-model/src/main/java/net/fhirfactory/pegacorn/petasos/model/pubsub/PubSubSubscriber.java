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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.dates.EffectivePeriod;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class PubSubSubscriber implements Serializable {
    private DistributedPubSubSubscriber remoteSubscriber;
    private LocalPubSubSubscriber localSubscriber;
    private PubSubSubscriptionStatusEnum subscriptionStatus;
    private EffectivePeriod subscriptionPeriod;

    public PubSubSubscriber(){
        super();
        this.subscriptionPeriod = null;
        this.remoteSubscriber = null;
        this.localSubscriber = null;
        this.subscriptionStatus = PubSubSubscriptionStatusEnum.SUBSCRIPTION_UNKNOWN;
    }

    public PubSubSubscriber(TopologyNodeFDNToken localWUP){
        super();
        EffectivePeriod period = new EffectivePeriod();
        period.setEffectiveStartDate(Date.from(Instant.now()));
        this.setSubscriptionPeriod(period);
        this.setSubscriptionStatus(PubSubSubscriptionStatusEnum.SUBSCRIPTION_ACTIVE);
        this.setRemoteSubscriber(null);
        LocalPubSubSubscriber localSubscriber = new LocalPubSubSubscriber(localWUP);
        this.setLocalSubscriber(localSubscriber);
    }

    public boolean hasRemoteSubscriber(){
        boolean hasRS = this.remoteSubscriber != null;
        return(hasRS);
    }

    public boolean hasLocalSubscriber(){
        boolean hasLS = this.localSubscriber != null;
        return(hasLS);
    }

    public DistributedPubSubSubscriber getRemoteSubscriber() {
        return remoteSubscriber;
    }

    public void setRemoteSubscriber(DistributedPubSubSubscriber remoteSubscriber) {
        this.remoteSubscriber = remoteSubscriber;
    }

    public LocalPubSubSubscriber getLocalSubscriber() {
        return localSubscriber;
    }

    public void setLocalSubscriber(LocalPubSubSubscriber localSubscriber) {
        this.localSubscriber = localSubscriber;
    }

    public PubSubSubscriptionStatusEnum getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(PubSubSubscriptionStatusEnum subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public EffectivePeriod getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(EffectivePeriod subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    @Override
    public String toString() {
        return "PubSubSubscriber{" +
                "remoteSubscriber=" + remoteSubscriber +
                ", localSubscriber=" + localSubscriber +
                ", subscriptionStatus=" + subscriptionStatus +
                ", subscriptionPeriod=" + subscriptionPeriod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PubSubSubscriber)) return false;
        PubSubSubscriber that = (PubSubSubscriber) o;
        return Objects.equals(getRemoteSubscriber(), that.getRemoteSubscriber()) && Objects.equals(getLocalSubscriber(), that.getLocalSubscriber()) && getSubscriptionStatus() == that.getSubscriptionStatus() && Objects.equals(getSubscriptionPeriod(), that.getSubscriptionPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRemoteSubscriber(), getLocalSubscriber(), getSubscriptionStatus(), getSubscriptionPeriod());
    }
}
