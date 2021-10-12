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
import net.fhirfactory.pegacorn.common.model.componentid.ComponentID;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;

public class PetasosActionableTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;
    private SerializableObject taskFulfillmentLock;

    //
    // Constructor(s)
    //

    public PetasosActionableTask(){
        super();
        this.taskFulfillment = null;
        this.taskFulfillmentLock = new SerializableObject();
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasActualFulfillerId(){
        boolean hasValue = this.actualFulfillerId != null;
        return(hasValue);
    }

    public ComponentID getActualFulfillerId() {
        return actualFulfillerId;
    }

    public void setActualFulfillerId(ComponentID actualFulfillerId) {
        this.actualFulfillerId = actualFulfillerId;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosActionableTask{" +
                "idSegment=" + getTaskId() +
                ", workSegment=" + getTaskWorkItem() +
                ", historySegment=" + getTaskTraceability() +
                ", outcomeStatusSegment=" + getTaskOutcomeStatus() +
                ", registered=" + isRegistered() +
                ", actualFulfillerId=" + actualFulfillerId +
                '}';
    }
}
