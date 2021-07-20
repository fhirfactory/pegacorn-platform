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

import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.util.Date;

public class InterSubsystemPubSubPublisherRegistration implements Serializable {
    private InterSubsystemPubSubParticipant publisher;
    private InterSubsystemPubSubPublisherStatusEnum publisherStatus;
    private String registrationCommentary;
    private Date registrationDate;
    private Date lastActivityDate;
    private SerializableObject lock;

    public InterSubsystemPubSubPublisherRegistration(){
        this.publisher = null;
        this.registrationDate = null;
        this.lastActivityDate = null;
        this.registrationCommentary = null;
        this.publisherStatus = null;
        this.lock = new SerializableObject();
    }

    //
    // Getters and Setters
    //

    public InterSubsystemPubSubParticipant getPublisher() {
        return publisher;
    }

    public void setPublisher(InterSubsystemPubSubParticipant publisher) {
        this.publisher = publisher;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public String getRegistrationCommentary() {
        return registrationCommentary;
    }

    public void setRegistrationCommentary(String registrationCommentary) {
        this.registrationCommentary = registrationCommentary;
    }

    public InterSubsystemPubSubPublisherStatusEnum getPublisherStatus() {
        return publisherStatus;
    }

    public void setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum publisherStatus) {
        this.publisherStatus = publisherStatus;
    }

    //
    // Utility Classes
    //

    @Override
    public String toString() {
        return "DistributedPubSubPublisherRegistration{" +
                "publisher=" + publisher +
                ", publisherStatus=" + publisherStatus +
                ", registrationCommentary='" + registrationCommentary + '\'' +
                ", registrationDate=" + registrationDate +
                ", lastActivityDate=" + lastActivityDate +
                '}';
    }
}
