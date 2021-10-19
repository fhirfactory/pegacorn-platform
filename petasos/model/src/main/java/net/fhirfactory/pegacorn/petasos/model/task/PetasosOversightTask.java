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
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reporting.datatypes.TaskReportingType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.status.datatypes.TaskOversightStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;

import java.util.concurrent.ConcurrentHashMap;

public class PetasosOversightTask extends PetasosTask{

    private TaskFulfillmentType activeTaskFulfillment;
    private SerializableObject activeTaskFulfillmentLock;

    private TaskReportingType taskReporting;
    private SerializableObject taskReportingLock;

    private ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> fulfillmentMap;
    private SerializableObject fulfillmentMapLock;

    private TaskOversightStatusType taskOversightStatus;
    private SerializableObject taskOversightStatusLock;

    private TaskIdType actionableTaskId;
    private SerializableObject actionableTaskIdLock;

    //
    // Constructor(s)
    //

    public PetasosOversightTask(){
        super();
        this.activeTaskFulfillment = null;
        this.activeTaskFulfillmentLock = new SerializableObject();
        this.taskReporting = null;
        this.taskReportingLock = new SerializableObject();
        this.fulfillmentMap = new ConcurrentHashMap<>();
        this.fulfillmentMapLock = new SerializableObject();
        this.taskOversightStatus = null;
        this.taskOversightStatusLock =  new SerializableObject();
        this.actionableTaskId = null;
        this.actionableTaskIdLock = new SerializableObject();
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.OVERSIGHT_TASK_TYPE));
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasActiveTaskFulfillment(){
        boolean hasValue = this.activeTaskFulfillment != null;
        return(hasValue);
    }

    public TaskFulfillmentType getActiveTaskFulfillment() {
        return activeTaskFulfillment;
    }

    public void setActiveTaskFulfillment(TaskFulfillmentType activeTaskFulfillment) {
        this.activeTaskFulfillment = activeTaskFulfillment;
    }

    @JsonIgnore
    public boolean hasActionableTaskId() {
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType actionableTaskId) {
        this.actionableTaskId = actionableTaskId;
    }

    @JsonIgnore
    boolean hasTaskReporting(){
        boolean hasValue = this.taskReporting != null;
        return(hasValue);
    }

    public TaskReportingType getTaskReporting() {
        return taskReporting;
    }

    public void setTaskReporting(TaskReportingType taskReporting) {
        this.taskReporting = taskReporting;
    }

    @JsonIgnore
    public boolean hasFulfillmentMap(){
        boolean hasValue = this.fulfillmentMap != null;
        return(hasValue);
    }

    public ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> getFulfillmentMap() {
        return fulfillmentMap;
    }

    public void setFulfillmentMap(ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> fulfillmentMap) {
        this.fulfillmentMap = fulfillmentMap;
    }

    @JsonIgnore
    public boolean hasTaskOversightStatus(){
        boolean hasValue = this.taskOversightStatus != null;
        return(hasValue);
    }

    public TaskOversightStatusType getTaskOversightStatus() {
        return taskOversightStatus;
    }

    public void setTaskOversightStatus(TaskOversightStatusType taskOversightStatus) {
        this.taskOversightStatus = taskOversightStatus;
    }

    public SerializableObject getActiveTaskFulfillmentLock() {
        return activeTaskFulfillmentLock;
    }

    public void setActiveTaskFulfillmentLock(SerializableObject activeTaskFulfillmentLock) {
        this.activeTaskFulfillmentLock = activeTaskFulfillmentLock;
    }

    public SerializableObject getTaskReportingLock() {
        return taskReportingLock;
    }

    public void setTaskReportingLock(SerializableObject taskReportingLock) {
        this.taskReportingLock = taskReportingLock;
    }

    public SerializableObject getFulfillmentMapLock() {
        return fulfillmentMapLock;
    }

    public void setFulfillmentMapLock(SerializableObject fulfillmentMapLock) {
        this.fulfillmentMapLock = fulfillmentMapLock;
    }

    public SerializableObject getTaskOversightStatusLock() {
        return taskOversightStatusLock;
    }

    public void setTaskOversightStatusLock(SerializableObject taskOversightStatusLock) {
        this.taskOversightStatusLock = taskOversightStatusLock;
    }

    public SerializableObject getActionableTaskIdLock() {
        return actionableTaskIdLock;
    }

    public void setActionableTaskIdLock(SerializableObject actionableTaskIdLock) {
        this.actionableTaskIdLock = actionableTaskIdLock;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosOversightTask{" +
                "activeTaskFulfillment=" + activeTaskFulfillment +
                ", taskReporting=" + taskReporting +
                ", fulfillmentMap=" + fulfillmentMap +
                ", taskOversightStatus=" + taskOversightStatus +
                ", actionableTaskId=" + actionableTaskId +
                ", taskId=" + getTaskId() +
                ", taskType=" + getTaskType() +
                ", taskWorkItem=" + getTaskWorkItem() +
                ", taskTraceability=" + getTaskTraceability() +
                ", taskOutcomeStatus=" + getTaskOutcomeStatus() +
                ", registered=" + isRegistered() +
                ", taskPerformerTypes=" + getTaskPerformerTypes() +
                ", taskReason=" + getTaskReason() +
                '}';
    }
}
