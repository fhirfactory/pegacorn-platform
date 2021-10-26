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

package net.fhirfactory.pegacorn.petasos.core.tasks.caches;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.processingplant.LocalPetasosFulfillmentTaskDM;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;

/**
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class LocalPetasosFulfilmentTaskBroker {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosFulfilmentTaskBroker.class);
//    private FDN nodeInstanceFDN;

    @Inject
    LocalPetasosFulfillmentTaskDM parcelCacheDM;

    @Inject
    MOAServicesAuditBroker auditServicesBroker;

    //
    // PetasosFulfillmentTask Registration
    //

    public PetasosFulfillmentTask registerFulfillmentTask(ActivityID activityID, UoW unitOfWork, boolean synchronousWriteToAudit){
        PetasosFulfillmentTask registeredParcel = registerFulfillmentTask(activityID, unitOfWork, null, null, synchronousWriteToAudit);
        return(registeredParcel);
    }

    public PetasosFulfillmentTask registerFulfillmentTask(ActivityID activityID, UoW unitOfWork, String portType, String portValue, boolean synchronousWriteToAudit){
        LOG.debug(".registerParcel(): Entry"); 
        if ((unitOfWork == null) || (activityID == null)) {
            throw (new IllegalArgumentException("unitOfWork, wupTypeID or wupInstanceID are null in method invocation"));
        }
        LOG.trace(".registerParcel(): Checking and/or Creating a WUAEpisde ID");
        if(!activityID.hasPresentEpisodeIdentifier()) {
        	FDN newWUAFDN = new FDN(activityID.getPresentWUPFunctionToken().toVersionBasedFDNToken());
        	FDN uowTypeFDN = new FDN(unitOfWork.getTypeID());
        	newWUAFDN.appendFDN(uowTypeFDN);
        	PetasosEpisodeIdentifier wuaEpisodeToken = new PetasosEpisodeIdentifier(newWUAFDN.getToken());
        	activityID.setPresentEpisodeIdentifier(wuaEpisodeToken);
        }
        // 1st, lets register the parcel
        LOG.trace(".registerParcel(): check for existing ResilienceParcel instance for this WUP/UoW combination");
        PetasosFulfillmentTask fulfillmentTask =  parcelCacheDM.getCurrentFulfillmetTaskForWUP(activityID.getPresentWUPIdentifier(), unitOfWork.getTypeID());
        if(fulfillmentTask != null){
            LOG.trace(".registerParcel(): Well, there seems to be a Parcel already for this WUPInstanceID/UoWInstanceID. Odd, but let's use it!");
        } else {
            LOG.trace(".registerParcel(): Attempted to retrieve existing ResilienceParcel, and there wasn't one, so let's create it!");
            fulfillmentTask = new ResilienceParcel(activityID, unitOfWork);
            if(portType != null){
                fulfillmentTask.setAssociatedPortType(portType);
                fulfillmentTask.setAssociatedPortValue(portValue);
            }
            parcelCacheDM.addFulfillmentTask(fulfillmentTask);
            LOG.trace(".registerParcel(): Set the PresentParcelInstanceID in the ActivityID (ActivityID), ParcelInstanceID --> {}", fulfillmentTask.getIdentifier());
            activityID.setPresentParcelIdentifier(fulfillmentTask.getIdentifier());
            Date registrationDate = Date.from(Instant.now());
            LOG.trace(".registerParcel(): Set the Registration Date --> {}", registrationDate);
            fulfillmentTask.setRegistrationDate(registrationDate);
            LOG.trace(".registerParcel(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
//            fulfillmentTask.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
            LOG.trace(".registerParcel(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_REGISTERED);
            fulfillmentTask.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_REGISTERED);
            LOG.trace(".registerParcel(): Doing an Audit Write");
            auditServicesBroker.logActivity(fulfillmentTask);

        }
        LOG.debug(".registerParcel(): Exit");
        return(fulfillmentTask);
    }

    @Transactional
    public ResilienceParcel notifyParcelProcessingStart(ResilienceParcelIdentifier parcelID) {
        LOG.debug(".notifyParcelProcessingStart(): Entry, parcelID --> {}", parcelID);
        if (parcelID == null) {
            throw (new IllegalArgumentException("parcelID is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingStart(): retrieve existing Parcel");
        ResilienceParcel currentParcel = parcelCacheDM.getFulfillmentTask(parcelID);
        Date startDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingStart(): Set the Start Date --> {}", startDate);
        currentParcel.setStartDate(startDate);
        LOG.trace(".notifyParcelProcessingStart(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
//        currentParcel.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
        LOG.trace(".notifyParcelProcessingStart(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE);
        currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE);
        // TODO Check to see if we should do an Audit Entry when we start processing (as well as when it is registered)
        // LOG.trace(".notifyParcelProcessingStart(): Doing an Audit Write, note that it is asynchronous by design");
        // auditWriter.writeAuditEntry(currentParcel,false);
        LOG.debug(".notifyParcelProcessingStart(): Exit, returning finished Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public ResilienceParcel notifyParcelProcessingFinish(ResilienceParcelIdentifier parcelID, UoW unitOfWork) {
        LOG.debug(".notifyParcelProcessingFinish(): Entry, parcelID (ResilienceParcelIdentifier) --> {}, unitOfWork (UoW) --> {}", parcelID, unitOfWork);
        if ((unitOfWork == null) || (parcelID == null)) {
            throw (new IllegalArgumentException("unitOfWork or parcelID are null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFinish(): retrieve existing Parcel");
        ResilienceParcel currentParcel = parcelCacheDM.getFulfillmentTask(parcelID);
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
        LOG.trace(".notifyParcelProcessingFinish(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
//        currentParcel.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
        LOG.trace(".notifyParcelProcessingFinish(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
        currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
        // TODO Check to see if we should do an Audit Entry when we finish processing
        // LOG.trace(".notifyParcelProcessingFinish(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
       	LOG.debug(".notifyParcelProcessingFinish(): Exit, parcelInstance (ResilienceParcel) --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public ResilienceParcel notifyParcelProcessingFailure(ResilienceParcelIdentifier parcelID, UoW unitOfWork) {
        LOG.debug(".notifyParcelProcessingFailure(): Entry, parcelID --> {}, unitOfWork --> {}", parcelID, unitOfWork);
        if ((unitOfWork == null) || (parcelID == null)) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFailure(): unitOfWork or parcelID are null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFailure(): retrieve existing Parcel");
        ResilienceParcel currentParcel = parcelCacheDM.getFulfillmentTask(parcelID);
        LOG.trace(".notifyParcelProcessingFailure(): update the UoW (Egress Content)");
        currentParcel.getActualUoW().setEgressContent(unitOfWork.getEgressContent());
        LOG.trace(".notifyParcelProcessingFailure(): update the UoW Processing Outcome --> {}", unitOfWork.getProcessingOutcome());
        currentParcel.getActualUoW().setProcessingOutcome(unitOfWork.getProcessingOutcome());
        Date finishDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingFailure(): Set the Finish Date --> {}", finishDate);
        currentParcel.setFinishedDate(finishDate);
        LOG.trace(".notifyParcelProcessingFailure(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
//        currentParcel.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
        LOG.trace(".notifyParcelProcessingFailure(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
        currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
        LOG.trace(".notifyParcelProcessingFailure(): Doing an Audit Write, note that it is asynchronous by desgin");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingFailure(): Exit, returning failed Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public ResilienceParcel notifyParcelProcessingFinalisation(ResilienceParcelIdentifier parcelID) {
        LOG.debug(".notifyParcelProcessingFinalisation(): Entry, parcelID --> {}, unitOfWork --> {}", parcelID);
        if (parcelID == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFinalisation(): parcelID is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingFinalisation(): retrieve existing Parcel");
        ResilienceParcel currentParcel = parcelCacheDM.getFulfillmentTask(parcelID);
        LOG.trace(".notifyParcelProcessingFinalisation(): checking to see if finish date has been set and, if not, setting it");
        if(!currentParcel.hasFinishedDate()) {
            Date finishDate = Date.from(Instant.now());
            LOG.trace(".notifyParcelProcessingFinalisation(): Set the Finish Date --> {}", finishDate);
            currentParcel.setFinishedDate(finishDate);
        }
        Date finalisationDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Finalisation Date --> {}", finalisationDate);
        currentParcel.setFinalisationDate(finalisationDate);
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
//        currentParcel.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
        LOG.trace(".notifyParcelProcessingFinalisation(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingFinalisation(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingFinalisation(): Exit, returning finished Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public ResilienceParcel notifyParcelProcessingCancellation(ResilienceParcelIdentifier parcelID) {
        LOG.debug(".notifyParcelProcessingCancellation(): Entry, parcelID --> {}", parcelID);
        if (parcelID == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingFinalisation(): parcelID is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingCancellation(): retrieve existing Parcel");
        ResilienceParcel currentParcel = parcelCacheDM.getFulfillmentTask(parcelID);
        LOG.trace(".notifyParcelProcessingCancellation(): checking to see if finish date has been set and, if not, setting it");
        if(!currentParcel.hasFinishedDate()) {
            Date finishDate = Date.from(Instant.now());
            LOG.trace(".notifyParcelProcessingCancellation(): Set the Finish Date --> {}", finishDate);
            currentParcel.setFinishedDate(finishDate);
        }
        Date finalisationDate = Date.from(Instant.now());
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Finalisation Date --> {}", finalisationDate);
        currentParcel.setFinalisationDate(finalisationDate);
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Parcel Finalisation Status --> {} ", TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
//        currentParcel.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
        LOG.trace(".notifyParcelProcessingCancellation(): Set the Parcel Processing Status --> {}", ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        LOG.trace(".notifyParcelProcessingCancellation(): Doing an Audit Write, note that it is asynchronous by design");
        auditServicesBroker.logActivity(currentParcel);
        LOG.debug(".notifyParcelProcessingCancellation(): Exit, returning finished Parcel --> {}", currentParcel);
        return(currentParcel);
    }

    @Transactional
    public void notifyParcelProcessingPurge(ResilienceParcelIdentifier parcelID) {
        LOG.debug(".notifyParcelProcessingPurge(): Entry, parcelID --> {}, unitOfWork --> {}", parcelID);
        if (parcelID == null) {
            throw (new IllegalArgumentException(".notifyParcelProcessingPurge(): parcelID is null in method invocation"));
        }
        LOG.trace(".notifyParcelProcessingPurge(): retrieve existing Parcel");
        // TODO: Ascertain if we need to do an audit-entry for this.
        //        LOG.trace(".notifyParcelProcessingPurge(): Doing an Audit Write, note that it is asynchronous by design");
        //        auditWriter.writeAuditEntry(currentParcel,false);
        //LOG.debug(".notifyParcelProcessingPurge(): Exit, returning finished Parcel --> {}", currentParcel);
    }
}
