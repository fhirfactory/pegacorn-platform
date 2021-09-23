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
package net.fhirfactory.pegacorn.petasos.core.common.resilience.processingplant.cache;

import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
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
public class ProcessingPlantParcelCacheDM {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantParcelCacheDM.class);
    private boolean initialised;
    private static long CLEAN_UP_SERVICE_PERIOD = 60000L; // in milliseconds (1 minute)
    private static long CACHE_PARCEL_MAXIMUM_AGE = 600000L; // in milliseconds (10 minutes)

    private ConcurrentHashMap<ResilienceParcelIdentifier, ResilienceParcel> petasosParcelCache;
    private Object cacheLock;

    public ProcessingPlantParcelCacheDM() {
        petasosParcelCache = new ConcurrentHashMap<ResilienceParcelIdentifier, ResilienceParcel>();
        this.initialised = false;
        cacheLock = new Object();
    }
    
    @PostConstruct
    public void initialise(){
        LOG.debug(".initialised(): Entry");
        if(!this.initialised){
            LOG.info(".initialise(): Initialising....");
            LOG.info(".initialise(): [Cache CleanUP Agent Task Scheduling] Start");
            scheduleCleanUpAgent();
            LOG.info(".initialise(): [Cache CleanUp Agent Task Scheduling] Finish");
            LOG.info(".initialise(): Done.");
        } else {
            LOG.debug(".initialised(): Already initialised, nothing to do!");
        }
        LOG.debug(".initialise(): Exit");
    }

    /**
     * This function adds a ResilienceParcel to the Parcel Cache. If a Parcel was already associated
     * to the particular ParcelID, it replaces it.
     * @param parcel The ResilienceParcel to be added to the (ConcurrentHashMap) Cache
     */
    @Transactional
    public void addParcel(ResilienceParcel parcel) {
        LOG.debug(".addParcel(): Entry, parcel --> {}", parcel);
        if (parcel == null) {
            return;
        }
        if (!parcel.hasInstanceIdentifier()) {
            return;
        }
        ResilienceParcelIdentifier parcelInstanceID = parcel.getIdentifier();
        synchronized (cacheLock) {
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
    @Transactional
    public void removeParcel(ResilienceParcel parcel) {
        LOG.debug(".removeParcel(): Entry, parcel --> {}", parcel);
        if (parcel == null) {
            return;
        }
        if (!parcel.hasInstanceIdentifier()) {
            return;
        }
        synchronized (cacheLock) {
            if (petasosParcelCache.containsKey(parcel.getIdentifier())) {
                petasosParcelCache.remove(parcel.getIdentifier());
            }
        }
    }

    /**
     * This function removes the ResilienceParcel from the Cache.
     * @param parcelInstanceID The Identifier (FDNToken) of the ResilienceParcel to be removed
     */
    @Transactional
    public void removeParcel(ResilienceParcelIdentifier parcelInstanceID) {
        LOG.debug(".removeParcel(): Entry, parcelInstanceID --> {}", parcelInstanceID);
        if (parcelInstanceID == null) {
            return;
        }
        synchronized (cacheLock) {
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
    @Transactional
    public void updateParcel(ResilienceParcel newParcel) {
        LOG.debug(".updateParcel() Entry, parcel --> {}", newParcel);
        if (newParcel == null) {
            throw (new IllegalArgumentException("newParcel is null"));
        }
        synchronized (cacheLock) {
            if (petasosParcelCache.containsKey(newParcel.getIdentifier())) {
                petasosParcelCache.remove(newParcel.getIdentifier());
            }
            petasosParcelCache.put(newParcel.getIdentifier(), newParcel);
        }
    }

    /**
     * This function returns a List of all the ResilienceParcel instances within the cache
     * @return A List of all the ResilienceParcel instances contained within the Cache
     */
    public List<ResilienceParcel> getParcelSet() {
        LOG.debug(".getParcelSet(): Entry");
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        synchronized (cacheLock) {
            petasosParcelCache.entrySet().forEach(entry -> parcelList.add(entry.getValue()));
        }
        return (parcelList);
    }

    public List<ResilienceParcel> getParcelSetByState(ResilienceParcelProcessingStatusEnum status) {
        LOG.debug(".getParcelSet(): Entry, status --> {}", status);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        synchronized (cacheLock) {
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
        List<ResilienceParcel> parcelList = getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE);
        return (parcelList);
    }

    public List<ResilienceParcel> getFinishedParcelSet() {
        List<ResilienceParcel> parcelList = getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
        return (parcelList);
    }

    public List<ResilienceParcel> getFinalisedParcelSet() {
        List<ResilienceParcel> parcelList = getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
        return (parcelList);
    }

    public List<ResilienceParcel> getInProgressParcelSet() {
        LOG.debug(".getInProgressParcelSet(): Entry");
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        parcelList.addAll(getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_ACTIVE));
        parcelList.addAll(getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_INITIATED));
        parcelList.addAll(getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_REGISTERED));
        return (parcelList);
    }

    public List<ResilienceParcel> getParcelByEpisodeID(FDNToken parcelTypeID) {
        LOG.debug(".getInProgressParcelSet(): Entry, parcelTypeID --> {}" + parcelTypeID);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
        synchronized (cacheLock) {
            while (parcelListIterator.hasNext()) {
                ResilienceParcel currentParcel = parcelListIterator.next();
                if (currentParcel.hasEpisodeIdentifier()) {
                    if (currentParcel.getEpisodeIdentifier().equals(parcelTypeID)) {
                        parcelList.add(currentParcel);
                    }
                }
            }
        }
        return (parcelList);
    }



    public ResilienceParcel getCurrentParcelForWUP(WUPIdentifier wupInstanceID, FDNToken uowInstanceID) {
        LOG.debug(".getCurrentParcel(): Entry, wupInstanceID --> {}" + wupInstanceID);
        List<ResilienceParcel> parcelList = new LinkedList<ResilienceParcel>();
        Iterator<ResilienceParcel> parcelListIterator = getParcelSet().iterator();
        synchronized (cacheLock) {
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

    public int getCacheSize(){
        return(this.petasosParcelCache.size());
    }

    //
    // Cleanup Agent
    //

    private void scheduleCleanUpAgent() {
        LOG.debug(".scheduleCleanUpAgent(): Entry");
        TimerTask cleanupAgentTask = new TimerTask() {
            public void run() {
                LOG.debug(".cleanupAgentTask(): Entry");
                cleanUpAgentActivity();
                LOG.debug(".cleanupAgentTask(): Exit");
            }
        };
        Timer timer = new Timer("PetasosParcelCacheCleanUpAgentTask");
        timer.schedule(cleanupAgentTask, CLEAN_UP_SERVICE_PERIOD, CLEAN_UP_SERVICE_PERIOD);
        LOG.debug(".scheduleCleanUpAgent(): Exit");
    }

    public void cleanUpAgentActivity(){
        if(this.petasosParcelCache.isEmpty()){
            return;
        }
        Long currentTimeInMilliseconds = Date.from(Instant.now()).getTime();
        synchronized (cacheLock) {
            Enumeration<ResilienceParcelIdentifier> parcelIdentifierEnumeration = this.petasosParcelCache.keys();
            List<ResilienceParcelIdentifier> listOfParcelsToBePurged = new ArrayList<>();
            while (parcelIdentifierEnumeration.hasMoreElements()) {
                ResilienceParcelIdentifier currentIdentifier = parcelIdentifierEnumeration.nextElement();
                ResilienceParcel currentResilienceParcel = this.petasosParcelCache.get(currentIdentifier);
                switch (currentResilienceParcel.getProcessingStatus()) {
                    case PARCEL_STATUS_REGISTERED:
                    case PARCEL_STATUS_INITIATED:
                    case PARCEL_STATUS_ACTIVE:
                    case PARCEL_STATUS_ACTIVE_ELSEWHERE:
                        break;
                    case PARCEL_STATUS_FINISHED:
                    case PARCEL_STATUS_FINALISED:
                    case PARCEL_STATUS_FAILED:
                    case PARCEL_STATUS_FINISHED_ELSEWHERE:
                    case PARCEL_STATUS_FINALISED_ELSEWHERE:
                    case PARCEL_STATUS_CANCELLED:
                    default: {
                        Date endDateOfInterest = null;
                        if (currentResilienceParcel.hasFinalisationDate()) {
                            endDateOfInterest = currentResilienceParcel.getFinalisationDate();
                        } else if (currentResilienceParcel.hasCancellationDate()) {
                            endDateOfInterest = currentResilienceParcel.getCancellationDate();
                        } else if (currentResilienceParcel.hasFinishedDate()) {
                            endDateOfInterest = currentResilienceParcel.getFinishedDate();
                        } else {
                            currentResilienceParcel.setCancellationDate(Date.from(Instant.now()));
                        }
                        if (endDateOfInterest != null) {
                            Long endDateOfInterestInMilliseconds = endDateOfInterest.getTime();
                            Long age = currentTimeInMilliseconds - endDateOfInterestInMilliseconds;
                            if (age > CACHE_PARCEL_MAXIMUM_AGE) {
                                listOfParcelsToBePurged.add(currentIdentifier);
                            }
                        }
                    }
                }
            }
            if (!listOfParcelsToBePurged.isEmpty()) {
                for(ResilienceParcelIdentifier currentIdentifier: listOfParcelsToBePurged){
                    this.petasosParcelCache.remove(currentIdentifier);
                }
            }
        }
    }
}
