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

import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskFinalisationStatusType implements Serializable {
    private Map<TaskIdType, TaskFinalisationStatusElementType> finalisationMap;

    //
    // Constructor(s)
    //

    public TaskFinalisationStatusType(){
        this.finalisationMap = new ConcurrentHashMap<TaskIdType, TaskFinalisationStatusElementType>();
    }

    //
    // Helper Methods
    //

    public void addDownstreamTask(TaskIdType downstreamActionableTaskId){
        if(downstreamActionableTaskId == null){
            return;
        }
        if(getFinalisationMap().containsKey(downstreamActionableTaskId)){
            return;
        }
        TaskFinalisationStatusElementType finalisationStatus = new TaskFinalisationStatusElementType();
        finalisationStatus.setDownstreamActionableTaskId(downstreamActionableTaskId);
        finalisationStatus.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
        finalisationStatus.setRegistrationInstant(Instant.now());
        getFinalisationMap().put(downstreamActionableTaskId, finalisationStatus);
    }

    public void notifyDownstreamTaskBeingFulfilled(TaskIdType downstreamActionableTaskId){
        if(downstreamActionableTaskId == null){
            return;
        }
        if(!getFinalisationMap().containsKey(downstreamActionableTaskId)){
            addDownstreamTask(downstreamActionableTaskId);
        }
        getFinalisationMap().get(downstreamActionableTaskId).setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasFinalisationMap(){
        boolean hasValue = this.finalisationMap != null;
        return(hasValue);
    }

    public Map<TaskIdType, TaskFinalisationStatusElementType> getFinalisationMap() {
        return finalisationMap;
    }

    public void setFinalisationMap(Map<TaskIdType, TaskFinalisationStatusElementType> finalisationMap) {
        this.finalisationMap = finalisationMap;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskFinalisationStatusType{" +
                "finalisationMap=" + finalisationMap +
                ", hasFinalisationMap=" + hasFinalisationMap() +
                '}';
    }
}
