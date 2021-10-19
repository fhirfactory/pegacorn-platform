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
package net.fhirfactory.pegacorn.petasos.audit.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories.AuditEventEntityFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories.AuditEventFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.*;
import net.fhirfactory.pegacorn.petasos.audit.transformers.common.Pegacorn2FHIRAuditEventBase;
import net.fhirfactory.pegacorn.petasos.model.task.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UoWPayload2FHIRAuditEvent extends Pegacorn2FHIRAuditEventBase {
    private static final Logger LOG = LoggerFactory.getLogger(UoWPayload2FHIRAuditEvent.class);

    ObjectMapper jsonMapper = null;

    @Inject
    private AuditEventFactory auditEventFactory;

    @Inject
    private AuditEventEntityFactory auditEventEntityFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public UoWPayload2FHIRAuditEvent(){
        jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
//        jsonMapper = new ObjectMapper();
    }


    public AuditEvent transform(ResilienceParcel parcel, UoW uow){
        if(parcel == null){
            return(null);
        }

        String auditEventEntityName = extractAuditEventEntityNameFromParcel(parcel);

        List<AuditEvent.AuditEventEntityDetailComponent> detailList = new ArrayList<>();
        AuditEvent.AuditEventEntityDetailComponent ingresDetailComponent = auditEventEntityFactory.newAuditEventEntityDetailComponent("UoW.Ingress.Payload", uow.getIngresContent().getPayload());
        detailList.add(ingresDetailComponent);
        if(uow.hasEgressContent()) {
            int counter = 0;
            for (UoWPayload currentPayload : uow.getEgressContent().getPayloadElements()) {
                AuditEvent.AuditEventEntityDetailComponent currentEgressDetail = auditEventEntityFactory.newAuditEventEntityDetailComponent("UoW.Egress.Payload[" + counter + "]", currentPayload.getPayload());
                detailList.add(currentEgressDetail);
                counter += 1;
            }
        }

        AuditEvent.AuditEventEntityComponent auditEventEntityComponent = auditEventEntityFactory.newAuditEventEntity(
                AuditEventEntityTypeEnum.PEGACORN_MLLP_MSG,
                AuditEventEntityRoleEnum.HL7_JOB,
                AuditEventEntityLifecycleEnum.HL7_TRANSMIT,
                auditEventEntityName,
                "Ingres and Egress content fromm UoW (Unit of Work) Processors",
                detailList);

        String portValue = parcel.getAssociatedPortValue();
        String portType = parcel.getAssociatedPortType();
        String sourceSite;
        if(portValue != null && portType != null) {
            sourceSite = processingPlant.getIPCServiceRoutingName() + ":" + portValue;
        } else {
            sourceSite = processingPlant.getIPCServiceRoutingName();
        }
        AuditEvent auditEvent = auditEventFactory.newAuditEvent(
                null,
                processingPlant.getSimpleInstanceName(),
                processingPlant.getDeploymentPlatformName(),
                sourceSite,
                null,
                AuditEventSourceTypeEnum.HL7_APPLICATION_SERVER,
                extractAuditEventType(parcel),
                extractAuditEventSubType(parcel),
                AuditEvent.AuditEventAction.C,
                extractAuditEventOutcome(parcel),
                null,
                extractProcessingPeriod(parcel),
                auditEventEntityComponent);

        return(auditEvent);
    }



    //
    //  To Do
    //

    public AuditEvent transform(PetasosFulfillerTaskAuditEntry parcel){
        AuditEvent auditEvent = new AuditEvent();
        return(auditEvent);
    }
}
