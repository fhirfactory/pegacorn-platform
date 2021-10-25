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
package net.fhirfactory.pegacorn.petasos.core.tasks.processingplant;

import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class acts as the Data Manager for the Parcel Cache within the local
 * ProcessingPlant. That is, it is the single management point for the
 * Parcel element set itself. It does not implement business logic associated
 * with the surrounding activity associated with each Parcel beyond provision
 * of helper methods associated with search-set and status-set collection
 * methods.
 *
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
@ApplicationScoped
public class LocalPetasosFulfillmentTaskDM {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosFulfillmentTaskDM.class);

    private ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> fulfillmentTaskCache;

    public LocalPetasosFulfillmentTaskDM() {
        fulfillmentTaskCache = new ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask>();
    }

    public void addFulfillmentTask(PetasosFulfillmentTask task) {
        LOG.debug(".addFulfillmentTask(): Entry, task --> {}", task);
        if (task == null) {
            return;
        }
        if (!task.hasTaskId()) {
            return;
        }
        TaskIdType taskId = task.getTaskId();
        if(fulfillmentTaskCache.containsKey(taskId)){
            fulfillmentTaskCache.remove(taskId);
        }
        fulfillmentTaskCache.put(taskId, task);
    }


    public PetasosFulfillmentTask getFulfillmentTask(FDNToken taskIdToken) {
        LOG.debug(".getFulfillmentTask(): Entry, taskIdToken --> {}", taskIdToken);
        TaskIdType taskId = new ResilienceParcelIdentifier(taskIdToken);
        if (fulfillmentTaskCache.containsKey(taskId)) {
            return (fulfillmentTaskCache.get(taskId));
        }
        return (null);
    }

    public PetasosFulfillmentTask getFulfillmentTask(TaskIdType taskId) {
        LOG.debug(".getFulfillmentTask(): Entry, taskId --> {}", taskId);
        if (fulfillmentTaskCache.containsKey(taskId)) {
            return (fulfillmentTaskCache.get(taskId));
        }
        return (null);
    }

    public void removeFulfillmentTask(ResilienceParcel parcel) {
        LOG.debug(".removeParcel(): Entry, parcel --> {}", parcel);
        if (parcel == null) {
            return;
        }
        removeFulfillmentTask(parcel.getTaskId());
    }

    public void removeFulfillmentTask(ResilienceParcelIdentifier parcelInstanceID) {
        LOG.debug(".removeFulfillmentTask(): Entry, parcelInstanceID --> {}", parcelInstanceID);
        if (parcelInstanceID == null) {
            return;
        }
        if(fulfillmentTaskCache.containsKey(parcelInstanceID)) {
            fulfillmentTaskCache.remove(parcelInstanceID);
        }
    }

    public void removeFulfillmentTask(TaskIdType taskId) {
        LOG.debug(".removeParcel(): Entry, taskId --> {}", taskId);
        if (taskId == null) {
            return;
        }
        if(fulfillmentTaskCache.containsKey(taskId)) {
            fulfillmentTaskCache.remove(taskId);
        }
    }

    public void updateFulfillmentTask(PetasosFulfillmentTask fulfillmentTask) {
        LOG.debug(".updateFulfillmentTask() Entry, task --> {}", fulfillmentTask);
        if (fulfillmentTask == null) {
            throw (new IllegalArgumentException("fulfillmentTask is null"));
        }
        if (fulfillmentTaskCache.containsKey(fulfillmentTask.getTaskId())) {
            fulfillmentTaskCache.remove(fulfillmentTask.getTaskId());
        }
        fulfillmentTaskCache.put(fulfillmentTask.getTaskId(), fulfillmentTask);
    }


    public List<PetasosFulfillmentTask> getFulfillmentTaskSet() {
        LOG.debug(".getFulfillmentTaskSet(): Entry");
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        fulfillmentTaskCache.entrySet().forEach(entry -> parcelList.add(entry.getValue()));
        return (parcelList);
    }

    public List<PetasosFulfillmentTask> getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum status) {
        LOG.debug(".getFulfillmentTaskByStatus(): Entry, status --> {}", status);
        List<PetasosFulfillmentTask> taskList = new LinkedList<PetasosFulfillmentTask>();
        Iterator<PetasosFulfillmentTask> parcelListIterator = getFulfillmentTaskSet().iterator();
        while (parcelListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = parcelListIterator.next();
            if (currentParcel.hasTaskFulfillment()) {
                if(currentParcel.getTaskFulfillment().hasStatus()){
                    if(currentParcel.getTaskFulfillment().getStatus().equals(status)){
                        taskList.add(currentParcel);
                    }
                }
            }
        }
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getActiveFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getFinishedFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getFinalisedFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getInProgressFulfillmentTasks() {
        LOG.debug(".getInProgressParcelSet(): Entry");
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        parcelList.addAll(getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE));
        parcelList.addAll(getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_INITIATED));
        parcelList.addAll(getFulfillmentTaskByStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_REGISTERED));
        return (parcelList);
    }

    public List<PetasosFulfillmentTask> getFulfillmentTaskByActionableTaskId(TaskIdType taskId) {
        LOG.debug(".getInProgressParcelSet(): Entry, taskId --> {}" + taskId);
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        Iterator<PetasosFulfillmentTask> parcelListIterator = getFulfillmentTaskSet().iterator();
        while (parcelListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = parcelListIterator.next();
             if (currentParcel.hasActionableTaskId()) {
                if (currentParcel.getActionableTaskId().equals(taskId)) {
                    parcelList.add(currentParcel);
                }
            }
        }
        return (parcelList);
    }

    public PetasosFulfillmentTask getCurrentFulfillmetTaskForWUP(WUPIdentifier wupInstanceID, FDNToken uowInstanceID) {
        LOG.debug(".getCurrentParcel(): Entry, wupInstanceID --> {}" + wupInstanceID);
        List<PetasosFulfillmentTask> taskList = new LinkedList<PetasosFulfillmentTask>();
        Iterator<PetasosFulfillmentTask> taskListIterator = getFulfillmentTaskSet().iterator();
        while (taskListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = taskListIterator.next();
            if (currentParcel.hasTaskFulfillment()) {
                if (currentParcel.getTaskFulfillment().hasFulfillerComponent()){
                    if(currentParcel.getTaskFulfillment().getFulfillerComponent().getNodeFDN().getToken().equals(wupInstanceID)){
                        return (currentParcel);
                    }
                }
            }
        }
        return (null);
    }
}
