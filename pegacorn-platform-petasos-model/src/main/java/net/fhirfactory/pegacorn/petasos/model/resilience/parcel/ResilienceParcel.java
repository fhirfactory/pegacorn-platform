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
package net.fhirfactory.pegacorn.petasos.model.resilience.parcel;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mark A. Hunter
 * @author Scott Yeadon
 */
public class ResilienceParcel implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ResilienceParcel.class);

    private ResilienceParcelIdentifier identifier;
    private SerializableObject instanceIDLock;
    private FDNToken typeID;
    private SerializableObject typeIDLock;
    private PetasosEpisodeIdentifier episodeIdentifier;
    private SerializableObject episodeIdentifierLock;
    private UoW actualUoW;
    private SerializableObject actualUoWLock;
    private WUPIdentifier associatedWUPIdentifier;
    private SerializableObject associatedWUPIdentifierLock;
    private WUPFunctionToken associatedWUPFunction;
    private SerializableObject associatedWUPFunctionLock;
    private HashSet<PetasosEpisodeIdentifier> downstreamEpisodeIdentifierSet;
    private SerializableObject downstreamEpisodeIdentifierSetLock;
    private PetasosEpisodeIdentifier upstreamEpisodeIdentifier;
    private SerializableObject upstreamEpisodeIdentifierLock;
    private final static String INSTANCE_QUALIFIER_TYPE = "ParcelInstance";
    private final static String TYPE_QUALIFIER_TYPE = "ParcelType";
    private ResilienceParcelFinalisationStatusEnum finalisationStatus;
    private SerializableObject finalisationStatusLock;
    private ResilienceParcelProcessingStatusEnum processingStatus;
    private SerializableObject processingStatusLock;
    private Date registrationDate;
    private SerializableObject registrationDateLock;
    private Date startDate;
    private SerializableObject startDateLock;
    private Date finishedDate;
    private SerializableObject finishedDateLock;
    private Date finalisationDate;
    private SerializableObject finalisationDateLock;
    private Date cancellationDate;
    private SerializableObject cancellationDateLock;

    private boolean anInteractWUP;
    private String associatedPortValue;
    private String associatedPortType;

    //
    // Constructors
    //
    public ResilienceParcel(ActivityID activityID, UoW theUoW) {
        // Clean the slate
        this.identifier = null;
        this.typeID = null;
        this.associatedWUPIdentifier = null;
        this.associatedWUPFunction = null;
        this.actualUoW = null;
        this.downstreamEpisodeIdentifierSet = null;
        this.upstreamEpisodeIdentifier = null;
        this.registrationDate = null;
        this.startDate = null;
        this.finishedDate = null;
        this.finalisationDate = null;
        this.finalisationStatus = null;
        this.episodeIdentifier = null;
        this.cancellationDate = null;
        this.anInteractWUP = false;
        this.associatedPortType = null;
        this.associatedPortValue = null;
        this.instanceIDLock = new SerializableObject();
        this.typeIDLock = new SerializableObject();
        this.episodeIdentifierLock = new SerializableObject();
        this.actualUoWLock = new SerializableObject();
        this.associatedWUPIdentifierLock = new SerializableObject();
        this.downstreamEpisodeIdentifierSetLock = new SerializableObject();
        this.upstreamEpisodeIdentifierLock = new SerializableObject();
        this.finalisationStatusLock = new SerializableObject();
        this.processingStatusLock = new SerializableObject();
        this.registrationDateLock = new SerializableObject();
        this.startDateLock = new SerializableObject();
        this.finishedDateLock = new SerializableObject();
        this.finalisationDateLock = new SerializableObject();
        this.cancellationDateLock = new SerializableObject();
        this.associatedWUPFunctionLock = new SerializableObject();
        // Now, add what we have been supplied
        this.associatedWUPIdentifier = activityID.getPresentWUPIdentifier();
        this.associatedWUPFunction = new WUPFunctionToken(activityID.getPresentWUPFunctionToken());
        this.episodeIdentifier = activityID.getPresentEpisodeIdentifier();
        this.typeID = this.buildParcelTypeID(activityID, theUoW);
        this.identifier = this.buildParcelInstanceIdentifier(activityID, theUoW);
        this.actualUoW = theUoW;
        this.downstreamEpisodeIdentifierSet = new HashSet<PetasosEpisodeIdentifier>();
        this.upstreamEpisodeIdentifier = activityID.getPreviousEpisodeIdentifier();
        this.registrationDate = Date.from(Instant.now());
        this.finalisationStatus = ResilienceParcelFinalisationStatusEnum.PARCEL_FINALISATION_STATUS_NOT_FINALISED;
    }

    public ResilienceParcel(ResilienceParcel originalParcel) {
        // Clean the slate
        this.identifier = null;
        this.typeID = null;
        this.associatedWUPIdentifier = null;
        this.associatedWUPFunction = null;
        this.actualUoW = null;
        this.downstreamEpisodeIdentifierSet = null;
        this.upstreamEpisodeIdentifier = null;
        this.registrationDate = null;
        this.startDate = null;
        this.finishedDate = null;
        this.finalisationDate = null;
        this.episodeIdentifier = null;
        this.cancellationDate = null;
        this.anInteractWUP = false;
        this.associatedPortType = null;
        this.associatedPortValue = null;
        this.instanceIDLock = new SerializableObject();
        this.typeIDLock = new SerializableObject();
        this.episodeIdentifierLock = new SerializableObject();
        this.actualUoWLock = new SerializableObject();
        this.associatedWUPIdentifierLock = new SerializableObject();
        this.downstreamEpisodeIdentifierSetLock = new SerializableObject();
        this.upstreamEpisodeIdentifierLock = new SerializableObject();
        this.finalisationStatusLock = new SerializableObject();
        this.processingStatusLock = new SerializableObject();
        this.registrationDateLock = new SerializableObject();
        this.startDateLock = new SerializableObject();
        this.finishedDateLock = new SerializableObject();
        this.finalisationDateLock = new SerializableObject();
        this.cancellationDateLock = new SerializableObject();
        this.associatedWUPFunctionLock = new SerializableObject();
        // Now, add what we have been supplied
        if (originalParcel.hasCancellationDate()) {
            this.cancellationDate = originalParcel.getCancellationDate();
        }
        if (originalParcel.hasAssociatedWUPIdentifier()) {
            this.associatedWUPIdentifier = originalParcel.getAssociatedWUPIdentifier();
        }
        if (originalParcel.hasAssociatedWUPFunction()) {
            this.associatedWUPFunction = originalParcel.getAssociatedWUPFunction();
        }
        if (originalParcel.hasActualUoW()) {
            this.actualUoW = originalParcel.getActualUoW();
        }
        if (originalParcel.hasFinalisationDate()) {
            this.finalisationDate = originalParcel.getFinalisationDate();
        }
        if (originalParcel.hasFinishedDate()) {
            this.finishedDate = originalParcel.getFinishedDate();
        }
        if (originalParcel.hasInstanceIdentifier()) {
            this.identifier = originalParcel.getIdentifier();
        }
        if (originalParcel.hasRegistrationDate()) {
            this.registrationDate = originalParcel.getRegistrationDate();
        }
        if (originalParcel.hasStartDate()) {
            this.startDate = originalParcel.getStartDate();
        }
        if (originalParcel.hasTypeID()) {
            this.typeID = originalParcel.getTypeID();
        }
        if (originalParcel.hasUpstreamEpisodeIdentifier()) {
            this.upstreamEpisodeIdentifier = originalParcel.getUpstreamEpisodeIdentifier();
        }
        if (originalParcel.hasDownstreamEpisodeIdentifierSet()) {
            this.downstreamEpisodeIdentifierSet = new HashSet<PetasosEpisodeIdentifier>();
            this.downstreamEpisodeIdentifierSet.addAll(originalParcel.getDownstreamEpisodeIdentifierSet());
        }
        if (originalParcel.hasEpisodeIdentifier()) {
            this.episodeIdentifier = originalParcel.getEpisodeIdentifier();
        }
        if(originalParcel.hasAssociatedPortValue()){
            this.associatedPortValue = originalParcel.getAssociatedPortValue();
        }
        if(originalParcel.hasAssociatedPortType()){
            this.associatedPortType = originalParcel.getAssociatedPortType();
        }
        this.anInteractWUP = originalParcel.isAnInteractWUP();
    }

    //
    // Bean/Attribute Methods
    //

    public boolean hasAssociatedWUPFunction(){
        boolean hasValue = this.associatedWUPFunction != null;
        return(hasValue);
    }

    public WUPFunctionToken getAssociatedWUPFunction() {
        return associatedWUPFunction;
    }

    public void setAssociatedWUPFunction(WUPFunctionToken associatedWUPFunction) {
        this.associatedWUPFunction = associatedWUPFunction;
    }

    public boolean isAnInteractWUP() {
        return anInteractWUP;
    }

    public void setAnInteractWUP(boolean anInteractWUP) {
        this.anInteractWUP = anInteractWUP;
    }

    public boolean hasAssociatedPortValue(){
        boolean hasValue = this.associatedPortValue != null;
        return(hasValue);
    }

    public String getAssociatedPortValue() {
        return associatedPortValue;
    }

    public void setAssociatedPortValue(String associatedPortValue) {
        this.associatedPortValue = associatedPortValue;
    }

    public boolean hasAssociatedPortType(){
        boolean hasValue = this.associatedPortType != null;
        return(hasValue);
    }

    public String getAssociatedPortType() {
        return associatedPortType;
    }

    public void setAssociatedPortType(String associatedPortType) {
        this.associatedPortType = associatedPortType;
    }

    // Helper methods for the this.cancellationDate attribute
    public boolean hasCancellationDate() {
        if (this.cancellationDate == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public Date getCancellationDate() {
        return (this.cancellationDate);
    }

    public void setCancellationDate(Date newCancellationDate) {
        synchronized (cancellationDateLock) {
            this.cancellationDate = newCancellationDate;
        }
    }

    // Helper methods for the this.actualUoW attribute
    public boolean hasActualUoW() {
        if (this.actualUoW == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * @return the containedUoW
     */
    public UoW getActualUoW() {
        return actualUoW;
    }

    /**
     * @param actualUoW the containedUoW to set
     */
    public void setActualUoW(UoW actualUoW) {
        synchronized (actualUoWLock) {
            this.actualUoW = new UoW(actualUoW);
        }
    }

    // Helper methods for the this.actualUoW attribute
    public boolean hasDownstreamEpisodeIdentifierSet() {
        if (this.downstreamEpisodeIdentifierSet == null) {
            return (false);
        }
        if (this.downstreamEpisodeIdentifierSet.isEmpty()) {
            return (false);
        }
        return (true);
    }

    /**
     * @return the downstreamParcelIDSet
     */
    public Set<PetasosEpisodeIdentifier> getDownstreamEpisodeIdentifierSet() {
        if (this.downstreamEpisodeIdentifierSet == null) {
            return (null);
        } else {
            return (this.downstreamEpisodeIdentifierSet);
        }
    }

    /**
     * @param downstreamEpisodeIdentifierSet the Parcels that continue on the work from
     * this Parcel
     */
    public void setDownstreamEpisodeIdentifierSet(HashSet<PetasosEpisodeIdentifier> downstreamEpisodeIdentifierSet) {
        synchronized (downstreamEpisodeIdentifierSetLock) {
            if (downstreamEpisodeIdentifierSet == null) {
                this.downstreamEpisodeIdentifierSet = new HashSet<PetasosEpisodeIdentifier>();
            }
        }
    }

    // Helper methods for the this.upstreamEpisodeID attribute
    public boolean hasUpstreamEpisodeIdentifier() {
        if (this.upstreamEpisodeIdentifier == null) {
            return (false);
        }
        return (true);
    }

    /**
     * @return the upstreamParcelInstanceID
     */
    public PetasosEpisodeIdentifier getUpstreamEpisodeIdentifier() {

        return this.upstreamEpisodeIdentifier;

    }

    /**
     * @param upstreamEpisodeIdentifier the "Upstream" or "Precursor" Parcel to set
     */
    public void setUpstreamEpisodeIdentifier(PetasosEpisodeIdentifier upstreamEpisodeIdentifier) {
        synchronized (upstreamEpisodeIdentifierLock) {
            this.upstreamEpisodeIdentifier = upstreamEpisodeIdentifier;
        }
    }

    // Helper methods for the this.parcelInstanceID attribute
    public boolean hasInstanceIdentifier() {
        if (this.identifier == null) {
            return (false);
        }
        return (true);
    }

    public ResilienceParcelIdentifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(ResilienceParcelIdentifier parcelInstance) {
        synchronized (instanceIDLock) {
            this.identifier = parcelInstance;
        }
    }

    // Helper methods for the this.parcelTypeID attribute
    public boolean hasTypeID() {
        if (this.typeID == null) {
            return (false);
        }
        return (true);
    }

    public FDNToken getTypeID() {
        return this.typeID;
    }

    public void setTypeID(FDNToken typeID) {
        synchronized (typeIDLock) {
            this.typeID = typeID;
        }
    }

    public void setParcelTypeFDN(FDNToken parcelType) {
        synchronized (typeIDLock) {
            this.typeID = parcelType;
        }
    }

    // Helper methods for the this.parcelRegistrationDate attribute
    public boolean hasRegistrationDate() {
        if (registrationDate == null) {
            return (false);
        }
        return (true);
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        synchronized (registrationDateLock) {
            this.registrationDate = registrationDate;
        }
    }

    // Helper methods for the this.parcelStartDate attribute
    public boolean hasStartDate() {
        if (this.startDate == null) {
            return (false);
        }
        return (true);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        synchronized (startDateLock) {
            this.startDate = startDate;
        }
    }

    // Helper methods for the this.parcelFinishedDate attribute
    public boolean hasFinishedDate() {
        if (this.finishedDate == null) {
            return (false);
        }
        return (true);
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        synchronized (finishedDateLock) {
            this.finishedDate = finishedDate;
        }
    }

    // Helper methods for the this.parcelFinalisationDate attribute
    public boolean hasFinalisationDate() {
        if (this.finalisationDate == null) {
            return (false);
        }
        return (true);
    }

    public Date getFinalisationDate() {
        return finalisationDate;
    }

    public void setFinalisationDate(Date finalisationDate) {
        synchronized (finalisationDateLock) {
            this.finalisationDate = finalisationDate;
        }
    }

    // Helper methods for the this.associatedWUPInstanceID attribute
    public boolean hasAssociatedWUPIdentifier() {
        if (this.associatedWUPIdentifier == null) {
            return (false);
        }
        return (true);
    }

    public WUPIdentifier getAssociatedWUPIdentifier() {
        return associatedWUPIdentifier;
    }

    public void setAssociatedWUPIdentifier(WUPIdentifier associatedWUPIdentifier) {
        synchronized (associatedWUPIdentifierLock) {
            this.associatedWUPIdentifier = associatedWUPIdentifier;
        }
    }

    public boolean hasFinalisationStatus() {
        if (this.finalisationStatus == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public ResilienceParcelFinalisationStatusEnum getFinalisationStatus() {
        return finalisationStatus;
    }

    public void setFinalisationStatus(ResilienceParcelFinalisationStatusEnum finalisationStatus) {
        synchronized (finalisationStatusLock) {
            this.finalisationStatus = finalisationStatus;
        }
    }

    public boolean hasProcessingStatus() {
        if (this.processingStatus == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public ResilienceParcelProcessingStatusEnum getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ResilienceParcelProcessingStatusEnum processingStatus) {
        synchronized (processingStatusLock) {
            this.processingStatus = processingStatus;
        }
    }

    public boolean hasEpisodeIdentifier() {
        if (this.episodeIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public PetasosEpisodeIdentifier getEpisodeIdentifier() {
        return this.episodeIdentifier;
    }

    public void setEpisodeIdentifier(PetasosEpisodeIdentifier episodeIdentifier) {
        synchronized (episodeIdentifierLock) {
            this.episodeIdentifier = episodeIdentifier;
        }
    }

    public FDNToken buildParcelTypeID(ActivityID activityID, UoW theUoW) {
        if (theUoW == null) {
            throw (new IllegalArgumentException(".buildEpisodeID(): null UoW passed as parameter"));
        }
        if (activityID == null) {
            throw (new IllegalArgumentException(".buildEpisodeID(): null ActivityID passed as parameter"));
        }
        FDN uowTypeFDN;
        if (theUoW.hasTypeID()) {
            uowTypeFDN = new FDN(theUoW.getTypeID());
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): UoW has no type value, bad parameter"));
        }
        FDN newTypeID;
        if (activityID.hasPresentWUPFunctionToken()) {
            TopologyNodeFunctionFDN nodeFunctionFDN = new TopologyNodeFunctionFDN(activityID.getPresentWUPFunctionToken());
            newTypeID = nodeFunctionFDN.toTypeBasedFDNWithVersion();
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): ActivityID has no PresentWUPTypeID value, bad parameter"));
        }
        newTypeID.appendFDN(uowTypeFDN);
        return (newTypeID.getToken());
    }

    public ResilienceParcelIdentifier buildParcelInstanceIdentifier(ActivityID activityID, UoW theUoW) {
        if (theUoW == null) {
            throw (new IllegalArgumentException(".buildEpisodeID(): null UoW passed as parameter"));
        }
        if (activityID == null) {
            throw (new IllegalArgumentException(".buildEpisodeID(): null ActivityID passed as parameter"));
        }
        FDN uowInstanceFDN;
        if (theUoW.hasInstanceID()) {
            uowInstanceFDN = new FDN(theUoW.getInstanceID());
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): UoW has no instance value, bad parameter"));
        }
        FDN newInstanceID;
        if (activityID.hasPresentWUPIdentifier()) {
            newInstanceID = new FDN(activityID.getPresentWUPIdentifier().toTypeBasedFDNToken());
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): ActivityID has no PresentWUPInstanceID value, bad parameter"));
        }
        newInstanceID.appendFDN(uowInstanceFDN);
        ResilienceParcelIdentifier parcelIdentifier = new ResilienceParcelIdentifier(newInstanceID.getToken());
        return (parcelIdentifier);
    }

    @Override
    public String toString() {
        return "ResilienceParcel{" +
                "identifier=" + identifier +
                ", typeID=" + typeID +
                ", episodeIdentifier=" + episodeIdentifier +
                ", actualUoW=" + actualUoW +
                ", associatedWUPIdentifier=" + associatedWUPIdentifier +
                ", associatedWUPFunciton=" + associatedWUPFunction +
                ", downstreamEpisodeIdentifierSet=" + downstreamEpisodeIdentifierSet +
                ", upstreamEpisodeIdentifier=" + upstreamEpisodeIdentifier +
                ", finalisationStatus=" + finalisationStatus +
                ", processingStatus=" + processingStatus +
                ", registrationDate=" + registrationDate +
                ", startDate=" + startDate +
                ", finishedDate=" + finishedDate +
                ", finalisationDate=" + finalisationDate +
                ", cancellationDate=" + cancellationDate +
                ", anInteractWUP=" + anInteractWUP +
                ", associatedPortValue=" + associatedPortValue +
                ", associatedPortType=" + associatedPortType +
                '}';
    }
}
