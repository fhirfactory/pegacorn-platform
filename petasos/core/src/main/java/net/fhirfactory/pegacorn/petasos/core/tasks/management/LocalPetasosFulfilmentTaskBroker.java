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

package net.fhirfactory.pegacorn.petasos.core.tasks.management;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.processingplant.LocalPetasosFulfillmentTaskDM;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

/**
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class LocalPetasosFulfilmentTaskBroker {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosFulfilmentTaskBroker.class);
//    private FDN nodeInstanceFDN;

    @Inject
    LocalPetasosFulfillmentTaskDM fulfillmentTaskDM;

    @Inject
    MOAServicesAuditBroker auditServicesBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // PetasosFulfillmentTask Registration
    //

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask task, boolean synchronousWriteToAudit){

        PetasosTaskJobCard petasosTaskJobCard = null;
        if(task.hasTaskJobCard()) {
            petasosTaskJobCard = task.getTaskJobCard();
        } else {
            petasosTaskJobCard = new PetasosTaskJobCard();
            petasosTaskJobCard.setActionableTaskIdentifier(task.getActionableTaskId());
            petasosTaskJobCard.setClusterMode(task.getTaskFulfillment().getFulfillerComponent().getConcurrencyMode());
            petasosTaskJobCard.setFulfillmentTaskIdentifier(task.getTaskId());
            petasosTaskJobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
            petasosTaskJobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setProcessingPlant(processingPlant.getProcessingPlantNode().getComponentID());
            petasosTaskJobCard.setSystemMode(task.getTaskFulfillment().getFulfillerComponent().getResilienceMode());
            petasosTaskJobCard.setToBeDiscarded(false);
            petasosTaskJobCard.setWorkUnitProcessor(task.getTaskFulfillment().getFulfillerComponent().getComponentID());
            petasosTaskJobCard.setLocalUpdateInstant(Instant.now());
            task.setTaskJobCard(petasosTaskJobCard);
        }
        fulfillmentTaskDM.addFulfillmentTask(task);
        return(task);
    }

    public void deleteFulfillmentTask(PetasosFulfillmentTask task){
        fulfillmentTaskDM.removeFulfillmentTask(task.getTaskId());
    }

    public void deleteFulfillmentTask(TaskIdType taskId){
        fulfillmentTaskDM.removeFulfillmentTask(taskId);
    }
}
