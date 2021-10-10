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

public class TraceabilityElementType extends TaskInstantDetailSegmentBase implements Serializable {
    private ComponentID implementationWUPIdentifier;
    private TaskIdType taskIdentifier;

    //
    // Constructors
    //

    public TraceabilityElementType(){
        super();
        this.implementationWUPIdentifier = null;
        this.taskIdentifier = null;
    }

    public TraceabilityElementType(TraceabilityElementType ori){
        super(ori);
        this.implementationWUPIdentifier = null;
        this.taskIdentifier = null;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public boolean hasImplementationWUPIdentifier(){
        boolean hasValue = this.implementationWUPIdentifier != null;
        return(hasValue);
    }

    public ComponentID getImplementationWUPIdentifier() {
        return implementationWUPIdentifier;
    }

    public void setImplementationWUPIdentifier(ComponentID implementationWUPIdentifier) {
        this.implementationWUPIdentifier = implementationWUPIdentifier;
    }

    public boolean hasTaskIdentifier(){
        boolean hasValue = this.taskIdentifier != null;
        return(hasValue);
    }

    public TaskIdType getTaskIdentifier() {
        return taskIdentifier;
    }

    public void setTaskIdentifier(TaskIdType taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
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
                ", implementationWUPIdentifier=" + implementationWUPIdentifier +
                ", taskIdentifier=" + taskIdentifier +
                '}';
    }
}
