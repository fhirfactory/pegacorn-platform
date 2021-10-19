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
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;

public class PetasosFulfillmentTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;
    private SerializableObject taskFulfillmentLock;

    private PetasosTaskJobCard taskJobCard;
    private SerializableObject taskJobCardLock;

    private TaskIdType actionableTaskId;
    private SerializableObject actionableTaskIdLock;

    private boolean aRetry;

    //
    // Constructor(s)
    //

    public PetasosFulfillmentTask(){
        super();
        this.actionableTaskId = null;
        this.actionableTaskIdLock = new SerializableObject();
        this.taskFulfillment = null;
        this.taskFulfillmentLock = new SerializableObject();
        this.taskJobCard = null;
        this.taskJobCardLock = new SerializableObject();
        this.aRetry = false;
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.FULFILLMENT_TASK_TYPE));
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasTaskFulfillment(){
        boolean hasValue = this.taskFulfillment != null;
        return(hasValue);
    }

    public TaskFulfillmentType getTaskFulfillment() {
        return taskFulfillment;
    }

    public void setTaskFulfillment(TaskFulfillmentType taskFulfillment) {
        this.taskFulfillment = taskFulfillment;
    }

    public SerializableObject getTaskFulfillmentLock() {
        return taskFulfillmentLock;
    }

    public void setTaskFulfillmentLock(SerializableObject taskFulfillmentLock) {
        this.taskFulfillmentLock = taskFulfillmentLock;
    }

    @JsonIgnore
    public boolean hasActionableTaskId(){
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType fulfilledTaskPetasosTaskIdentity) {
        this.actionableTaskId = fulfilledTaskPetasosTaskIdentity;
    }

    public SerializableObject getActionableTaskIdLock() {
        return actionableTaskIdLock;
    }

    public void setActionableTaskIdLock(SerializableObject actionableTaskIdLock) {
        this.actionableTaskIdLock = actionableTaskIdLock;
    }

    @JsonIgnore
    public boolean hasTaskJobCard(){
        boolean hasValue = this.taskJobCard != null;
        return(hasValue);
    }

    public PetasosTaskJobCard getTaskJobCard() {
        return taskJobCard;
    }

    public void setTaskJobCard(PetasosTaskJobCard taskJobCard) {
        this.taskJobCard = taskJobCard;
    }

    public SerializableObject getTaskJobCardLock() {
        return taskJobCardLock;
    }

    public void setTaskJobCardLock(SerializableObject taskJobCardLock) {
        this.taskJobCardLock = taskJobCardLock;
    }

    public boolean isaRetry() {
        return aRetry;
    }

    public void setaRetry(boolean aRetry) {
        this.aRetry = aRetry;
    }

    //
    // ToString
    //

    @Override
    public String toString() {
        return "PetasosFulfillmentTask{" +
                "taskFulfillment=" + taskFulfillment +
                ", taskJobCard=" + taskJobCard +
                ", actionableTaskId=" + actionableTaskId +
                ", taskId=" + getTaskId() +
                ", hasTaskType=" + hasTaskType() +
                ", taskType=" + getTaskType() +
                ", taskWorkItem=" + getTaskWorkItem() +
                ", taskTraceability=" + getTaskTraceability() +
                ", taskOutcomeStatus=" + getTaskOutcomeStatus() +
                ", registered=" + isRegistered() +
                ", taskPerformerTypes=" + getTaskPerformerTypes() +
                ", isARetry=" + isaRetry() +
                '}';
    }
}
