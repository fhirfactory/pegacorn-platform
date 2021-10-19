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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupframework.worker.buildingblocks;

import net.fhirfactory.pegacorn.components.auditing.AuditEventCaptureLevelEnum;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.PetasosJobActivityStatusEnum;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;

/**
 * @author Mark A. Hunter
 * @since 2020-07-05
 */
@Dependent
public class WUPEgressConduit {
    private static final Logger LOG = LoggerFactory.getLogger(WUPEgressConduit.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private MOAServicesAuditBroker auditBroker;
    
    /**
     * This function reconstitutes the WorkUnitTransportPacket by extracting the WUPJobCard and ParcelStatusElement
     * from the Camel Exchange, and injecting them plus the UoW into it.
     *
     * @param incomingUoW   The Unit of Work (UoW) received as output from the actual Work Unit Processor (Business Logic)
     * @param camelExchange The Apache Camel Exchange object, for extracting the WUPJobCard & ParcelStatusElement from
     * @return A WorkUnitTransportPacket object for relay to the other
     */
    public PetasosFulfillmentTask receiveFromWUP(UoW incomingUoW, Exchange camelExchange) {
        getLogger().debug(".receiveFromWUP(): Entry, incomingUoW->{}", incomingUoW);
        // Retrieve the information from the CamelExchange
        PetasosFulfillmentTask fulfillmentTask = camelExchange.getProperty(PetasosPropertyConstants.WUP_FULFILLMENT_TASK_PROPERTY_NAME, PetasosFulfillmentTask.class);
        // Update the UoW Egress Content and processing status in the TaskWorkItem of the FulfillmentTask
        fulfillmentTask.getTaskWorkItem().setEgressContent(incomingUoW.getEgressContent());
        fulfillmentTask.getTaskWorkItem().setProcessingOutcome(incomingUoW.getProcessingOutcome());
        if(incomingUoW.hasFailureDescription()){
            fulfillmentTask.getTaskWorkItem().setFailureDescription(incomingUoW.getFailureDescription());
        }
        getLogger().trace(".receiveFromWUP(): We only want to check if the UoW was successful and modify the JobCard/StatusElement accordingly.");
        getLogger().trace(".receiveFromWUP(): All detailed checking of the Cluster/SiteWide details is done in the ClusterTaskCoordinator");
        switch (incomingUoW.getProcessingOutcome()) {
            case UOW_OUTCOME_SUCCESS:
                getLogger().trace(".receiveFromWUP(): UoW was processed successfully - updating JobCard/StatusElement to FINISHED!");
                fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);
                fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);
                fulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
                fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
                fulfillmentTask.getTaskFulfillment().setFinishInstant(Instant.now());
                fulfillmentTask.getTaskFulfillment().setLastCheckedInstant(Instant.now());
                break;
            case UOW_OUTCOME_NO_PROCESSING_REQUIRED:
                getLogger().trace(".receiveFromWUP(): UoW was processed with no actions required - updating JobCard/StatusElement to FINISHED!");
                fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);
                fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);
                fulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED);
                fulfillmentTask.getTaskJobCard().setToBeDiscarded(true);
                fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED);
                fulfillmentTask.getTaskFulfillment().setFinishInstant(Instant.now());
                fulfillmentTask.getTaskFulfillment().setLastCheckedInstant(Instant.now());
                break;
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_FAILED:
            default:
                getLogger().trace(".receiveFromWUP(): UoW was not processed or processing failed - updating JobCard/StatusElement to FAILED!");
                fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);
                fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);
                fulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
                fulfillmentTask.getTaskJobCard().setToBeDiscarded(true);
                fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
                fulfillmentTask.getTaskFulfillment().setFinishInstant(Instant.now());
                fulfillmentTask.getTaskFulfillment().setLastCheckedInstant(Instant.now());
                break;
        }
        //
        // Audit Trail
        if(processingPlant.getAuditingLevel().getAuditLevel() >= AuditEventCaptureLevelEnum.LEVEL_5_COMPONENT_ALL.getAuditLevel()){
            auditBroker.logActivity(fulfillmentTask, false);
        }
        //
        // We're done
        return (fulfillmentTask);
    }
}
