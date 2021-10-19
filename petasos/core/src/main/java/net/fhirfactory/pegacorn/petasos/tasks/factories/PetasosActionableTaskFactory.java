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
package net.fhirfactory.pegacorn.petasos.tasks.factories;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.factories.TaskIdTypeFactory;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reason.datatypes.TaskReasonType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reason.valuesets.TaskReasonTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.factories.TaskTraceabilityElementTypeFactory;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.factories.TaskTraceabilityTypeFactory;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.tasks.operations.cluster.im.PetasosActionableTaskIM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosActionableTaskFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosActionableTaskFactory.class);

    @Inject
    private TaskIdTypeFactory taskIdFactory;

    @Inject
    private PetasosActionableTaskIM actionableTaskIM;

    @Inject
    private TaskTraceabilityTypeFactory traceabilityTypeFactory;

    @Inject
    private TaskTraceabilityElementTypeFactory traceabilityElementTypeFactory;

    //
    // Constructor(s)
    //


    //
    // Post Construct Methods
    //


    //
    // Business Methods
    //

    public PetasosActionableTask newMessageBasedActionableTask(PetasosActionableTask upstreamTask, TaskTraceabilityElementType fulfillmentTaskSummary, UoWPayload payload ){

        //
        // Grab the Upstream ActionableTask

        PetasosActionableTask newTask = new PetasosActionableTask();
        //
        // create a new id
        getLogger().trace(".newActionableTask(): [Create ActionableTask ID] Start");
        TaskIdType taskId = taskIdFactory.newTaskId(TaskReasonTypeEnum.TASK_REASON_MESSAGE_PROCESSING, payload.getPayloadManifest().getContentDescriptor());
        newTask.setTaskId(taskId);
        //
        // create task traceability information
        TaskTraceabilityType taskTraceabilityType = traceabilityTypeFactory.newTaskTraceabilityFromTask(upstreamTask);
        taskTraceabilityType.addToTaskJourney(fulfillmentTaskSummary);
        newTask.setTaskTraceability(taskTraceabilityType);
        //
        // create task work item
        TaskWorkItemType workItem = new TaskWorkItemType(payload);
        newTask.setTaskWorkItem(workItem);
        //
        // add the task reason
        TaskReasonType taskReason = new TaskReasonType(TaskReasonTypeEnum.TASK_REASON_MESSAGE_PROCESSING);
        newTask.setTaskReason(taskReason);
        return(newTask);
    }


    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
