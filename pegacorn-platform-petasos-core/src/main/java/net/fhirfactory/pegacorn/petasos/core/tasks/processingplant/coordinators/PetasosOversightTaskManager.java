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

package net.fhirfactory.pegacorn.petasos.core.tasks.processingplant.coordinators;

import net.fhirfactory.pegacorn.petasos.core.tasks.cluster.cache.PetasosTaskJobCardRegistry;
import net.fhirfactory.pegacorn.petasos.core.tasks.cluster.managers.PetasosActionableTaskManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.processingplant.cache.PetasosFulfillmentTaskRegistry;
import net.fhirfactory.pegacorn.petasos.core.tasks.processingplant.cache.PetasosOversightTaskRegistry;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 */
@ApplicationScoped
public class PetasosOversightTaskManager {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOversightTaskManager.class);

    private int actionableTaskCount;
    private int oversightTaskCount;
    private int fulfillmentTaskCount;

    @Inject
    private PetasosOversightTaskRegistry oversightTaskRegistry;

    @Inject
    private PetasosFulfillmentTaskRegistry fulfillmentTaskRegistry;

    @Inject
    private PetasosTaskJobCardRegistry jobCardRegistry;

    @Inject
    private PetasosActionableTaskManager actionableTaskManager;

    //
    // Constructor
    //

    public PetasosOversightTaskManager(){
        this.actionableTaskCount = 0;
        this.oversightTaskCount = 0;
        this.fulfillmentTaskCount = 0;
    }

    //
    // Metrics Reporting
    //

    public String getCacheName(){
        return("ProcessingPlantActionableTaskRegistry");
    }

    public String getMetrics(){
        String metrics = "ActionableTaskCount["+ actionableTaskCount +"], OversightTaskCount["+oversightTaskCount+"], FulfillmentTaskCount["+fulfillmentTaskCount+"]";
        return(metrics);
    }

    //
    // Work Unit Processor (WUP) Management Methods
    //

    public PetasosTaskJobCard registerFulfillmentTask(PetasosActionableTask actionableTask, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".registerFulfillmentTask(): Entry, actionableTask->{}, fulfillmentTask->{}", actionableTask, fulfillmentTask);
        //
        // (Defensive Programming) Check the passed-in parameters and confirm that they have all the bits needed!
        if(actionableTask == null || fulfillmentTask == null){
            getLogger().trace(".registerFulfillmentTask(): Exit, either actionableTask or fulfillmentTask is null");
            PetasosTaskJobCard badJobCard= new PetasosTaskJobCard();
            badJobCard.setGrantedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_UNDOABLE);
            badJobCard.setCurrentStateReason("Either actionableTask or fulfillmentTask is null");
            getLogger().trace(".registerFulfillmentTask(): Exit, either actionableTask or fulfillmentTask is null");
        }

        return(null);
    }

    public PetasosTaskJobCard unregisterFulfillmentTask(PetasosFulfillmentTask fulfillmentTask){

        return(null);
    }

    public PetasosTaskJobCard requestExecutionStatusChange(PetasosTaskJobCard jobCard){

        return(null);
    }

    public PetasosTaskJobCard getExecutionStatusChange(PetasosTaskJobCard jobCard){

        return(null);
    }


    //
    // PetasosActionableTask Activity Management
    //

    public PetasosActionableTask registerPetasosActionableTask(PetasosActionableTask actionableTask){

        return(actionableTask);
    }

    public PetasosActionableTask finishPetasosActionableTask(PetasosActionableTask actionableTask){

        return(actionableTask);
    }

    public PetasosActionableTask finalisePetasosActionableTask(PetasosActionableTask actionableTask){

        return(actionableTask);
    }


    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected PetasosOversightTaskRegistry getOversightTaskRegistry() {
        return oversightTaskRegistry;
    }

    protected PetasosFulfillmentTaskRegistry getFulfillmentTaskRegistry() {
        return fulfillmentTaskRegistry;
    }

    protected PetasosTaskJobCardRegistry getJobCardRegistry() {
        return jobCardRegistry;
    }

    protected PetasosActionableTaskManager getActionableTaskManager() {
        return actionableTaskManager;
    }
}
