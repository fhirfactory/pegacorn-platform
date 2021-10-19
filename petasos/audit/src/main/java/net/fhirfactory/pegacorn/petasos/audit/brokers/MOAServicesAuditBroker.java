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
package net.fhirfactory.pegacorn.petasos.audit.brokers;

import net.fhirfactory.pegacorn.petasos.audit.transformers.PetasosFulfillmentTask2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.audit.transformers.UoWPayload2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.model.audit.PetasosAuditWriterInterface;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MOAServicesAuditBroker {
    private static final Logger LOG = LoggerFactory.getLogger(STAServicesAuditBroker.class);

    @Inject
    private PetasosAuditWriterInterface auditWriter;

    @Inject
    PetasosFulfillmentTask2FHIRAuditEvent fulfillmentTask2FHIRAuditEvent;

    @Inject
    UoWPayload2FHIRAuditEvent uow2auditevent;

    public AuditEvent logActivity(PetasosFulfillmentTask fulfillmentTask) {
        AuditEvent entry = logActivity(fulfillmentTask, false);

        return(entry);
    }

    public AuditEvent logActivity(PetasosFulfillmentTask fulfillmentTask, boolean requiresSynchronousWrite){
        AuditEvent auditEvent = fulfillmentTask2FHIRAuditEvent.transform(fulfillmentTask);
        AuditEvent resultAuditEntry;
        if(requiresSynchronousWrite){
            resultAuditEntry = auditWriter.logAuditEventSynchronously(auditEvent);
        } else {
            resultAuditEntry = auditWriter.logAuditEventAsynchronously(auditEvent);
        }
        return(resultAuditEntry);
    }

    public AuditEvent logActivity(PetasosActionableTask fulfillmentTask, boolean requiresSynchronousWrite){
        AuditEvent auditEvent = fulfillmentTask2FHIRAuditEvent.transform(fulfillmentTask);
        AuditEvent resultAuditEntry;
        if(requiresSynchronousWrite){
            resultAuditEntry = auditWriter.logAuditEventSynchronously(auditEvent);
        } else {
            resultAuditEntry = auditWriter.logAuditEventAsynchronously(auditEvent);
        }
        return(resultAuditEntry);
    }

    public void logMLLPTransactions(ResilienceParcel parcelAuditInstance, UoW uow, boolean requiresSynchronousWrite){
        if(parcelAuditInstance.hasAssociatedPortType() && parcelAuditInstance.hasAssociatedPortValue()) {
            AuditEvent uowEntry = uow2auditevent.transform(parcelAuditInstance, uow);
            AuditEvent resultUoWEntry;
            if (requiresSynchronousWrite) {
                resultUoWEntry = auditWriter.logAuditEventSynchronously(uowEntry);
            } else {
                resultUoWEntry = auditWriter.logAuditEventAsynchronously(uowEntry);
            }
        }
    }
}
