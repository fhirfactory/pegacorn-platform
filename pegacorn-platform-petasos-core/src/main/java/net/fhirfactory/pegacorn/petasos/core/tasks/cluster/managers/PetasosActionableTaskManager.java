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
package net.fhirfactory.pegacorn.petasos.core.tasks.cluster.managers;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories.AuditEventFactory;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.PetasosActionableTaskIdentifierFactory;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosOversightTask;
import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosActionableTaskManager {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosActionableTaskManager.class);

    @Inject
    private AuditEventFactory auditEventFactory;

    @Inject
    private MOAServicesAuditBroker auditServicesBroker;

    @Inject
    private PetasosActionableTaskIdentifierFactory episodeIdentifierFactory;

    //
    // Constructor(s)
    //


    //
    // ActionableTask Activities
    //

    public PetasosActionableTask requestCreationOfPetasosActionableTask(PetasosActionableTask task){
        getLogger().debug(".requestCreationOfPetasosActionableTask(): Entry, task->{}", task);
        return(task);
    }

    public PetasosActionableTask requestRetirementOfPetasosActionableTask(PetasosActionableTask task){

        return(task);
    }

    public PetasosActionableTask requestRetirementOfPetasosActionableTask(TaskIdType taskIdentifier){

        PetasosActionableTask task = new PetasosActionableTask();
        return(task);
    }

    public PetasosActionableTask notifyFulfillmentRegistration(TaskIdType taskIdentifier, PetasosFulfillmentTask fulfillmentTask){

        PetasosActionableTask task = new PetasosActionableTask();
        return(task);
    }

    public PetasosActionableTask notifyFulfillmentStart(TaskIdType taskIdentifier, PetasosFulfillmentTask fulfillmentTask){

        PetasosActionableTask task = new PetasosActionableTask();
        return(task);
    }

    public PetasosActionableTask notifyFulfillmentFinish(TaskIdType taskIdentifier, PetasosFulfillmentTask fulfillmentTask){

        PetasosActionableTask task = new PetasosActionableTask();
        return(task);
    }

    public PetasosActionableTask notifyFulfillmentFinalisation(TaskIdType taskIdentifier, PetasosOversightTask oversightTask){

        PetasosActionableTask task = new PetasosActionableTask();
        return(task);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected AuditEventFactory getAuditEventFactory() {
        return auditEventFactory;
    }

    protected MOAServicesAuditBroker getAuditServicesBroker() {
        return auditServicesBroker;
    }

    protected PetasosActionableTaskIdentifierFactory getEpisodeIdentifierFactory() {
        return episodeIdentifierFactory;
    }
}
