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
package net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.common.model.componentid.ComponentID;
import net.fhirfactory.pegacorn.petasos.model.task.segments.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Mark A. Hunter
 */
public class TaskFulfillmentType extends TaskInstantDetailSegmentBase implements Serializable {
    private ComponentID fulfillerComponentId;
    private FulfillmentTrackingIdType trackingID;
    private FulfillmentExecutionStatusEnum status;
    private boolean resilientActivity;

    public TaskFulfillmentType(FulfillmentTrackingIdType trackingID, ComponentID implementingWUP, Instant registrationInstant) {
        super();
        this.fulfillerComponentId = SerializationUtils.clone(implementingWUP);
        this.trackingID = SerializationUtils.clone(trackingID);
        setRegistrationInstant(SerializationUtils.clone(registrationInstant));
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType(FulfillmentTrackingIdType trackingID, ComponentID implementingWUP) {
        super();
        this.fulfillerComponentId = SerializationUtils.clone(implementingWUP);
        this.trackingID = SerializationUtils.clone(trackingID);
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType() {
        super();
        this.fulfillerComponentId = null;
        this.trackingID = null;
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;
    }

    public TaskFulfillmentType(TaskFulfillmentType ori) {
        super(ori);
        this.fulfillerComponentId = null;
        this.trackingID = null;
        this.status = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.resilientActivity = false;

        // Set Values
        if (ori.hasImplementingWorkUnitProcessID()) {
            this.fulfillerComponentId = SerializationUtils.clone(ori.getFulfillerComponentId());
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
    public boolean hasImplementingWorkUnitProcessID(){
        boolean hasValue = this.fulfillerComponentId != null;
        return(hasValue);
    }

    public ComponentID getFulfillerComponentId() {
        return fulfillerComponentId;
    }

    public void setFulfillerComponentId(ComponentID fulfillerComponentId) {
        this.fulfillerComponentId = fulfillerComponentId;
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
                ", implementingWorkUnitProcessID=" + fulfillerComponentId +
                ", trackingID=" + trackingID +
                ", status=" + status +
                ", resilientActivity=" + resilientActivity +
                '}';
    }
}
