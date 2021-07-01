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
import java.util.Objects;

public class LocalPubSubParticipant implements Serializable {
    private LocalPubSubParticipantIdentifier identifier;

    //
    // Constructors
    //
    public LocalPubSubParticipant(){
        this.identifier = null;
    }

    public LocalPubSubParticipant(LocalPubSubParticipantIdentifier identifier){
        this.identifier = identifier;
    }

    public LocalPubSubParticipant(TopologyNodeFDNToken wupToken){
        this.identifier = new LocalPubSubParticipantIdentifier(wupToken);
    }

    public LocalPubSubParticipant(LocalPubSubParticipant ori){
        this.identifier = ori.getIdentifier();
    }

    //
    // Getters and Setters
    //

    public LocalPubSubParticipantIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(LocalPubSubParticipantIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalPubSubParticipant)) return false;
        LocalPubSubParticipant that = (LocalPubSubParticipant) o;
        return Objects.equals(getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }

    @Override
    public String toString() {
        return "LocalPubSubParticipant{" +
                "identifier=" + identifier +
                '}';
    }
}
