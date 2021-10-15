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

package net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskStatusType implements Serializable {

    private FulfillmentExecutionStatusEnum fulfillmentExecutionStatus;
    private boolean beingFulfilled;
    private Integer retryCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant entryInstant;


    //
    // Constructor(s)
    //

    public TaskStatusType() {
        this.entryInstant = Instant.now();
        this.fulfillmentExecutionStatus = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.retryCount = 0;
        this.beingFulfilled = false;

    }

    //
    // Getters and Setters
    //

    public boolean hasFulfillmentExecutionStatus(){
        boolean hasValue = this.fulfillmentExecutionStatus != null;
        return(hasValue);
    }

    public FulfillmentExecutionStatusEnum getFulfillmentExecutionStatus() {
        return fulfillmentExecutionStatus;
    }

    public void setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum fulfillmentExecutionStatus) {
        this.fulfillmentExecutionStatus = fulfillmentExecutionStatus;
    }

    public boolean isBeingFulfilled() {
        return beingFulfilled;
    }

    public void setBeingFulfilled(boolean beingFulfilled) {
        this.beingFulfilled = beingFulfilled;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public boolean hasEntryInstant(){
        boolean hasValue = this.entryInstant != null;
        return(hasValue);
    }

    public Instant getEntryInstant() {
        return entryInstant;
    }

    public void setEntryInstant(Instant entryInstant) {
        this.entryInstant = entryInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "StatusSegment{" +
                "fulfillmentExecutionStatus=" + fulfillmentExecutionStatus +
                ", beingFulfilled=" + beingFulfilled +
                ", retryCount=" + retryCount +
                ", entryInstant=" + entryInstant +
                '}';
    }
}
