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

package net.fhirfactory.pegacorn.petasos.core.tasks.management;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.cluster.SharedTaskJobCardCache;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.manager.WorkUnitProcessorFrameworkManager;
import net.fhirfactory.pegacorn.petasos.model.audit.PetasosParcelAuditTrailEntry;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class LocalTaskActivityController {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskActivityController.class);

    @Inject
    private SharedTaskJobCardCache jobCardCache;

    @Inject
    WorkUnitProcessorFrameworkManager wupFrameworkManager;

    @Inject
    MOAServicesAuditBroker auditWriter;
    

    /**
     *
     * @param jobCard
     * @return
     */
    public PetasosTaskJobCard registerTaskJobCard(PetasosTaskJobCard jobCard) {
        LOG.debug(".registerTaskJobCard(): Entry, jobCard->{}", jobCard);

        //
        // Defensive Programming
        if (jobCard == null) {
            LOG.debug(".registerTaskJobCard(): Exit, jobCard is null, returning null");
            return(null);
        }
        if(!jobCard.hasActionableTaskIdentifier()){
            LOG.debug(".registerTaskJobCard(): Exit, jobCard has no associated Actionable Task, returning null");
            return(null);
        }

        //
        // Now check to see if it already registered.
        jobCardCache.addJobCard(jobCard);


        ResilienceParcel newParcel = parcelServicesIM.registerFulfillmentTask(jobCard.getActivityID(), initialUoW, false);
        jobCard.getActivityID().setPresentParcelIdentifier(newParcel.getIdentifier());
        ParcelStatusElement statusElement = rasController.registerNewWorkUnitActivity(jobCard);
        return (statusElement);
    }

    /**
     *
     * @param jobCard
     * @param initialUoW
     * @return
     */
    public ParcelStatusElement registerSystemEdgeWorkUnitActivity(WUPJobCard jobCard, UoW initialUoW) {
        if ((jobCard == null) || (initialUoW == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard or initialUoW are null"));
        }
        ResilienceParcel newParcel = parcelServicesIM.registerFulfillmentTask(jobCard.getActivityID(), initialUoW, true);
        jobCard.getActivityID().setPresentParcelIdentifier(newParcel.getIdentifier());
        ParcelStatusElement statusElement = rasController.registerNewWorkUnitActivity(jobCard);
        return (statusElement);
    }

    public ParcelStatusElement registerSystemEdgeWorkUnitActivity(WUPJobCard jobCard, UoW initialUoW, String portType, String portValue) {
        if ((jobCard == null) || (initialUoW == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard or initialUoW are null"));
        }
        ResilienceParcel newParcel = parcelServicesIM.registerFulfillmentTask(jobCard.getActivityID(), initialUoW, portType, portValue, true);
        jobCard.getActivityID().setPresentParcelIdentifier(newParcel.getIdentifier());
        ParcelStatusElement statusElement = rasController.registerNewWorkUnitActivity(jobCard);
        return (statusElement);
    }

    /**
     *
     * @param jobCard
     */
    public void notifyStartOfWorkUnitActivity(WUPJobCard jobCard) {
        if ((jobCard == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard or startedUoW are null"));
        }
        ResilienceParcel finishedParcel = parcelServicesIM.notifyParcelProcessingStart(jobCard.getActivityID().getPresentParcelIdentifier());
        rasController.synchroniseJobCard(jobCard);
    }

    /**
     *
     * @param jobCard
     * @param finishedUoW
     */
    public void notifyFinishOfWorkUnitActivity(WUPJobCard jobCard, UoW finishedUoW) {
        if ((jobCard == null) || (finishedUoW == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard or finishedUoW are null"));
        }
        ResilienceParcel finishedParcel = parcelServicesIM
                .notifyParcelProcessingFinish(jobCard.getActivityID().getPresentParcelIdentifier(), finishedUoW);
        rasController.synchroniseJobCard(jobCard);
    }

    /**
     *
     * @param jobCard
     */
    public void notifyFinalisationOfWorkUnitActivity(WUPJobCard jobCard) {
        if ((jobCard == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard is null"));
        }
        ResilienceParcel finishedParcel = parcelServicesIM
                .notifyParcelProcessingFinalisation(jobCard.getActivityID().getPresentParcelIdentifier());
        rasController.synchroniseJobCard(jobCard);
    }

    /**
     *
     * @param jobCard
     * @param failedUoW
     */
    public void notifyFailureOfWorkUnitActivity(WUPJobCard jobCard, UoW failedUoW) {
        if ((jobCard == null) || (failedUoW == null)) {
            throw (new IllegalArgumentException(".notifyFailureOfWorkUnitActivity(): jobCard or finishedUoW are null"));
        }
        ResilienceParcel failedParcel = parcelServicesIM
                .notifyParcelProcessingFinish(jobCard.getActivityID().getPresentParcelIdentifier(), failedUoW);
        rasController.synchroniseJobCard(jobCard);
    }

    /**
     *
     * @param jobCard
     */
    public void notifyCancellationOfWorkUnitActivity(WUPJobCard jobCard) {
        if (jobCard == null) {
            throw (new IllegalArgumentException(
                    ".notifyCancellationOfWorkUnitActivity(): jobCard or finishedUoW are null"));
        }
        ResilienceParcel failedParcel = parcelServicesIM
                .notifyParcelProcessingCancellation(jobCard.getActivityID().getPresentParcelIdentifier());
    }

    /**
     *
     * @param jobCard
     */
    public void notifyPurgeOfWorkUnitActivity(WUPJobCard jobCard) {
        if ((jobCard == null)) {
            throw (new IllegalArgumentException(".registerWorkUnitActivity(): jobCard is null"));
        }
        if (!jobCard.hasActivityID()) {
            return;
        }
        if (!jobCard.getActivityID().hasPresentParcelIdentifier()) {
            return;
        }
        parcelServicesIM.notifyParcelProcessingPurge(jobCard.getActivityID().getPresentParcelIdentifier());
    }

    /**
     *
     * @param existingJobCard
     */
    public void synchroniseJobCard(WUPJobCard existingJobCard) {
        rasController.synchroniseJobCard(existingJobCard);
    }

    public ParcelStatusElement getCurrentParcelStatusElement(ResilienceParcelIdentifier parcelInstanceID) {
        ParcelStatusElement statusElement = rasController.getStatusElement(parcelInstanceID);
        return (statusElement);
    }

    public void registerDownstreamWUP(PetasosEpisodeIdentifier wuaEpisodeID, WUPFunctionToken interestedWUPFunctionID) {
        rasController.registerWUAEpisodeDownstreamWUPInterest(wuaEpisodeID, interestedWUPFunctionID);
    }

    public ResilienceParcel getUnprocessedParcel(FDNToken wupTypeID) {
        // TODO - this is the mechanism to re-start on failure, not currently
        // implemented.
        return (null);
    }

    public void registerWorkUnitProcessor(WorkUnitProcessorTopologyNode newElement, List<DataParcelManifest> payloadTopicSet,
                                          WUPArchetypeEnum wupNature) {
        LOG.debug(".registerWorkUnitProcessor(): Entry, newElement --> {}, payloadTopicSet --> {}", newElement,
                payloadTopicSet);
        switch (wupNature) {
            case WUP_NATURE_TIMER_TRIGGERED_WORKFLOW:
            case WUP_NATURE_STIMULI_TRIGGERED_WORKFLOW:
                // Do nothing, as the above WUPs are handled by their own specific frameworks.
                break;
            case WUP_NATURE_LADON_BEHAVIOUR_WRAPPER:
                wupInterchangeManager.buildWUPInterchangeRoutes(newElement, wupNature);
                break;
            case WUP_NATURE_API_ANSWER:
            case WUP_NATURE_API_CLIENT:
            case WUP_NATURE_API_PUSH:
            case WUP_NATURE_API_RECEIVE:
            case WUP_NATURE_LADON_STANDARD_MOA:
            case WUP_NATURE_MESSAGE_EXTERNAL_CONCURRENT_INGRES_POINT:
            case WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT:
            case WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT:
            case WUP_NATURE_MESSAGE_WORKER:
            default:
                wupFrameworkManager.buildWUPFramework(newElement, payloadTopicSet, wupNature);
                wupInterchangeManager.buildWUPInterchangeRoutes(newElement, wupNature);
        }
    }

    public PetasosParcelAuditTrailEntry transactionFailedPriorOnInitialValidation(WUPIdentifier wup, String action, UoW theUoW) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(".transactionAuditEntry(): Entry, ");
            LOG.debug(".transactionAuditEntry(): Entry, wup (WUPIdentifier) --> {}", wup);
            LOG.debug(".transactionAuditEntry(): Entry, action (String) --> {}", action);
            LOG.debug(".transactionAuditEntry(): Entry, theUoW (UoW) --> {}", theUoW);
        }
        if ((wup == null) || (action == null) || (theUoW == null)) {
            throw (new IllegalArgumentException(".writeAuditEntry(): wup, action or theUoW are null"));
        }
        PetasosParcelAuditTrailEntry newAuditEntry = new PetasosParcelAuditTrailEntry();
        newAuditEntry.setAuditTrailEntryDate(Date.from(Instant.now()));
        newAuditEntry.setActualUoW(theUoW);
        FDN auditEntryType = new FDN();
        auditEntryType.appendFDN(new FDN(theUoW.getTypeID()));
        auditEntryType.appendRDN(new RDN("action", action));
        newAuditEntry.setParcelTypeID(auditEntryType.getToken());
        FDN auditEntryIdentifier = new FDN();
        auditEntryIdentifier.appendFDN(new FDN(theUoW.getInstanceID()));
        auditEntryIdentifier.appendRDN(new RDN("action", action));
        ResilienceParcelIdentifier parcelId = new ResilienceParcelIdentifier(auditEntryIdentifier.getToken());
        newAuditEntry.setIdentifier(parcelId);
        newAuditEntry.setParcelFinalsationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
        newAuditEntry.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
        newAuditEntry.setParcelFinalisedDate(Date.from(Instant.now()));
        newAuditEntry.setParcelFinishedDate(Date.from(Instant.now()));
        newAuditEntry.setPrimaryWUPIdentifier(wup);
        auditWriter.logActivity(newAuditEntry, true);
        return (newAuditEntry);
    }

}
