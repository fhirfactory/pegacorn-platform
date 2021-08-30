/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.audit.transformers.common;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventSubTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public abstract class Pegacorn2FHIRAuditEventBase {

    protected String stripEscapeCharacters(String incomingString){
        String outgoingString0 = incomingString.replaceAll("\\\\", "");
        String outgoingString1 = outgoingString0.replaceAll("\\\"","\"");
        return(outgoingString1);
    }

    protected Period extractProcessingPeriod(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        Date startDate = null;
        Date endDate = null;
        if(parcel.hasStartDate()){
            startDate = parcel.getStartDate();
        }
        if(parcel.hasFinishedDate()){
            endDate = parcel.getFinishedDate();
        }
        if(parcel.hasFinalisationDate()){
            endDate = parcel.getFinalisationDate();
        }
        if(parcel.hasCancellationDate()){
            if(endDate == null){
                endDate = parcel.getCancellationDate();
            }
        }
        if(startDate == null){
            startDate = endDate;
        }
        Period period = new Period();
        period.setStart(startDate);
        period.setEnd(endDate);
        return(period);
    }

    protected AuditEvent.AuditEventOutcome extractAuditEventOutcome(ResilienceParcel parcel){
        if(parcel == null){
            return(AuditEvent.AuditEventOutcome._8);
        }
        switch(parcel.getProcessingStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:
                return(null);
            case PARCEL_STATUS_FINISHED:
            case PARCEL_STATUS_FINALISED:
                return(AuditEvent.AuditEventOutcome._0);
            case PARCEL_STATUS_CANCELLED:
                return(AuditEvent.AuditEventOutcome._4);
            case PARCEL_STATUS_FAILED:
            default:
                return(AuditEvent.AuditEventOutcome._8);
        }
    }

    protected AuditEventTypeEnum extractAuditEventType(ResilienceParcel parcel){
        if(parcel == null){
            return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
        }
        WUPIdentifier wupIdentifier = parcel.getAssociatedWUPIdentifier();
        TopologyNodeFDN nodeFDN = new TopologyNodeFDN(wupIdentifier);
        TopologyNodeRDN topologyNodeRDN = nodeFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP);
        if(topologyNodeRDN == null){
            return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
        }
        if(topologyNodeRDN.getNodeName().contentEquals(DefaultWorkshopSetEnum.TRANSFORM_WORKSHOP.getWorkshop())){
            return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSFORM);
        } else {
            return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSMIT);
        }
    }

    protected AuditEventSubTypeEnum extractAuditEventSubType(ResilienceParcel parcel){
        if(parcel == null){
            return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED) ;
        }
        switch(parcel.getProcessingStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STARTED);
            case PARCEL_STATUS_FINISHED:
            case PARCEL_STATUS_FINALISED:
            default:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED);
        }
    }

    protected String extractAuditEventEntityNameFromParcel(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        UoW uow = parcel.getActualUoW();
        if(uow == null){
            return(null);
        }
        DataParcelManifest dataParcelManifest = null;
        switch(parcel.getProcessingStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:
                if (uow.hasIngresContent()) {
                    dataParcelManifest = uow.getIngresContent().getPayloadManifest();
                }
                break;
            case PARCEL_STATUS_FINISHED:
            case PARCEL_STATUS_FINALISED:
                if (uow.hasEgressContent()) {
                    if (!uow.getEgressContent().getPayloadElements().isEmpty()) {
                        dataParcelManifest = uow.getEgressContent().getPayloadElements().iterator().next().getPayloadManifest();
                    }
                }
                break;
            default:
        }
        if(dataParcelManifest == null){
            return(null);
        }
        String bestValue = null;
        if(dataParcelManifest.hasContentDescriptor()){
            bestValue = extractBestDescriptorValue(dataParcelManifest.getContentDescriptor());
        }
        if(bestValue == null){
            if(dataParcelManifest.hasContainerDescriptor()){
                bestValue = extractBestDescriptorValue(dataParcelManifest.getContainerDescriptor());
            }
        }
        return(bestValue);
    }

    protected String extractBestDescriptorValue(DataParcelTypeDescriptor descriptor){
        if(descriptor == null){
            return(null);
        }
        String value = new String();
        if(descriptor.hasDataParcelResource()){
            value = descriptor.getDataParcelResource();
        }
        if(descriptor.hasDataParcelSubCategory()){
            value = descriptor.getDataParcelSubCategory() + "." + value;
        }
        if(descriptor.hasDataParcelCategory()){
            value = descriptor.getDataParcelCategory() + "." + value;
        }
        if(descriptor.hasDataParcelDefiner()){
            value = descriptor.getDataParcelDefiner() + "." + value;
        }
        if(descriptor.hasVersion()){
            value = value + "(" + descriptor.getVersion() + ")";
        }
        return(value);
    }

    protected String extractNiceNodeName(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        WUPIdentifier associatedWUPIdentifier = parcel.getAssociatedWUPIdentifier();
        TopologyNodeFDN wupFDN = new TopologyNodeFDN(associatedWUPIdentifier);
        TopologyNodeRDN processingPlantRDN = wupFDN.extractRDNForNodeType(TopologyNodeTypeEnum.PROCESSING_PLANT);
        TopologyNodeRDN workshopRDN = wupFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP);
        TopologyNodeRDN wupRDN = wupFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WUP);

        String name = new String();
        if(processingPlantRDN != null){
            name = processingPlantRDN.getNodeName() + ".";
        }
        if(workshopRDN != null){
            name = name + workshopRDN.getNodeName() + ".";
        }
        if(wupRDN != null){
            name = name + wupRDN.getNodeName();
        }
        return(name);
    }

}
