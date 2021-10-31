/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class ParcelStatusElement implements Serializable {

    private ActivityID activityID;
    private SerializableObject activityIDLock;
    private ResilienceParcelProcessingStatusEnum parcelStatus;
    private SerializableObject parcelStatusLock;
    private Integer retryCount;
    private SerializableObject retryCountLock;
    private Date entryDate;
    private SerializableObject entryDateLock;
    private boolean hasClusterFocus;
    private SerializableObject hasClusterFocusLock;
    private boolean hasSystemWideFocus;
    private SerializableObject hasSystemWideFocusLock;
    private boolean requiresRetry;
    private SerializableObject requiresRetryLock;

    public ParcelStatusElement(ActivityID newID) {
        this.activityID = new ActivityID(newID);
        this.entryDate = Date.from(Instant.now());
        this.hasClusterFocus = false;
        this.hasSystemWideFocus = false;
        this.parcelStatus = null;
        this.requiresRetry = false;
        this.parcelStatusLock = new SerializableObject();
        this.activityIDLock = new SerializableObject();
        this.requiresRetryLock = new SerializableObject();
        this.hasClusterFocusLock = new SerializableObject();
        this.entryDateLock = new SerializableObject();
        this.hasSystemWideFocusLock = new SerializableObject();
        this.requiresRetryLock = new SerializableObject();
    }

    public ResilienceParcelIdentifier getParcelInstanceID() {
        return (this.activityID.getPresentParcelIdentifier());
    }

    public TopologyNodeFDNToken getWupInstanceID() {
        return (this.activityID.getPresentWUPIdentifier());
    }

    public TopologyNodeFunctionFDNToken getWUPFunctionToken() {
        return (this.activityID.getPresentWUPFunctionToken());
    }

    public ActivityID getActivityID() {
        return activityID;
    }

    public void setActivityID(ActivityID activityID) {
        synchronized (activityIDLock) {
            this.activityID = activityID;
        }
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        synchronized (entryDateLock) {
            this.entryDate = entryDate;
        }
    }

    public boolean getHasClusterFocus() {
        return(this.hasClusterFocus);
    }

    public void setHasClusterFocus(boolean hasFocus) {
        synchronized (hasClusterFocusLock) {
            this.hasClusterFocus = hasFocus;
        }
    }

    public boolean getHasSystemWideFocus() {
        return this.hasSystemWideFocus;
    }

    public void setHasSystemWideFocus(boolean systemWideFocus) {
        synchronized(hasSystemWideFocusLock) {
            this.hasSystemWideFocus = systemWideFocus;
        }
    }

    public ResilienceParcelProcessingStatusEnum getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(ResilienceParcelProcessingStatusEnum parcelStatus) {
        synchronized (parcelStatusLock) {
            this.parcelStatus = parcelStatus;
        }
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        synchronized (retryCountLock) {
            this.retryCount = retryCount;
        }
    }

    public boolean isRequiresRetry() {
        return requiresRetry;
    }

    public void setRequiresRetry(boolean requiresRetry) {
        synchronized (requiresRetryLock) {
            this.requiresRetry = requiresRetry;
        }
    }
}
