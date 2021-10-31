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

package net.fhirfactory.pegacorn.petasos.core.tasks.manager.outcomes;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.cluster.SharedActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayloadSet;
import org.apache.camel.Exchange;
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
public class TaskOutcome2NewTasksProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(TaskOutcome2NewTasksProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    TopologyIM topologyProxy;

    @Inject
    private SharedActionableTaskCache actionableTaskCache;

    @Inject
    private PetasosActionableTaskFactory actionableTaskFactory;
    
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
     * @param fulfillmentTask
     * @param camelExchange
     * @return A List<> of WorkUnitTransportPackets - one for each egress UoWPayload element within the incoming UoW.
     */

    public List<PetasosActionableTask> collectOutcomesAndCreateNewTasks(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
        getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): Entry, fulfillmentTask (WorkUnitTransportPacket)->{}", fulfillmentTask);
        // Get my Petasos Context
        getLogger().trace(".extractUoWPayloadAndCreateNewUoWSet(): Retrieving the WUPTopologyNode from the camelExchange (Exchange) passed in");
        WorkUnitProcessorTopologyNode node = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        TaskWorkItemType incomingUoW = fulfillmentTask.getTaskWorkItem();
        UoWPayloadSet egressContent = incomingUoW.getEgressContent();
        Set<UoWPayload> egressPayloadList = egressContent.getPayloadElements();
        if (getLogger().isDebugEnabled()) {
            int counter = 0;
            for(UoWPayload currentPayload: egressPayloadList){
                getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): payload (UoWPayload).PayloadTopic --> [{}] {}", counter, currentPayload.getPayloadManifest());
                getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): payload (UoWPayload).Payload --> [{}] {}", counter, currentPayload.getPayload());
                counter++;
            }
        }
        ArrayList<PetasosActionableTask> newEgressTransportPacketSet = new ArrayList<>();
        for(UoWPayload currentPayload: egressPayloadList) {
            TaskWorkItemType newUoW = new TaskWorkItemType(currentPayload);
            getLogger().trace(".extractUoWPayloadAndCreateNewUoWSet(): newUoW->{}", newUoW);
            WorkUnitTransportPacket transportPacket = new WorkUnitTransportPacket(fulfillmentTask.getPacketID(), Date.from(Instant.now()), newUoW);
            newEgressTransportPacketSet.add(transportPacket);
        }
        getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): Exit, new WorkUnitTransportPackets created, number --> {} ", newEgressTransportPacketSet.size());

        return (newEgressTransportPacketSet);
    }

    private PetasosActionableTask newActionableTask(TaskIdType previousActionableTaskId, TaskFulfillmentType previousTaskFulfillmentDetail, TaskWorkItemType work){
        getLogger().debug(".newActionableTask(): Entry, previousActionableTaskId->{}, previousTaskFulfillmentDetail->{}, work->{}", previousActionableTaskId, previousTaskFulfillmentDetail, work);

        if(previousTaskFulfillmentDetail == null){
            getLogger().debug(".newActionableTask(): Exit, previousTaskFulfillmentDetail is null, returning null");
            return(null);
        }
        if(previousActionableTaskId == null){
            getLogger().debug(".newActionableTask(): Exit, No associated Actionable Task (previousActionableTaskId == null), returning null");
            return(null);
        }
        if(work == null){
            getLogger().debug(".newActionableTask(): Exit, No new work to be done, returning null");
            return(null);
        }
        PetasosActionableTask actionableTask = actionableTaskCache.getActionableTask(previousActionableTaskId);
        if(actionableTask == null){
            getLogger().debug(".newActionableTask(): Exit, cannot resolve previousActionableTaskId, returning null");
            return(null);
        }
        actionableTaskFactory.newMessageBasedActionableTask(actionableTask, previousTaskFulfillmentDetail, work);
    }
}
