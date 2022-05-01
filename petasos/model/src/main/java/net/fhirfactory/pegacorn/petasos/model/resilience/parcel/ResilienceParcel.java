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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.topology.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.datatypes.WUPIdentifier;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Mark A. Hunter
 * @author Scott Yeadon
 */
@Deprecated
public class ResilienceParcel extends PetasosFulfillmentTask {

    private static final Logger LOG = LoggerFactory.getLogger(ResilienceParcel.class);

    private final static String INSTANCE_QUALIFIER_TYPE = "ParcelInstance";
    private final static String TYPE_QUALIFIER_TYPE = "ParcelType";
    private ResilienceParcelProcessingStatusEnum processingStatus;
    private SerializableObject processingStatusLock;
    private String associatedPortValue;
    private String associatedPortType;

    //
    // Constructors
    //
    public ResilienceParcel(ActivityID activityID, UoW theUoW) {
        super();
        // Clean the slate
        this.associatedPortType = null;
        this.associatedPortValue = null;
        this.processingStatusLock = new SerializableObject();
        // Now, add what we have been supplied
        setAssociatedWUPIdentifier(activityID.getPresentWUPIdentifier());
        setEpisodeIdentifier(this.buildEpisodeID(activityID, theUoW));
    }

    public ResilienceParcel(ResilienceParcel originalParcel) {
        super();
        // Clean the slate
        this.associatedPortType = null;
        this.associatedPortValue = null;
        this.processingStatusLock = new SerializableObject();
        // Now, add what we have been supplied
        if (originalParcel.hasCancellationDate()) {
            setCancellationDate(SerializationUtils.clone(getCancellationDate()));
        }
        if (originalParcel.hasAssociatedWUPIdentifier()) {
            setAssociatedWUPIdentifier(SerializationUtils.clone(originalParcel.getAssociatedWUPIdentifier()));
        }
        if (originalParcel.hasActualUoW()) {
            setActualUoW(SerializationUtils.clone(originalParcel.getActualUoW()));
        }
        if (originalParcel.hasFinalisationDate()) {
            setFinalisationDate(SerializationUtils.clone(originalParcel.getFinalisationDate()));
        }
        if (originalParcel.hasFinishedDate()) {
            setFinishedDate(SerializationUtils.clone(originalParcel.getFinishedDate()));
        }
        if (originalParcel.hasIdentifier()) {
           setIdentifier(originalParcel.getIdentifier());
        }
        if (originalParcel.hasRegistrationDate()) {
            setRegistrationDate(originalParcel.getRegistrationDate());
        }
        if (originalParcel.hasStartDate()) {
            setStartDate(originalParcel.getStartDate());
        }
        if (originalParcel.hasTypeID()) {
            setTypeID(originalParcel.getTypeID());
        }
        if (originalParcel.hasUpstreamEpisodeIdentifier()) {
            setUpstreamEpisodeIdentifier(originalParcel.getUpstreamEpisodeIdentifier());
        }
        if (originalParcel.hasEpisodeIdentifier()) {
            setEpisodeIdentifier(originalParcel.getEpisodeIdentifier());
        }
        if(originalParcel.hasAssociatedPortValue()){
            setAssociatedPortValue(originalParcel.getAssociatedPortValue());
        }
        if(originalParcel.hasAssociatedPortType()){
            setAssociatedPortType(originalParcel.getAssociatedPortType());
        }
    }

    //
    // Bean/Attribute Methods
    //

    //
    // Is An Interact Task
    @JsonIgnore @Deprecated
    public boolean isAnInteractWUP() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasFulfillerComponent()){
                TopologyNodeFDN nodeFDN = getTaskFulfillment().getFulfillerComponent().getComponentFDN();
                TopologyNodeRDN topologyNodeRDN = nodeFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WORKSHOP);
                if(topologyNodeRDN != null){
                    if(topologyNodeRDN.getNodeName().equalsIgnoreCase(DefaultWorkshopSetEnum.INTERACT_WORKSHOP.getWorkshop())){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public boolean hasAssociatedPortValue(){
        boolean hasValue = this.associatedPortValue != null;
        return(hasValue);
    }

    @JsonIgnore @Deprecated
    public String getAssociatedPortValue() {
        return associatedPortValue;
    }

    @JsonIgnore @Deprecated
    public void setAssociatedPortValue(String associatedPortValue) {
        this.associatedPortValue = associatedPortValue;
    }

    @JsonIgnore @Deprecated
    public boolean hasAssociatedPortType(){
        boolean hasValue = this.associatedPortType != null;
        return(hasValue);
    }

    @JsonIgnore @Deprecated
    public String getAssociatedPortType() {
        return associatedPortType;
    }

    @JsonIgnore @Deprecated
    public void setAssociatedPortType(String associatedPortType) {
        this.associatedPortType = associatedPortType;
    }

    //
    // UoW / WorkItem Methods

    @JsonIgnore @Deprecated
    public boolean hasActualUoW() {
        return(hasTaskWorkItem());
    }

    @JsonIgnore @Deprecated
    public UoW getActualUoW() {
        return (getTaskWorkItem());
    }

    @JsonIgnore @Deprecated
    public void setActualUoW(UoW actualUoW) {
        TaskWorkItemType workItem = new TaskWorkItemType(actualUoW);
        synchronized (getTaskWorkItemLock()) {
            setTaskWorkItem(workItem);
        }
    }

    //
    // Current Episode Identifier


    @JsonIgnore @Deprecated
    public boolean hasEpisodeIdentifier() {
        boolean hasValue = hasActionableTaskId();
        return(hasValue);
    }

    @JsonIgnore @Deprecated
    public PetasosEpisodeIdentifier getEpisodeIdentifier() {
        if(hasActionableTaskId()) {
            TaskIdType actionableTaskId = getActionableTaskId();
            PetasosEpisodeIdentifier episodeIdentifier = new PetasosEpisodeIdentifier(actionableTaskId);
            return (episodeIdentifier);
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setEpisodeIdentifier(PetasosEpisodeIdentifier episodeIdentifier) {
        synchronized (getActionableTaskIdLock()) {
            setActionableTaskId(episodeIdentifier);
        }
    }

    //
    // Upstream Episode Methods

    @JsonIgnore @Deprecated
    public boolean hasUpstreamEpisodeIdentifier() {
        if(hasTaskTraceability()){
            if(getTaskTraceability().hasUpstreamActionableTaskId()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public PetasosEpisodeIdentifier getUpstreamEpisodeIdentifier() {
        if(hasUpstreamEpisodeIdentifier()){
            TaskIdType upstreamActionableTaskId = getTaskTraceability().getUpstreamActionableTaskId();
            PetasosEpisodeIdentifier episodeId = new PetasosEpisodeIdentifier(upstreamActionableTaskId);
            return(episodeId);
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setUpstreamEpisodeIdentifier(PetasosEpisodeIdentifier upstreamEpisodeIdentifier) {
        synchronized (getTaskTraceabilityLock()) {
            if(getTaskTraceability().getTaskJourney().isEmpty()){
                TaskTraceabilityElementType traceabilityElement = new TaskTraceabilityElementType();
                traceabilityElement.setActionableTaskId(upstreamEpisodeIdentifier);
                getTaskTraceability().addToTaskJourney(traceabilityElement);
            } else {
                int historySize = getTaskTraceability().getTaskJourney().size();
                getTaskTraceability().getTaskJourney().get(historySize-1).getActionableTaskId().setId(upstreamEpisodeIdentifier.getId());
            }
        }
    }

    //
    // Identifier Methods

    @JsonIgnore @Deprecated
    public boolean hasIdentifier() {
        if(hasTaskId()) {
            boolean hasValue = getTaskId().hasId();
            return (hasValue);
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public ResilienceParcelIdentifier getIdentifier() {
        if(hasTaskId()){
            ResilienceParcelIdentifier resilienceParcelIdentifier = new ResilienceParcelIdentifier(getTaskId());
            return(resilienceParcelIdentifier);
        }
        return(null);
    }


    @JsonIgnore @Deprecated
    public void setIdentifier(ResilienceParcelIdentifier parcelInstance) {
        synchronized (getTaskIdLock()) {
            setTaskId(parcelInstance);
        }
    }

    //
    // Task / Parcel Type Id Methods

    @JsonIgnore @Deprecated
    public boolean hasTypeID() {
        if(hasTaskType()){
            if(getTaskType().hasTaskSubType()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public FDNToken getTypeID() {
        if (hasTypeID()) {
            FDNToken token = new FDNToken(getTaskType().getTaskSubType());
            return(token);
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setTypeID(FDNToken typeID) {
        synchronized (getTaskTypeLock()) {
            if(!hasTaskType()){
                TaskTypeType taskType = new TaskTypeType();
                taskType.setTaskType(TaskTypeTypeEnum.FULFILLMENT_TASK_TYPE);
                taskType.setTaskSubType(typeID.getContent());
            }
        }
    }

    //
    // Date / Time Getters and Setters

    @JsonIgnore @Deprecated
    public boolean hasCancellationDate(){
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasCancellationDate()){
                return(true);
            }
        }
        return(false);
    }


    @JsonIgnore @Deprecated
    public Date getCancellationDate() {
        if(hasTaskFulfillment()) {
            if(getTaskFulfillment().hasCancellationDate()){
                return(getTaskFulfillment().getCancellationDate());
            }
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setCancellationDate(Date newCancellationDate) {
        synchronized (getTaskFulfillmentLock()) {
            getTaskFulfillment().setCancellationDate(newCancellationDate);
        }
    }

    @JsonIgnore @Deprecated
    public boolean hasRegistrationDate() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasRegistrationDate()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public Date getRegistrationDate() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasRegistrationDate()){
                return(getTaskFulfillment().getRegistrationDate());
            }
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setRegistrationDate(Date registrationDate) {
        synchronized (getTaskFulfillmentLock()) {
            getTaskFulfillment().setRegistrationDate(registrationDate);
        }
    }

    @JsonIgnore @Deprecated
    public boolean hasStartDate() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasStartDate()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public Date getStartDate() {
        if(hasTaskOutcomeStatus()){
            return(getTaskFulfillment().getStartDate());
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setStartDate(Date startDate) {
        synchronized (getTaskFulfillmentLock()) {
            getTaskFulfillment().setStartDate(startDate);
        }
    }

    @JsonIgnore @Deprecated
    public boolean hasFinishedDate() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasFinishedDate()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public Date getFinishedDate() {
        if(hasFinishedDate()){
            return(getTaskFulfillment().getFinishedDate());
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setFinishedDate(Date finishedDate) {
        synchronized (getTaskFulfillmentLock()) {
            getTaskFulfillment().setFinishedDate(finishedDate);
        }
    }

    @JsonIgnore @Deprecated
    public boolean hasFinalisationDate() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasFinalisationDate()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public Date getFinalisationDate() {
        if(hasFinalisationDate()){
            return(getTaskFulfillment().getFinalisationDate());
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setFinalisationDate(Date finalisationDate) {
        synchronized (getTaskFulfillmentLock()) {
            getTaskFulfillment().setFinalisationDate(finalisationDate);
        }
    }

    //
    // Associated WUP Identifier

    @JsonIgnore @Deprecated
    public boolean hasAssociatedWUPIdentifier() {
        if(hasTaskFulfillment()){
            if(getTaskFulfillment().hasFulfillerComponent()){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore @Deprecated
    public WUPIdentifier getAssociatedWUPIdentifier() {
        if(hasAssociatedWUPIdentifier()){
            TopologyNodeFDNToken nodeToken = getTaskFulfillment().getFulfillerComponent().getComponentFDN().getToken();
            WUPIdentifier wupID = new WUPIdentifier(nodeToken);
            return(wupID);
        }
        return(null);
    }

    @JsonIgnore @Deprecated
    public void setAssociatedWUPIdentifier(WUPIdentifier associatedWUPIdentifier) {
        throw(new UnsupportedOperationException("Please implement utilise getTaskFulfillment().setFulfillmentComponent() method"));
    }


    @JsonIgnore @Deprecated
    public boolean hasProcessingStatus() {
        boolean hasValue = this.processingStatus != null;
        return(hasValue);
    }

    @JsonIgnore @Deprecated
    public ResilienceParcelProcessingStatusEnum getProcessingStatus() {
        return processingStatus;
    }

    @JsonIgnore @Deprecated
    public void setProcessingStatus(ResilienceParcelProcessingStatusEnum processingStatus) {
        synchronized (processingStatusLock) {
            this.processingStatus = processingStatus;
        }
    }




    //
    // Helper Methods
    //

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
            newEpisodeID = nodeFunctionFDN.toTypeBasedFDNWithVersion();
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

}
