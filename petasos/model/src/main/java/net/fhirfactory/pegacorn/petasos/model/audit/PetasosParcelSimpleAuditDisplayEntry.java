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

package net.fhirfactory.pegacorn.petasos.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import org.hl7.fhir.r4.model.IdType;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PetasosParcelSimpleAuditDisplayEntry {
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date auditTrailEntryDate;
    private UoW actualUoW;
    private TaskIdType identifier;
    private TaskFinalisationStatusEnum parcelFinalsationStatus;
    private ResilienceParcelProcessingStatusEnum processingStatus;
    private HashSet<String> alternativeWUPIdentifierSet;
    private HashSet<String> alternativeParcelIdentifiersSet;
    private HashSet<String> downstreamEpisodeIdentifierSet;
    private String upstreamEpisodeIdentifier;
    private String primaryWUPIdentifier;
    private String parcelTypeID;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date parcelRegistrationDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date parcelStartDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date parcelFinishedDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date parcelFinalisedDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date parcelCancellationDate;

    //
    // Constructor(s)
    //

    public PetasosParcelSimpleAuditDisplayEntry(){
        this.auditTrailEntryDate = null;
        this.actualUoW = null;
        this.identifier = null;
        this.parcelFinalsationStatus = null;
        this.alternativeWUPIdentifierSet = null;
        this.processingStatus = null;
        this.downstreamEpisodeIdentifierSet = null;
        this.upstreamEpisodeIdentifier = null;
        this.primaryWUPIdentifier = null;
        this.parcelRegistrationDate = null;
        this.parcelTypeID = null;
        this.parcelStartDate = null;
        this.parcelFinishedDate = null;
        this.parcelFinalisedDate = null;
        this.alternativeParcelIdentifiersSet = null;
    }

    public PetasosParcelSimpleAuditDisplayEntry(PetasosParcelAuditTrailEntry theParcel ){
        // First, we clean the slate
        this.auditTrailEntryDate = null;
        this.actualUoW = null;
        this.identifier = null;
        this.parcelFinalsationStatus = null;
        this.alternativeWUPIdentifierSet = null;
        this.processingStatus = null;
        this.downstreamEpisodeIdentifierSet = null;
        this.upstreamEpisodeIdentifier = null;
        this.primaryWUPIdentifier = null;
        this.parcelRegistrationDate = null;
        this.parcelTypeID = null;
        this.parcelStartDate = null;
        this.parcelFinishedDate = null;
        this.parcelFinalisedDate = null;
        this.alternativeParcelIdentifiersSet = null;
        // Then, we try and add what we get given
        if( theParcel == null ){
            return;
        }
        this.auditTrailEntryDate = Date.from(Instant.now());
        if( theParcel.hasActualUoW()) {
            this.actualUoW = theParcel.getActualUoW();
        }
        if(theParcel.getDownstreamEpisodeIdentifierSet() != null){
            this.downstreamEpisodeIdentifierSet = new HashSet<>();
            for(PetasosEpisodeIdentifier currentParcelIdentifier: theParcel.getDownstreamEpisodeIdentifierSet()){
                this.downstreamEpisodeIdentifierSet.add(currentParcelIdentifier.getUnqualifiedToken());
            }
        }
        if(theParcel.getUpstreamEpisodeIdentifier() != null){
            this.upstreamEpisodeIdentifier = theParcel.getUpstreamEpisodeIdentifier().getUnqualifiedToken();
        }
        if(theParcel.getParcelTypeID() != null){
            this.parcelTypeID = theParcel.getParcelTypeID().getUnqualifiedToken();
        }
        if(theParcel.getParcelFinalisedDate() != null){
            this.parcelFinalisedDate = theParcel.getParcelFinalisedDate();
        }
        if(theParcel.getIdentifier() != null){
            setIdentifier(theParcel.getIdentifier());
        }
        if(theParcel.getParcelFinishedDate() != null){
            this.parcelFinishedDate = theParcel.getParcelFinishedDate();
        }
        if(theParcel.getParcelRegistrationDate() != null){
            this.parcelRegistrationDate = theParcel.getParcelRegistrationDate();
        }
        if(theParcel.getParcelStartDate() != null){
            this.parcelStartDate = theParcel.getParcelStartDate();
        }
        if(theParcel.getParcelCancellationDate() != null){
            this.parcelCancellationDate = theParcel.getParcelCancellationDate();
        }
        if(theParcel.getProcessingStatus() != null){
            this.processingStatus = theParcel.getProcessingStatus();
        }
        if(theParcel.getParcelFinalsationStatus() != null){
            this.parcelFinalsationStatus = theParcel.getParcelFinalsationStatus();
        }
        if(theParcel.getPrimaryWUPIdentifier() != null){
            this.primaryWUPIdentifier = theParcel.getPrimaryWUPIdentifier().getTokenValue();
        }
    }

    //
    // Bean/Attribute Helper Methods
    //
    
    // Helpers for the this.downstreamEpisodeIDSet attribute

    public Date getAuditTrailEntryDate() {
        return auditTrailEntryDate;
    }

    public void setAuditTrailEntryDate(Date auditTrailEntryDate) {
        this.auditTrailEntryDate = auditTrailEntryDate;
    }

    public UoW getActualUoW() {
        return actualUoW;
    }

    public void setActualUoW(UoW actualUoW) {
        this.actualUoW = actualUoW;
    }

    public TaskIdType getIdentifier() {
        return identifier;
    }

    public void setIdentifier(TaskIdType identifier) {
        this.identifier = identifier;
    }

    public TaskFinalisationStatusEnum getParcelFinalsationStatus() {
        return parcelFinalsationStatus;
    }

    public void setParcelFinalsationStatus(TaskFinalisationStatusEnum parcelFinalsationStatus) {
        this.parcelFinalsationStatus = parcelFinalsationStatus;
    }

    public ResilienceParcelProcessingStatusEnum getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ResilienceParcelProcessingStatusEnum processingStatus) {
        this.processingStatus = processingStatus;
    }

    public HashSet<String> getAlternativeWUPIdentifierSet() {
        return alternativeWUPIdentifierSet;
    }

    public void setAlternativeWUPIdentifierSet(HashSet<String> alternativeWUPIdentifierSet) {
        this.alternativeWUPIdentifierSet = alternativeWUPIdentifierSet;
    }

    public HashSet<String> getAlternativeParcelIdentifiersSet() {
        return alternativeParcelIdentifiersSet;
    }

    public void setAlternativeParcelIdentifiersSet(HashSet<String> alternativeParcelIdentifiersSet) {
        this.alternativeParcelIdentifiersSet = alternativeParcelIdentifiersSet;
    }

    public HashSet<String> getDownstreamEpisodeIdentifierSet() {
        return downstreamEpisodeIdentifierSet;
    }

    public void setDownstreamEpisodeIdentifierSet(HashSet<String> downstreamEpisodeIdentifierSet) {
        this.downstreamEpisodeIdentifierSet = downstreamEpisodeIdentifierSet;
    }

    public String getUpstreamEpisodeIdentifier() {
        return upstreamEpisodeIdentifier;
    }

    public void setUpstreamEpisodeIdentifier(String upstreamEpisodeIdentifier) {
        this.upstreamEpisodeIdentifier = upstreamEpisodeIdentifier;
    }

    public String getPrimaryWUPIdentifier() {
        return primaryWUPIdentifier;
    }

    public void setPrimaryWUPIdentifier(String primaryWUPIdentifier) {
        this.primaryWUPIdentifier = primaryWUPIdentifier;
    }

    public String getParcelTypeID() {
        return parcelTypeID;
    }

    public void setParcelTypeID(String parcelTypeID) {
        this.parcelTypeID = parcelTypeID;
    }

    public Date getParcelRegistrationDate() {
        return parcelRegistrationDate;
    }

    public void setParcelRegistrationDate(Date parcelRegistrationDate) {
        this.parcelRegistrationDate = parcelRegistrationDate;
    }

    public Date getParcelStartDate() {
        return parcelStartDate;
    }

    public void setParcelStartDate(Date parcelStartDate) {
        this.parcelStartDate = parcelStartDate;
    }

    public Date getParcelFinishedDate() {
        return parcelFinishedDate;
    }

    public void setParcelFinishedDate(Date parcelFinishedDate) {
        this.parcelFinishedDate = parcelFinishedDate;
    }

    public Date getParcelFinalisedDate() {
        return parcelFinalisedDate;
    }

    public void setParcelFinalisedDate(Date parcelFinalisedDate) {
        this.parcelFinalisedDate = parcelFinalisedDate;
    }

    public Date getParcelCancellationDate() {
        return parcelCancellationDate;
    }

    public void setParcelCancellationDate(Date parcelCancellationDate) {
        this.parcelCancellationDate = parcelCancellationDate;
    }
}
