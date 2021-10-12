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
package net.fhirfactory.pegacorn.petasos.core.sta.resilience.processingplant.cache;

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.processingplant.cache.ProcessingPlantResilienceParcelCacheDM;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.segments.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the re-factored Resilience framework ActivityMatrix for
 * ResilienceParcels for SOA (Synchronous Oriented Activities).
 * It is a representational Matrix of all the Resilience
 * Parcel activity (effectively, Transactions) within the ServiceModule -
 * and has hooks for supporting updates from Clustered and Multi-Site equivalents.
 *
 * @author Mark A. Hunter
 * @since 2020-08-26
 */
@ApplicationScoped
public class STAServiceModuleActivityMatrixDM {

    private static final Logger LOG = LoggerFactory.getLogger(STAServiceModuleActivityMatrixDM.class);

    private ConcurrentHashMap<FulfillmentTrackingIdType, TaskStatusType> parcelStatusElementCache;
    private ConcurrentHashMap<PetasosEpisodeIdentifier, HashSet<FulfillmentTrackingIdType>> wuaEpisode2ParcelInstanceMap;

    @Inject
    ProcessingPlantResilienceParcelCacheDM parcelCacheDM;

    @Inject
    TopologyIM moduleIM;

    public STAServiceModuleActivityMatrixDM() {
        parcelStatusElementCache = new ConcurrentHashMap<FulfillmentTrackingIdType, TaskStatusType>();
        wuaEpisode2ParcelInstanceMap = new ConcurrentHashMap<PetasosEpisodeIdentifier, HashSet<FulfillmentTrackingIdType>>();
    }
    
    /**
     * This function registers (adds) the ParcelIdentifier and an associated ParcelStatusElement to the
     * ParcelStatusElementCache (ConcurrentHashMap<ResilienceParcelIdentifier, ParcelStatusElement>). It
     * first checks to see if there is an instance already there. This functionality needs to be
     * enhanced to support cluster-based behaviours.
     *
     * It then registers (adds) the ParcelIdentifier to the WUAEpisode2ParcelMap
     * (ConcurrentHashMap<EpisodeIdentifier,HashSet<ResilienceParcelIdentifier>>) to track that
     * the specific Parcel is part of a processing Episode.
     *
     * @param petasosTaskFulfillment The WUP/Parcel ActivityID
     * @param initialProcessingStatus The initial (provided) Processing Status of the ResilienceParcel
     * @return A ParcelStatusElement which is used by the WUP Components to determine execution & status privileges.
     */
    public TaskStatusType startTransaction(TaskFulfillmentType petasosTaskFulfillment, FulfillmentExecutionStatusEnum initialProcessingStatus) {
    	if(LOG.isDebugEnabled()) {
    		// There's just too much information in this object to have it print on a single line and be able to debug with it!!!
    		LOG.debug(".startTransaction(): Entry");
    		LOG.debug(".startTransaction(): activityID (ActivityID).previousParcelIdentifier -->{}", petasosTaskFulfillment.getPreviousParcelIdentifier());
    		LOG.debug(".startTransaction(): activityID (ActivityID).previousEpisodeIdentifier --> {}", petasosTaskFulfillment.getPreviousEpisodeIdentifier());
    		LOG.debug(".startTransaction(): activityID (ActivityID).previousWUPFunctionToken --> {}", petasosTaskFulfillment.getPreviousWUPFunctionToken());
    		LOG.debug(".startTransaction(): activityID (ActivityID).perviousWUPIdentifier --> {}", petasosTaskFulfillment.getPreviousWUPIdentifier());
    		LOG.debug(".startTransaction(): activityID (ActivityID).presentParcelIdentifier -->{}", petasosTaskFulfillment.getPresentParcelIdentifier());
    		LOG.debug(".startTransaction(): activityID (ActivityID).presentEpisodeIdentifier --> {}", petasosTaskFulfillment.getPresentEpisodeIdentifier());
    		LOG.debug(".startTransaction(): activityID (ActivityID).presentWUPFunctionTokan --> {}", petasosTaskFulfillment.getPresentWUPFunctionToken());
    		LOG.debug(".startTransaction(): activityID (ActivityID).presentWUPIdentifier --> {}", petasosTaskFulfillment.getFulfillerComponentId());
    		LOG.debug(".startTransaction(): activityID (ContunuityID).createDate --> {}", petasosTaskFulfillment.getCreationDate());
    		LOG.debug(".startTransaction(): initialProcessingStatus (ResilienceParcelProcessingStatusEnum) --> {}", initialProcessingStatus);
    	}
        if (petasosTaskFulfillment == null) {
            throw (new IllegalArgumentException(".startTransaction(): activityID is null"));
        }
        // First we are going to update the ParcelCache
        LOG.trace(".startTransaction(): Adding/Updating the ParcelStatusElementCache with a new ParcelStatusElement");
        TaskStatusType newStatusElement = null;
        if(parcelStatusElementCache.containsKey(petasosTaskFulfillment.getPresentParcelIdentifier()))
        {
            // This really shouldn't happen, as concurrency should already be handled by the calling system... But, it's
            // better to be safe than sorry!
            LOG.trace(".startTransaction(): ParcelIdentifier already registered in the ParcelStatusElementCache, let's make sure it's the same though!");
            TaskStatusType existingStatusElement = parcelStatusElementCache.get(petasosTaskFulfillment.getPresentParcelIdentifier());
            boolean sameInstanceID = existingStatusElement.getParcelInstanceID().equals(petasosTaskFulfillment.getPresentParcelIdentifier());
            boolean sameEpisodeID = existingStatusElement.getActivityID().getPresentEpisodeIdentifier().equals(petasosTaskFulfillment.getPresentEpisodeIdentifier());
            boolean sameWUPInstanceID = existingStatusElement.getActivityID().getImplementingWorkUnitProcessID().equals(petasosTaskFulfillment.getFulfillerComponentId());
            boolean sameWUPTypeID = existingStatusElement.getActivityID().getPresentWUPFunctionToken().equals(petasosTaskFulfillment.getPresentWUPFunctionToken());
            boolean sameUpstreamEpisodeID = existingStatusElement.getActivityID().getPreviousEpisodeIdentifier().equals(petasosTaskFulfillment.getPreviousEpisodeIdentifier());
            if( sameInstanceID && sameEpisodeID && sameWUPInstanceID && sameWUPTypeID && sameUpstreamEpisodeID ){
                LOG.trace(".startTransaction(): New ActivityID and existing (registered) ID the same, so update the status (maybe) and then exit");
                existingStatusElement.setFulfillmentExecutionStatus(initialProcessingStatus);
                LOG.trace(".startTransaction(): Set the to-be-returned ParcelStatusElement to the existingStatusElement");
                newStatusElement = existingStatusElement;
            } else {
                LOG.trace(".startTransaction(): New ActivityID and existing (registered) ID are different, so delete the existing one from the ParcelStatusElementCache!");
                parcelStatusElementCache.remove(petasosTaskFulfillment.getPresentParcelIdentifier());
                LOG.trace(".startTransaction(): Now create a new ParcelStatusElement, set its initial status and add it to the ParcelStatusElementCache!");
                newStatusElement = new TaskStatusType(petasosTaskFulfillment);
                newStatusElement.setFulfillmentExecutionStatus(initialProcessingStatus);
                LOG.trace(".startTransaction(): New ParcelStatusElement created, newStatusElement --> {}", newStatusElement);
                parcelStatusElementCache.put(petasosTaskFulfillment.getPresentParcelIdentifier(), newStatusElement);
            }
        } else {
            LOG.trace(".startTransaction(): Create a new ParcelStatusElement, set its initial status and add it to the ParcelStatusElementCache!");
            newStatusElement = new TaskStatusType(petasosTaskFulfillment);
            newStatusElement.setFulfillmentExecutionStatus(initialProcessingStatus);
            LOG.trace(".startTransaction(): New ParcelStatusElement created, newStatusElement --> {}", newStatusElement);
            parcelStatusElementCache.put(petasosTaskFulfillment.getPresentParcelIdentifier(), newStatusElement);
        }
        // Now let's update the WUAEpisode2ParcelMap for the Episode/ResilienceParcel combination
        if(LOG.isTraceEnabled()) {
            LOG.trace(".startTransaction(): Adding the ReslienceParcelIdentifier to the WUAEpisode2ParcelMap");
            LOG.trace(".startTransaction(): EpisodeIdentifier --> {}", petasosTaskFulfillment.getPresentEpisodeIdentifier() );
            LOG.trace(".startTransaction(): ResilienceParcelIdentifier --> {}", petasosTaskFulfillment.getPresentParcelIdentifier());
        }
        if(!wuaEpisode2ParcelInstanceMap.containsKey(petasosTaskFulfillment.getPresentEpisodeIdentifier()) ){
        	LOG.trace(".startTransaction(): No WUAEpisode2ParcelMap Entry for this Episode, creating!");
        	HashSet<FulfillmentTrackingIdType> wuaEpisodeParcelSet = new HashSet<FulfillmentTrackingIdType>();
        	LOG.trace(".startTransaction(): Add the ResilienceParcelIdentifier to the HashSet<ResilienceParcelIdentifier> set of Parcels associated to this Episode");
            wuaEpisodeParcelSet.add(petasosTaskFulfillment.getPresentParcelIdentifier());
            LOG.trace(".startTransaction(): Add the EpisodeIdentifier/HashSet<ResilienceParcelIdentifier> combination to the WUAEpisode2ParcelMap");
        	wuaEpisode2ParcelInstanceMap.put(petasosTaskFulfillment.getPresentEpisodeIdentifier(),wuaEpisodeParcelSet );
        } else {
        	LOG.trace(".startTransaction(): WUAEpisode2ParcelMap Entry exists for this Episode, so retrieve the HashSet<ResilienceParcelIdentifier> set of Parcels associated to this Episode!");
            Set<FulfillmentTrackingIdType> wuaEpisodeParcelSet = wuaEpisode2ParcelInstanceMap.get(petasosTaskFulfillment.getPresentEpisodeIdentifier());
            LOG.trace(".startTransaction(): Now check to see if the ResilienceParcelIdentifier is already in the HashSet<ResilienceParcelIdentifier> set of Parcels associated to this Episode?");
            Iterator<FulfillmentTrackingIdType> setIterator = wuaEpisodeParcelSet.iterator();
            boolean foundParcelIdentifierInSet = false;
            while(setIterator.hasNext()){
                FulfillmentTrackingIdType currentParcelIdentifier = setIterator.next();
                if(currentParcelIdentifier.equals(petasosTaskFulfillment.getPresentParcelIdentifier())){
                    foundParcelIdentifierInSet = true;
                    break;
                }
            }
            if(!foundParcelIdentifierInSet) {
                LOG.trace(".startTransaction(): The ResilienceParcelIdentifier is not already in the HashSet<ResilienceParcelIdentifier> set, so add it!");
                wuaEpisodeParcelSet.add(petasosTaskFulfillment.getPresentParcelIdentifier());
            } else {
                LOG.trace(".startTransaction(): The ResilienceParcelIdentifier is already in the HashSet<ResilienceParcelIdentifier> set, so do nothing!");
            }
        }
        LOG.debug(".startTransaction(): Exit, newStatusElement --> {}", newStatusElement);
        return(newStatusElement);
    }

    /**
     * Update the Status of the WUA Element to reflect the requested change.
     *
     * @param petasosTaskFulfillment The unique Identifier that distinctly represents this work/resilience activity function
     * @param status     The new status to be applied to the WUA Element
     */
    public void finishTransaction(TaskFulfillmentType petasosTaskFulfillment, FulfillmentExecutionStatusEnum status) {
        if (petasosTaskFulfillment == null) {
            throw (new IllegalArgumentException(".updateParcelActivity(): ActivityID (activityID) Processing Status (status) is null"));
        }
        if(LOG.isDebugEnabled()) {
            // There's just too much information in this object to have it print on a single line and be able to debug with it!!!
            LOG.debug(".startTransaction(): Entry");
            LOG.debug(".startTransaction(): activityID (ActivityID).previousParcelIdentifier -->{}", petasosTaskFulfillment.getPreviousParcelIdentifier());
            LOG.debug(".startTransaction(): activityID (ActivityID).previousEpisodeIdentifier --> {}", petasosTaskFulfillment.getPreviousEpisodeIdentifier());
            LOG.debug(".startTransaction(): activityID (ActivityID).previousWUPFunctionToken --> {}", petasosTaskFulfillment.getPreviousWUPFunctionToken());
            LOG.debug(".startTransaction(): activityID (ActivityID).perviousWUPIdentifier --> {}", petasosTaskFulfillment.getPreviousWUPIdentifier());
            LOG.debug(".startTransaction(): activityID (ActivityID).presentParcelIdentifier -->{}", petasosTaskFulfillment.getPresentParcelIdentifier());
            LOG.debug(".startTransaction(): activityID (ActivityID).presentEpisodeIdentifier --> {}", petasosTaskFulfillment.getPresentEpisodeIdentifier());
            LOG.debug(".startTransaction(): activityID (ActivityID).presentWUPFunctionTokan --> {}", petasosTaskFulfillment.getPresentWUPFunctionToken());
            LOG.debug(".startTransaction(): activityID (ActivityID).presentWUPIdentifier --> {}", petasosTaskFulfillment.getFulfillerComponentId());
            LOG.debug(".startTransaction(): activityID (ContunuityID).createDate --> {}", petasosTaskFulfillment.getCreationDate());
            LOG.debug(".startTransaction(): initialProcessingStatus (ResilienceParcelProcessingStatusEnum) --> {}", status);
        }
        LOG.trace(".finishTransaction(): Get the current ParcelStatusElement");
        FulfillmentTrackingIdType parcelInstanceID = petasosTaskFulfillment.getPresentParcelIdentifier();
        TaskStatusType currentStatusElement;
        if(parcelStatusElementCache.containsKey(parcelInstanceID)) {
            LOG.trace(".finishTransaction(): ParcelStatusElement exists -> get it!");
            currentStatusElement = parcelStatusElementCache.get(parcelInstanceID);
            LOG.trace(".finishTransaction(): Updating status of the ParcelStatusElement!");
            currentStatusElement.setFulfillmentExecutionStatus(status);
        }
        LOG.debug(".finishTransaction(): Exit, updated currentStatusElement --> {}");
    }

    public TaskStatusType getTransactionElement(FulfillmentTrackingIdType parcelInstanceID) {
        LOG.debug(".getCurrentParcelStatusElement(): Entry, parcelInstanceID --> {}", parcelInstanceID);
        if(parcelStatusElementCache.containsKey(parcelInstanceID)) {
	        TaskStatusType requestedElement = parcelStatusElementCache.get(parcelInstanceID);
	        LOG.debug(".getCurrentParcelStatusElement(): Exit, returning requestedElement --> {}", requestedElement);
	        return (requestedElement);
        }
        else {
            LOG.debug(".getCurrentParcelStatusElement(): no matching element.");
            return (null);        	
        }
    }

}
