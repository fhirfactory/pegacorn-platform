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
package net.fhirfactory.pegacorn.petasos.model.uow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark A. Hunter
 */
public class UoW implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(UoW.class);
    protected Logger getLogger(){
        return(LOG);
    }

    public static final String HASH_ATTRIBUTE = "InstanceQualifier";
    /**
     * The FDN (fully distinguished name) of this UoW Type.
     */
    private FDNToken typeID;
    /**
     * The FDN (fully distinguished name) of this UoW instance.
     */
    private UoWIdentifier instanceID;
    /**
     * The set of (JSON) objects that represent the ingress (or starting set) of
     * information of this UoW.
     */
    private UoWPayload ingresContent;
    /**
     * The set of (JSON) objects created as part of the completion of this UoW.
     */
    private UoWPayloadSet egressContent;
    /**
     * The (enum) outcome status of the processing of this UoW.
     */
    private UoWProcessingOutcomeEnum processingOutcome;

    /**
     * The failure/error condition explanation
     */
    private String failureDescription;

    //
    // Constructors
    //

    public UoW(){
        this.ingresContent = null;
        this.egressContent = new UoWPayloadSet();
        this.processingOutcome = null;
        this.typeID = null;
        this.failureDescription = null;
        this.instanceID = null;
    }

    public UoW(UoWPayload inputPayload) {
        getLogger().debug(".UoW(): Constructor: inputPayload -->{}", inputPayload);
        getLogger().trace(".UoW(): Clone the ingressContent and assign it");
        this.ingresContent = SerializationUtils.clone(inputPayload);
        getLogger().trace(".UoW(): ingressContent cloned and assigned");
        getLogger().trace(".UoW(): create an empty UoWPayloadSet and assign it to the egressContent");
        this.egressContent = new UoWPayloadSet();
        getLogger().trace(".UoW(): egressContent created and assigned");
        this.processingOutcome = UoWProcessingOutcomeEnum.UOW_OUTCOME_NOTSTARTED;
        getLogger().trace(".UoW(): Creating the typeID, first extract inputPayload manifest");
        FDN contentFDN = inputPayload.getPayloadManifest().getContentDescriptor().toFDN();
        getLogger().trace(".UoW(): Creating the typeID, now convert to an FDNToken");
        FDNToken contentFDNToken = contentFDN.getToken();
        getLogger().trace(".UoW(): Creating the typeID, now clone it and assign it to the typeID of this UoW");
        this.typeID = SerializationUtils.clone(contentFDNToken);
        getLogger().trace(".UoW(): typeID --> {}", this.typeID);
        this.failureDescription = null;
        getLogger().trace(".UoW(): Now generating instanceID");
        generateInstanceID();
        getLogger().trace(".UoW(): instanceID generated");
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(".UoW(FDN, UoWPayloadSet): this.typeID --> {}, this.instanceID --> {}", this.typeID, this.instanceID);
        }
    }

    public UoW(UoW originalUoW) {
        this.failureDescription = null;
        this.instanceID = SerializationUtils.clone(originalUoW.getInstanceID());
        this.ingresContent = SerializationUtils.clone(originalUoW.getIngresContent());
        this.egressContent = new UoWPayloadSet();
        for(UoWPayload currentPayload: originalUoW.egressContent.getPayloadElements()) {
            this.egressContent.getPayloadElements().add(SerializationUtils.clone(currentPayload));
        }
        this.processingOutcome = originalUoW.getProcessingOutcome();
        this.typeID = SerializationUtils.clone(originalUoW.getTypeID());
    }

    private void generateInstanceID() {
        getLogger().debug(".generateInstanceID(): Entry");
        getLogger().trace(".generateInstanceID(): generating an instance id based on Timestamp");
        String generatedInstanceValue = Long.toString(Instant.now().getNano());
        FDN instanceFDN = new FDN(this.typeID);
        RDN newRDN = new RDN(HASH_ATTRIBUTE, generatedInstanceValue);
        instanceFDN.appendRDN(newRDN);
        this.instanceID = new UoWIdentifier(instanceFDN.getToken());
        getLogger().debug(".generateInstanceID(): Exit");
    }

    // instanceID Helper/Bean methods
    public boolean hasInstanceID() {
        if (this.instanceID == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * @return FDN - the UoW Instance FDN (which will be unique for each UoW
     * within the system).
     */
    public UoWIdentifier getInstanceID() {
        return instanceID;
    }
    
    public void setInstanceID(UoWIdentifier uowID) {
    	this.instanceID = uowID;
    }

    // typeID Helper/Bean methods
    public boolean hasTypeID() {
        if (this.typeID == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * @return FDN - the UoW Type FDN (which describes the type or context of
     * UoW).
     */
    public FDNToken getTypeID() {
        return this.typeID;
    }

    public void setUoWTypeID(FDNToken uowTypeID) {
        this.typeID = uowTypeID;
        generateInstanceID();
    }

    // ingresContent Helper/Bean methods
    public boolean hasIngresContent() {
        if (this.ingresContent == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     *
     * @return UoWPayload- the Ingres Payload (captured as a set of JSON
     * Strings).
     */
    public UoWPayload getIngresContent() {
        return ingresContent;
    }

    public void setIngresContent(UoWPayload ingresContent) {
        this.ingresContent = ingresContent;
        generateInstanceID();
    }

    // egressContent Bean/Helper methods
    public boolean hasEgressContent() {
        if (this.egressContent == null) {
            return (false);
        }
        if (this.egressContent.getPayloadElements().isEmpty()) {
            return (false);
        }
        return (true);
    }

    /**
     *
     * @return UoWPayloadSet - the Egress Payload Set (captured as a set of JSON
     * Strings).
     */
    public UoWPayloadSet getEgressContent() {
        return egressContent;
    }

    public void setEgressContent(UoWPayloadSet egressContent) {
        this.egressContent.getPayloadElements().clear();
        this.egressContent.getPayloadElements().addAll(egressContent.getPayloadElements());
    }

    // processingOutcome Bean/Helper methods
    public boolean hasProcessingOutcome() {
        if (this.processingOutcome == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     *
     * @return UoWProcessingOutcomeEnum - the status of the processing performed
     * on (or applied to) this UoW.
     */
    public UoWProcessingOutcomeEnum getProcessingOutcome() {
        return processingOutcome;
    }

    public void setProcessingOutcome(UoWProcessingOutcomeEnum processingOutcome) {
        this.processingOutcome = processingOutcome;
    }

    @Override
    public String toString() {
        getLogger().debug("toString(): Entry");

        String uowToString = "UoW={";
        if (hasInstanceID()) {
            uowToString = uowToString + "(instanceID:" + instanceID.toString() + "),";
        } else {
            uowToString = uowToString + "(instanceID:null),";
        }
        if (hasTypeID()) {
            uowToString = uowToString + "(typeID:" + typeID.toString() + "),";
        } else {
            uowToString = uowToString + "(typeID:null),";
        }
        if (hasIngresContent()) {
            if (ingresContent.getPayloadManifest() != null) {
                uowToString = uowToString + "(payloadTopic:" + getPayloadTopicID().toString() + "),";
            } else {
                uowToString = uowToString + "(payloadTopic:null),";
            }
            uowToString = uowToString + "(ingresContent:" + ingresContent.toString() + "),";
        } else {
            uowToString = uowToString + "(ingresContent:null),";
        }
        if (hasEgressContent()) {
            uowToString = uowToString + "(egressContent:" + egressContent.toString() + "),";
        } else {
            uowToString = uowToString + "(egressContent:null),";
        }
        if (hasProcessingOutcome()) {
            uowToString = uowToString + "(processingOutcome:" + processingOutcome.toString() + ")}";
        } else {
            uowToString = uowToString + "(processingOutcome:null)}";
        }
        return (uowToString);
    }

    public String toJSONString() {
        ObjectMapper jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
        try {
            String jsonString = jsonMapper.writeValueAsString(this);
            return (jsonString);
        } catch (JsonProcessingException jsonException) {
            // TODO fix the exception handling for JSON String generation
            return (null);
        }
    }

    // payloadCreator Bean/Helper methods
    public boolean hasPayloadTopicID() {
        if (this.ingresContent == null) {
            return (false);
        } else {
            if (ingresContent.getPayloadManifest() == null) {
                return (false);
            } else {
                return (true);
            }
        }
    }

    @JsonIgnore
    public DataParcelManifest getPayloadTopicID() {
        if (hasPayloadTopicID()) {
            return (this.getIngresContent().getPayloadManifest());
        } else {
            return (null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UoW uoW = (UoW) o;
        return Objects.equals(getTypeID(), uoW.getTypeID()) &&
                Objects.equals(getInstanceID(), uoW.getInstanceID()) &&
                Objects.equals(getIngresContent(), uoW.getIngresContent()) &&
                Objects.equals(getEgressContent(), uoW.getEgressContent()) &&
                Objects.equals(getFailureDescription(), uoW.getFailureDescription()) &&
                getProcessingOutcome() == uoW.getProcessingOutcome();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeID(), getInstanceID(), getIngresContent(), getEgressContent(), getFailureDescription(), getProcessingOutcome());
    }

    public boolean hasFailureDescription(){
        if(this.failureDescription==null){
            return(false);
        } else {
            return(true);
        }
    }

    public String getFailureDescription() {
        return failureDescription;
    }

    public void setFailureDescription(String failureDescription) {
        this.failureDescription = failureDescription;
    }

    public void setTypeID(FDNToken typeID) {
        this.typeID = typeID;
    }
}
