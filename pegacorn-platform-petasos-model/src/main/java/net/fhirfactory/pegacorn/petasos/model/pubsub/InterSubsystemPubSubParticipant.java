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

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class InterSubsystemPubSubParticipant extends PetasosEndpoint implements Serializable {
    private PubSubParticipantUtilisationStatusEnum connectionStatus;
    private Date utilisationUpdateDate;

    public InterSubsystemPubSubParticipant(){
        super();
        this.connectionStatus = PubSubParticipantUtilisationStatusEnum.PUB_SUB_PARTICIPANT_NO_SUBSCRIBERS;
        this.utilisationUpdateDate = null;
    }

    public InterSubsystemPubSubParticipant(PetasosEndpoint ori){
        super(ori);
        this.connectionStatus = PubSubParticipantUtilisationStatusEnum.PUB_SUB_PARTICIPANT_NO_SUBSCRIBERS;
        this.utilisationUpdateDate = Date.from(Instant.now());
    }

    public InterSubsystemPubSubParticipant(InterSubsystemPubSubParticipant ori){
        this.setEndpointDescription(ori.getEndpointDescription());
        this.setEndpointID(ori.getEndpointID());
        this.setInterfaceFunction(ori.getInterfaceFunction());
        this.setEndpointServiceName(ori.getEndpointServiceName());
        this.setRepresentativeFHIREndpoint(ori.getRepresentativeFHIREndpoint());
        this.setEndpointStatus(ori.getEndpointStatus());
        this.setUtilisationStatus(ori.getConnectionStatus());
        this.setUtilisationUpdateDate(ori.getUtilisationUpdateDate());
    }

    public PubSubParticipantUtilisationStatusEnum getConnectionStatus() {
        return connectionStatus;
    }

    public void setUtilisationStatus(PubSubParticipantUtilisationStatusEnum connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Date getUtilisationUpdateDate() {
        return utilisationUpdateDate;
    }

    public void setUtilisationUpdateDate(Date utilisationUpdateDate) {
        this.utilisationUpdateDate = utilisationUpdateDate;
    }

    @Override
    public String toString() {
        return "InterSubsystemPubSubParticipant{" +
                "endpointScope=" + getEndpointScope() +
                ", interfaceFunction=" + getInterfaceFunction() +
                ", endpointServiceName=" + getEndpointServiceName() +
                ", representativeFHIREndpoint=" + getRepresentativeFHIREndpoint() +
                ", endpointStatus=" + getEndpointStatus() +
                ", endpointDescription=" + getEndpointDescription() +
                ", endpointID=" + getEndpointID() +
                ", connectionStatus=" + connectionStatus +
                ", utilisationUpdateDate=" + utilisationUpdateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterSubsystemPubSubParticipant)) return false;
        if (!super.equals(o)) return false;
        InterSubsystemPubSubParticipant that = (InterSubsystemPubSubParticipant) o;
        return getConnectionStatus() == that.getConnectionStatus() && Objects.equals(getUtilisationUpdateDate(), that.getUtilisationUpdateDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getConnectionStatus(), getUtilisationUpdateDate());
    }
}
