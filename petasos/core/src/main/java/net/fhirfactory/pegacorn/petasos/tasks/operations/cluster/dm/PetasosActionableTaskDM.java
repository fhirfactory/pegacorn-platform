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
package net.fhirfactory.pegacorn.petasos.tasks.operations.cluster.dm;

import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
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
public class PetasosActionableTaskDM {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosActionableTaskDM.class);

    private int entriesAdded;
    private int entriesRemoved;

    private ConcurrentHashMap<TaskIdType, PetasosActionableTask> actionableTaskRegistry;
    private Object actionableTaskRegistryLock;

    //
    // Constructor
    //

    public PetasosActionableTaskDM() {
        this.actionableTaskRegistry = new ConcurrentHashMap<>();
        this.actionableTaskRegistryLock = new Object();
        this.entriesRemoved = 0;
        this.entriesAdded = 0;
    }

    //
    // Metrics Reporting
    //

    public String getCacheName(){
        return("ProcessingPlantActionableTaskRegistry");
    }

    public String getMetrics(){
        String metrics = "CurrentSize["+ actionableTaskRegistry.size() +"], TotalEntriesAdded["+entriesAdded+"], TotalEntriesRemoved["+entriesRemoved+"]";
        return(metrics);
    }

    //
    // Actionable Task Registry Methods
    //

    public PetasosActionableTask registerActionableTask(PetasosActionableTask task) {
        getLogger().debug(".registerActionableTask(): Entry, task->{}", task);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (task == null) {
            getLogger().debug(".registerActionableTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosTask contain all the necessary bits?
        boolean isBadTask = true;
        if(task.hasTaskId() && task.hasTaskWorkItem()) {
            isBadTask = false;
        }
        if(isBadTask){
            getLogger().debug(".registerActionableTask(): Exit, the PetasosActionableTask does not have all the required bits!");
            return(null);
        }
        //
        // Is the task already registered?
        TaskIdType idSegment = task.getTaskId();
        PetasosActionableTask registeredTask = null;
        synchronized (this.actionableTaskRegistryLock) {
            if (getActionableTaskRegistry().containsKey(idSegment)) {
                registeredTask = getActionableTaskRegistry().get(idSegment);
            }
        }
        if(registeredTask != null){
            getLogger().debug(".registerActionableTask(): Exit, Task already registered, returning it!");
            return(registeredTask);
        }
        //
        // It's not registered, so register it!
        //
        getLogger().trace(".registerActionableTask(): [Registering PetasosOversightTask into OversightTaskRegister] Start");
        synchronized (this.actionableTaskRegistryLock) {
            getActionableTaskRegistry().put(task.getTaskId(), task);
        }
        getLogger().trace(".registerActionableTask(): [Registering PetasosOversightTask into OversightTaskRegister] Finish");
        //
        // Change the registered flag in the Task
        task.setRegistered(true);
        //
        // Increment the Metrics counter
        incrementEntriesAdded();
        //
        // All done!
        getLogger().debug(".registerActionableTask(): Exit, task->{}", task);
        return(task);
    }

    public PetasosActionableTask unregisterActionableTask(PetasosActionableTask task) {
        getLogger().debug(".unregisterActionableTask(): Entry, task->{}", task);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (task == null) {
            getLogger().debug(".unregisterActionableTask(): Exit, task is null, so nothing to do!");
            return(null);
        }
        //
        // (Defensive Programming) Does the PetasosActionableTask contain all the necessary bits?
        boolean isBadTask = true;
        if (task.hasTaskId()){
            isBadTask = false;
        }
        if (isBadTask) {
            getLogger().debug(".unregisterActionableTask(): Exit, the Task does not have all the required bits (no IdSegment)!");
            return (null);
        }
        //
        // Is the task actually registered?
        TaskIdType idSegment = task.getTaskId();
        synchronized (this.actionableTaskRegistryLock) {
            if (getActionableTaskRegistry().containsKey(idSegment)) {
                getActionableTaskRegistry().remove(task.getTaskId());
            }
        }
        //
        // Change the registered flag in the Task
        task.setRegistered(false);
        //
        // Increment the Metrics counter
        incrementEntriesRemoved();
        //
        // All done!
        getLogger().debug(".unregisterActionableTask(): Exit, the now unregisteredTask->{}", task);
        return(task);
    }

    public PetasosActionableTask unregisterActionableTask(TaskIdType taskIdentity) {
        getLogger().debug(".unregisterActionableTask(): Entry, taskIdentity->{}", taskIdentity);
        //
        // (Defensive Programming) Were we given a bad parameter?
        if (taskIdentity == null) {
            getLogger().debug(".unregisterOversightTask(): Exit, taskIdentity is null, so nothing to do!");
            return(null);
        }
        //
        // Is the task actually registered, if so, remove it
        PetasosActionableTask registeredTask = null;
        synchronized (this.actionableTaskRegistryLock) {
            if (getActionableTaskRegistry().containsKey(taskIdentity)) {
                registeredTask = getActionableTaskRegistry().get(taskIdentity);
                getActionableTaskRegistry().remove(taskIdentity);
            }
        }
        //
        // Change the registered flag in the Task
        if(registeredTask != null) {
            registeredTask.setRegistered(false);
        }
        //
        // Increment the Metrics counter
        incrementEntriesRemoved();
        //
        // All done!
        getLogger().debug(".unregisterActionableTask(): Exit, the now unregisteredTask->{}", registeredTask);
        return(registeredTask);
    }

    public PetasosActionableTask getActionableTask(TaskIdType taskIdentity){
        getLogger().debug(".getOversightTask(): Entry, taskIdentity->{}", taskIdentity);
        //
        // (Definsive Programming) Were we given a bad parameter?
        if(taskIdentity == null){
            getLogger().debug(".getOversightTask(): Exit, taskIdentity is null, therefore returning null");
            return(null);
        }
        PetasosActionableTask registeredTask = null;
        synchronized (this.actionableTaskRegistryLock) {
            if (getActionableTaskRegistry().containsKey(taskIdentity)) {
                registeredTask = getActionableTaskRegistry().remove(taskIdentity);
            }
        }
        getLogger().debug(".getFulfillmentTask(): Exit, registeredTask->{}", registeredTask);
        return(registeredTask);
    }


    //
    // Getters (and Setters)
    //

    public ConcurrentHashMap<TaskIdType, PetasosActionableTask> getActionableTaskRegistry() {
        return actionableTaskRegistry;
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
