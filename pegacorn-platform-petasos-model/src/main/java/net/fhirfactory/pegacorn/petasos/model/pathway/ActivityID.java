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

import java.time.Instant;
import java.util.Date;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.EpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;

/**
 *
 * @author Mark A. Hunter
 */
public class ActivityID {

    private ResilienceParcelIdentifier previousParcelIdenifier;
    private Object previousParcelIdentifierLock;
    private EpisodeIdentifier previousEpisodeIdentifier;
    private Object previousEpisodeIdentifierLock;
    private ResilienceParcelIdentifier presentParcelIdentifier;
    private Object presentParcelIdenifierLock;
    private EpisodeIdentifier presentEpisodeIdentifier;
    private Object presentEpisodeIdentifierLock;
    private WUPIdentifier previousWUPIdentifier;
    private Object previousWUPIdentifierLock;
    private TopologyNodeFunctionFDNToken previousWUPFunctionToken;
    private Object previousWUPFunctionTokenLock;
    private WUPIdentifier presentWUPIdentifier;
    private Object presentWUPIdentifierLock;
    private TopologyNodeFunctionFDNToken presentWUPFunctionToken;
    private Object presentWUPFunctionTokenLock;
    private Date creationDate;
    private Object creationDateLock;

    public ActivityID(ResilienceParcelIdentifier previousParcelInstanceID, ResilienceParcelIdentifier presentParcelInstanceID, WUPIdentifier previousWUPInstanceID, WUPIdentifier presentWUPInstanceID, Date creationDate) {
        // Clear the deck
        this.previousParcelIdenifier = null;
        this.previousEpisodeIdentifier = null;
        this.presentParcelIdentifier = null;
        this.presentEpisodeIdentifier = null;
        this.previousWUPIdentifier = null;
        this.previousWUPFunctionToken = null;
        this.presentWUPIdentifier = null;
        this.presentWUPFunctionToken = null;
        this.creationDate = null;
        this.creationDateLock = new Object();
        this.presentParcelIdenifierLock = new Object();
        this.presentEpisodeIdentifierLock = new Object();
        this.presentWUPFunctionTokenLock = new Object();
        this.presentWUPIdentifierLock = new Object();
        this.previousParcelIdentifierLock = new Object();
        this.previousEpisodeIdentifierLock = new Object();
        this.previousWUPFunctionTokenLock = new Object();
        this.previousWUPIdentifierLock = new Object();
        // Set Values
        this.previousParcelIdenifier = previousParcelInstanceID;
        this.presentParcelIdentifier = presentParcelInstanceID;
        this.previousWUPIdentifier = previousWUPInstanceID;
        this.presentWUPIdentifier = presentWUPInstanceID;
        this.creationDate = creationDate;
    }

    public ActivityID(ResilienceParcelIdentifier previousParcelInstanceID, ResilienceParcelIdentifier presentParcelInstanceID, WUPIdentifier previousWUPInstanceID, WUPIdentifier presentWUPInstanceID) {
        this.previousParcelIdenifier = null;
        this.previousEpisodeIdentifier = null;
        this.presentParcelIdentifier = null;
        this.presentEpisodeIdentifier = null;
        this.previousWUPIdentifier = null;
        this.previousWUPFunctionToken = null;
        this.presentWUPIdentifier = null;
        this.presentWUPFunctionToken = null;
        this.creationDate = null;
        this.creationDateLock = new Object();
        this.presentParcelIdenifierLock = new Object();
        this.presentEpisodeIdentifierLock = new Object();
        this.presentWUPFunctionTokenLock = new Object();
        this.presentWUPIdentifierLock = new Object();
        this.previousParcelIdentifierLock = new Object();
        this.previousEpisodeIdentifierLock = new Object();
        this.previousWUPFunctionTokenLock = new Object();
        this.previousWUPIdentifierLock = new Object();
        // Set Values
        this.previousParcelIdenifier = previousParcelInstanceID;
        this.presentParcelIdentifier = presentParcelInstanceID;
        this.previousWUPIdentifier = previousWUPInstanceID;
        this.presentWUPIdentifier = presentWUPInstanceID;
        this.creationDate = Date.from(Instant.now());
    }

    public ActivityID() {
        this.previousParcelIdenifier = null;
        this.previousEpisodeIdentifier = null;
        this.presentParcelIdentifier = null;
        this.presentEpisodeIdentifier = null;
        this.previousWUPIdentifier = null;
        this.previousWUPFunctionToken = null;
        this.presentWUPIdentifier = null;
        this.presentWUPFunctionToken = null;
        this.creationDate = Date.from(Instant.now());
        this.creationDateLock = new Object();
        this.presentParcelIdenifierLock = new Object();
        this.presentEpisodeIdentifierLock = new Object();
        this.presentWUPFunctionTokenLock = new Object();
        this.presentWUPIdentifierLock = new Object();
        this.previousParcelIdentifierLock = new Object();
        this.previousEpisodeIdentifierLock = new Object();
        this.previousWUPFunctionTokenLock = new Object();
        this.previousWUPIdentifierLock = new Object();
    }

    public ActivityID(ActivityID originalRecord) {
        this.previousParcelIdenifier = null;
        this.previousEpisodeIdentifier = null;
        this.presentParcelIdentifier = null;
        this.presentEpisodeIdentifier = null;
        this.previousWUPIdentifier = null;
        this.previousWUPFunctionToken = null;
        this.presentWUPIdentifier = null;
        this.presentWUPFunctionToken = null;
        this.creationDate = null;
        this.creationDateLock = new Object();
        this.presentParcelIdenifierLock = new Object();
        this.presentEpisodeIdentifierLock = new Object();
        this.presentWUPFunctionTokenLock = new Object();
        this.presentWUPIdentifierLock = new Object();
        this.previousParcelIdentifierLock = new Object();
        this.previousEpisodeIdentifierLock = new Object();
        this.previousWUPFunctionTokenLock = new Object();
        this.previousWUPIdentifierLock = new Object();
        // Set Values
        if (originalRecord.hasCreationDate()) {
            this.creationDate = originalRecord.getCreationDate();
        }
        if (originalRecord.hasPresentParcelIdentifier()) {
            this.presentParcelIdentifier = new ResilienceParcelIdentifier(originalRecord.getPresentParcelIdentifier());
        }
        if (originalRecord.hasPresentEpisodeIdentifier()) {
            this.presentEpisodeIdentifier = new EpisodeIdentifier(originalRecord.getPresentEpisodeIdentifier());
        }
        if (originalRecord.hasPresentWUPIdentifier()) {
            this.presentWUPIdentifier = new WUPIdentifier(originalRecord.getPresentWUPIdentifier());
        }
        if (originalRecord.hasPresentWUPFunctionToken()) {
            this.presentWUPFunctionToken = new TopologyNodeFunctionFDNToken(originalRecord.getPresentWUPFunctionToken());
        }
        if (originalRecord.hasPreviousParcelIdentifier()) {
            this.previousParcelIdenifier = new ResilienceParcelIdentifier(originalRecord.getPreviousParcelIdentifier());
        }
        if (originalRecord.hasPreviousEpisodeIdentifier()) {
            this.previousEpisodeIdentifier = new EpisodeIdentifier(originalRecord.getPreviousEpisodeIdentifier());
        }
        if (originalRecord.hasPreviousWUPIdentifier()) {
            this.previousWUPIdentifier = new WUPIdentifier(originalRecord.getPresentWUPIdentifier());
        }
        if (originalRecord.hasPreviousWUPFunctionToken()) {
            this.previousWUPFunctionToken = new TopologyNodeFunctionFDNToken(originalRecord.getPresentWUPFunctionToken());
        }
    }

    public boolean hasPreviousParcelIdentifier() {
        if (this.previousParcelIdenifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public ResilienceParcelIdentifier getPreviousParcelIdentifier() {
        return previousParcelIdenifier;
    }

    public void setPreviousParcelIdentifier(ResilienceParcelIdentifier previousParcelID) {
        synchronized (previousParcelIdentifierLock) {
            this.previousParcelIdenifier = previousParcelID;
        }
    }

    public boolean hasPresentParcelIdentifier() {
        if (this.presentParcelIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public ResilienceParcelIdentifier getPresentParcelIdentifier() {
        return presentParcelIdentifier;
    }

    public void setPresentParcelIdentifier(ResilienceParcelIdentifier presentParcelID) {
        synchronized (presentParcelIdenifierLock) {
            this.presentParcelIdentifier = presentParcelID;
        }
    }

    public boolean hasPreviousWUPIdentifier() {
        if (this.previousWUPIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public WUPIdentifier getPreviousWUPIdentifier() {
        return previousWUPIdentifier;
    }

    public void setPreviousWUPIdentifier(WUPIdentifier previousWUPID) {
        synchronized (previousWUPIdentifierLock) {
            this.previousWUPIdentifier = previousWUPID;
        }
    }

    public boolean hasPresentWUPIdentifier() {
        if (this.presentWUPIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public WUPIdentifier getPresentWUPIdentifier() {
        return presentWUPIdentifier;
    }

    public void setPresentWUPIdentifier(WUPIdentifier presentWUPID) {
        synchronized (presentWUPIdentifierLock) {
            this.presentWUPIdentifier = presentWUPID;
        }
    }

    public boolean hasCreationDate() {
        if (this.creationDate == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        synchronized (creationDateLock) {
            this.creationDate = creationDate;
        }
    }

    public boolean hasPreviousEpisodeIdentifier() {
        if (this.previousEpisodeIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public EpisodeIdentifier getPreviousEpisodeIdentifier() {
        return previousEpisodeIdentifier;
    }

    public void setPreviousEpisodeIdentifier(EpisodeIdentifier previousWUAEpisodeID) {
        synchronized (previousEpisodeIdentifierLock) {
            this.previousEpisodeIdentifier = previousWUAEpisodeID;
        }
    }

    public boolean hasPresentEpisodeIdentifier() {
        if (this.presentEpisodeIdentifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public EpisodeIdentifier getPresentEpisodeIdentifier() {
        return presentEpisodeIdentifier;
    }

    public void setPresentEpisodeIdentifier(EpisodeIdentifier presentWUAEpisodeID) {
        synchronized (presentEpisodeIdentifierLock) {
            this.presentEpisodeIdentifier = presentWUAEpisodeID;
        }
    }

    public boolean hasPreviousWUPFunctionToken() {
        if (this.previousWUPFunctionToken == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public TopologyNodeFunctionFDNToken getPreviousWUPFunctionToken() {
        return previousWUPFunctionToken;
    }

    public void setPreviousWUPFunctionToken(TopologyNodeFunctionFDNToken previousWUPFunctionToken) {
        synchronized (previousWUPFunctionTokenLock) {
            this.previousWUPFunctionToken = previousWUPFunctionToken;
        }
    }

    public boolean hasPresentWUPFunctionToken() {
        if (this.presentWUPFunctionToken == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public TopologyNodeFunctionFDNToken getPresentWUPFunctionToken() {
        return presentWUPFunctionToken;
    }

    public void setPresentWUPFunctionToken(TopologyNodeFunctionFDNToken presentWUPFunctionToken) {
        synchronized (presentWUPFunctionTokenLock) {
            this.presentWUPFunctionToken = presentWUPFunctionToken;
        }
    }

    @Override
    public String toString() {
        String previousResilienceParcelInstanceIDString;
        if (hasPreviousParcelIdentifier()) {
            previousResilienceParcelInstanceIDString = "(previousParcelIdenifier:" + this.previousParcelIdenifier.toString() + ")";
        } else {
            previousResilienceParcelInstanceIDString = "(previousParcelIdenifier:null)";
        }
        String presentResilienceParcelInstanceIDString;
        if (hasPresentParcelIdentifier()) {
            presentResilienceParcelInstanceIDString = "(presentParcelIdentifier:" + this.presentParcelIdentifier.toString() + ")";
        } else {
            presentResilienceParcelInstanceIDString = "(presentParcelIdentifier:null)";
        }
        String presentWUAEpisodeString;
        if (hasPresentEpisodeIdentifier()) {
            presentWUAEpisodeString = "(presentEpisodeIdentifier:" + this.presentEpisodeIdentifier.toString() + ")";
        } else {
            presentWUAEpisodeString = "(presentEpisodeIdentifier:null)";
        }
        String previousWUAEpisodeString;
        if (hasPreviousEpisodeIdentifier()) {
            previousWUAEpisodeString = "(previousEpisodeIdentifier:" + this.previousEpisodeIdentifier.toString() + ")";
        } else {
            previousWUAEpisodeString = "(previousEpisodeIdentifier:null)";
        }
        String previousWUPFunctionTokenString;
        if (hasPreviousWUPFunctionToken()) {
            previousWUPFunctionTokenString = "(previousWUPFunctionToken:" + this.previousWUPFunctionToken.toString() + ")";
        } else {
            previousWUPFunctionTokenString = "(previousWUPFunctionToken:null)";
        }
        String presentWUPFunctionTokenString;
        if (hasPresentWUPFunctionToken()) {
            presentWUPFunctionTokenString = "(presentWUPFunctionToken:" + this.presentWUPFunctionToken.toString() + ")";
        } else {
            presentWUPFunctionTokenString = "(presentWUPFunctionToken:null)";
        }
        String previousWUPInstanceIDString;
        if (hasPreviousWUPIdentifier()) {
            previousWUPInstanceIDString = "(previousWUPIdentifier:" + this.previousWUPIdentifier.toString() + ")";
        } else {
            previousWUPInstanceIDString = "(previousWUPIdentifier:null)";
        }
        String presentWUPInstanceIDString;
        if (hasPresentWUPIdentifier()) {
            presentWUPInstanceIDString = "(presentWUPIdentifier:" + this.presentWUPIdentifier.toString() + ")";
        } else {
            presentWUPInstanceIDString = "(presentWUPIdentifier:null)";
        }
        String creationDateString;
        if (hasCreationDate()) {
            creationDateString = "(creationDate:" + this.creationDate.toString() + ")";
        } else {
            creationDateString = "(creationDate:null)";
        }
        String theString = "ActivityID{"
                + previousResilienceParcelInstanceIDString + ","
                + presentResilienceParcelInstanceIDString + ","
                + previousWUAEpisodeString + ","
                + presentWUAEpisodeString + ","
                + previousWUPFunctionTokenString + ","
                + presentWUPFunctionTokenString + ","
                + previousWUPInstanceIDString + ","
                + presentWUPInstanceIDString + ","
                + creationDateString + "}";
        return (theString);
    }
    
}
