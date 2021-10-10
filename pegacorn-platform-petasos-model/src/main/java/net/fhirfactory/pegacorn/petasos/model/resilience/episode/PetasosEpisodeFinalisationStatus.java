/*
 * Copyright (c) 2020 mhunter
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

/**
 * 
 * @author Mark A. Hunter
 *
 */
public class PetasosEpisodeFinalisationStatus {
    private PetasosEpisodeIdentifier episodeID;
    private PetasosEpisodeIdentifier downstreamEpisodeID;
    private WUPFunctionToken downstreamWUPFunction;
    private Object registrationStatusLock;
    private PetasosEpisodeFinalisationStatusEnum registrationStatus;


    public PetasosEpisodeFinalisationStatus(PetasosEpisodeIdentifier episodeID, WUPFunctionToken downstreamWUPID){
        this.downstreamWUPFunction = downstreamWUPID;
        this.episodeID = episodeID;
        this.downstreamEpisodeID = null;
        this.registrationStatusLock = new Object();
        this.registrationStatus = PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_NOT_REGISTERED;
    }

    public PetasosEpisodeFinalisationStatusEnum getRegistrationStatus(){
        return(this.registrationStatus);
    }

    public PetasosEpisodeIdentifier getDownstreamEpisodeID(){
        return(this.downstreamEpisodeID);
    }

    public void setDownstreamEpisodeID(PetasosEpisodeIdentifier downstreamEpisodeID){
        synchronized (registrationStatusLock){
            this.downstreamEpisodeID = downstreamEpisodeID;
            this.registrationStatus = PetasosEpisodeFinalisationStatusEnum.DOWNSTREAM_EPISODE_ID_REGISTERED;
        }
    }

    public WUPFunctionToken getDownstreamWUPFunction(){
        return(this.downstreamWUPFunction);
    }

    public PetasosEpisodeIdentifier getEpisodeID() {
        return episodeID;
    }

    public void setEpisodeID(PetasosEpisodeIdentifier episodeID) {
        this.episodeID = episodeID;
    }

    public void setDownstreamWUPFunction(WUPFunctionToken downstreamWUPFunction) {
        this.downstreamWUPFunction = downstreamWUPFunction;
    }

    public void setRegistrationStatus(PetasosEpisodeFinalisationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
