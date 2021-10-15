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
package net.fhirfactory.pegacorn.petasos.model.task.segments.traceability.datatypes;

import net.fhirfactory.pegacorn.common.model.componentid.ComponentID;
import net.fhirfactory.pegacorn.petasos.model.task.segments.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;

import java.io.Serializable;

public class TaskTraceabilityElementType extends TaskInstantDetailSegmentBase implements Serializable {
    private ComponentID fulfillerId;
    private TaskIdType taskId;

    //
    // Constructors
    //

    public TaskTraceabilityElementType(){
        super();
        this.fulfillerId = null;
        this.taskId = null;
    }

    public TaskTraceabilityElementType(TaskTraceabilityElementType ori){
        super(ori);
        this.fulfillerId = null;
        this.taskId = null;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public boolean hasImplementationWUPIdentifier(){
        boolean hasValue = this.fulfillerId != null;
        return(hasValue);
    }

    public ComponentID getFulfillerId() {
        return fulfillerId;
    }

    public void setFulfillerId(ComponentID fulfillerId) {
        this.fulfillerId = fulfillerId;
    }

    public boolean hasTaskIdentifier(){
        boolean hasValue = this.taskId != null;
        return(hasValue);
    }

    public TaskIdType getTaskId() {
        return taskId;
    }

    public void setTaskId(TaskIdType taskId) {
        this.taskId = taskId;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "TraceabilityElement{" +
                "registrationInstant=" + getRegistrationInstant() +
                ", readyInstant=" + getReadyInstant() +
                ", startInstant=" + getStartInstant() +
                ", finishInstant=" + getFinishInstant() +
                ", finalisationInstant=" + getFinalisationInstant() +
                ", lastCheckedInstant=" + getLastCheckedInstant() +
                ", fulfillerId=" + fulfillerId +
                ", taskId=" + taskId +
                '}';
    }
}
