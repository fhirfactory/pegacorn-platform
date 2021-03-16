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

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;

/**
 * @author Mark A. Hunter
 */
public class UoWPayload {
    private static final Logger LOG = LoggerFactory.getLogger(UoWPayload.class);
    private TopicToken payloadTopicID;
    private String payload;

    public UoWPayload() {
        payload = null;
        payloadTopicID = null;
    }

    public UoWPayload(UoWPayload originalUoWPayload) {
        payload = new String(originalUoWPayload.getPayload());
        TopicToken newToken = new TopicToken();
        FDN newTokenFDN = new FDN(originalUoWPayload.getPayloadTopicID().getIdentifier());
        newToken.setIdentifier(newTokenFDN.getToken());
        newToken.setVersion(new String(originalUoWPayload.getPayloadTopicID().getVersion()));
        payloadTopicID = newToken;
    }

    public UoWPayload(TopicToken payloadType, String payloadContent){
        TopicToken newToken = new TopicToken();
        FDN newTokenFDN = new FDN(payloadType.getIdentifier());
        newToken.setIdentifier(newTokenFDN.getToken());
        newToken.setVersion(new String(payloadType.getVersion()));
        payloadTopicID = newToken;
        this.payload = new String(payloadContent);
    }

    public String getPayload() {
        LOG.debug(".getPayload(): Entry");
        LOG.debug(".getPayload(): Exit, returning Payload (String) --> {}", this.payload);
        return payload;
    }

    public void setPayload(String payload) {
        LOG.debug(".setPayload(): Entry, payload (String) --> {}", payload);
        this.payload = new String(payload);
    }

    public TopicToken getPayloadTopicID() {
        LOG.debug(".getPayloadTopicID(): Entry");
        LOG.debug(".getPayloadTopicID(): Exit, returning Payload (String) --> {}", this.payloadTopicID);
        return payloadTopicID;
    }

    public void setPayloadTopicID(TopicToken payloadTopicID) {
        LOG.debug(".setPayloadTopicID(): Entry, payloadTopicID (TopicToken) --> {}", payloadTopicID);
        TopicToken newToken = new TopicToken();
        FDN newTokenFDN = new FDN(payloadTopicID.getIdentifier());
        newToken.setIdentifier(newTokenFDN.getToken());
        newToken.setVersion(new String(payloadTopicID.getVersion()));
        this.payloadTopicID = newToken;
    }

    @Override
    public String toString(){
        String payloadToString = "UoWPayload{" +
                "(payloadTopicID="+ payloadTopicID + ")," +
                "(payload=" +payload + ")" +
                "}";
        return(payloadToString);
    }
}
