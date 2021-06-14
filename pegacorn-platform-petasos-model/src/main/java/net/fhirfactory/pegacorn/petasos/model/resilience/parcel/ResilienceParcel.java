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
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mark A. Hunter
 * @author Scott Yeadon
 */
public class ResilienceParcel {

    private static final Logger LOG = LoggerFactory.getLogger(ResilienceParcel.class);

    private ResilienceParcelIdentifier identifier;
    private Object instanceIDLock;
    private FDNToken typeID;
    private Object typeIDLock;
    private PetasosEpisodeIdentifier episodeIdentifier;
    private Object episodeIdentifierLock;
    private UoW actualUoW;
    private Object actualUoWLock;
    private WUPIdentifier associatedWUPIdentifier;
    private Object associatedWUPIdentifierLock;
    private HashSet<PetasosEpisodeIdentifier> downstreamEpisodeIdentifierSet;
    private Object downstreamEpisodeIdentifierSetLock;
    private PetasosEpisodeIdentifier upstreamEpisodeIdentifier;
    private Object upstreamEpisodeIdentifierLock;
    private final static String INSTANCE_QUALIFIER_TYPE = "ParcelInstance";
    private final static String TYPE_QUALIFIER_TYPE = "ParcelType";
    private ResilienceParcelFinalisationStatusEnum finalisationStatus;
    private Object finalisationStatusLock;
    private ResilienceParcelProcessingStatusEnum processingStatus;
    private Object processingStatusLock;
    private Date registrationDate;
    private Object registrationDateLock;
    private Date startDate;
    private Object startDateLock;
    private Date finishedDate;
    private Object finishedDateLock;
    private Date finalisationDate;
    private Object finalisationDateLock;
    private Date cancellationDate;
    private Object cancellationDateLock;

    //
    // Constructors
    //
    public ResilienceParcel(ActivityID activityID, UoW theUoW) {
        // Clean the slate
        this.identifier = null;
        this.typeID = null;
        this.associatedWUPIdentifier = null;
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
        this.instanceIDLock = new Object();
        this.typeIDLock = new Object();
        this.episodeIdentifierLock = new Object();
        this.actualUoWLock = new Object();
        this.associatedWUPIdentifierLock = new Object();
        this.downstreamEpisodeIdentifierSetLock = new Object();
        this.upstreamEpisodeIdentifierLock = new Object();
        this.finalisationStatusLock = new Object();
        this.processingStatusLock = new Object();
        this.registrationDateLock = new Object();
        this.startDateLock = new Object();
        this.finishedDateLock = new Object();
        this.finalisationDateLock = new Object();
        this.cancellationDateLock = new Object();
        // Now, add what we have been supplied
        this.associatedWUPIdentifier = activityID.getPresentWUPIdentifier();
        this.episodeIdentifier = this.buildEpisodeID(activityID, theUoW);
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
        this.actualUoW = null;
        this.downstreamEpisodeIdentifierSet = null;
        this.upstreamEpisodeIdentifier = null;
        this.registrationDate = null;
        this.startDate = null;
        this.finishedDate = null;
        this.finalisationDate = null;
        this.episodeIdentifier = null;
        this.cancellationDate = null;
        this.instanceIDLock = new Object();
        this.typeIDLock = new Object();
        this.episodeIdentifierLock = new Object();
        this.actualUoWLock = new Object();
        this.associatedWUPIdentifierLock = new Object();
        this.downstreamEpisodeIdentifierSetLock = new Object();
        this.upstreamEpisodeIdentifierLock = new Object();
        this.finalisationStatusLock = new Object();
        this.processingStatusLock = new Object();
        this.registrationDateLock = new Object();
        this.startDateLock = new Object();
        this.finishedDateLock = new Object();
        this.finalisationDateLock = new Object();
        this.cancellationDateLock = new Object();
        // Now, add what we have been supplied
        if (originalParcel.hasCancellationDate()) {
            this.cancellationDate = originalParcel.getCancellationDate();
        }
        if (originalParcel.hasAssociatedWUPIdentifier()) {
            this.associatedWUPIdentifier = originalParcel.getAssociatedWUPIdentifier();
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
    }

    //
    // Bean/Attribute Methods
    //
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

    // toString()
    @Override
    public String toString() {
        String newString = new String("ResilienceParcel={");
        String parcelInstanceIDString;
        if (hasInstanceIdentifier()) {
            parcelInstanceIDString = "(instanceID:" + identifier.toString() + ")";
        } else {
            parcelInstanceIDString = "(instanceID:null)";
        }
        String parcelTypeIDString;
        if (hasTypeID()) {
            parcelTypeIDString = "(typeID:" + typeID.toString() + ")";
        } else {
            parcelTypeIDString = "(typeID:null)";
        }
        String associatedWUPInstanceIDString;
        if (hasAssociatedWUPIdentifier()) {
            associatedWUPInstanceIDString = "(associatedWUPInstanceID=" + associatedWUPIdentifier.toString() + ")";
        } else {
            associatedWUPInstanceIDString = "(associatedWUPInstanceID=null)";
        }
        String upstreamParcelIDString;
        if (hasUpstreamEpisodeIdentifier()) {
            upstreamParcelIDString = "(upstreamEpisodeID:" + upstreamEpisodeIdentifier.toString() + ")";
        } else {
            upstreamParcelIDString = "(upstreamEpisodeID:null)";
        }
        String downstreamParcelIDSetString = new String();
        if (hasDownstreamEpisodeIdentifierSet()) {
            downstreamParcelIDSetString = "(downstreamEpisodeIDSet:" + this.downstreamEpisodeIdentifierSet.toString() + ")";
        } else {
            downstreamParcelIDSetString = "(downstreamEpisodeIDSet:null)";
        }
        String actualUoWString;
        if (hasActualUoW()) {
            actualUoWString = "(actualUoW:" + actualUoW.toString() + ")";
        } else {
            actualUoWString = "(actualUoW:null)";
        }
        String parcelRegistrationDateString;
        if (hasRegistrationDate()) {
            parcelRegistrationDateString = "(registrationDate:" + registrationDate.toString() + ")";
        } else {
            parcelRegistrationDateString = "(registrationDate:null)";
        }
        String parcelStartDateString;
        if (hasStartDate()) {
            parcelStartDateString = "(startDateString:" + startDate.toString() + ")";
        } else {
            parcelStartDateString = "(startDateString:null)";
        }
        String parcelFinishedDateString;
        if (hasFinishedDate()) {
            parcelFinishedDateString = "(finishedDate:" + finishedDate.toString() + ")";
        } else {
            parcelFinishedDateString = "(finishedDate:null)";
        }
        String parcelFinalisationDateString;
        if (hasFinalisationDate()) {
            parcelFinalisationDateString = "(finalisationDate:" + finalisationDate.toString() + ")";
        } else {
            parcelFinalisationDateString = "(finalisationDate:null)";
        }
        String parcelFinalisationStatusEnumString;
        if (hasFinalisationStatus()) {
            parcelFinalisationStatusEnumString = "(finalisationStatus:" + finalisationStatus.toString() + ")";
        } else {
            parcelFinalisationStatusEnumString = "(finalisationStatus:null)";
        }
        String parcelProcessingStatusEnumString;
        if (hasProcessingStatus()) {
            parcelProcessingStatusEnumString = "(processingStatus:" + processingStatus.toString() + ")";
        } else {
            parcelProcessingStatusEnumString = "(processingStatus:null)";
        }
        String parcelEpisodeString;
        if (hasEpisodeIdentifier()) {
            parcelEpisodeString = "(episodeID:" + episodeIdentifier.toString() + ")";
        } else {
            parcelEpisodeString = "(episodeID:null)";
        }
        String cancellationDateString;
        if (hasCancellationDate()) {
            cancellationDateString = "(cancellationDate:" + episodeIdentifier.toString() + ")";
        } else {
            cancellationDateString = "(cancellationDate:null)";
        }
        newString = newString
                + parcelInstanceIDString + ","
                + parcelEpisodeString + ","
                + parcelTypeIDString + ","
                + associatedWUPInstanceIDString + ","
                + upstreamParcelIDString + ","
                + downstreamParcelIDSetString + ","
                + actualUoWString + ","
                + parcelRegistrationDateString + ","
                + parcelStartDateString + ","
                + parcelFinishedDateString + ","
                + parcelFinalisationDateString + ","
                + parcelProcessingStatusEnumString + ","
                + parcelFinalisationDateString + ","
                + cancellationDateString + "}";
        return (newString);
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

    public PetasosEpisodeIdentifier buildEpisodeID(ActivityID activityID, UoW theUoW) {
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
        FDN newEpisodeID;
        if (activityID.hasPresentWUPFunctionToken()) {
            TopologyNodeFunctionFDN nodeFunctionFDN = new TopologyNodeFunctionFDN(activityID.getPresentWUPFunctionToken());
            newEpisodeID = nodeFunctionFDN.toVersionBasedFDN();
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): ActivityID has no PresentWUPTypeID value, bad parameter"));
        }
        newEpisodeID.appendFDN(uowInstanceFDN);
        PetasosEpisodeIdentifier episodeId = new PetasosEpisodeIdentifier(newEpisodeID.getToken());
        return (episodeId);
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
            newTypeID = nodeFunctionFDN.toVersionBasedFDN();
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
            newInstanceID = new FDN(activityID.getPresentWUPIdentifier().toVersionBasedFDNToken());
        } else {
            throw (new IllegalArgumentException(".buildEpisodeID(): ActivityID has no PresentWUPInstanceID value, bad parameter"));
        }
        newInstanceID.appendFDN(uowInstanceFDN);
        ResilienceParcelIdentifier parcelIdentifier = new ResilienceParcelIdentifier(newInstanceID.getToken());
        return (parcelIdentifier);
    }
}
