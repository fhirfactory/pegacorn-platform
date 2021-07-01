/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.model.uow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelQualityStatement;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Mark A. Hunter
 */
public class UoWPayload implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(UoWPayload.class);
    private DataParcelManifest payloadManifest;
    private String payload;

    public UoWPayload() {
        payload = null;
        payloadManifest = null;
    }

    public UoWPayload(UoWPayload originalUoWPayload) {
        this.payload = (String)SerializationUtils.clone(originalUoWPayload.getPayload());
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(originalUoWPayload.getPayloadManifest());
    }

    public UoWPayload(DataParcelManifest payloadType, String payloadContent){
        this.payload = (String)SerializationUtils.clone(payloadContent);
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(payloadType);
    }

    public String getPayload() {
        LOG.debug(".getPayload(): Entry");
        LOG.debug(".getPayload(): Exit, returning Payload (String) --> {}", this.payload);
        return payload;
    }

    public void setPayload(String payload) {
        LOG.debug(".setPayload(): Entry, payload (String) --> {}", payload);
        this.payload = (String) SerializationUtils.clone(payload);
    }

    public DataParcelManifest getPayloadManifest() {
        LOG.debug(".getPayloadTopicID(): Entry");
        LOG.debug(".getPayloadTopicID(): Exit, returning Payload (String) --> {}", this.payloadManifest);
        return payloadManifest;
    }

    public void setPayloadManifest(DataParcelManifest payloadManifest) {
        LOG.debug(".setPayloadTopicID(): Entry, payloadTopicID (TopicToken) --> {}", payloadManifest);
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(payloadManifest);
    }

    public boolean hasDataParcelQualityStatement(){
        if(payloadManifest == null){
            return(false);
        }
        return(payloadManifest.hasDataParcelQualityStatement());
    }

    @JsonIgnore
    public DataParcelQualityStatement getPayloadQuality() {
        LOG.debug(".getPayloadQuality(): Entry");
        if(hasDataParcelQualityStatement()){
            LOG.debug(".getPayloadQuality(): Exit, returning payloadQuality->{}", this.getPayloadManifest().getPayloadQuality());
            return(this.getPayloadManifest().getPayloadQuality());
        } else {
            LOG.debug(".getPayloadQuality(): Exit, no payloadQuality statement available");
            return (null);
        }
    }

    @Override
    public String toString() {
        return "UoWPayload{" +
                "payloadManifest=" + payloadManifest +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UoWPayload)) return false;
        UoWPayload that = (UoWPayload) o;
        return Objects.equals(getPayloadManifest(), that.getPayloadManifest()) && Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayloadManifest(), getPayload());
    }
}
