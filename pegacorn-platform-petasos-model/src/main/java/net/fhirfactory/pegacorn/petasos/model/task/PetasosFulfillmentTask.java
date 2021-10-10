/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;

public class PetasosFulfillmentTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;
    private SerializableObject taskFulfillmentLock;

    private TaskIdType actionableTaskId;
    private SerializableObject actionableTaskIdLock;

    //
    // Constructor(s)
    //

    public PetasosFulfillmentTask(){
        super();
        this.actionableTaskId = null;
        this.actionableTaskIdLock = new SerializableObject();
        this.taskFulfillment = null;
        this.taskFulfillmentLock = new SerializableObject();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasFulfillmentSegment(){
        boolean hasValue = this.taskFulfillment != null;
        return(hasValue);
    }

    public TaskFulfillmentType getFulfillmentSegment() {
        return taskFulfillment;
    }

    public void setFulfillmentSegment(TaskFulfillmentType petasosTaskFulfillment) {
        this.taskFulfillment = petasosTaskFulfillment;
    }

    public SerializableObject getTaskFulfillmentLock() {
        return taskFulfillmentLock;
    }

    public void setTaskFulfillmentLock(SerializableObject taskFulfillmentLock) {
        this.taskFulfillmentLock = taskFulfillmentLock;
    }

    @JsonIgnore
    public boolean hasFulfilledTaskIdentitySegment(){
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getFulfilledTaskIdentitySegment() {
        return actionableTaskId;
    }

    public void setFulfilledTaskIdentitySegment(TaskIdType fulfilledTaskPetasosTaskIdentity) {
        this.actionableTaskId = fulfilledTaskPetasosTaskIdentity;
    }

    public SerializableObject getActionableTaskIdLock() {
        return actionableTaskIdLock;
    }

    public void setActionableTaskIdLock(SerializableObject actionableTaskIdLock) {
        this.actionableTaskIdLock = actionableTaskIdLock;
    }

    @Override
    public String toString() {
        return "PetasosFulfillmentTask{" +
                "taskFulfillment=" + taskFulfillment +
                ", actionableTaskId=" + actionableTaskId +
                ", taskId=" + getTaskId() +
                ", taskWorkItem=" + getTaskWorkItem() +
                ", taskTraceability=" + getTaskTraceability() +
                ", taskOutcomeStatus=" + getTaskOutcomeStatus() +
                ", isRegistered=" + isRegistered() +
                '}';
    }
}
