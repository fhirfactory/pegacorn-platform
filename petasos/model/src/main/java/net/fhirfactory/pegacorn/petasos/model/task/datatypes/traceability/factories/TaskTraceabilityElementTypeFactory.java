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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.factories;

import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskTraceabilityElementTypeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskTraceabilityElementTypeFactory.class);

    //
    // Constructor(s)
    //

    /* none required */

    //
    // Business Methods
    //

    public TaskTraceabilityElementType newTaskTraceabilityElementFromTask(PetasosActionableTask task){
        getLogger().debug(".newTaskTraceabilityElementFromTask(): Entry, task->{}", task);
        if(task == null){
            return(null);
        }
        TaskTraceabilityElementType traceabilityElement = newTaskTraceabilityElementFromTask(task.getTaskId(), task.getTaskFulfillment());
        getLogger().debug(".newTaskTraceabilityElementFromTask(): Exit, traceabilityElement->{}", traceabilityElement);
        return(traceabilityElement);
    }

    public TaskTraceabilityElementType newTaskTraceabilityElementFromTask(TaskIdType taskId, TaskFulfillmentType taskFulfillment){
        getLogger().debug(".newTaskTraceabilityElementFromTask(): Entry, taskId->{}, taskFulfillment->{}", taskId, taskFulfillment);
        if(taskId == null || taskFulfillment == null){
            return(null);
        }
        TaskTraceabilityElementType traceabilityElement = new TaskTraceabilityElementType();
        traceabilityElement.setFulfillerId(SerializationUtils.clone(taskFulfillment.getFulfillerComponentId()));
        traceabilityElement.setActionableTaskId(SerializationUtils.clone(taskId));
        traceabilityElement.setFulfillerTaskId(SerializationUtils.clone(taskFulfillment.getTrackingID()));
        if(taskFulfillment.hasFinalisationInstant()){
            traceabilityElement.setFinalisationInstant(SerializationUtils.clone(taskFulfillment.getFinalisationInstant()));
        }
        if(taskFulfillment.hasReadyInstant()){
            traceabilityElement.setReadyInstant(SerializationUtils.clone(taskFulfillment.getReadyInstant()));
        }
        if(taskFulfillment.hasFinishInstant()){
            traceabilityElement.setFinishInstant(SerializationUtils.clone(taskFulfillment.getFinishInstant()));
        }
        if(taskFulfillment.hasStartInstant()){
            traceabilityElement.setStartInstant(SerializationUtils.clone(taskFulfillment.getStartInstant()));
        }
        if(taskFulfillment.hasRegistrationInstant()){
            traceabilityElement.setRegistrationInstant(SerializationUtils.clone(taskFulfillment.getRegistrationInstant()));
        }
        getLogger().debug(".newTaskTraceabilityElementFromTask(): Exit, traceabilityElement->{}", traceabilityElement);
        return(traceabilityElement);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
