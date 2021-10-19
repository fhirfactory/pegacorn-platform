/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.im;

import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.dm.PetasosFulfillmentDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class PetasosFulfillmentTaskIM {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosFulfillmentTaskIM.class);

    @Inject
    private PetasosFulfillmentDM fulfillmentTaskRegistry;

    public PetasosFulfillmentTask addFulfillmentTask(PetasosFulfillmentTask fulfillmentTask){
        LOG.debug(".addFulfillmentTask(): Entry");
        //
        // Verify the fulfillmentTask (PetasosFulfillmentTask) has all the necessary bits
        boolean isGood = false;
        if(fulfillmentTask != null){
            if(fulfillmentTask.hasTaskFulfillment() && fulfillmentTask.hasTaskWorkItem() & fulfillmentTask.hasActionableTaskId()){
                isGood = true;
            }
        }
        if (!isGood) {
            throw (new IllegalArgumentException("fulfillmentTask does not contain all the necessary details"));
        }
        //
        // Register the PetasosFulfillmentTask
        LOG.trace(".addFulfillmentTask(): check for existing ResilienceParcel instance for this WUP/UoW combination");
        PetasosFulfillmentTask registeredTask = fulfillmentTaskRegistry.registerFulfillmentTask(fulfillmentTask);
        return(registeredTask);
    }

    public PetasosFulfillmentTask getFulfillmentTask(TaskIdType taskId){
        LOG.debug(".getFulfillmentTask(): Entry, taskId->{}", taskId);
        PetasosFulfillmentTask fulfillmentTask = fulfillmentTaskRegistry.getFulfillmentTask(taskId);
        LOG.debug(".getFulfillmentTask(): Exit, fulfillmentTask->{}", fulfillmentTask);
        return(fulfillmentTask);
    }

    public List<PetasosFulfillmentTask> getFulfillmentTasksForActionableTask(TaskIdType actionableTaskId){

    }
}
