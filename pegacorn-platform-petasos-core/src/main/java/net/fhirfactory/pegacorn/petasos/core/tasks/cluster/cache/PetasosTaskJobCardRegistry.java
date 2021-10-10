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
package net.fhirfactory.pegacorn.petasos.core.tasks.cluster.cache;

import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCardSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosTaskJobCardRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskJobCardRegistry.class);

    private ConcurrentHashMap<TaskIdType, PetasosTaskJobCardSet> actionableTaskJobCardMap;
    private Object actionableTaskJobCardMapLock;


    //
    // Constructor(s)
    //
    public PetasosTaskJobCardRegistry(){
        this.actionableTaskJobCardMap = new ConcurrentHashMap<>();
        this.actionableTaskJobCardMapLock = new Object();
    }

    //
    // Registry Business Methods
    //

    public void registerActionableTaskIdentity(TaskIdType actionableTaskIdentifier){
        getLogger().debug(".registerActionableTaskIdentity(): Entry, actionableTaskIdentifier->{}", actionableTaskIdentifier);
        //
        // (Defensive Programming) Check passed-in attributes
        if(actionableTaskIdentifier == null){
            getLogger().debug(".registerActionableTaskIdentity(): Exit, actionableTaskIdentifier is null");
            return;
        }
        //
        // Now add identifier, but first check to see if it is already there (and don't bother adding it if it is).
        // We should also create/initialise a Set and including it into the map.
        synchronized (getActionableTaskJobCardMapLock()){
            if(getActionableTaskJobCardMap().contains(actionableTaskIdentifier)){
                // Do nothing
            } else {
                PetasosTaskJobCardSet newJobCardSet = new PetasosTaskJobCardSet();
                getActionableTaskJobCardMap().put(actionableTaskIdentifier,newJobCardSet);
            }
        }
        //
        // All done!
        getLogger().debug(".registerActionableTaskIdentity(): Exit");
    }

    public void unregisterActionableTaskIdentity(TaskIdType actionableTaskIdentifier){
        getLogger().debug(".unregisterActionableTaskIdentity(): Entry, actionableTaskIdentifier->{}", actionableTaskIdentifier);
        //
        // (Defensive Programming) Check passed-in attributes
        if(actionableTaskIdentifier == null){
            getLogger().debug(".unregisterActionableTaskIdentity(): Exit, actionableTaskIdentifier is null");
            return;
        }
        //
        // Now remove entry, note that we don't care if there are still JobCards within the queue --> that's SEP!
        // But we can WARN people about it!
        boolean hasEntries = false;
        synchronized (getActionableTaskJobCardMapLock()){
            if(getActionableTaskJobCardMap().containsKey(actionableTaskIdentifier)){
                if(getActionableTaskJobCardMap().get(actionableTaskIdentifier).getJobCardSet().size() > 0){
                    hasEntries = true;
                }
            }
        }
        if(hasEntries){
            getLogger().warn(".unregisterActionableTaskIdentity(): Unregistering an ActionableTask with pending JobCards!");
        }
        //
        // We've done our good deed and WARN'ed the world, so now let's remove it!
        synchronized (getActionableTaskJobCardMapLock()){
            if(getActionableTaskJobCardMap().containsKey(actionableTaskIdentifier)){
                getActionableTaskJobCardMap().remove(actionableTaskIdentifier);
            }
        }
        //
        // All done!
        getLogger().debug(".unregisterActionableTaskIdentity(): Exit");
    }

    public void registerTaskJobCard(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskJobCard(): Entry, jobCard->{}", jobCard);
        //
        // (Defensive Programming) Check passed-in attributes
        if(jobCard == null){
            getLogger().debug(".registerTaskJobCard(): Exit, jobCard is null");
            return;
        }
        TaskIdType actionableTaskIdentifier = jobCard.getActionableTaskIdentifier();
        if(actionableTaskIdentifier == null){
            getLogger().debug(".registerTaskJobCard(): Exit, jobCard.getActionableTaskIdentifier() is null");
            return;
        }
        //
        // Add the PetasosTaskJobCard to the PetasosActionableTask's job map, if no map is there, add one!
        //

        //
        // Let's check that a JobMap exists for this actionableTaskIdentifier
        boolean doesNotExist = true;
        synchronized (getActionableTaskJobCardMapLock()){
            if(getActionableTaskJobCardMap().containsKey(actionableTaskIdentifier)){
                doesNotExist = false;
            }
        }
        if(doesNotExist){
            registerActionableTaskIdentity(actionableTaskIdentifier);
        }
        //
        // Now let's add our JobCard
        synchronized (getActionableTaskJobCardMapLock()){
            getActionableTaskJobCardMap().get(actionableTaskIdentifier).addPetasosTaskJobCard(jobCard);
        }
        //
        // All done!
        getLogger().debug(".registerTaskJobCard(): Exit");
    }

    public void unregisterTaskJobCard(PetasosTaskJobCard jobCard) {
        getLogger().debug(".unregisterTaskJobCard(): Entry, jobCard->{}", jobCard);
        //
        // (Defensive Programming) Check passed-in attributes
        if(jobCard == null){
            getLogger().debug(".unregisterTaskJobCard(): Exit, jobCard is null");
            return;
        }
        TaskIdType actionableTaskIdentifier = jobCard.getActionableTaskIdentifier();
        if(actionableTaskIdentifier == null){
            getLogger().debug(".unregisterTaskJobCard(): Exit, jobCard.getActionableTaskIdentifier() is null");
            return;
        }
        //
        // Let's check that a JobMap exists for this actionableTaskIdentifier
        boolean doesNotExist = true;
        synchronized (getActionableTaskJobCardMapLock()){
            if(getActionableTaskJobCardMap().containsKey(actionableTaskIdentifier)){
                doesNotExist = false;
            }
        }
        if(doesNotExist){
            getLogger().debug(".unregisterTaskJobCard(): Exit, no entry in the map for PetasosActionableTask");
            return;
        }
        //
        // Now let's add our JobCard
        synchronized (getActionableTaskJobCardMapLock()){
            getActionableTaskJobCardMap().get(actionableTaskIdentifier).removePetasosTaskJobCard(jobCard);
        }
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public ConcurrentHashMap<TaskIdType, PetasosTaskJobCardSet> getActionableTaskJobCardMap() {
        return actionableTaskJobCardMap;
    }

    public Object getActionableTaskJobCardMapLock() {
        return actionableTaskJobCardMapLock;
    }
}
