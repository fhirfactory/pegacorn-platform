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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;

import java.io.Serializable;
import java.util.Objects;

public class DistributedPubSubParticipantIdentifier implements Serializable {
    private String subsystemName;
    private String subsystemInstanceName;
    private NetworkSecurityZoneEnum securityZone;

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getSubsystemInstanceName() {
        return subsystemInstanceName;
    }

    public void setSubsystemInstanceName(String subsystemInstanceName) {
        this.subsystemInstanceName = subsystemInstanceName;
    }

    @JsonIgnore
    public String getJoinedName(){
        String joinedName = subsystemName + "-" + subsystemInstanceName;
        return(joinedName);
    }

    @Override
    public String toString() {
        return "PubSubMemberIdentifier{" +
                "subsystemName='" + subsystemName + '\'' +
                ", subsystemInstanceName='" + subsystemInstanceName + '\'' +
                ", networkZone'" + securityZone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DistributedPubSubParticipantIdentifier)) return false;
        DistributedPubSubParticipantIdentifier that = (DistributedPubSubParticipantIdentifier) o;
        return Objects.equals(getSubsystemName(), that.getSubsystemName()) && Objects.equals(getSubsystemInstanceName(), that.getSubsystemInstanceName()) && Objects.equals(getSecurityZone(), that.getSecurityZone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubsystemName(), getSubsystemInstanceName(), getSecurityZone());
    }

    public NetworkSecurityZoneEnum getSecurityZone() {
        return securityZone;
    }

    public void setSecurityZone(NetworkSecurityZoneEnum securityZone) {
        this.securityZone = securityZone;
    }
}
