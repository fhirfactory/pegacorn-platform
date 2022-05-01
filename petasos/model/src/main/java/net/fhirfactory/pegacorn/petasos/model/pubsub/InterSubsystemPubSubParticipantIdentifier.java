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

import java.io.Serializable;
import java.util.Objects;

@Deprecated
public class InterSubsystemPubSubParticipantIdentifier implements Serializable {
    private String serviceName;
    private String petasosEndpointName;


    public InterSubsystemPubSubParticipantIdentifier(){
        this.petasosEndpointName = null;
        this.serviceName = null;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPetasosEndpointName() {
        return petasosEndpointName;
    }

    public void setPetasosEndpointName(String petasosEndpointName) {
        this.petasosEndpointName = petasosEndpointName;
    }



    @JsonIgnore
    public String getJoinedName(){
        String joinedName = serviceName + "-" + petasosEndpointName;
        return(joinedName);
    }

    @Override
    public String toString() {
        return "PubSubMemberIdentifier{" +
                "subsystemName=" + serviceName +
                ", subsystemInstanceName=" + petasosEndpointName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterSubsystemPubSubParticipantIdentifier)) return false;
        InterSubsystemPubSubParticipantIdentifier that = (InterSubsystemPubSubParticipantIdentifier) o;
        boolean serviceNameIsEqual = Objects.equals(getServiceName(), that.getServiceName());
        boolean petasosEndpointNameEqual = Objects.equals(getPetasosEndpointName(), that.getPetasosEndpointName());
        return (serviceNameIsEqual && petasosEndpointNameEqual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServiceName(), getPetasosEndpointName());
    }
}
