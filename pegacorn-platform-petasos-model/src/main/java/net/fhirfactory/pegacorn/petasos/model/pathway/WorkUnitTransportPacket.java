/*
 * Copyright (c) 2020 Mark A. Hunter
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

package net.fhirfactory.pegacorn.petasos.model.pathway;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkUnitTransportPacket implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitTransportPacket.class);

    private ActivityID packetID;
    private Object packetIDLock;
    private Date senderSendDate;
    private Object senderSendDateLock;
    private boolean isARetry;
    private Object isARetryLock;
    private WUPJobCard currentJobCard;
    private Object currentJobCardLock;
    private ParcelStatusElement currentParcelStatus;
    private Object currentParcelStatusLock;
    private UoW payload;
    private Object payloadLock;
    private String generatedString;
    private Object generatedStringLock;
    
    public WorkUnitTransportPacket(ActivityID newPacketID, Date senderSendDate, UoW payload) {
        this.senderSendDate = senderSendDate;
        this.payload = payload;
        this.packetID = newPacketID;
        this.isARetry = false;
        this.currentJobCard = null;
        this.currentParcelStatus = null;
        this.generatedString = null;
        this.senderSendDateLock = new Object();
        this.payloadLock = new Object();
        this.isARetryLock = new Object();
        this.currentJobCardLock = new Object();
        this.currentParcelStatusLock = new Object();
        this.generatedStringLock = new Object();
        this.packetIDLock = new Object();
        generateString();
    }

    public WorkUnitTransportPacket(WorkUnitTransportPacket originalPacket) {
        this.senderSendDate = null;
        this.payload = null;
        this.packetID = null;
        this.isARetry = false;
        this.currentJobCard = null;
        this.currentParcelStatus = null;
        this.generatedString = null;
        this.senderSendDateLock = new Object();
        this.payloadLock = new Object();
        this.isARetryLock = new Object();
        this.currentJobCardLock = new Object();
        this.currentParcelStatusLock = new Object();
        this.generatedStringLock = new Object();
        this.packetIDLock = new Object();
        // Assign values if available
        if(originalPacket.hasPacketID()) {
            this.packetID = originalPacket.getPacketID();
        }
        if(originalPacket.hasSenderSendDate()) {
            this.senderSendDate = originalPacket.getSenderSendDate();
        }
        this.isARetry = originalPacket.getIsARetry();
        if(originalPacket.hasCurrentJobCard()) {
            this.currentJobCard = originalPacket.getCurrentJobCard();
        }
        if(originalPacket.hasCurrentParcelStatus()) {
            this.currentParcelStatus = originalPacket.getCurrentParcelStatus();
        }
        if(originalPacket.hasPayload()) {
            this.payload = originalPacket.getPayload();
        }
        generateString();
    }

    public boolean hasIsARetry(){
        return(true);
    }

    public boolean getIsARetry() {
        return (this.isARetry);
    }

    public void setRetryCount(boolean retry) {
        synchronized (isARetryLock) {
            this.isARetry = retry;
            generateString();
        }
    }
    
    // CurrentJobCard helper/bean methods

    public boolean hasCurrentJobCard(){
        if(this.currentJobCard==null){
            return(false);
        } else {
            return(true);
        }
    }

    public WUPJobCard getCurrentJobCard() {
        return currentJobCard;
    }

    public void setCurrentJobCard(WUPJobCard currentJobCard) {
        synchronized (currentJobCardLock) {
            this.currentJobCard = currentJobCard;
            generateString();
        }
    }

    // CurrentParcelStatus helper/bean methods

    public boolean hasCurrentParcelStatus(){
        if(this.currentParcelStatus==null){
            return(false);
        } else {
            return(true);
        }
    }

    public ParcelStatusElement getCurrentParcelStatus() {
        return currentParcelStatus;
    }

    public void setCurrentParcelStatus(ParcelStatusElement currentParcelStatus) {
        synchronized (currentParcelStatusLock) {
            this.currentParcelStatus = currentParcelStatus;
            generateString();
        }
    }

    // SenderSendDate helper/bean methods

    public boolean hasSenderSendDate(){
        if(this.senderSendDate==null){
            return(false);
        } else {
            return(true);
        }
    }
    public Date getSenderSendDate() {
        return senderSendDate;
    }

    public void setSenderSendDate(Date senderSendDate) {
        synchronized (senderSendDateLock) {
            this.senderSendDate = senderSendDate;
            generateString();
        }
    }

    // PacketID helper/bean methods

    public boolean hasPacketID(){
        if(this.packetID == null){
            return(false);
        } else {
            return(true);
        }
    }

    public ActivityID getPacketID(){
        return(this.packetID);
    }

    public void setPacketID(ActivityID newPacketID){
        synchronized(packetIDLock){
            this.packetID = newPacketID;
            this.generateString();
        }
    }

    // Payload helper/bean methods

    public boolean hasPayload(){
        if(this.payload==null){
            return(false);
        } else {
            return(true);
        }
    }
    
    public UoW getPayload() {
        return payload;
    }

    public void setPayload(UoW payload) {
        synchronized (payloadLock) {
            this.payload = payload;
            generateString();
        }
    }

    // toString method(s)

    private void generateString(){
        String packetIDString;
        String currentJobCardString;
        String currentParcelStatusString;
        String senderSendDateString;
        String payloadString;
        String isARetryString;
        if(hasPayload()){
            payloadString = "(payload:" + this.payload + ")";
        } else {
            payloadString = "(payload:null)";
        }
        if(hasPacketID()){
            packetIDString = "(packetID:" + this.packetID + ")";
        } else {
            packetIDString = "(packetID:null)";
        }
        if(hasCurrentParcelStatus()){
            currentParcelStatusString = "(currentParcelStatus:" + this.currentParcelStatus + ")";
        } else {
            currentParcelStatusString = "(currentParcelStatus:null)";
        }
        if(hasSenderSendDate()){
            senderSendDateString = "(senderSendDate:" + this.senderSendDate + ")";
        } else {
            senderSendDateString = "(senderSendDate:null)";
        }
        if(hasCurrentParcelStatus()){
            currentJobCardString = "(currentJobCard:" + this.currentJobCard + ")";
        } else {
            currentJobCardString = "(currentJobCard:null)";
        }
        isARetryString = "(isARetry:" + this.isARetry + ")";
        this.generatedString = "WorkUnitTransportPacket={"
                + packetIDString + ","
                + senderSendDateString + ","
                + currentJobCardString + ","
                + currentParcelStatusString + ","
                + payloadString + ","
                + isARetryString + "}";
    }

    @Override
    public String toString(){
        return(this.generatedString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkUnitTransportPacket that = (WorkUnitTransportPacket) o;
        return isARetry == that.isARetry &&
                Objects.equals(getPacketID(), that.getPacketID()) &&
                Objects.equals(getSenderSendDate(), that.getSenderSendDate()) &&
                Objects.equals(getCurrentJobCard(), that.getCurrentJobCard()) &&
                Objects.equals(getCurrentParcelStatus(), that.getCurrentParcelStatus()) &&
                Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPacketID(), getSenderSendDate(), isARetry, getCurrentJobCard(), getCurrentParcelStatus(), getPayload());
    }

    public WorkUnitTransportPacket deepClone(){
        WorkUnitTransportPacket newPacket = (WorkUnitTransportPacket) SerializationUtils.clone(this);
        newPacket.generateString();
        return(newPacket);
    }

    private WorkUnitTransportPacket(){
        this.senderSendDate = null;
        this.payload = null;
        this.packetID = null;
        this.isARetry = false;
        this.currentJobCard = null;
        this.currentParcelStatus = null;
        this.generatedString = null;
        this.senderSendDateLock = new Object();
        this.payloadLock = new Object();
        this.isARetryLock = new Object();
        this.currentJobCardLock = new Object();
        this.currentParcelStatusLock = new Object();
        this.generatedStringLock = new Object();
        this.packetIDLock = new Object();
    }
}
