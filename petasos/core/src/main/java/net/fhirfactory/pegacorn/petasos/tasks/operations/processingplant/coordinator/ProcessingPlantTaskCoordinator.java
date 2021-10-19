/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.coordinator;

import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.PetasosFulfillmentTaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.tasks.operations.cluster.coordinator.ClusterTaskCoordinator;
import net.fhirfactory.pegacorn.petasos.tasks.operations.cluster.dm.PetasosTaskJobCardDM;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.im.PetasosFulfillmentTaskIM;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.im.PetasosOversightTaskIM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ProcessingPlantTaskCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantTaskCoordinator.class);

    @Inject
    private PetasosFulfillmentTaskIM fulfillmentTaskIM;

    @Inject
    private PetasosOversightTaskIM oversightTaskIM;

    @Inject
    private ClusterTaskCoordinator clusterTaskCoordinator;

    @Inject
    private PetasosTaskJobCardDM jobCardDM;

    //
    // Business Methods
    //

    public PetasosActionableTask registerActionableTask(PetasosActionableTask task){
        
        clusterTaskCoordinator.registerActionableTask(task);
        return(task);
    }

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask task){

        return(task);
    }

    public PetasosTaskJobCard requestExecutionStatusChange(PetasosTaskJobCard jobCard){

        return(jobCard);
    }

    public PetasosTaskJobCard notifyExecutionStart(PetasosTaskJobCard jobCard){

        return(jobCard);
    }

    public PetasosTaskJobCard notifyExecutionFinish(PetasosTaskJobCard jobCard){

        return(jobCard);
    }

    public PetasosTaskJobCard notifyExecutionFailure(PetasosTaskJobCard jobCard){

        return(jobCard);
    }

    public PetasosTaskJobCard notifyExecutionCancellation(PetasosTaskJobCard jobCard){

        return(jobCard);
    }
    
    public PetasosTaskJobCard notifyTaskFulfillmentStart(PetasosTaskJobCard jobCard) {
        LOG.debug(".notifyTaskFulfillmentStart(): Entry, jobCard->{}", jobCard);
        if (jobCard == null) {
            throw (new IllegalArgumentException("jobCard is null in method invocation"));
        }
        //
        // We need to check if no other WUP is attempting to process this Task and, at the same time,
        // notify all other WUP's via their coordinators that some IS IN FACT processing the Task.
        LOG.trace(".notifyTaskFulfillmentStart(): check with Cluster Coordinator");
        PetasosTaskJobCard clusterCoordinatorAuthorisedJobCard = clusterTaskCoordinator.notifyTaskFulfillmentStart(jobCard);
        //
        // We now have a decision tree. The happy flow is for the Cluster Coordinator to rubber-stamp the processing
        // of the task by the WUP which called this method. If that is the case, we then need to iterate through any
        // other JobCard's under the control of this ProcessingPlant Coordinator and which are wanting to process
        // the associated ActionableTask --> and tell them to wait.
        // If, on the other hand, the Cluster Coordinator doesn't want the calling WUP to process this ActionableTask,
        // then we need to update accordingly.
        //
        // Check to see the status as returned by the Cluster Coordinator
        if(clusterCoordinatorAuthorisedJobCard.getGrantedStatus().equals(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING)){
            //
            // All good - we can let this one continue on. So, we just need to make sure no other WUPs under
            // our control are doing anything for the associated ActionableTask.
            List<PetasosTaskJobCard> taskFilteredByProcessingPlant = jobCardDM.getJobCardsForActionableTaskFilteredByProcessingPlant(jobCard.getActionableTaskIdentifier(), jobCard.getProcessingPlant());
            for(PetasosTaskJobCard currentCard: taskFilteredByProcessingPlant){
                if()
            }
        } else {
            //
            // Put the brakes on!
        }
        PetasosFulfillmentTask fulfillmentTask = fulfillmentTaskIM.getFulfillmentTask(taskId);
        Instant now = Instant.now();
        LOG.trace(".notifyTaskFulfillmentStart(): Set the Start Date->{}", now);
        fulfillmentTask.getTaskFulfillment().setStartDate(startDate);
        LOG.trace(".notifyTaskFulfillmentStart(): Set the Parcel Finalisation Status --> {} ", PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        fulfillmentTask.setFinalisationStatus(PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        LOG.trace(".notifyTaskFulfillmentStart(): Set the Parcel Processing Status --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
        fulfillmentTask.setProcessingStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
        // TODO Check to see if we should do an Audit Entry when we start processing (as well as when it is registered)
        // LOG.trace(".notifyParcelProcessingStart(): Doing an Audit Write, note that it is asynchronous by design");
        // auditWriter.writeAuditEntry(fulfillmentTask,false);
        LOG.debug(".notifyTaskFulfillmentStart(): Exit, returning finished Parcel --> {}", fulfillmentTask);
        return(fulfillmentTask);
    }

    @Transactional
    public PetasosFulfillmentTask notifyParcelProcessingFinish(FulfillmentTrackingIdType taskId, UoW unitOfWork) {
        LOG.debug(".notifyParcelProcessingFinish(): Entry, taskId (PetasosFulfillmentTaskIdentifier) --> {}, unitOfWork (UoW) --> {}", taskId, unitOfWork);
        if ((unitOfWork == null) || (taskId == null)) {
            throw (new IllegalArgumentException("unitOfWork or taskId are null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFinish(): retrieve existing Parcel");
        PetasosFulfillmentTask currentParcel = fulfillmentTaskIM.getTask(taskId);
        LOG.trace(".notifyParcelProcessingFinish(): Parcel Retrieved, contents --> {}", currentParcel);
        LOG.trace(".notifyParcelProcessingFinish(): update the UoW --> but only if the UoW content comes from the Agent, not the actual WUP itself");
        if(!(unitOfWork == currentParcel.getActualUoW())) {
            LOG.trace(".notifyParcelProcessingFinish(): update the UoW (Egress Content)");
            currentParcel.getActualUoW().setEgressContent(unitOfWork.getEgressContent());
            LOG.trace(".notifyParcelProcessingFinish(): update the UoW Processing Outcome --> {}", unitOfWork.getProcessingOutcome());
            currentParcel.getActualUoW().setProcessingOutcome(unitOfWork.getProcessingOutcome());
        }
        Date finishDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingFinish(): Set the Finish Date --> {}", finishDate);
        currentParcel.setFinishedDate(finishDate);
        LOG.trace(".notifyParcelProcessingFinish(): Set the Parcel Finalisation Status --> {} ", PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        currentParcel.setFinalisationStatus(PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        LOG.trace(".notifyParcelProcessingFinish(): Set the Parcel Processing Status --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
        currentParcel.setProcessingStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
        // TODO Check to see if we should do an Audit Entry when we finish processing
        // LOG.trace(".notifyParcelProcessingFinish(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingFinish(): Exit, parcelInstance (PetasosFulfillmentTask) --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public PetasosFulfillmentTask notifyParcelProcessingFailure(FulfillmentTrackingIdType taskId, UoW unitOfWork) {
        LOG.debug(".notifyParcelProcessingFailure(): Entry, taskId --> {}, unitOfWork --> {}", taskId, unitOfWork);
        if ((unitOfWork == null) || (taskId == null)) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFailure(): unitOfWork or taskId are null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFailure(): retrieve existing Parcel");
        PetasosFulfillmentTask currentParcel = fulfillmentTaskIM.getTask(taskId);
        LOG.trace(".notifyParcelProcessingFailure(): update the UoW (Egress Content)");
        currentParcel.getActualUoW().setEgressContent(unitOfWork.getEgressContent());
        LOG.trace(".notifyParcelProcessingFailure(): update the UoW Processing Outcome --> {}", unitOfWork.getProcessingOutcome());
        currentParcel.getActualUoW().setProcessingOutcome(unitOfWork.getProcessingOutcome());
        Date finishDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingFailure(): Set the Finish Date --> {}", finishDate);
        currentParcel.setFinishedDate(finishDate);
        LOG.trace(".notifyParcelProcessingFailure(): Set the Parcel Finalisation Status --> {} ", PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        currentParcel.setFinalisationStatus(PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED);
        LOG.trace(".notifyParcelProcessingFailure(): Set the Parcel Processing Status --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
        currentParcel.setProcessingStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
        LOG.trace(".notifyParcelProcessingFailure(): Doing an Audit Write, note that it is asynchronous by desgin");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingFailure(): Exit, returning failed Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public PetasosFulfillmentTask notifyParcelProcessingFinalisation(FulfillmentTrackingIdType taskId) {
        LOG.debug(".notifyParcelProcessingFinalisation(): Entry, taskId --> {}, unitOfWork --> {}", taskId);
        if (taskId == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFinalisation(): taskId is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFinalisation(): retrieve existing Parcel");
        PetasosFulfillmentTask currentParcel = fulfillmentTaskIM.getTask(taskId);
        LOG.trace(".notifyParcelProcessingFinalisation(): checking to see if finish date has been set and, if not, setting it");
        if(!currentParcel.hasFinishedDate()) {
            Date finishDate = Date.from(Instant.now());
            LOG.trace(".notifyParcelProcessingFinalisation(): Set the Finish Date --> {}", finishDate);
            currentParcel.setFinishedDate(finishDate);
        }
        Date finalisationDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Finalisation Date --> {}", finalisationDate);
        currentParcel.setFinalisationDate(finalisationDate);
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Parcel Finalisation Status --> {} ", PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_FINALISED);
        currentParcel.setFinalisationStatus(PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Parcel Processing Status --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
        currentParcel.setProcessingStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingFinalisation(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingFinalisation(): Exit, returning finished Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public PetasosFulfillmentTask notifyParcelProcessingCancellation(FulfillmentTrackingIdType taskId) {
        LOG.debug(".notifyParcelProcessingCancellation(): Entry, taskId --> {}", taskId);
        if (taskId == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFinalisation(): taskId is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingCancellation(): retrieve existing Parcel");
        PetasosFulfillmentTask currentParcel = fulfillmentTaskIM.getTask(taskId);
        LOG.trace(".notifyParcelProcessingCancellation(): checking to see if finish date has been set and, if not, setting it");
        if(!currentParcel.hasFinishedDate()) {
            Date finishDate = Date.from(Instant.now());
            LOG.trace(".notifyParcelProcessingCancellation(): Set the Finish Date --> {}", finishDate);
            currentParcel.setFinishedDate(finishDate);
        }
        Date finalisationDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Finalisation Date --> {}", finalisationDate);
        currentParcel.setFinalisationDate(finalisationDate);
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Parcel Finalisation Status --> {} ", PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_FINALISED);
        currentParcel.setFinalisationStatus(PetasosFulfillmentTaskFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Parcel Processing Status --> {}", FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
        currentParcel.setProcessingStatus(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingCancellation(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingCancellation(): Exit, returning finished Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public void notifyParcelProcessingPurge(FulfillmentTrackingIdType taskId) {
        LOG.debug(".notifyParcelProcessingPurge(): Entry, taskId --> {}, unitOfWork --> {}", taskId);
        if (taskId == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingPurge(): taskId is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingPurge(): retrieve existing Parcel");
        // TODO: Ascertain if we need to do an audit-entry for this.
        //        LOG.trace(".notifyParcelProcessingPurge(): Doing an Audit Write, note that it is asynchronous by design");
        //        auditWriter.writeAuditEntry(currentParcel,false);
        //LOG.debug(".notifyParcelProcessingPurge(): Exit, returning finished Parcel --> {}", currentParcel);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
