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

public class PubSubPublisher implements Serializable {
    private LocalPubSubPublisher localPublisher;
    private DistributedPubSubPublisher distributedPublisher;

    public PubSubPublisher(){
        super();
        this.localPublisher = null;
        this.distributedPublisher = null;
    }

    public PubSubPublisher(TopologyNodeFDNToken localWUP){
        super();
        EffectivePeriod period = new EffectivePeriod();
        period.setEffectiveStartDate(Date.from(Instant.now()));
        this.setDistributedPublisher(null);
        LocalPubSubPublisher localSubscriber = new LocalPubSubPublisher(localWUP);
        this.setLocalPublisher(localSubscriber);
    }

    public LocalPubSubPublisher getLocalPublisher() {
        return localPublisher;
    }

    public void setLocalPublisher(LocalPubSubPublisher localPublisher) {
        this.localPublisher = localPublisher;
    }

    public DistributedPubSubPublisher getDistributedPublisher() {
        return distributedPublisher;
    }

    public void setDistributedPublisher(DistributedPubSubPublisher distributedPublisher) {
        this.distributedPublisher = distributedPublisher;
    }

    public boolean hasRemotePublisher(){
        boolean hasRS = this.distributedPublisher != null;
        return(hasRS);
    }

    public boolean hasLocalPublisher(){
        boolean hasLS = this.localPublisher != null;
        return(hasLS);
    }

    @Override
    public String toString() {
        return "PubSubPublisher{" +
                "localPublisher=" + localPublisher +
                ", distributedPublisher=" + distributedPublisher +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PubSubPublisher)) return false;
        PubSubPublisher that = (PubSubPublisher) o;
        return Objects.equals(getLocalPublisher(), that.getLocalPublisher()) && Objects.equals(getDistributedPublisher(), that.getDistributedPublisher());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocalPublisher(), getDistributedPublisher());
    }
}
