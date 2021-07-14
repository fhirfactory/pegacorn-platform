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
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class InterSubsystemPubSubPublisherSubscriptionRegistration implements Serializable {
    private InterSubsystemPubSubParticipant publisher;
    private String publisherServiceName;
    private InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum registrationStatus;
    private List<DataParcelManifest> subscriptionList;
    private String registrationCommentary;
    private Date registrationDate;
    private SerializableObject lock;

    public InterSubsystemPubSubPublisherSubscriptionRegistration(){
        this.publisher = null;
        this.registrationStatus = null;
        this.registrationDate = null;
        this.registrationCommentary = null;
        this.subscriptionList  = new ArrayList<>();
        this.lock = new SerializableObject();
        this.publisherServiceName = null;
    }

    //
    // Business Methods
    //

    public InterSubsystemPubSubPublisherSubscriptionRegistration addSubscriptionList(List<DataParcelManifest> parcelList){
        if(parcelList == null){
            return(this);
        }
        if(parcelList.isEmpty()){
            return(this);
        }
        synchronized (this.lock) {
            for (DataParcelManifest currentManifest : parcelList) {
                if (!subscriptionList.contains(currentManifest)) {
                    subscriptionList.add(currentManifest);
                }
            }
        }
        return(this);
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistration removeSubscriptionList(List<DataParcelManifest> parcelList){
        if(parcelList == null){
            return(this);
        }
        if(parcelList.isEmpty()){
            return(this);
        }
        synchronized (this.lock) {
            for (DataParcelManifest currentManifest : parcelList) {
                DataParcelManifest foundEntry = null;
                for (DataParcelManifest currentSubscriptionListEntry : this.getSubscriptionList()) {
                    if (currentSubscriptionListEntry.equals(currentManifest)) {
                        foundEntry = currentSubscriptionListEntry;
                        break;
                    }
                }
                if (foundEntry != null) {
                    this.getSubscriptionList().remove(foundEntry);
                }
            }
        }
        return(this);
    }

    //
    // Getters and Setters
    //


    public String getPublisherServiceName() {
        return publisherServiceName;
    }

    public void setPublisherServiceName(String publisherServiceName) {
        this.publisherServiceName = publisherServiceName;
    }

    public InterSubsystemPubSubParticipant getPublisher() {
        return publisher;
    }

    public void setPublisher(InterSubsystemPubSubParticipant publisher) {
        this.publisher = publisher;
    }

    public InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationCommentary() {
        return registrationCommentary;
    }

    public void setRegistrationCommentary(String registrationCommentary) {
        this.registrationCommentary = registrationCommentary;
    }

    public List<DataParcelManifest> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<DataParcelManifest> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    //
    // Utility Classes
    //

    @Override
    public String toString() {
        return "DistributedPubSubPublisherSubscriptionRegistration{" +
                "publisher=" + publisher +
                ", publisherService='" + publisherServiceName + '\'' +
                ", registrationStatus=" + registrationStatus +
                ", subscriptionList=" + subscriptionList +
                ", registrationCommentary='" + registrationCommentary + '\'' +
                ", registrationDate=" + registrationDate +
                ", lock=" + lock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterSubsystemPubSubPublisherSubscriptionRegistration)) return false;
        InterSubsystemPubSubPublisherSubscriptionRegistration that = (InterSubsystemPubSubPublisherSubscriptionRegistration) o;
        return Objects.equals(getPublisher(), that.getPublisher()) && Objects.equals(getPublisherServiceName(), that.getPublisherServiceName()) && getRegistrationStatus() == that.getRegistrationStatus() && Objects.equals(getSubscriptionList(), that.getSubscriptionList()) && Objects.equals(getRegistrationCommentary(), that.getRegistrationCommentary()) && Objects.equals(getRegistrationDate(), that.getRegistrationDate()) && Objects.equals(lock, that.lock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublisher(), getPublisherServiceName(), getRegistrationStatus(), getSubscriptionList(), getRegistrationCommentary(), getRegistrationDate(), lock);
    }
}
