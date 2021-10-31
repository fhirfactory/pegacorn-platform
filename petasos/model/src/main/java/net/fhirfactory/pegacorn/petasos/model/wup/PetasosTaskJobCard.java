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
package net.fhirfactory.pegacorn.petasos.model.wup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;


public class PetasosTaskJobCard implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskJobCard.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private TaskIdType fulfillmentTaskIdentifier;
    private TaskIdType actionableTaskIdentifier;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant localUpdateInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant coordinatorUpdateInstant;
    private ComponentIdType processingPlant;
    private ComponentIdType workUnitProcessor;
    private PetasosJobActivityStatusEnum currentStatus;
    private PetasosJobActivityStatusEnum requestedStatus;
    private PetasosJobActivityStatusEnum grantedStatus;
    private FulfillmentExecutionStatusEnum localFulfillmentStatus;
    private FulfillmentExecutionStatusEnum globalFulfillmentStatus;
    private ConcurrencyModeEnum clusterMode;
    private ResilienceModeEnum systemMode;
    private boolean isToBeDiscarded;
    private String currentStateReason;

    //
    // Constructor(s)
    //

    public PetasosTaskJobCard(){
        this.fulfillmentTaskIdentifier = null;
        this.actionableTaskIdentifier = null;
        this.localUpdateInstant = null;
        this.coordinatorUpdateInstant = null;
        this.currentStatus = null;
        this.requestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.isToBeDiscarded = false;
        this.currentStateReason = null;
        this.localFulfillmentStatus = null;
        this.globalFulfillmentStatus = null;
        this.processingPlant = null;
        this.workUnitProcessor = null;
    }

    public PetasosTaskJobCard(
            TaskIdType fulfillmentTaskIdentifier,
            TaskIdType actionableTaskIdentifier,
            PetasosJobActivityStatusEnum currentStatus,
            PetasosJobActivityStatusEnum requestedStatus,
            ConcurrencyModeEnum clusterMode, 
            ResilienceModeEnum systemMode, 
            Instant localUpdateInstant) {
        //
        // Clear the deck
        this.fulfillmentTaskIdentifier = null;
        this.actionableTaskIdentifier = null;
        this.localUpdateInstant = null;
        this.coordinatorUpdateInstant = null;
        this.currentStatus = null;
        this.requestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.isToBeDiscarded = false;
        this.currentStateReason = null;
        this.localFulfillmentStatus = null;
        this.globalFulfillmentStatus = null;
        this.processingPlant = null;
        this.workUnitProcessor = null;

        //
        // Assign provided values
        if ((fulfillmentTaskIdentifier == null)) {
            throw (new IllegalArgumentException("fulfillmentTaskIdentifier is null in Constructor"));
        } else {
            setFulfillmentTaskIdentifier(fulfillmentTaskIdentifier);
        }

        if ((actionableTaskIdentifier == null)) {
            throw (new IllegalArgumentException("actionableTaskIdentifier is null Constructor"));
        } else {
            setActionableTaskIdentifier(actionableTaskIdentifier);
        }
        setLocalUpdateInstant(localUpdateInstant);
        setCurrentStatus(currentStatus);
        setClusterMode(clusterMode);
        setRequestedStatus(requestedStatus);
        setSystemMode(systemMode);
        setToBeDiscarded(false);
    }

    public PetasosTaskJobCard(PetasosTaskJobCard ori) {
        //
        // Clear the deck
        this.fulfillmentTaskIdentifier = null;
        this.actionableTaskIdentifier = null;
        this.localUpdateInstant = null;
        this.coordinatorUpdateInstant = null;
        this.currentStatus = null;
        this.requestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.isToBeDiscarded = false;
        this.currentStateReason = null;
        this.processingPlant = null;
        this.workUnitProcessor = null;
        this.localFulfillmentStatus = null;
        this.globalFulfillmentStatus = null;
        //
        // Assign provided values
        if(ori.hasActionableTaskIdentifier()){
            setActionableTaskIdentifier(SerializationUtils.clone(ori.getActionableTaskIdentifier()));
        }
        if(ori.hasFulfillmentTaskIdentifier()){
            setFulfillmentTaskIdentifier(SerializationUtils.clone(ori.getFulfillmentTaskIdentifier()));
        }
        if(ori.hasClusterMode()){
            setClusterMode(ori.getClusterMode());
        }
        if(ori.hasCurrentStatus()){
            setCurrentStatus(ori.getCurrentStatus());
        }
        if(ori.hasGrantedStatus()){
            setGrantedStatus(ori.getGrantedStatus());
        }
        if(ori.hasRequestedStatus()){
            setRequestedStatus(ori.getRequestedStatus());
        }
        if(ori.hasSystemMode()){
            setSystemMode(ori.getSystemMode());
        }
        if(ori.hasWorkUnitProcessor()){
            setWorkUnitProcessor(SerializationUtils.clone(ori.getWorkUnitProcessor()));
        }
        if(ori.hasProcessingPlant()){
            setProcessingPlant(SerializationUtils.clone(ori.getProcessingPlant()));
        }
        setToBeDiscarded(ori.isToBeDiscarded);
        if(ori.hasLocalUpdateInstant()){
            setLocalUpdateInstant(SerializationUtils.clone(ori.getLocalUpdateInstant()));
        }
        if(ori.hasCoordinatorUpdateInstant()){
            setCoordinatorUpdateInstant(SerializationUtils.clone(ori.getCoordinatorUpdateInstant()));
        }
        if(ori.hasLocalFulfillmentStatus()){
            setLocalFulfillmentStatus(ori.getLocalFulfillmentStatus());
        }
        if(ori.hasGlobalFulfillmentStatus()){
            setGlobalFulfillmentStatus(ori.getGlobalFulfillmentStatus());
        }
        if(ori.hasCurrentStateReason()){
            setCurrentStateReason(ori.getCurrentStateReason());
        }
    }

    //
    // Getters (and Setters)
    //

    @JsonIgnore
    public boolean hasFulfillmentTaskIdentifier(){
        boolean hasValue = this.fulfillmentTaskIdentifier != null;
        return(hasValue);
    }

    public TaskIdType getFulfillmentTaskIdentifier() {
        return fulfillmentTaskIdentifier;
    }

    public void setFulfillmentTaskIdentifier(TaskIdType fulfillmentTaskIdentifier) {
        this.fulfillmentTaskIdentifier = fulfillmentTaskIdentifier;
    }

    @JsonIgnore
    public boolean hasActionableTaskIdentifier(){
        boolean hasValue = this.actionableTaskIdentifier != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskIdentifier() {
        return actionableTaskIdentifier;
    }

    public void setActionableTaskIdentifier(TaskIdType actionableTaskIdentifier) {
        this.actionableTaskIdentifier = actionableTaskIdentifier;
    }

    @JsonIgnore
    public boolean hasLocalUpdateInstant(){
        boolean hasValue = this.localUpdateInstant != null;
        return(hasValue);
    }

    public Instant getLocalUpdateInstant() {
        return localUpdateInstant;
    }

    public void setLocalUpdateInstant(Instant localUpdateInstant) {
        this.localUpdateInstant = localUpdateInstant;
    }

    @JsonIgnore
    public boolean hasCoordinatorUpdateInstant(){
        boolean hasValue = this.coordinatorUpdateInstant != null;
        return(hasValue);
    }

    public Instant getCoordinatorUpdateInstant() {
        return coordinatorUpdateInstant;
    }

    public void setCoordinatorUpdateInstant(Instant coordinatorUpdateInstant) {
        this.coordinatorUpdateInstant = coordinatorUpdateInstant;
    }

    @JsonIgnore
    public boolean hasCurrentStatus(){
        boolean hasValue = this.currentStatus != null;
        return(hasValue);
    }

    public PetasosJobActivityStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(PetasosJobActivityStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    @JsonIgnore
    public boolean hasRequestedStatus(){
        boolean hasValue = this.requestedStatus != null;
        return(hasValue);
    }

    public PetasosJobActivityStatusEnum getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(PetasosJobActivityStatusEnum requestedStatus) {
        this.requestedStatus = requestedStatus;
    }

    @JsonIgnore
    public boolean hasGrantedStatus(){
        boolean hasValue = this.grantedStatus != null;
        return(hasValue);
    }

    public PetasosJobActivityStatusEnum getGrantedStatus() {
        return grantedStatus;
    }

    public void setGrantedStatus(PetasosJobActivityStatusEnum grantedStatus) {
        this.grantedStatus = grantedStatus;
    }

    @JsonIgnore
    public boolean hasClusterMode(){
        boolean hasValue = this.clusterMode != null;
        return(hasValue);
    }

    public ConcurrencyModeEnum getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(ConcurrencyModeEnum clusterMode) {
        this.clusterMode = clusterMode;
    }

    @JsonIgnore
    public boolean hasSystemMode(){
        boolean hasValue = this.systemMode != null;
        return(hasValue);
    }

    public ResilienceModeEnum getSystemMode() {
        return systemMode;
    }

    public void setSystemMode(ResilienceModeEnum systemMode) {
        this.systemMode = systemMode;
    }

    public boolean isToBeDiscarded() {
        return isToBeDiscarded;
    }

    public void setToBeDiscarded(boolean toBeDiscarded) {
        isToBeDiscarded = toBeDiscarded;
    }

    @JsonIgnore
    public boolean hasCurrentStateReason(){
        boolean hasValue = this.currentStateReason != null;
        return(hasValue);
    }

    public String getCurrentStateReason() {
        return currentStateReason;
    }

    public void setCurrentStateReason(String currentStateReason) {
        this.currentStateReason = currentStateReason;
    }

    @JsonIgnore
    public boolean  hasLocalFulfillmentStatus(){
        boolean hasValue = this.localFulfillmentStatus != null;
        return(hasValue);
    }

    public FulfillmentExecutionStatusEnum getLocalFulfillmentStatus() {
        return localFulfillmentStatus;
    }

    public void setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum localFulfillmentStatus) {
        this.localFulfillmentStatus = localFulfillmentStatus;
    }

    @JsonIgnore
    public boolean  hasGlobalFulfillmentStatus(){
        boolean hasValue = this.globalFulfillmentStatus != null;
        return(hasValue);
    }

    public FulfillmentExecutionStatusEnum getGlobalFulfillmentStatus() {
        return globalFulfillmentStatus;
    }

    public void setGlobalFulfillmentStatus(FulfillmentExecutionStatusEnum localFulfillmentStatus) {
        this.globalFulfillmentStatus = localFulfillmentStatus;
    }

    @JsonIgnore
    public boolean hasProcessingPlant(){
        boolean hasValue = this.processingPlant != null;
        return(hasValue);
    }

    public ComponentIdType getProcessingPlant() {
        return processingPlant;
    }

    public void setProcessingPlant(ComponentIdType processingPlant) {
        this.processingPlant = processingPlant;
    }

    @JsonIgnore
    public boolean hasWorkUnitProcessor(){
        boolean hasValue = this.workUnitProcessor != null;
        return(hasValue);
    }

    public ComponentIdType getWorkUnitProcessor() {
        return workUnitProcessor;
    }

    public void setWorkUnitProcessor(ComponentIdType workUnitProcessor) {
        this.workUnitProcessor = workUnitProcessor;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTaskJobCard{" +
                "fulfillmentTaskIdentifier=" + fulfillmentTaskIdentifier +
                ", actionableTaskIdentifier=" + actionableTaskIdentifier +
                ", updateInstant=" + localUpdateInstant +
                ", currentStatus=" + currentStatus +
                ", requestedStatus=" + requestedStatus +
                ", grantedStatus=" + grantedStatus +
                ", localFulfillmentStatus=" + localFulfillmentStatus +
                ", globalFulfillmentStatus=" + globalFulfillmentStatus +
                ", clusterMode=" + clusterMode +
                ", systemMode=" + systemMode +
                ", isToBeDiscarded=" + isToBeDiscarded +
                ", currentStateReason='" + currentStateReason + '\'' +
                ", workUnitProcessor=" + getWorkUnitProcessor() +
                ", processingPlant=" + getProcessingPlant() +
                '}';
    }
}
