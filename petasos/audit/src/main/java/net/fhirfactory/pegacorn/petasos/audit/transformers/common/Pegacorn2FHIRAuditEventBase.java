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

import net.fhirfactory.pegacorn.common.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.common.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.valuesets.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventSubTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public abstract class Pegacorn2FHIRAuditEventBase {

    protected String stripEscapeCharacters(String incomingString){
        String outgoingString0 = incomingString.replaceAll("\\\\", "");
        String outgoingString1 = outgoingString0.replaceAll("\\\"","\"");
        return(outgoingString1);
    }

    protected Period extractProcessingPeriod(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(null);
        }
        Date startDate = null;
        Date endDate = null;
        if(fulfillmentTask.getTaskFulfillment().hasStartInstant()){
            startDate = Date.from(fulfillmentTask.getTaskFulfillment().getStartInstant());
        }
        if(fulfillmentTask.getTaskFulfillment().hasFinishInstant()){
            endDate = Date.from(fulfillmentTask.getTaskFulfillment().getFinishInstant());
        }
        if(fulfillmentTask.getTaskFulfillment().hasFinalisationInstant()){
            endDate = Date.from(fulfillmentTask.getTaskFulfillment().getFinalisationInstant());
        }
        if(startDate == null){
            startDate = endDate;
        }
        Period period = new Period();
        period.setStart(startDate);
        period.setEnd(endDate);
        return(period);
    }

    protected AuditEvent.AuditEventOutcome extractAuditEventOutcome(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(AuditEvent.AuditEventOutcome._8);
        }
        switch(fulfillmentTask.getTaskFulfillment().getStatus()) {
            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_ACTIVE:
            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
                return(null);
            case FULFILLMENT_EXECUTION_STATUS_FINALISED:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                return(AuditEvent.AuditEventOutcome._0);
            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
                return(AuditEvent.AuditEventOutcome._4);
            case FULFILLMENT_EXECUTION_STATUS_FAILED:
            default:
                return(AuditEvent.AuditEventOutcome._8);
        }
    }

    protected AuditEventTypeEnum extractAuditEventType(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
        }
        ComponentIdType wupIdentifier = fulfillmentTask.getTaskFulfillment().getFulfillerComponent().getComponentId();
        if(fulfillmentTask.hasTaskFulfillment()){
            if(fulfillmentTask.getTaskFulfillment().hasFulfillerComponent()){
                if(fulfillmentTask.getTaskFulfillment().getFulfillerComponent() instanceof WorkUnitProcessorTopologyNode){
                    WorkUnitProcessorTopologyNode node = (WorkUnitProcessorTopologyNode) fulfillmentTask.getTaskFulfillment().getFulfillerComponent();
                    if(node.hasParentNode()){
                        TopologyNode parentNode = node.getParentNode();
                        if(parentNode.getComponentType().getComponentArchetype().equals(ComponentTypeTypeEnum.WORKSHOP)){
                            if(parentNode.getComponentType().getTypeName().contentEquals(DefaultWorkshopSetEnum.TRANSFORM_WORKSHOP.getWorkshop())){
                                return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSFORM);
                            } else {
                                return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSMIT);
                            }
                        }
                    }
                }
            }
        }
        return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
    }

    protected AuditEventSubTypeEnum extractAuditEventSubType(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED) ;
        }
        switch(fulfillmentTask.getTaskFulfillment().getStatus()) {
            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_ACTIVE:
            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STARTED);
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
            case FULFILLMENT_EXECUTION_STATUS_FINALISED:
            default:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED);
        }
    }

    protected String extractAuditEventEntityNameFromParcel(PetasosFulfillmentTask actionableTask){
        if(actionableTask == null){
            return(null);
        }
        UoW uow = actionableTask.getTaskWorkItem();
        if(uow == null){
            return(null);
        }
        DataParcelManifest dataParcelManifest = null;
        switch(actionableTask.getTaskFulfillment().getStatus()) {
            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
            case FULFILLMENT_EXECUTION_STATUS_ACTIVE:
            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
                if (uow.hasIngresContent()) {
                    dataParcelManifest = uow.getIngresContent().getPayloadManifest();
                }
                break;
            case FULFILLMENT_EXECUTION_STATUS_FINALISED:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
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

    protected String extractNiceNodeName(PetasosFulfillmentTask task){
        if(task == null){
            return(null);
        }
        ComponentIdType associatedWUPIdentifier = task.getTaskFulfillment().getFulfillerComponent().getComponentId();
        return(associatedWUPIdentifier.getDisplayName());
    }

}
