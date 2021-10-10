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
import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.work.datatypes.TaskWorkItemType;

import java.io.Serializable;

public class PetasosTask implements Serializable {

    private TaskIdType taskId;
    private SerializableObject taskIdLock;

    private TaskWorkItemType taskWorkItem;
    private SerializableObject taskWorkItemLock;

    private TaskTraceabilityType taskTraceability;
    private SerializableObject taskTraceabilityLock;

    private TaskOutcomeStatusType taskOutcomeStatus;
    private SerializableObject taskOutcomeStatusLock;

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
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasIdSegment(){
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
    public boolean hasWorkSegment(){
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
    public boolean hasHistorySegment(){
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
    public boolean hasOutcomeStatusSegment(){
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTask{" +
                "taskId=" + taskId +
                ", taskWorkItem=" + taskWorkItem +
                ", taskTraceability=" + taskTraceability +
                ", taskOutcomeStatus=" + taskOutcomeStatus +
                ", registered=" + registered +
                '}';
    }
}
