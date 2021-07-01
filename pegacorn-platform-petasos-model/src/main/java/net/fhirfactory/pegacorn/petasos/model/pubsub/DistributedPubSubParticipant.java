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

import java.io.Serializable;
import java.util.Objects;

public class DistributedPubSubParticipant implements Serializable {
    private DistributedPubSubParticipantIdentifier identifier;
    private PubSubSubscriptionStatusEnum membershipStatus;
    private EffectivePeriod membershipPeriod;

    public DistributedPubSubParticipant(){
        this.identifier = null;
        this.membershipStatus = PubSubSubscriptionStatusEnum.SUBSCRIPTION_UNKNOWN;
        this.membershipPeriod = null;
    }

    public DistributedPubSubParticipant(DistributedPubSubParticipant ori){
        this.setIdentifier(ori.getIdentifier());
        this.setMembershipStatus(ori.getMembershipStatus());
        this.setMembershipPeriod(ori.getMembershipPeriod());
    }

    public PubSubSubscriptionStatusEnum getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(PubSubSubscriptionStatusEnum membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public EffectivePeriod getMembershipPeriod() {
        return membershipPeriod;
    }

    public void setMembershipPeriod(EffectivePeriod membershipPeriod) {
        this.membershipPeriod = membershipPeriod;
    }

    public DistributedPubSubParticipantIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(DistributedPubSubParticipantIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "PubSubMember{" +
                "identifier=" + getIdentifier() +
                ", membershipStatus=" + membershipStatus +
                ", membershipPeriod=" + membershipPeriod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DistributedPubSubParticipant)) return false;
        DistributedPubSubParticipant that = (DistributedPubSubParticipant) o;
        return Objects.equals(getIdentifier(), that.getIdentifier()) && membershipStatus == that.membershipStatus && Objects.equals(membershipPeriod, that.membershipPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), membershipStatus, membershipPeriod);
    }
}
