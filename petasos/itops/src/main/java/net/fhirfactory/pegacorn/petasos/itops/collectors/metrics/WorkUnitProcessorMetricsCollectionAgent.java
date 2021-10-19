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
package net.fhirfactory.pegacorn.petasos.itops.collectors.metrics;

import net.fhirfactory.pegacorn.common.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.common.ITOpsMetricsCollectionAgentBase;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.WorkUnitProcessorNodeMetrics;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WorkUnitProcessorMetricsCollectionAgent extends ITOpsMetricsCollectionAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorMetricsCollectionAgent.class);

    
    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Metrics Collectors and Helpers
    //

    // Locking Function

    public WorkUnitProcessorNodeMetrics getNodeMetrics(String componentID){
        if(componentID == null){
            throw(new IllegalArgumentException("componentID is null"));
        }
        WorkUnitProcessorNodeMetrics existingMetrics = (WorkUnitProcessorNodeMetrics)getMetricsDM().getNodeMetrics(componentID);
        if(existingMetrics == null){
            Object lockObject = new Object();
            WorkUnitProcessorNodeMetrics metrics = new WorkUnitProcessorNodeMetrics(componentID);
            synchronized (getMetricsDM().getMetricsSetMapLock()) {
                getMetricsDM().getNodeMetricsLockMap().put(componentID, lockObject);
                getMetricsDM().getNodeMetricsMap().put(componentID, metrics);
            }
            return(metrics);
        } else {
            return(existingMetrics);
        }
    }

    public void touchLastActivityInstant(String componentID){
        getLogger().debug(".updateLastActivityInstant(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.touchLastActivityInstant();
        }
        getLogger().debug(".updateLastActivityInstant(): Exit");
    }

    public void touchActivityStartInstant(String componentID){
        getLogger().debug(".touchLastActivityStartInstant(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.touchLastActivityStartInstant();
        }
        getLogger().debug(".touchLastActivityStartInstant(): Exit");
    }

    public void touchActivityFinishInstant(String componentID){
        getLogger().debug(".touchLastActivityFinishInstant(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.touchLastActivityFinishInstant();
        }
        getLogger().debug(".touchLastActivityFinishInstant(): Exit");
    }

    public void touchEventDistributionFinishInstant(String componentID){
        getLogger().debug(".touchEventDistributionFinishInstant(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.touchEventDistributionFinishInstant();
        }
        getLogger().debug(".touchEventDistributionFinishInstant(): Exit");
    }

    public void incrementDistributedMessageEndpointCount(String componentID, String targetName){
        getLogger().debug(".incrementDistributedMessageEndpointCount(): Entry, componentID->{}, targetName->{}", componentID, targetName);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementDistributedMessageEndpointCount(targetName);
        }
        getLogger().debug(".touchEventDistributionFinishInstant(): Exit");
    }

    public void touchEventDistributionStartInstant(String componentID){
        getLogger().debug(".touchEventDistributionStartInstant(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.touchEventDistributionStartInstant();
        }
        getLogger().debug(".touchEventDistributionStartInstant(): Exit");
    }

    public void incrementDistributedMessageCount(String componentID){
        getLogger().debug(".incrementDistributedMessageCount(): Entry, componentID->{}", componentID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementDistributedMessageCount();
        }
        getLogger().debug(".incrementDistributedMessageCount(): Exit");
    }

    public void updateCurrentActionableTask(String componentID, TaskIdType episodeID){
        getLogger().debug(".updatePresentEpisodeID(): Entry, componentID->{}, episodeID->{}", componentID, episodeID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.setCurrentActionableTask(episodeID);
        }
        getLogger().debug(".updatePresentEpisodeID(): Exit");
    }

    public void updatePreviousEpisodeID(String componentID, TaskIdType episodeID){
        getLogger().debug(".updatePreviousEpisodeID(): Entry, componentID->{}, episodeID->{}", componentID, episodeID);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.setLastActionableTask(episodeID);
        }
        getLogger().debug(".updatePreviousEpisodeID(): Exit");
    }

    public void updateWorkUnitProcessorStatus(String componentID, String status){
        getLogger().debug(".updateWorkUnitProcessorStatus(): Entry, componentID->{}, status->{}", componentID, status);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.setNodeStatus(status);
        }
        getLogger().debug(".updateWorkUnitProcessorStatus(): Exit");
    }
    
    public void updateWUPIngresSEDAQueueSize(String componentID, int queueSize){
        getLogger().debug(".updateWUPIngresSEDAQueueSize(): Entry, componentID->{}, queueSize->{}", componentID, queueSize);
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.setNodeIngresQueueSize(queueSize);
        }
        getLogger().debug(".updateWUPIngresSEDAQueueSize(): Exit");
    }

    public void incrementIngresMessageCount(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementIngresMessageCount();
        }
    }

    public void incrementEgressMessageCount(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementEgressMessageCount();
        }
    }

    public void incrementFinalisedTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementFinalisedTasks();
        }
    }

    public void incrementFinishedTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementFinishedTasks();
        }
    }

    public void incrementFailedTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementFailedTasks();
        }
    }

    public void incrementStartedTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementStartedTasks();
        }
    }

    public void incrementRegisteredTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementRegisteredTasks();
        }
    }

    public void incrementCancelledTasks(String componentID){
        WorkUnitProcessorNodeMetrics nodeMetrics = getNodeMetrics(componentID);
        synchronized(getMetricsDM().getNodeMetricsLock(componentID)) {
            nodeMetrics.incrementCancelledTasks();
        }
    }


}
