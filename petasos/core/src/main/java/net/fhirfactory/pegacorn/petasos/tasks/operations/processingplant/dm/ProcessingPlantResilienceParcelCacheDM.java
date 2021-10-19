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
package net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.dm;

import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
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
public class ProcessingPlantResilienceParcelCacheDM {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantResilienceParcelCacheDM.class);

    private ConcurrentHashMap<FulfillmentTrackingIdType, ResilienceParcel> petasosParcelCache;
    private Object petasosParcelCacheLock;

    //
    // Constructor
    //

    public ProcessingPlantResilienceParcelCacheDM() {
        this.petasosParcelCache = new ConcurrentHashMap<FulfillmentTrackingIdType, ResilienceParcel>();
        this.petasosParcelCacheLock = new Object();
    }

    //
    // Getters (and Setters)
    //


    public Object getPetasosParcelCacheLock() {
        return petasosParcelCacheLock;
    }

    /**
     * This function adds a ResilienceParcel to the Parcel Cache. If a Parcel was already associated
     * to the particular ParcelID, it replaces it.
     * @param parcel The ResilienceParcel to be added to the (ConcurrentHashMap) Cache
     */

    public void addParcel(ResilienceParcel parcel) {
        LOG.debug(".addParcel(): Entry, parcel --> {}", parcel);
        if (parcel == null) {
            return;
        }
        if (!parcel.hasInstanceIdentifier()) {
            return;
        }
        synchronized (getPetasosParcelCacheLock()) {
            FulfillmentTrackingIdType parcelInstanceID = parcel.getIdentifier();
            if (petasosParcelCache.containsKey(parcelInstanceID)) {
                petasosParcelCache.remove(parcelInstanceID);
            }
            petasosParcelCache.put(parcelInstanceID, parcel);
        }
    }

    /**
     * This function returns the ResilienceParcel for the given Resilience Parcel ID.
     * @param parcelInstanceID The FDNToken of the ResilienceParcel requested
     * @return The ResilienceParcel instance associated with the provided ParcelInstanceID (FDNToken)
     */
    public ResilienceParcel getParcelInstance(FDNToken parcelInstanceID) {
        LOG.debug(".getParcelInstance(): Entry, parcelInstanceID --> {}", parcelInstanceID);
        if (petasosParcelCache.containsKey(parcelInstanceID)) {
            return (petasosParcelCache.get(parcelInstanceID));
        }
        return (null);
    }

    /**
     * This function removes the ResilienceParcel from the Cache.
     * @param parcel The ResilienceParcel to be removed from the Cache
     */

    public void removeParcel(ResilienceParcel parcel) {
        LOG.debug(".removeParcel(): Entry, parcel --> {}", parcel);
        if (parcel == null) {
            return;
        }
        if (!parcel.hasInstanceIdentifier()) {
            return;
        }
        synchronized (getPetasosParcelCacheLock()) {
            if (petasosParcelCache.containsKey(parcel.getIdentifier())) {
                petasosParcelCache.remove(parcel.getIdentifier());
            }
        }
    }

    /**
     * This function removes the ResilienceParcel from the Cache.
     * @param parcelInstanceID The Identifier (FDNToken) of the ResilienceParcel to be removed
     */

    public void removeParcel(FulfillmentTrackingIdType parcelInstanceID) {
        LOG.debug(".removeParcel(): Entry, parcelInstanceID --> {}", parcelInstanceID);
        if (parcelInstanceID == null) {
            return;
        }
        synchronized (getPetasosParcelCacheLock()) {
            if (petasosParcelCache.containsKey(parcelInstanceID)) {
                petasosParcelCache.remove(parcelInstanceID);
            }
        }
    }

    /**
     * This function replaces the ResilienceParcel within the Cache with a new
     * instance.
     * @param newParcel The new ResilienceParcel instance
     */

    public void updateParcel(ResilienceParcel newParcel) {
        LOG.debug(".updateParcel() Entry, parcel --> {}", newParcel);
        if (newParcel == null) {
            throw (new IllegalArgumentException("newParcel is null"));
        }
        if (petasosParcelCache.containsKey(newParcel.getIdentifier())) {
            petasosParcelCache.remove(newParcel.getIdentifier());
        }
        petasosParcelCache.put(newParcel.getIdentifier(), newParcel);
    }

    /**
     * This function returns a List of all the ResilienceParcel instances within the cache
     * @return A List of all the ResilienceParcel instances contained within the Cache
     */
    public List<ResilienceParcel> getParcelSet() {
        LOG.debug(".getParcelSet(): Entry");
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        synchronized (getPetasosParcelCacheLock()) {
            petasosParcelCache.entrySet().forEach(entry -> parcelList.add(entry.getValue()));
        }
        return (parcelList);
    }

    public List<ResilienceParcel> getParcelSetByState(FulfillmentExecutionStatusEnum status) {
        LOG.debug(".getParcelSet(): Entry, status --> {}", status);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        synchronized (getPetasosParcelCacheLock()) {
            Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
            while (parcelListIterator.hasNext()) {
                ResilienceParcel currentParcel = parcelListIterator.next();
                if (currentParcel.hasProcessingStatus()) {
                    if (currentParcel.getProcessingStatus() == status) {
                        parcelList.add(currentParcel);
                    }
                }
            }
        }
        return (parcelList);
    }

    public List<ResilienceParcel> getActiveParcelSet() {
        List<ResilienceParcel> parcelList = getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE);
        return (parcelList);
    }

    public List<ResilienceParcel> getFinishedParcelSet() {
        List<ResilienceParcel> parcelList = getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINISHED);
        return (parcelList);
    }

    public List<ResilienceParcel> getFinalisedParcelSet() {
        List<ResilienceParcel> parcelList = getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FINALISED);
        return (parcelList);
    }

    public List<ResilienceParcel> getCancelledParcelSet(){
        List<ResilienceParcel> parcelList = getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_CANCELLED);
        return(parcelList);
    }

    public List<ResilienceParcel> getFailedParcelSet(){
        List<ResilienceParcel> parcelList = getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_FAILED);
        return(parcelList);
    }

    public List<ResilienceParcel> getInProgressParcelSet() {
        LOG.debug(".getInProgressParcelSet(): Entry");
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        parcelList.addAll(getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_ACTIVE));
        parcelList.addAll(getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_INITIATED));
        parcelList.addAll(getParcelSetByState(FulfillmentExecutionStatusEnum.PARCEL_STATUS_REGISTERED));
        return (parcelList);
    }

    public List<ResilienceParcel> getParcelByEpisodeID(PetasosEpisodeIdentifier parcelTypeID) {
        LOG.debug(".getInProgressParcelSet(): Entry, parcelTypeID --> {}" + parcelTypeID);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        if(getParcelSet().isEmpty()){
            LOG.debug(".getInProgressParcelSet(): Exit, returning empty list");
            return(parcelList);
        }
        synchronized (getPetasosParcelCacheLock()) {
            Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
            while (parcelListIterator.hasNext()) {
                ResilienceParcel currentParcel = parcelListIterator.next();
                if (currentParcel.hasEpisodeIdentifier()) {
                    if (currentParcel.getEpisodeIdentifier().equals(parcelTypeID)) {
                        parcelList.add(currentParcel);
                    }
                }
            }
        }
        LOG.debug(".getInProgressParcelSet(): Exit, returning non-empty list");
        return (parcelList);
    }

    public ResilienceParcel getCurrentParcelForWUP(WUPIdentifier wupInstanceID, FDNToken uowInstanceID) {
        LOG.debug(".getCurrentParcel(): Entry, wupInstanceID --> {}" + wupInstanceID);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        synchronized (getPetasosParcelCacheLock()) {
            Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
            while (parcelListIterator.hasNext()) {
                ResilienceParcel currentParcel = parcelListIterator.next();
                if (currentParcel.hasAssociatedWUPIdentifier()) {
                    if (currentParcel.getAssociatedWUPIdentifier().equals(wupInstanceID)) {
                        if (currentParcel.hasActualUoW()) {
                            if (currentParcel.getActualUoW().getInstanceID().equals(uowInstanceID)) {
                                return (currentParcel);
                            }
                        }
                    }
                }
            }
        }
        return (null);
    }

    public String getMetrics(){
        LOG.debug(".getMetrics(): Entry");
        int registeredCount = 0;
        int waitingCount = 0;
        int activeCount = 0;
        int cancelledCount = 0;
        int failedCount = 0;
        int finishedCount = 0;
        int finalisedCount = 0;
        if(!this.petasosParcelCache.isEmpty()){
            synchronized (getPetasosParcelCacheLock()){
                Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
                while (parcelListIterator.hasNext()) {
                    ResilienceParcel currentParcel = parcelListIterator.next();
                    switch(currentParcel.getProcessingStatus()){
                        case PARCEL_STATUS_REGISTERED:
                            registeredCount += 1;
                            break;
                        case PARCEL_STATUS_CANCELLED:
                            cancelledCount += 1;
                            break;
                        case PARCEL_STATUS_INITIATED:
                        case PARCEL_STATUS_ACTIVE:
                        case PARCEL_STATUS_ACTIVE_ELSEWHERE:
                            activeCount += 1;
                            break;
                        case PARCEL_STATUS_FINISHED:
                        case PARCEL_STATUS_FINISHED_ELSEWHERE:
                            finishedCount += 1;
                            break;
                        case PARCEL_STATUS_FINALISED:
                        case PARCEL_STATUS_FINALISED_ELSEWHERE:
                            finalisedCount += 1;
                            break;
                        case PARCEL_STATUS_FAILED:
                            failedCount += 1;
                            break;
                    }
                }
            }
        }
        String metricsString =
                "Register["+registeredCount+"]"
                + ", Active["+activeCount+"]"
                + ", Cancelled["+cancelledCount+"]"
                + ", Failed["+failedCount+"]"
                + ", Finished["+finishedCount+"]"
                + ", Finalised["+finalisedCount+"]";
        return(metricsString);
    }

    public String getCacheName(){
        return("ProcessingPlantResilienceParcelCache");
    }
}
