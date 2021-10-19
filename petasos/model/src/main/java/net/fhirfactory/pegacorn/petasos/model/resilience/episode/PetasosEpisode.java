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

package net.fhirfactory.pegacorn.petasos.model.resilience.episode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.petasos.model.audit.JsonDateSerializer;
import net.fhirfactory.pegacorn.petasos.model.task.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;

import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class PetasosEpisode {
    private ConcurrentHashMap<WUPIdentifier, ResilienceParcel> episodeParcelMap;
    private PetasosEpisodeIdentifier episodeIdentifier;
    private ResilienceParcelFinalisationStatusEnum episodeFinalisationStatus;
    private FulfillmentExecutionStatusEnum episodeProcessingStatus;
    private HashSet<PetasosEpisodeIdentifier> downstreamEpisodeIdentifierSet;
    private PetasosEpisodeIdentifier upstreamEpisodeIdentifier;
    private WUPIdentifier activeWUPIdentifier;
    private FDNToken episodeFunction;
    private Date episodeStartTime;
    private Date episodeFinishTime;
    private Date episodeFinalisationTime;
    private Date episodeCancellationTime;
    private Date episodeLastUpdateTime;

    @JsonIgnore
    private Object episodeLock;

    //
    // Constructor(s)
    //

    public PetasosEpisode(){
        this.episodeParcelMap = new ConcurrentHashMap<>();
        this.episodeIdentifier = null;
        this.episodeFinalisationStatus = null;
        this.episodeProcessingStatus = null;
        this.downstreamEpisodeIdentifierSet = new HashSet<>();
        this.upstreamEpisodeIdentifier = null;
        this.activeWUPIdentifier = null;
        this.episodeStartTime = null;
        this.episodeFunction = null;
        this.episodeFinishTime = null;
        this.episodeFinalisationTime = null;
        this.episodeCancellationTime = null;
        this.episodeLastUpdateTime = null;
    }

    public PetasosEpisode(PetasosEpisode originalEpisode ){
        // First, we clean the slate
        this.episodeParcelMap = new ConcurrentHashMap<>();
        this.episodeIdentifier = null;
        this.episodeFinalisationStatus = null;
        this.episodeProcessingStatus = null;
        this.downstreamEpisodeIdentifierSet = new HashSet<>();
        this.upstreamEpisodeIdentifier = null;
        this.activeWUPIdentifier = null;
        this.episodeStartTime = null;
        this.episodeFunction = null;
        this.episodeFinishTime = null;
        this.episodeFinalisationTime = null;
        this.episodeCancellationTime = null;
        this.episodeLastUpdateTime = null;

        this.episodeLock = new Object();

        // Then, we try and add what we get given
        if( originalEpisode == null ){
            return;
        }
        this.episodeLastUpdateTime = Date.from(Instant.now());
        if( originalEpisode.hasEpisodeParcelMap()) {
            Enumeration<WUPIdentifier> idSet = originalEpisode.getEpisodeParcelMap().keys();
            while(idSet.hasMoreElements()){
                WUPIdentifier currentParcelKey = idSet.nextElement();
                ResilienceParcel currentParcel = originalEpisode.getEpisodeParcelMap().get(currentParcelKey);
                this.episodeParcelMap.put(currentParcelKey, currentParcel);
            }
        }
        if(originalEpisode.hasEpisodeIdentifier()){
            this.episodeIdentifier = originalEpisode.getEpisodeIdentifier();
        }
        if(originalEpisode.hasEpisodeFinalisationStatus()){
            this.episodeFinalisationStatus = originalEpisode.getEpisodeFinalisationStatus();
        }
        if(originalEpisode.hasProcessingStatus()){
            this.episodeProcessingStatus = originalEpisode.getEpisodeProcessingStatus();
        }
        if(originalEpisode.hasDownstreamEpisodeIdentifierSet()){
            this.setDownstreamEpisodeIdentifierSet(originalEpisode.getDownstreamEpisodeIdentifierSet());
        }
        if(originalEpisode.hasUpstreamEpisodeIdentifier()){
            this.upstreamEpisodeIdentifier = originalEpisode.getUpstreamEpisodeIdentifier();
        }
        if(originalEpisode.hasEpisodeStartTime()){
            this.episodeStartTime = originalEpisode.getEpisodeStartTime();
        }
        if(originalEpisode.hasEpisodeFunction()){
            this.episodeFunction = originalEpisode.getEpisodeFunction();
        }
        if (originalEpisode.hasEpisodeFinishTime()) {
            this.episodeFinishTime = originalEpisode.getEpisodeFinishTime();
        }
        if(originalEpisode.hasEpisodeFinalisationTime()){
            this.episodeFinalisationTime = originalEpisode.getEpisodeFinalisationTime();
        }
        if(originalEpisode.hasEpisodeCancellationTime()){
            this.episodeCancellationTime = originalEpisode.getEpisodeCancellationTime();
        }
    }

    //
    // Bean/Attribute Helper Methods
    //
    
    // Helpers for the this.episodeParcelMap attribute

    public boolean hasEpisodeParcelMap(){
        if(this.episodeParcelMap == null){
            return(false);
        }
        if(this.episodeParcelMap.isEmpty()){
            return(false);
        }
        return(true);
    }

    public ConcurrentHashMap<WUPIdentifier, ResilienceParcel> getEpisodeParcelMap() {
        return episodeParcelMap;
    }

    public void setEpisodeParcelMap(ConcurrentHashMap<WUPIdentifier, ResilienceParcel> episodeParcelMap) {
        synchronized (this.episodeLock) {
            this.episodeParcelMap = episodeParcelMap;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeIdentifier attribute

    public boolean hasEpisodeIdentifier(){
        if(this.episodeIdentifier == null){
            return(false);
        } else {
            return(true);
        }
    }

    public PetasosEpisodeIdentifier getEpisodeIdentifier() {
        return episodeIdentifier;
    }

    public void setEpisodeIdentifier(PetasosEpisodeIdentifier episodeIdentifier) {
        synchronized(this.episodeLock) {
            this.episodeIdentifier = episodeIdentifier;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeFinalisationStatus attribute

    public boolean hasEpisodeFinalisationStatus(){
        if(this.episodeFinalisationStatus == null ){
            return(false);
        } else {
            return (true);
        }
    }

    public ResilienceParcelFinalisationStatusEnum getEpisodeFinalisationStatus() {
        return episodeFinalisationStatus;
    }

    public void setEpisodeFinalisationStatus(ResilienceParcelFinalisationStatusEnum episodeFinalisationStatus) {
        synchronized(this.episodeLock) {
            this.episodeFinalisationStatus = episodeFinalisationStatus;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeProcessingStatus attribute

    public boolean hasProcessingStatus(){
        if(this.episodeProcessingStatus == null){
            return(false);
        }
        return(true);
    }
    public FulfillmentExecutionStatusEnum getEpisodeProcessingStatus() {
        return(this.episodeProcessingStatus);
    }

    public void setEpisodeProcessingStatus(FulfillmentExecutionStatusEnum newProcessingStatus) {
        synchronized(this.episodeLock) {
            this.episodeProcessingStatus = newProcessingStatus;
            updateEpisodeLastUpdateTime();
        }
    }


    public boolean hasDownstreamParcelIDSet() {
    	if(this.downstreamEpisodeIdentifierSet == null ) {
    		return(false);
    	}
    	if(this.downstreamEpisodeIdentifierSet.isEmpty()) {
    		return(false);
    	}
    	return(true);
    }

    // Helpers for the this.downstreamEpisodeIdentifierSet attribute

    public boolean hasDownstreamEpisodeIdentifierSet(){
        if(this.downstreamEpisodeIdentifierSet == null) {
            return(false);
        }
        if(this.downstreamEpisodeIdentifierSet.isEmpty()) {
            return(false);
        }
        return(true);
    }

    public void setDownstreamEpisodeIdentifierSet(HashSet<PetasosEpisodeIdentifier> newDownstreamEpisodeIdentifierSet) {
        synchronized (this.episodeLock) {
            boolean anUpdateAvailable = false;
            if (newDownstreamEpisodeIdentifierSet != null) {
                if (!newDownstreamEpisodeIdentifierSet.isEmpty()) {
                    this.downstreamEpisodeIdentifierSet.addAll(newDownstreamEpisodeIdentifierSet);
                    updateEpisodeLastUpdateTime();
                }
            }
        }
    }
    
    public HashSet<PetasosEpisodeIdentifier> getDownstreamEpisodeIdentifierSet() {
    	if(this.downstreamEpisodeIdentifierSet == null) {
    		return(new HashSet<>());
    	}
    	return(this.downstreamEpisodeIdentifierSet);
    }

    // Helpers for the this.upstreamEpisodeIdentifier attribute

    public boolean hasUpstreamEpisodeIdentifier(){
        if(this.upstreamEpisodeIdentifier ==null){
            return(false);
        }
        return(true);
    }

    public PetasosEpisodeIdentifier getUpstreamEpisodeIdentifier() {
        return(upstreamEpisodeIdentifier);
    }

    public void setUpstreamEpisodeIdentifier(PetasosEpisodeIdentifier upstreamEpisodeIdentifier) {
        synchronized(this.episodeLock) {
            this.upstreamEpisodeIdentifier = upstreamEpisodeIdentifier;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.activeWUPIdentifier attribute

    public boolean hasActiveWUPIdentifier(){
        if(this.activeWUPIdentifier == null){
            return(false);
        }
        return(true);
    }

    public WUPIdentifier getActiveWUPIdentifier() {
        return activeWUPIdentifier;
    }

    public void setActiveWUPIdentifier(WUPIdentifier activeWUPIdentifier) {
        synchronized(this.episodeLock) {
            this.activeWUPIdentifier = activeWUPIdentifier;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeStartTime attribute

    public boolean hasEpisodeStartTime(){
        if(this.episodeStartTime == null){
            return(false);
        }
        return(true);
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getEpisodeStartTime() {
        return episodeStartTime;
    }

    public void setEpisodeStartTime(Date episodeStartTime) {
        synchronized(this.episodeLock) {
            this.episodeStartTime = episodeStartTime;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeFunction attribute

    public boolean hasEpisodeFunction(){
        if(this.episodeFunction == null){
            return(false);
        }
        return(true);
    }

    public FDNToken getEpisodeFunction() {
        return episodeFunction;
    }

    public void setEpisodeFunction(FDNToken episodeFunction) {
        synchronized(this.episodeLock) {
            this.episodeFunction = episodeFunction;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeFinishTime attribute

    public boolean hasEpisodeFinishTime(){
        if(this.episodeFinishTime == null){
            return(false);
        }
        return(true);
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getEpisodeFinishTime() {
        return episodeFinishTime;
    }

    public void setEpisodeFinishTime(Date episodeFinishTime) {
        synchronized(this.episodeLock) {
            this.episodeFinishTime = episodeFinishTime;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeFinalisationTime attribute

    public boolean hasEpisodeFinalisationTime(){
        if(this.episodeFinalisationTime == null){
            return(false);
        }
        return(true);
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getEpisodeFinalisationTime() {
        return episodeFinalisationTime;
    }

    public void setEpisodeFinalisationTime(Date episodeFinalisationTime) {
        synchronized(this.episodeLock) {
            this.episodeFinalisationTime = episodeFinalisationTime;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeCancellationTime attribute

    public boolean hasEpisodeCancellationTime(){
        if(this.episodeCancellationTime == null){
            return(false);
        }
        return(true);
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getEpisodeCancellationTime() {
        return episodeCancellationTime;
    }

    public void setEpisodeCancellationTime(Date episodeCancellationTime) {
        synchronized(this.episodeLock) {
            this.episodeCancellationTime = episodeCancellationTime;
            updateEpisodeLastUpdateTime();
        }
    }

    // Helpers for the this.episodeLastUpdateTime attribute

    public boolean hasEpisodeLastUpdateTime(){
        if(this.episodeLastUpdateTime == null){
            return(false);
        }
        return(true);
    }

    @JsonSerialize(using= JsonDateSerializer.class)
    public Date getEpisodeLastUpdateTime() {
        return episodeLastUpdateTime;
    }

    public void setEpisodeLastUpdateTime(Date episodeLastUpdateTime) {
        this.episodeLastUpdateTime = episodeLastUpdateTime;
    }

    @JsonIgnore
    protected void updateEpisodeLastUpdateTime(){
        this.setEpisodeLastUpdateTime(Date.from(Instant.now()));
    }
}

