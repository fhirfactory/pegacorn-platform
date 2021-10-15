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

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class PubSubSubscription implements Serializable {
    private DataParcelManifest parcelManifest;
    private PubSubParticipant subscriber;
    private Instant registrationInstant;

    public PubSubSubscription(){
        this.subscriber = null;
        this.parcelManifest = null;
    }

    public PubSubSubscription(DataParcelManifest parcelManifest, PubSubParticipant subscriber){
        this.subscriber = subscriber;
        this.parcelManifest = parcelManifest;
        this.registrationInstant = Instant.now();
    }

    public DataParcelManifest getParcelManifest() {
        return parcelManifest;
    }

    public void setParcelManifest(DataParcelManifest parcelManifest) {
        this.parcelManifest = parcelManifest;
    }

    public PubSubParticipant getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(PubSubParticipant subscriber) {
        this.subscriber = subscriber;
    }

    public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(Instant registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    @Override
    public String toString() {
        return "PubSubSubscription{" +
                "parcelManifest=" + parcelManifest +
                ", subscriber=" + subscriber +
                ", registrationInstant=" + registrationInstant +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PubSubSubscription)) return false;
        PubSubSubscription that = (PubSubSubscription) o;
        return Objects.equals(getParcelManifest(), that.getParcelManifest()) && Objects.equals(getSubscriber(), that.getSubscriber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParcelManifest(), getSubscriber());
    }
}
