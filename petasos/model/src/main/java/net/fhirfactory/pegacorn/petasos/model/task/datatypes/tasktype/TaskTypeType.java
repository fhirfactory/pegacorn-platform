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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;

import java.io.Serializable;

public class TaskTypeType implements Serializable {
    TaskTypeTypeEnum taskType;
    String taskSubType;

    //
    // Constructor(s)
    //

    public TaskTypeType(){
        this.taskType = TaskTypeTypeEnum.BASE_TASK_TYPE;
        this.taskSubType = null;
    }

    public TaskTypeType(TaskTypeTypeEnum typeEnum){
        this.taskType = typeEnum;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasTaskType(){
        boolean hasValue = this.taskType != null;
        return(hasValue);
    }

    public TaskTypeTypeEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeTypeEnum taskType) {
        this.taskType = taskType;
    }

    @JsonIgnore
    public boolean hasTaskSubType(){
        boolean hasValue = this.taskSubType != null;
        return(hasValue);
    }

    public String getTaskSubType() {
        return taskSubType;
    }

    public void setTaskSubType(String taskSubType) {
        this.taskSubType = taskSubType;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskTypeType{" +
                "taskType=" + taskType +
                "taskSubType=" + taskSubType +
                '}';
    }
}
