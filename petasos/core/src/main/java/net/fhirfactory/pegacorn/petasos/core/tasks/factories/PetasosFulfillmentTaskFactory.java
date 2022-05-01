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
package net.fhirfactory.pegacorn.petasos.core.tasks.factories;

import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.factories.TaskIdTypeFactory;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reason.datatypes.TaskReasonType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.work.datatypes.TaskWorkItemType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PetasosFulfillmentTaskFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosFulfillmentTaskFactory.class);

    @Inject
    private TaskIdTypeFactory taskIdFactory;

    //
    // Constructor(s)
    //

    /* none */

    //
    // Post Construct Initialisation(s)
    //

    /* none */

    //
    // Business Methods
    //

    public PetasosFulfillmentTask newFulfillmentTask(PetasosActionableTask actionableTask, WorkUnitProcessorTopologyNode wupNode) {
        getLogger().debug(".newFulfillmentTask(): Enter, actionableTask->{}, wupNode->{}", actionableTask, wupNode );

        //
        // Create Empty PetasosFulfillmentTask
        PetasosFulfillmentTask fulfillmentTask = new PetasosFulfillmentTask();

        //
        // Create a TaskId (is local, so simple UUID is ok) and add to our Task
        TaskIdType fulfillmentTaskId = new TaskIdType();
        fulfillmentTaskId.setId(UUID.randomUUID().toString());
        fulfillmentTaskId.setVersion(actionableTask.getTaskId().getVersion());
        fulfillmentTaskId.setCreationInstant(Instant.now());
        fulfillmentTask.setTaskId(fulfillmentTaskId);
        //
        // Get the Task Type, clone it and add it to our Task
        TaskTypeType taskType = SerializationUtils.clone(actionableTask.getTaskType());
        fulfillmentTask.setTaskType(taskType);
        //
        // Get the TaskWorkItem from the actionableTask, clone it and add it to our Task.
        TaskWorkItemType taskWorkItem = SerializationUtils.clone(actionableTask.getTaskWorkItem());
        fulfillmentTask.setTaskWorkItem(taskWorkItem);
        //
        // Get the TaskTraceability Detail from the actionableTask, clone it and add it to our Task
        TaskTraceabilityType taskTraceability = SerializationUtils.clone(actionableTask.getTaskTraceability());
        fulfillmentTask.setTaskTraceability(taskTraceability);
        //
        // Get the ActionableTask's Id, clone it and add it to our Task
        TaskIdType actionableTaskId = SerializationUtils.clone(actionableTask.getTaskId());
        fulfillmentTask.setActionableTaskId(actionableTaskId);
        //
        // Get the ActionableTask's Task Reason (we don't need to clone it, it's an enum) and add it to our Task
        TaskReasonType taskReason = actionableTask.getTaskReason();
        fulfillmentTask.setTaskReason(taskReason);
        //
        // Get the Task Performer Types from the Actionable Task, clone them and add them to our task
        List<TaskPerformerTypeType> taskPerformers = new ArrayList<>();
        if(actionableTask.hasTaskPerformerTypes()){
            for(TaskPerformerTypeType currentPerformerType: actionableTask.getTaskPerformerTypes()){
                TaskPerformerTypeType clonedPerformerType = SerializationUtils.clone(currentPerformerType);
                taskPerformers.add(clonedPerformerType);
            }
        }
        fulfillmentTask.setTaskPerformerTypes(taskPerformers);
        //
        // Assign the node affinity of the fulfillment task (from the actionable task)
        fulfillmentTask.setTaskNodeAffinity(actionableTask.getTaskNodeAffinity());
        //
        // Now to add Fulfillment details
        TaskFulfillmentType fulfillment = new TaskFulfillmentType();
        fulfillment.setFulfillerComponent(wupNode);
        fulfillment.setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
        fulfillment.setResilientActivity(true);
        FulfillmentTrackingIdType trackingId = new FulfillmentTrackingIdType(fulfillmentTask.getTaskId());
        fulfillment.setTrackingID(trackingId);
        fulfillment.setLastCheckedInstant(Instant.now());
        fulfillmentTask.setTaskFulfillment(fulfillment);
        //
        // Done! :)
        getLogger().debug(".newFulfillmentTask(): Exit, fulfillmentTask->{}", fulfillmentTask);
        return(fulfillmentTask);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
