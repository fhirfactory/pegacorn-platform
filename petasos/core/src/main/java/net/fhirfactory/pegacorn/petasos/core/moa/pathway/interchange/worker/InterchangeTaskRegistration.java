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
package net.fhirfactory.pegacorn.petasos.core.moa.pathway.interchange.worker;

import net.fhirfactory.pegacorn.components.auditing.AuditEventCaptureLevelEnum;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.tasks.operations.processingplant.coordinator.ProcessingPlantTaskCoordinator;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class InterchangeTaskRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(InterchangeTaskDistributor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private ProcessingPlantTaskCoordinator taskCoordinator;

    @Inject
    private MOAServicesAuditBroker auditBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask, Exchange camelExchange){
        getLogger().debug(".registerActionableTask(): Entry, actionableTask->{}", actionableTask);
        //
        // Register the ActionableTask
        taskCoordinator.registerActionableTask(actionableTask);
        //
        // Audit Trail
        if(processingPlant.getAuditingLevel().getAuditLevel() >= AuditEventCaptureLevelEnum.LEVEL_5_COMPONENT_ALL.getAuditLevel()){
            auditBroker.logActivity(actionableTask, false);
        }
        //
        // We're done
        getLogger().debug(".registerActionableTask(): Exit");
        return(actionableTask);
    }
}
