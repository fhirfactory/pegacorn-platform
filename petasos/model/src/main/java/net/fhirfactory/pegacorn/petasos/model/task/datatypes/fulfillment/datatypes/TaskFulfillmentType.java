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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Mark A. Hunter
 */
public class TaskFulfillmentType extends TaskInstantDetailSegmentBase implements Serializable {
    private SoftwareComponent fulfillerComponent;
    private FulfillmentTrackingIdType trackingID;
    private FulfillmentExecutionStatusEnum status;
    private boolean resilientActivity;

    public TaskFulfillmentType(FulfillmentTrackingIdType trackingID, SoftwareComponent fulfillerCommponent, Instant registrationInstant) {
        super();
        this.fulfillerComponent = fulfillerComponent;
        this.trackingID = SerializationUtils.clone(trackingID);
        setRegistrationInstant(SerializationUtils.clone(registrationInstant));
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType(FulfillmentTrackingIdType trackingID, SoftwareComponent fulfillerCommponent) {
        super();
        this.fulfillerComponent = fulfillerComponent;
        this.trackingID = SerializationUtils.clone(trackingID);
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType() {
        super();
        this.fulfillerComponent = null;
        this.trackingID = null;
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType(TaskFulfillmentType ori) {
        super(ori);
        this.fulfillerComponent = null;
        this.trackingID = null;
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;

        // Set Values
        if (ori.hasFulfillerComponent()) {
            this.fulfillerComponent = ori.getFulfillerComponent();
        }
        if (ori.hasTrackingID()) {
            this.trackingID = SerializationUtils.clone(ori.getTrackingID());
        }
        this.status = ori.getStatus();
        this.resilientActivity = ori.isResilientActivity();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasFulfillerComponent(){
        boolean hasValue = this.fulfillerComponent != null;
        return(hasValue);
    }

    public SoftwareComponent getFulfillerComponent() {
        return fulfillerComponent;
    }

    public void setFulfillerComponent(SoftwareComponent fulfillerComponent) {
        this.fulfillerComponent = fulfillerComponent;
    }

    @JsonIgnore
    boolean hasTrackingID(){
        boolean hasValue = this.trackingID != null;
        return(hasValue);
    }

    public FulfillmentTrackingIdType getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(FulfillmentTrackingIdType trackingID) {
        this.trackingID = trackingID;
    }

    @JsonIgnore
    public boolean hasStatus(){
        boolean hasValue = this.status != null;
        return(hasValue);
    }

    public FulfillmentExecutionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(FulfillmentExecutionStatusEnum status) {
        this.status = status;
    }

    public boolean isResilientActivity() {
        return resilientActivity;
    }

    public void setResilientActivity(boolean resilientActivity) {
        this.resilientActivity = resilientActivity;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "FulfillmentSegment{" +
                "registrationInstant=" + getRegistrationInstant() +
                ", readyInstant=" + getReadyInstant() +
                ", startInstant=" + getStartInstant() +
                ", finishInstant=" + getFinishInstant() +
                ", finalisationInstant=" + getFinalisationInstant() +
                ", lastCheckedInstant=" + getLastCheckedInstant() +
                ", fulfillerComponent=" + fulfillerComponent +
                ", trackingID=" + trackingID +
                ", status=" + status +
                ", resilientActivity=" + resilientActivity +
                '}';
    }

    //
    // Legacy Method Support
    //

    //
    // Date/Times

    @JsonIgnore
    public boolean hasCancellationDate(){
        if(status.equals(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED)){
            if(hasFinishInstant()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore
    public Date getCancellationDate() {
        if(hasCancellationDate()) {
            Date date = Date.from(getFinishInstant());
            return (date);
        }
        return(null);
    }

    @JsonIgnore
    public void setCancellationDate(Date newCancellationDate) {
        if(newCancellationDate == null){
            return;
        }
        Instant instant = newCancellationDate.toInstant();
        setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
        setFinishInstant(instant);
    }

    @JsonIgnore
    public boolean hasRegistrationDate() {
        boolean hasValue = hasRegistrationInstant();
        return(hasValue);
    }

    @JsonIgnore
    public Date getRegistrationDate() {
        if(hasRegistrationInstant()) {
            Date date = Date.from(getRegistrationInstant());
            return (date);
        }
        return(null);
    }

    @JsonIgnore
    public void setRegistrationDate(Date registrationDate) {
        if(registrationDate == null){
            return;
        }
        Instant instant = registrationDate.toInstant();
        setRegistrationInstant(instant);
    }

    @JsonIgnore
    public boolean hasStartDate() {
        boolean hasValue = hasStartInstant();
        return(hasValue);
    }

    @JsonIgnore
    public Date getStartDate() {
        if(hasStartInstant()) {
            Date date = Date.from(getStartInstant());
            return (date);
        }
        return(null);
    }

    @JsonIgnore
    public void setStartDate(Date startDate) {
        if(startDate == null){
            return;
        }
        Instant instant = startDate.toInstant();
        setStartInstant(instant);
    }

    @JsonIgnore
    public boolean hasFinishedDate() {
        boolean hasValue = hasFinishInstant();
        return(hasValue);
    }

    @JsonIgnore
    public Date getFinishedDate() {
        if(hasFinishInstant()){
            Date date = Date.from(getFinishInstant());
            return(date);
        }
        return(null);
    }

    @JsonIgnore
    public void setFinishedDate(Date finishedDate) {
        if(finishedDate == null){
            return;
        }
        Instant instant = finishedDate.toInstant();
        setFinishInstant(instant);
    }

    @JsonIgnore
    public boolean hasFinalisationDate() {
        boolean hasValue = hasFinalisationInstant();
        return(hasValue);
    }

    @JsonIgnore
    public Date getFinalisationDate() {
        if(hasFinalisationDate()){
            Date date = Date.from(getFinalisationInstant());
            return(date);
        }
        return(null);
    }

    @JsonIgnore
    public void setFinalisationDate(Date finalisationDate) {
        if(finalisationDate == null){
            return;
        }
        Instant instant = finalisationDate.toInstant();
        setFinalisationInstant(instant);
    }
}
