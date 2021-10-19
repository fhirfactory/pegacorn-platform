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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.finalisation.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.finalisation.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;

public class TaskFinalisationStatusElementType extends TaskInstantDetailSegmentBase implements Serializable {
    private TaskIdType downstreamActionableTaskId;
    private TaskFinalisationStatusEnum finalisationStatus;

    //
    // Constructor(s)
    //

    public TaskFinalisationStatusElementType(){
        super();
        this.downstreamActionableTaskId = null;
        this.finalisationStatus = null;
    }

    public TaskFinalisationStatusElementType(TaskFinalisationStatusElementType ori){
        super(ori);
        this.downstreamActionableTaskId = null;
        this.finalisationStatus = null;

        if(ori.hasFinalisationStatus()){
            setFinalisationStatus(ori.getFinalisationStatus());
        }
        if(ori.hasDownstreamActionableTaskId()){
            setDownstreamActionableTaskId(ori.getDownstreamActionableTaskId());
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasDownstreamActionableTaskId(){
        boolean hasValue = this.downstreamActionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getDownstreamActionableTaskId() {
        return downstreamActionableTaskId;
    }

    public void setDownstreamActionableTaskId(TaskIdType downstreamActionableTaskId) {
        this.downstreamActionableTaskId = downstreamActionableTaskId;
    }

    @JsonIgnore
    public boolean hasFinalisationStatus(){
        boolean hasValue = this.finalisationStatus != null;
        return(hasValue);
    }

    public TaskFinalisationStatusEnum getFinalisationStatus() {
        return finalisationStatus;
    }

    public void setFinalisationStatus(TaskFinalisationStatusEnum finalisationStatus) {
        this.finalisationStatus = finalisationStatus;
    }

    @Override
    public String toString() {
        return "TaskFinalisationStatusElementType{" +
                "registrationInstant=" + getRegistrationInstant() +
                ", readyInstant=" + getReadyInstant() +
                ", startInstant=" + getStartInstant() +
                ", finishInstant=" + getFinishInstant() +
                ", finalisationInstant=" + getFinalisationInstant() +
                ", lastCheckedInstant=" + getLastCheckedInstant() +
                ", downstreamActionableTaskId=" + downstreamActionableTaskId +
                ", finalisationStatus=" + finalisationStatus +
                '}';
    }
}
