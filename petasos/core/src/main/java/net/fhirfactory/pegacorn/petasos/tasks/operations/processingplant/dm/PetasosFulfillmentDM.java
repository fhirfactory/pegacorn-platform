/*
 * Copyright (c) 2020 MAHun
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
package net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.dm;

import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a refactored framework for the Petasos "Task" services.
 *
 * This class merely acts as a registry for the "Tasks", allowing other objects to "find them" and
 * explore/manage their relationships. It does not implement any business logic.
 *
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
@ApplicationScoped
public class PetasosFulfillmentDM {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosFulfillmentDM.class);

    private int entriesAdded;
    private int entriesRemoved;

    private ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> fulfillmentTaskRegistry;
    private Object fulfillmentTaskRegistryLock;

    public PetasosFulfillmentDM() {
        this.fulfillmentTaskRegistry = new ConcurrentHashMap<>();
        this.fulfillmentTaskRegistryLock = new Object();
        this.entriesRemoved = 0;
        this.entriesAdded = 0;
    }

    //
    // Metrics Reporting
    //

    public String getCacheName(){
        return("ProcessingPlantFulfillmentTaskRegistry");
    }

    public String getMetrics(){
        String metrics = "CurrentSize["+ fulfillmentTaskRegistry.size() +"], TotalEntriesAdded["+entriesAdded+"], TotalEntriesRemoved["+entriesRemoved+"]";
        return(metrics);
    }

    //
    // FulfillmentTask Registry Methods
    //

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask fulfillmentTask) {
  		getLogger().debug(".registerFulfillmentTask(): Entry, registerFulfillmentTask->{}", fulfillmentTask);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (fulfillmentTask == null) {
            getLogger().debug(".registerFulfillmentTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosFulfillmentTask contain all the necessary bits?
        boolean isBadTask = true;
        if(fulfillmentTask.hasTaskId() && fulfillmentTask.hasTaskWorkItem()) {
            if(fulfillmentTask.hasTaskFulfillment()){
                if(fulfillmentTask.getTaskFulfillment().hasFulfillerComponent()){
                    isBadTask = false;
                }
            }
        }
        if(isBadTask){
            getLogger().debug(".registerFulfillmentTask(): Exit, the PetasosFulfillmentTask does not have all the required bits!");
            return(null);
        }
        //
        // Is the task already registered?
        TaskIdType idSegment = fulfillmentTask.getTaskId();
        PetasosFulfillmentTask registeredFulfillmentTask = null;
        synchronized (this.fulfillmentTaskRegistryLock) {
            if (getFulfillmentTaskRegistry().containsKey(idSegment)) {
                registeredFulfillmentTask = getFulfillmentTaskRegistry().get(idSegment);
            }
        }
        if(registeredFulfillmentTask != null){
            getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask already registered, returning it!");
            return(registeredFulfillmentTask);
        }
        //
        // It's not registered, so register it!
        //
        getLogger().trace(".registerFulfillmentTask(): [Registering fulfillmentTask into FulfillmentTaskRegister] Start");
        synchronized (this.fulfillmentTaskRegistryLock) {
            getFulfillmentTaskRegistry().put(fulfillmentTask.getTaskId(), fulfillmentTask);
        }
        getLogger().trace(".registerFulfillmentTask(): [Registering fulfillmentTask into FulfillmentTaskRegister] Finish");
        //
        // Change the registered flag in the Task
        fulfillmentTask.setRegistered(true);
        //
        // All done!
        getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask->{}", fulfillmentTask);
        return(fulfillmentTask);
    }

    public PetasosFulfillmentTask unregisterFulfillmentTask(PetasosFulfillmentTask fulfillmentTask) {
        getLogger().debug(".unregisterFulfillmentTask(): Entry, registerFulfillmentTask->{}", fulfillmentTask);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (fulfillmentTask == null) {
            getLogger().debug(".unregisterFulfillmentTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosFulfillmentTask contain all the necessary bits?
        boolean isBadTask = true;
        if (fulfillmentTask.hasTaskId()){
                    isBadTask = false;
        }
        if (isBadTask) {
            getLogger().debug(".unregisterFulfillmentTask(): Exit, the PetasosFulfillmentTask does not have all the required bits!");
            return (null);
        }
        //
        // Is the task actually registered?
        TaskIdType idSegment = fulfillmentTask.getTaskId();
        synchronized (this.fulfillmentTaskRegistryLock) {
            if (getFulfillmentTaskRegistry().containsKey(idSegment)) {
                getFulfillmentTaskRegistry().remove(fulfillmentTask.getTaskId());
            }
        }
        //
        // Change the registered flag in the Task
        fulfillmentTask.setRegistered(false);
        //
        // All done!
        getLogger().debug(".unregisterFulfillmentTask(): Exit, the now unregisteredTask->{}", fulfillmentTask);
        return(fulfillmentTask);
    }

    public PetasosFulfillmentTask unregisterFulfillmentTask(TaskIdType taskIdentity) {
        getLogger().debug(".unregisterFulfillmentTask(): Entry, taskIdentity->{}", taskIdentity);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (taskIdentity == null) {
            getLogger().debug(".unregisterFulfillmentTask(): Exit, taskIdentity is null, so nothing to do!");
            return(null);
        }
        //
        // Is the task actually registered, if so, remove it
        PetasosFulfillmentTask registeredTask = null;
        synchronized (this.fulfillmentTaskRegistryLock) {
            if (getFulfillmentTaskRegistry().containsKey(taskIdentity)) {
                registeredTask = getFulfillmentTaskRegistry().get(taskIdentity);
                getFulfillmentTaskRegistry().remove(taskIdentity);
            }
        }
        //
        // Change the registered flag in the Task
        if(registeredTask != null) {
            registeredTask.setRegistered(false);
        }
        //
        // All done!
        getLogger().debug(".unregisterFulfillmentTask(): Exit, the now unregisteredTask->{}", registeredTask);
        return(registeredTask);
    }

    public PetasosFulfillmentTask getFulfillmentTask(TaskIdType fulfillmentTaskIdentity){
        getLogger().debug(".getFulfillmentTask(): Entry, fulfillmentTaskIdentity->{}", fulfillmentTaskIdentity);
        //
        // (Definsive Programming) Were we given a bad parameter?
        if(fulfillmentTaskIdentity == null){
            getLogger().debug(".getFulfillmentTask(): Exit, fulfillmentTaskIdentity is null, therefore returning null");
            return(null);
        }
        PetasosFulfillmentTask registeredFulfillmentTask = null;
        synchronized (this.fulfillmentTaskRegistryLock) {
            if (getFulfillmentTaskRegistry().containsKey(fulfillmentTaskIdentity)) {
                registeredFulfillmentTask = getFulfillmentTaskRegistry().remove(fulfillmentTaskIdentity);
            }
        }
        getLogger().debug(".getFulfillmentTask(): Exit, registeredFulfillmentTask->{}", registeredFulfillmentTask);
        return(registeredFulfillmentTask);
    }

    //
    // Getters (and Setters)
    //


    public ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> getFulfillmentTaskRegistry() {
        return fulfillmentTaskRegistry;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Simple Helper Methods
    //

    protected void incrementEntriesAdded(){
        this.entriesAdded += 1;
    }

    protected void incrementEntriesRemoved(){
        this.entriesRemoved += 1;
    }
}
