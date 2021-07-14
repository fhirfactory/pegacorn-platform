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

import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class InterSubsystemPubSubParticipant implements Serializable {
    private InterSubsystemPubSubParticipantIdentifier identifier;
    private NetworkSecurityZoneEnum securityZone;
    private String site;
    private PubSubNetworkConnectionStatusEnum connectionStatus;
    private Date connectionEstablishmentDate;

    public InterSubsystemPubSubParticipant(){
        this.identifier = null;
        this.connectionStatus = PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_NOT_ESTABLISHED;
        this.connectionEstablishmentDate = null;
        connectionEstablishmentDate = null;
    }

    public InterSubsystemPubSubParticipant(InterSubsystemPubSubParticipant ori){
        this.setIdentifier(ori.getIdentifier());
        this.setConnectionStatus(ori.getConnectionStatus());
        this.setConnectionEstablishmentDate(ori.getConnectionEstablishmentDate());
    }

    public PubSubNetworkConnectionStatusEnum getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(PubSubNetworkConnectionStatusEnum connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Date getConnectionEstablishmentDate() {
        return connectionEstablishmentDate;
    }

    public void setConnectionEstablishmentDate(Date connectionEstablishmentDate) {
        this.connectionEstablishmentDate = connectionEstablishmentDate;
    }

    public InterSubsystemPubSubParticipantIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(InterSubsystemPubSubParticipantIdentifier identifier) {
        this.identifier = identifier;
    }

    public NetworkSecurityZoneEnum getSecurityZone() {
        return securityZone;
    }

    public void setSecurityZone(NetworkSecurityZoneEnum securityZone) {
        this.securityZone = securityZone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterSubsystemPubSubParticipant)) return false;
        InterSubsystemPubSubParticipant that = (InterSubsystemPubSubParticipant) o;
        return Objects.equals(getIdentifier(), that.getIdentifier()) && getSecurityZone() == that.getSecurityZone() && Objects.equals(getSite(), that.getSite()) && getConnectionStatus() == that.getConnectionStatus() && Objects.equals(getConnectionEstablishmentDate(), that.getConnectionEstablishmentDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), getSecurityZone(), getSite(), getConnectionStatus(), getConnectionEstablishmentDate());
    }

    @Override
    public String toString() {
        return "DistributedPubSubParticipant{" +
                "identifier=" + identifier +
                ", securityZone=" + securityZone +
                ", site='" + site + '\'' +
                ", connectionStatus=" + connectionStatus +
                ", connectionEstablishmentDate=" + connectionEstablishmentDate +
                '}';
    }
}
