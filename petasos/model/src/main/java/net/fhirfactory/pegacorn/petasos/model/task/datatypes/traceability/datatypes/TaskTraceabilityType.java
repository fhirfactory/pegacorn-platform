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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class TaskTraceabilityType implements Serializable {
    ConcurrentHashMap<Integer, TaskTraceabilityElementType> taskJourney;

    //
    // Constructor(s)
    //

    public TaskTraceabilityType(){
        this.taskJourney = new ConcurrentHashMap<>();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public ConcurrentHashMap<Integer, TaskTraceabilityElementType> getTaskJourney() {
        return taskJourney;
    }

    public void setTaskJourney(ConcurrentHashMap<Integer, TaskTraceabilityElementType> taskJourney) {
        this.taskJourney = taskJourney;
    }

    //
    // Business / Helper Methods
    //

    public void addToTaskJourney(TaskTraceabilityElementType traceabilityElementType){
        if(traceabilityElementType == null){
            return;
        }
        int count = taskJourney.size();
        taskJourney.put(count, traceabilityElementType);
    }

    @JsonIgnore
    public TaskIdType getUpstreamTaskId(){
        if(taskJourney.isEmpty()){
            return(null);
        }
        int count = taskJourney.size();
        TaskTraceabilityElementType mostRecentTraceabilityElement = taskJourney.get(count - 1);
        TaskIdType upstreamTaskId = mostRecentTraceabilityElement.getActionableTaskId();
        return(upstreamTaskId);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskTraceabilityType{" +
                "taskJourney=" + taskJourney +
                '}';
    }
}
