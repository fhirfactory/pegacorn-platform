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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.interchange.worker;

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.PetasosActionableTaskIdentifierFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.cluster.managers.PetasosActionableTaskManager;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.reason.valuesets.TaskReasonTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.task.segments.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.petasos.model.task.segments.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayloadSet;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Dependent
public class InterchangeTaskPayload2NewTaskProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(InterchangeTaskPayload2NewTaskProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    TopologyIM topologyProxy;

    @Inject
    private PetasosActionableTaskManager actionableTaskManager;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

    @Inject
    private PetasosActionableTaskIdentifierFactory actionableTaskIdentifierFactory;
    
    /**
     * This method performs tree key tasks:
     * 
     * 1. It extracts each UoWPayload from the egressPayloadSet within the incomingUoW and creates a 
     * new UoW (and, subsequently, a new WorkUnitTransportPacket) based on the content of those egress 
     * UoWPayload elements. 
     * 2. As part of the WorkUnitTransportPacket creation, it embeds the current ActivityID.
     * 3. It then returns a List<> of these new WorkUnitTransportPackets for distribution.
     * 
     * It generates the 
     * @param actionableTask
     * @param camelExchange
     * @return A List<> of WorkUnitTransportPackets - one for each egress UoWPayload element within the incoming UoW.
     */

    public List<PetasosActionableTask> extractUoWPayloadAndCreateNewActionableTaskSet(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".extractUoWPayloadAndCreateNewActionableTaskSet(): Entry, actionableTask->{}", actionableTask);
        // Get my Petasos Context
        getLogger().trace(".extractUoWPayloadAndCreateNewActionableTaskSet(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        metricsAgent.touchEventDistributionStartInstant(node.getComponentID());
        TaskWorkItemType incomingWork = actionableTask.getTaskWorkItem();
        UoWPayloadSet egressContent = incomingWork.getEgressContent();
        Set<UoWPayload> egressPayloadList = egressContent.getPayloadElements();
        if (getLogger().isDebugEnabled()) {
            int counter = 0;
            for(UoWPayload currentPayload: egressPayloadList){
                getLogger().debug(".extractUoWPayloadAndCreateNewActionableTaskSet(): payload (UoWPayload).PayloadTopic --> [{}] {}", counter, currentPayload.getPayloadManifest());
                getLogger().debug(".extractUoWPayloadAndCreateNewActionableTaskSet(): payload (UoWPayload).Payload --> [{}] {}", counter, currentPayload.getPayload());
                counter++;
            }
        }
        ArrayList<PetasosTaskOld> newEgressTransportPacketSet = new ArrayList<PetasosTaskOld>();
        for(UoWPayload currentPayload: egressPayloadList) {
            TaskWorkItemType newUoW = new TaskWorkItemType(currentPayload);
            getLogger().trace(".extractUoWPayloadAndCreateNewActionableTaskSet(): newUoW->{}", newUoW);
            TaskFulfillmentType clonedPetasosTaskFulfillment = SerializationUtils.clone(actionableTask.getPacketID());
            PetasosTaskOld transportPacket = new PetasosTaskOld(clonedPetasosTaskFulfillment, Date.from(Instant.now()), newUoW);
            newEgressTransportPacketSet.add(transportPacket);
        }
        getLogger().debug(".extractUoWPayloadAndCreateNewActionableTaskSet(): Exit, new WorkUnitTransportPackets created, number --> {} ", newEgressTransportPacketSet.size());

        return (newEgressTransportPacketSet);
    }

    private PetasosActionableTask createNewPetasosActionableTask(PetasosActionableTask oldTask, TaskWorkItemType newWorkItem){
        if(oldTask == null || newWorkItem == null){
            return(null);
        }

        TaskIdType newActionableTaskId = actionableTaskIdentifierFactory.newActionableTaskId(TaskReasonTypeEnum.TASK_REASON_MESSAGE_PROCESSING, newWorkItem.getIngresContent().getPayloadManifest().getContentDescriptor());
        PetasosActionableTask task = new PetasosActionableTask();

        TaskTraceabilityElementType traceabilityElement = new TaskTraceabilityElementType();
        traceabilityElement.setTaskId(oldTask.getTaskId());
        traceabilityElement.set
        TaskTraceabilityType taskTraceability = oldTask.getTaskTraceability();
        taskTraceability.getTaskJourney()

    }
}
