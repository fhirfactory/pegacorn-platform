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

import net.fhirfactory.pegacorn.petasos.model.task.PetasosOversightTask;
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
public class PetasosOversightDM {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosOversightDM.class);

    private int entriesAdded;
    private int entriesRemoved;

    private ConcurrentHashMap<TaskIdType, PetasosOversightTask> oversightTaskRegistry;
    private Object oversightTaskRegistryLock;

    public PetasosOversightDM() {
        this.oversightTaskRegistry = new ConcurrentHashMap<>();
        this.oversightTaskRegistryLock = new Object();
        this.entriesRemoved = 0;
        this.entriesAdded = 0;
    }

    //
    // Metrics Reporting
    //

    public String getCacheName(){
        return("ProcessingPlantOversightTaskRegistry");
    }

    public String getMetrics(){
        String metrics = "CurrentSize["+ oversightTaskRegistry.size() +"], TotalEntriesAdded["+entriesAdded+"], TotalEntriesRemoved["+entriesRemoved+"]";
        return(metrics);
    }

    //
    // OversightTask Registry Methods
    //

    public PetasosOversightTask registerOversightTask(PetasosOversightTask task) {
        getLogger().debug(".registerOversightTask(): Entry, task->{}", task);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (task == null) {
            getLogger().debug(".registerOversightTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosOversightTask contain all the necessary bits?
        boolean isBadTask = true;
        if(task.hasTaskId() && task.hasFulfilledTaskIdentitySegment() && task.hasFulfillmentMap()) {
            if(task.getFulfilledTaskIdentitySegment().hasId()){
                isBadTask = false;
            }
        }
        if(isBadTask){
            getLogger().debug(".registerOversightTask(): Exit, the PetasosOversightTask does not have all the required bits!");
            return(null);
        }
        //
        // Is the task already registered?
        TaskIdType idSegment = task.getTaskId();
        PetasosOversightTask registeredTask = null;
        synchronized (this.oversightTaskRegistryLock) {
            if (getOversightTaskRegistry().containsKey(idSegment)) {
                registeredTask = getOversightTaskRegistry().get(idSegment);
            }
        }
        if(registeredTask != null){
            getLogger().debug(".registerOversightTask(): Exit, Task already registered, returning it!");
            return(registeredTask);
        }
        //
        // It's not registered, so register it!
        //
        getLogger().trace(".registerOversightTask(): [Registering PetasosOversightTask into OversightTaskRegister] Start");
        synchronized (this.oversightTaskRegistryLock) {
            getOversightTaskRegistry().put(task.getTaskId(), task);
        }
        getLogger().trace(".registerOversightTask(): [Registering PetasosOversightTask into OversightTaskRegister] Finish");
        //
        // Change the registered flag in the Task
        task.setRegistered(true);
        //
        // All done!
        getLogger().debug(".registerOversightTask(): Exit, task->{}", task);
        return(task);
    }

    public PetasosOversightTask unregisterOversightTask(PetasosOversightTask task) {
        getLogger().debug(".unregisterOversightTask(): Entry, task->{}", task);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (task == null) {
            getLogger().debug(".unregisterOversightTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosFulfillmentTask contain all the necessary bits?
        boolean isBadTask = true;
        if (task.hasTaskId()){
            isBadTask = false;
        }
        if (isBadTask) {
            getLogger().debug(".unregisterOversightTask(): Exit, the Task does not have all the required bits (no IdSegment)!");
            return (null);
        }
        //
        // Is the task actually registered?
        TaskIdType idSegment = task.getTaskId();
        synchronized (this.oversightTaskRegistryLock) {
            if (getOversightTaskRegistry().containsKey(idSegment)) {
                getOversightTaskRegistry().remove(task.getTaskId());
            }
        }
        //
        // Change the registered flag in the Task
        task.setRegistered(false);
        //
        // All done!
        getLogger().debug(".unregisterFulfillmentTask(): Exit, the now unregisteredTask->{}", task);
        return(task);
    }

    public PetasosOversightTask unregisterOversightTask(TaskIdType taskIdentity) {
        getLogger().debug(".unregisterOversightTask(): Entry, taskIdentity->{}", taskIdentity);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (taskIdentity == null) {
            getLogger().debug(".unregisterOversightTask(): Exit, taskIdentity is null, so nothing to do!");
            return(null);
        }
        //
        // Is the task actually registered, if so, remove it
        PetasosOversightTask registeredTask = null;
        synchronized (this.oversightTaskRegistryLock) {
            if (getOversightTaskRegistry().containsKey(taskIdentity)) {
                registeredTask = getOversightTaskRegistry().get(taskIdentity);
                getOversightTaskRegistry().remove(taskIdentity);
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

    public PetasosOversightTask getOversightTask(TaskIdType taskIdentity){
        getLogger().debug(".getOversightTask(): Entry, taskIdentity->{}", taskIdentity);
        //
        // (Definsive Programming) Were we given a bad parameter?
        if(taskIdentity == null){
            getLogger().debug(".getOversightTask(): Exit, taskIdentity is null, therefore returning null");
            return(null);
        }
        PetasosOversightTask registeredTask = null;
        synchronized (this.oversightTaskRegistryLock) {
            if (getOversightTaskRegistry().containsKey(taskIdentity)) {
                registeredTask = getOversightTaskRegistry().remove(taskIdentity);
            }
        }
        getLogger().debug(".getFulfillmentTask(): Exit, registeredTask->{}", registeredTask);
        return(registeredTask);
    }

    //
    // Getters (and Setters)
    //

    public ConcurrentHashMap<TaskIdType, PetasosOversightTask> getOversightTaskRegistry() {
        return oversightTaskRegistry;
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
