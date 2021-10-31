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
package net.fhirfactory.pegacorn.petasos.core.tasks.caches.cluster;

import net.fhirfactory.pegacorn.common.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCardSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SharedTaskJobCardCache {
    private static final Logger LOG = LoggerFactory.getLogger(SharedTaskJobCardCache.class);

    // ActionableTaskId, FulfillmentTask JobCard Set (will be across ALL processing plants) Map
    private ConcurrentHashMap<TaskIdType, PetasosTaskJobCardSet> actionableTaskJobCardMap;
    // FulfillmentTaskId, JobCard Map
    private ConcurrentHashMap<TaskIdType, PetasosTaskJobCard> fulfillmentTaskJobCardMap;
    // WUP, JobCard Map
    private ConcurrentHashMap<ComponentIdType, PetasosTaskJobCard> wupJobCardMap;

    private Object cacheLock;

    //
    // Constructor(s)
    //

    public SharedTaskJobCardCache(){
        this.actionableTaskJobCardMap = new ConcurrentHashMap<>();
        this.fulfillmentTaskJobCardMap = new ConcurrentHashMap<>();
        this.wupJobCardMap = new ConcurrentHashMap<>();
        this.cacheLock = new Object();
    }

    //
    // Business methods
    //

    public void addJobCard(PetasosTaskJobCard jobCard){
        getLogger().debug(".addJobCard(): Entry, jobCard->{}", jobCard);

        //
        // Defensive Programming
        if(jobCard == null){
            getLogger().debug(".addJobCard(): Exit, jobCard is null, exiting");
        }
        if(!jobCard.hasActionableTaskIdentifier()){
            getLogger().debug(".addJobCard(): Exit, jobCard has no Actionable Task Id, exiting");
        }
        if(!jobCard.hasFulfillmentTaskIdentifier()){
            getLogger().debug(".addJobCard(): Exit, jobCard has no Fulfillment Task Id, exiting");
        }
        if(!jobCard.hasWorkUnitProcessor()){
            getLogger().debug(".addJobCard(): Exit, jobCard has no associated WorkUnitProcessor, exiting");
        }

        //
        // Grab the main mapping values
        TaskIdType actionableTaskId = jobCard.getActionableTaskIdentifier();
        TaskIdType fulfillmentTaskId = jobCard.getFulfillmentTaskIdentifier();
        ComponentIdType componentId = jobCard.getWorkUnitProcessor();

        //
        // Clear any existing entries for these values
        if(getWUPJobCardMap().containsKey(componentId)){
            getWUPJobCardMap().remove(componentId);
        }
        if(getFulfillmentTaskJobCardMap().containsKey(fulfillmentTaskId)){
            getFulfillmentTaskJobCardMap().remove(fulfillmentTaskId);
        }
        if(getActionableTaskJobCardMap().containsKey(actionableTaskId)){
            PetasosTaskJobCardSet petasosTaskJobCardSet = getActionableTaskJobCardMap().get(actionableTaskId);
            if (petasosTaskJobCardSet.getJobCardSet().containsKey(fulfillmentTaskId)) {
                petasosTaskJobCardSet.getJobCardSet().remove(fulfillmentTaskId);
            }
        }

        //
        // Now Add them
        synchronized(getCacheLock()){
            getWUPJobCardMap().put(componentId, jobCard);
            getFulfillmentTaskJobCardMap().put(fulfillmentTaskId, jobCard);
            if(getActionableTaskJobCardMap().containsKey(actionableTaskId)){
                getActionableTaskJobCardMap().get(actionableTaskId).addPetasosTaskJobCard(jobCard);
            } else {
                PetasosTaskJobCardSet jobCardSet = new PetasosTaskJobCardSet();
                jobCardSet.addPetasosTaskJobCard(jobCard);
                getActionableTaskJobCardMap().put(actionableTaskId, jobCardSet);
            }
        }

        getLogger().debug(".addJobCard(): Exit");
    }

    public void removeJobCard(PetasosTaskJobCard jobCard){
        getLogger().debug(".removeJobCard(): Entry, jobCard->{}", jobCard);

        //
        // Defensive Programming
        if(jobCard == null){
            getLogger().debug(".removeJobCard(): Exit, jobCard is null, exiting");
        }
        if(!jobCard.hasActionableTaskIdentifier()){
            getLogger().debug(".removeJobCard(): Exit, jobCard has no Actionable Task Id, exiting");
        }
        if(!jobCard.hasFulfillmentTaskIdentifier()){
            getLogger().debug(".removeJobCard(): Exit, jobCard has no Fulfillment Task Id, exiting");
        }
        if(!jobCard.hasWorkUnitProcessor()){
            getLogger().debug(".removeJobCard(): Exit, jobCard has no associated WorkUnitProcessor, exiting");
        }

        //
        // Grab the main mapping values
        TaskIdType actionableTaskId = jobCard.getActionableTaskIdentifier();
        TaskIdType fulfillmentTaskId = jobCard.getFulfillmentTaskIdentifier();
        ComponentIdType componentId = jobCard.getWorkUnitProcessor();

        //
        // Delete the values
        if(getWUPJobCardMap().containsKey(componentId)){
            getWUPJobCardMap().remove(componentId);
        }
        if(getFulfillmentTaskJobCardMap().containsKey(fulfillmentTaskId)){
            getFulfillmentTaskJobCardMap().remove(fulfillmentTaskId);
        }
        if(getActionableTaskJobCardMap().containsKey(actionableTaskId)){
            PetasosTaskJobCardSet petasosTaskJobCardSet = getActionableTaskJobCardMap().get(actionableTaskId);
            if (petasosTaskJobCardSet.getJobCardSet().containsKey(fulfillmentTaskId)) {
                petasosTaskJobCardSet.getJobCardSet().remove(fulfillmentTaskId);
            }
        }

        getLogger().debug(".removeJobCard(): Exit");
    }

    public PetasosTaskJobCard getJobCardForFulfillmentTask(TaskIdType taskId){
        getLogger().debug(".getJobCardForFulfillmentTask(): Entry, taskId->{}", taskId);

        //
        // Defensive Programming
        if(taskId == null){
            getLogger().debug(".getJobCardForFulfillmentTask(): Exit, taskId is null, returning null");
            return(null);
        }

        //
        // Try and retrieve the Job Card
        PetasosTaskJobCard petasosTaskJobCard = getFulfillmentTaskJobCardMap().get(taskId);

        getLogger().debug(".getJobCardForFulfillmentTask(): Exit, retrieved JobCard ->{}", petasosTaskJobCard);
        return(petasosTaskJobCard);
    }

    public PetasosTaskJobCard getJobCardForWUP(ComponentIdType wupId){
        getLogger().debug(".getJobCardForWUP(): Entry, wupId->{}", wupId);

        //
        // Defensive Programming
        if(wupId == null){
            getLogger().debug(".getJobCardForWUP(): Exit, wupId is null, returning null");
            return(null);
        }

        //
        // Try and retrieve the Job Card
        PetasosTaskJobCard petasosTaskJobCard = getWUPJobCardMap().get(wupId);

        getLogger().debug(".getJobCardForWUP(): Exit, retrieved JobCard ->{}", petasosTaskJobCard);
        return(petasosTaskJobCard);
    }

    public List<PetasosTaskJobCard> getJobCardsForActionableTask(TaskIdType actionableTaskId){
        getLogger().debug(".getJobCardsForActionableTask(): Entry, actionableTaskId->{}", actionableTaskId);

        //
        // Defensive Programming
        if(actionableTaskId == null){
            getLogger().debug(".getJobCardsForActionableTask(): Exit, taskId is null, returning null");
            return(null);
        }

        //
        // Try and retrieve the Job Cards
        List<PetasosTaskJobCard> jobCardList = new ArrayList<>();
        PetasosTaskJobCardSet petasosTaskJobCardSet = getActionableTaskJobCardMap().get(actionableTaskId);
        if(petasosTaskJobCardSet == null){
            return(jobCardList);
        }
        synchronized (getCacheLock()){
            jobCardList.addAll(petasosTaskJobCardSet.getAllPetasosTaskJobCard());
        }
        return(jobCardList);
    }

    //
    // Getters (and Setters)
    //

    protected Map<TaskIdType, PetasosTaskJobCardSet> getActionableTaskJobCardMap(){
        return(actionableTaskJobCardMap);
    }

    protected Map<TaskIdType, PetasosTaskJobCard> getFulfillmentTaskJobCardMap(){
        return(fulfillmentTaskJobCardMap);
    }

    protected Map<ComponentIdType, PetasosTaskJobCard> getWUPJobCardMap(){
        return(wupJobCardMap);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected Object getCacheLock(){
        return(this.cacheLock);
    }
}
