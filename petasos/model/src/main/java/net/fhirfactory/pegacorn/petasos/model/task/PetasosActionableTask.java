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
import net.fhirfactory.pegacorn.common.model.componentid.ComponentTypeType;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.finalisation.datatypes.TaskFinalisationStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;

import java.util.List;

public class PetasosActionableTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;
    private SerializableObject taskFulfillmentLock;
    private TaskFinalisationStatusType taskFinalisation;
    private SerializableObject taskFinalisationLock;

    //
    // Constructor(s)
    //

    public PetasosActionableTask(){
        super();
        this.taskFulfillment = null;
        this.taskFulfillmentLock = new SerializableObject();
        this.taskFinalisation = null;
        this.taskFinalisationLock = new SerializableObject();
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.ACTIONABLE_TASK_TYPE));
    }

    //
    // Getters and Setters
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
    public boolean hasTaskFinalisation(){
        boolean hasValue = this.taskFinalisation != null;
        return(hasValue);
    }

    public TaskFinalisationStatusType getTaskFinalisation() {
        return taskFinalisation;
    }

    public void setTaskFinalisation(TaskFinalisationStatusType taskFinalisation) {
        this.taskFinalisation = taskFinalisation;
    }

    public SerializableObject getTaskFinalisationLock() {
        return taskFinalisationLock;
    }

    public void setTaskFinalisationLock(SerializableObject taskFinalisationLock) {
        this.taskFinalisationLock = taskFinalisationLock;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "PetasosActionableTask{" +
                "taskFulfillment=" + taskFulfillment +
                ", taskFulfillmentLock=" + taskFulfillmentLock +
                ", hasTaskFulfillment=" + hasTaskFulfillment() +
                ", taskId=" + getTaskId() +
                ", taskType=" + getTaskType() +
                ", taskWorkItem=" + getTaskWorkItem() +
                ", taskTraceability=" + getTaskTraceability() +
                ", taskOutcomeStatus=" + getTaskOutcomeStatus() +
                ", taskPerformerTypes=" + getTaskPerformerTypes() +
                ", taskFinalisation=" + getTaskFinalisation() +
                '}';
    }
}
