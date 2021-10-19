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
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reason.datatypes.TaskReasonType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.work.datatypes.TaskWorkItemType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PetasosTask implements Serializable {

    private TaskIdType taskId;
    private SerializableObject taskIdLock;

    private TaskTypeType taskType;
    private SerializableObject taskTypeLock;

    private TaskWorkItemType taskWorkItem;
    private SerializableObject taskWorkItemLock;

    private TaskTraceabilityType taskTraceability;
    private SerializableObject taskTraceabilityLock;

    private TaskOutcomeStatusType taskOutcomeStatus;
    private SerializableObject taskOutcomeStatusLock;

    private List<TaskPerformerTypeType> taskPerformerTypes;
    private SerializableObject taskPerformerTypesLock;

    private TaskReasonType taskReason;
    private SerializableObject taskReasonLock;

    private boolean registered;

    //
    // Constructor(s)
    //

    public PetasosTask(){
        this.taskId = null;
        this.taskIdLock = new SerializableObject();
        this.taskWorkItem = null;
        this.taskWorkItemLock = new SerializableObject();
        this.taskTraceability = null;
        this.taskTraceabilityLock = new SerializableObject();
        this.taskOutcomeStatus = null;
        this.taskOutcomeStatusLock = new SerializableObject();
        this.registered = false;
        this.taskPerformerTypes = new ArrayList<>();
        this.taskPerformerTypesLock = new SerializableObject();
        this.taskType = null;
        this.taskTypeLock = new SerializableObject();
        this.taskReason = null;
        this.taskReasonLock = new SerializableObject();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasTaskId(){
        boolean hasValue = this.taskId != null;
        return(hasValue);
    }

    public TaskIdType getTaskId() {
        return taskId;
    }

    public void setTaskId(TaskIdType taskId) {
        this.taskId = taskId;
    }

    public SerializableObject getTaskIdLock() {
        return taskIdLock;
    }

    public void setTaskIdLock(SerializableObject taskIdLock) {
        this.taskIdLock = taskIdLock;
    }

    @JsonIgnore
    public boolean hasTaskType(){
        boolean hasValue = this.taskType != null;
        return(hasValue);
    }

    public TaskTypeType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeType taskType) {
        this.taskType = taskType;
    }

    public SerializableObject getTaskTypeLock() {
        return taskTypeLock;
    }

    public void setTaskTypeLock(SerializableObject taskTypeLock) {
        this.taskTypeLock = taskTypeLock;
    }

    @JsonIgnore
    public boolean hasTaskWorkItem(){
        boolean hasValue = this.taskWorkItem != null;
        return(hasValue);
    }

    public TaskWorkItemType getTaskWorkItem() {
        return taskWorkItem;
    }

    public void setTaskWorkItem(TaskWorkItemType taskWorkItem) {
        this.taskWorkItem = taskWorkItem;
    }

    public SerializableObject getTaskWorkItemLock() {
        return taskWorkItemLock;
    }

    public void setTaskWorkItemLock(SerializableObject taskWorkItemLock) {
        this.taskWorkItemLock = taskWorkItemLock;
    }

    @JsonIgnore
    public boolean hasTaskTraceability(){
        boolean hasValue = this.taskTraceability != null;
        return(hasValue);
    }

    public TaskTraceabilityType getTaskTraceability() {
        return taskTraceability;
    }

    public void setTaskTraceability(TaskTraceabilityType taskTraceability) {
        this.taskTraceability = taskTraceability;
    }

    public SerializableObject getTaskTraceabilityLock() {
        return taskTraceabilityLock;
    }

    public void setTaskTraceabilityLock(SerializableObject taskTraceabilityLock) {
        this.taskTraceabilityLock = taskTraceabilityLock;
    }

    @JsonIgnore
    public boolean hasTaskOutcomeStatus(){
        boolean hasValue = this.taskOutcomeStatus != null;
        return(hasValue);
    }

    public TaskOutcomeStatusType getTaskOutcomeStatus() {
        return taskOutcomeStatus;
    }

    public void setTaskOutcomeStatus(TaskOutcomeStatusType taskOutcomeStatus) {
        this.taskOutcomeStatus = taskOutcomeStatus;
    }

    public SerializableObject getTaskOutcomeStatusLock() {
        return taskOutcomeStatusLock;
    }

    public void setTaskOutcomeStatusLock(SerializableObject taskOutcomeStatusLock) {
        this.taskOutcomeStatusLock = taskOutcomeStatusLock;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @JsonIgnore
    public boolean hasTaskPerformerTypes(){
        boolean hasValue = this.taskPerformerTypes != null;
        return(hasValue);
    }

    public List<TaskPerformerTypeType> getTaskPerformerTypes() {
        return taskPerformerTypes;
    }

    public void setTaskPerformerTypes(List<TaskPerformerTypeType> taskPerformerTypes) {
        this.taskPerformerTypes = taskPerformerTypes;
    }

    public SerializableObject getTaskPerformerTypesLock() {
        return taskPerformerTypesLock;
    }

    public void setTaskPerformerTypesLock(SerializableObject taskPerformerTypesLock) {
        this.taskPerformerTypesLock = taskPerformerTypesLock;
    }

    @JsonIgnore
    public boolean hasTaskReason(){
        boolean hasValue = this.taskReason != null;
        return(hasValue);
    }

    public TaskReasonType getTaskReason() {
        return taskReason;
    }

    public void setTaskReason(TaskReasonType taskReason) {
        this.taskReason = taskReason;
    }

    public SerializableObject getTaskReasonLock() {
        return taskReasonLock;
    }

    public void setTaskReasonLock(SerializableObject taskReasonLock) {
        this.taskReasonLock = taskReasonLock;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTask{" +
                "taskId=" + taskId +
                ", taskType=" + taskType +
                ", taskWorkItem=" + taskWorkItem +
                ", taskTraceability=" + taskTraceability +
                ", taskOutcomeStatus=" + taskOutcomeStatus +
                ", registered=" + registered +
                ", taskPerformers=" + taskPerformerTypes +
                ", taskReason=" + taskReason +
                '}';
    }
}
