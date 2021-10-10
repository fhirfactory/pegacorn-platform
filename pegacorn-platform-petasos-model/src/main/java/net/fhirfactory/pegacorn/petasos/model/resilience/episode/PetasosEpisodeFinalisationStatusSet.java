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
package net.fhirfactory.pegacorn.petasos.model.resilience.episode;

import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPFunctionToken;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PetasosEpisodeFinalisationStatusSet {
    private ConcurrentHashMap<WUPFunctionToken, PetasosEpisodeFinalisationStatus> statusSet;
    private Object updateLock;

    public PetasosEpisodeFinalisationStatusSet() {
        statusSet = new ConcurrentHashMap<>();
        updateLock = new Object();
    }

    public void addDownstreamWUPFunction(PetasosEpisodeIdentifier episodeID, WUPFunctionToken wupFunction) {
        synchronized (updateLock) {
            if(!this.statusSet.containsKey(wupFunction)) {
                PetasosEpisodeFinalisationStatus newStatusElement = new PetasosEpisodeFinalisationStatus(episodeID, wupFunction);
                newStatusElement.setRegistrationStatus(PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_NOT_REGISTERED);
                newStatusElement.setDownstreamWUPFunction(wupFunction);
            }
        }
    }

    public void addDownstreamEpisodeIdentifier(PetasosEpisodeIdentifier episodeID, WUPFunctionToken wupFunction, PetasosEpisodeIdentifier downstreamEpisodeID){
        synchronized (updateLock){
            if(this.statusSet.containsKey(wupFunction)){
                PetasosEpisodeFinalisationStatus existingStatusElement = this.statusSet.get(wupFunction);
                existingStatusElement.setRegistrationStatus(PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_REGISTERED);
                existingStatusElement.setDownstreamEpisodeID(episodeID);
            } else {
                PetasosEpisodeFinalisationStatus newStatusElement = new PetasosEpisodeFinalisationStatus(episodeID, wupFunction);
                newStatusElement.setRegistrationStatus(PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_REGISTERED);
                newStatusElement.setDownstreamEpisodeID(episodeID);
            }
        }
    }

    public void removeElement(WUPFunctionToken theFDNToken) {
        synchronized (updateLock) {
            if(this.statusSet.containsKey(theFDNToken)){
                this.statusSet.remove(theFDNToken);
            }
        }
    }

    public boolean isAllFinalised(){
        boolean isAllFinalised = true;
        synchronized (updateLock){
            Enumeration<WUPFunctionToken> keys = this.statusSet.keys();
            while(keys.hasMoreElements()){
                WUPFunctionToken wupFunctionToken = keys.nextElement();
                PetasosEpisodeFinalisationStatus petasosEpisodeFinalisationStatus = this.statusSet.get(wupFunctionToken);
                if(petasosEpisodeFinalisationStatus.getRegistrationStatus().equals(PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_NOT_REGISTERED)){
                    isAllFinalised = false;
                    break;
                }
            }
        }
        return(isAllFinalised);
    }

    public boolean isEmpty() {
        if (this.statusSet.isEmpty()) {
            return (true);
        } else {
            return (false);
        }
    }

    public List<WUPFunctionToken> getWUPFunctionTokenList(){
        List<WUPFunctionToken> tokenList = new ArrayList<>();
        synchronized (updateLock){
            Enumeration<WUPFunctionToken> keys = this.statusSet.keys();
            while(keys.hasMoreElements()){
                tokenList.add(keys.nextElement());
            }
        }
        return(tokenList);
    }
}
