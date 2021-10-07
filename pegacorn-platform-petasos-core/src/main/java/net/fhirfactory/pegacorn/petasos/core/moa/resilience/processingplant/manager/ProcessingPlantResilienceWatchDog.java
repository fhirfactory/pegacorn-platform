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

package net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.manager;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.cache.ProcessingPlantEpisodeFinalisationCacheDM;
import net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.cache.ProcessingPlantResilienceParcelCacheDM;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.ProcessingPlantMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.seda.SedaEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class ProcessingPlantResilienceWatchDog {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantResilienceWatchDog.class);

    private boolean initialised;
    private boolean taskScheduled;

    private static Long RESILIENCE_ACTIVITY_INITIAL_DELAY = 60000L;
    private static Long RESILIENCE_ACTIVITY_WATCHDOG_PERIOD = 10000L;

    @Inject
    private ProcessingPlantEpisodeFinalisationCacheDM finalisationCacheDM;

    @Inject
    private ProcessingPlantResilienceParcelCacheDM parcelCacheDM;

    @Inject
    private MOAServicesAuditBroker auditServicesBroker;

    @Inject
    private ProcessingPlantMetricsCollectionAgent metricsAgent;

    @Inject
    private WorkUnitProcessorMetricsCollectionAgent wupMetricsAgent;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private CamelContext camelContext;

    @Inject
    private TopologyIM topologyIM;

    //
    // Constructor
    //

    public ProcessingPlantResilienceWatchDog(){
        this.initialised = false;
        this.taskScheduled = false;
    }

    //
    // Post Construct Activities
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Nothing to do, already initialised");
        } else {
            getLogger().info(".initialise(): Initialising...");
            getLogger().info(".initialise(): [Scheduling ProcessingPlant Resilience Watchdog] Start");
            scheduleWatchdogTask();
            getLogger().info(".initialise(): [Scheduling ProcessingPlant Resilience Watchdog] Finish");
            this.initialised = true;
            getLogger().info(".initialise(): Done...");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    public Logger getLogger(){
        return(LOG);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isTaskScheduled() {
        return taskScheduled;
    }

    public static Long getResilienceActivityInitialDelay() {
        return RESILIENCE_ACTIVITY_INITIAL_DELAY;
    }

    public static Long getResilienceActivityWatchdogPeriod() {
        return RESILIENCE_ACTIVITY_WATCHDOG_PERIOD;
    }

    //
    // Scheduler
    //

    protected void scheduleWatchdogTask(){
        getLogger().debug(".scheduleWatchdogTask(): Entry");
        if(isTaskScheduled()){
            // do nothing
        } else {
            TimerTask ProcessingPlantResilienceWatchDogTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".ProcessingPlantResilienceWatchDogTask(): Entry");
                    watchdogFunction();
                    getLogger().debug(".ProcessingPlantResilienceWatchDogTask(): Exit");
                }
            };
            String timerName = "ProcessingPlantResilienceWatchDogTask";
            Timer timer = new Timer(timerName);
            timer.schedule(ProcessingPlantResilienceWatchDogTask, getResilienceActivityInitialDelay(), getResilienceActivityWatchdogPeriod());
            this.taskScheduled = true;
        }
        getLogger().debug(".scheduleWatchdogTask(): Exit");
    }

    //
    // WatchDog Service Function Set
    //

    protected void watchdogFunction(){
        getLogger().debug(".watchdogFunction(): Entry");
        List<PetasosEpisodeIdentifier> episodeList = finalisationCacheDM.getEpisodeList();
        if(episodeList.isEmpty()){
            return;
        }
        //
        // 1st, check for Finalised Episodes --> Log an AuditEvent then purge
        //
        getLogger().trace(".watchdogFunction(): Logging and Purging Finalised Episodes/Parcels");
        List<PetasosEpisodeIdentifier> finalisedList = new ArrayList<>();
        for(PetasosEpisodeIdentifier currentEpisode: episodeList){
            boolean isFinalised = finalisationCacheDM.checkForEpisodeFinalisation(currentEpisode);
            if(isFinalised){
                WUPIdentifier wupIdentifier = null;
                List<ResilienceParcel> parcelSetByEpisodeID = parcelCacheDM.getParcelByEpisodeID(currentEpisode);
                for(ResilienceParcel currentParcel: parcelSetByEpisodeID){
                    if(wupIdentifier != null) {
                        if (currentParcel.getAssociatedWUPIdentifier() != null){
                            wupIdentifier = currentParcel.getAssociatedWUPIdentifier();
                        }
                    }
                    currentParcel.setFinalisationDate(Date.from(Instant.now()));
                    currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
                    auditServicesBroker.logActivity(currentParcel);
                    parcelCacheDM.removeParcel(currentParcel);
                }
                finalisationCacheDM.removeEpisode(currentEpisode);
                finalisedList.add(currentEpisode);
                if(wupIdentifier != null) {
                    TopologyNode node = topologyIM.getNode(wupIdentifier);
                    if(node != null) {
                        if(node.getComponentID() != null) {
                            wupMetricsAgent.incrementFinalisedTasks(node.getComponentID());
                        }
                    }
                }
            }
        }
        //
        // We are going to go through and check each Episode's progress, but first, remove the finalised ones
        //
        getLogger().trace(".watchdogFunction(): Checking Failed, Active, Waiting Episodes/Parcels");
        for(PetasosEpisodeIdentifier finalisedEpisode: finalisedList){
            episodeList.remove(finalisedEpisode);
        }
        // TODO check episode progress

        //
        // Now we will clean up the ResilienceParcel Cache
        //
        getLogger().trace(".watchdogFunction(): Purging Cancelled Parcels");
        List<ResilienceParcel> cancelledParcelSet = parcelCacheDM.getCancelledParcelSet();
        for(ResilienceParcel currentCancelledParcel: cancelledParcelSet){
            parcelCacheDM.removeParcel(currentCancelledParcel);
        }
        getLogger().trace(".watchdogFunction(): Purging FinishedElsewhere etc. Parcels");
        List<ResilienceParcel> finishedElsewhereList = parcelCacheDM.getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED_ELSEWHERE);
        for(ResilienceParcel currentFinishedElsewhereParcel: finishedElsewhereList){
            parcelCacheDM.removeParcel(currentFinishedElsewhereParcel);
        }
        getLogger().trace(".watchdogFunction(): Purging FinalisedElsewhere etc. Parcels");
        List<ResilienceParcel> finalisedElsewhereList = parcelCacheDM.getParcelSetByState(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED_ELSEWHERE);
        List<ResilienceParcel> removedParcels = new ArrayList<>();
        for(ResilienceParcel currentFinalisedElsewhereParcel: finalisedElsewhereList){
            List<ResilienceParcel> parcelSetByEpisodeID = parcelCacheDM.getParcelByEpisodeID(currentFinalisedElsewhereParcel.getEpisodeIdentifier());
            for(ResilienceParcel currentParcel: parcelSetByEpisodeID){
                currentParcel.setFinalisationDate(Date.from(Instant.now()));
                currentParcel.setProcessingStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINALISED);
                auditServicesBroker.logActivity(currentParcel);
                parcelCacheDM.removeParcel(currentParcel);
                removedParcels.add(currentParcel);
            }
            finalisationCacheDM.removeEpisode(currentFinalisedElsewhereParcel.getEpisodeIdentifier());
            parcelCacheDM.removeParcel(currentFinalisedElsewhereParcel);
        }
        //
        // Do metrics agent update
        //
        String cacheDMMetrics = parcelCacheDM.getMetrics();
        metricsAgent.updateResilienceCacheMetrics(processingPlant.getProcessingPlantNode().getComponentID(), cacheDMMetrics);
        captureSEDAQueueSize();
    }

    private void captureSEDAQueueSize(){
        for(TopologyNodeFDN workshopFDN: processingPlant.getProcessingPlantNode().getWorkshops()) {
            WorkshopTopologyNode containedWorkshop = (WorkshopTopologyNode) topologyIM.getNode(workshopFDN);
            Collection<Endpoint> currentEndpointCollection = camelContext.getEndpoints();
            for (TopologyNodeFDN wupFDN : containedWorkshop.getWupSet()) {
                WorkUnitProcessorTopologyNode currentWUP = (WorkUnitProcessorTopologyNode) topologyIM.getNode(wupFDN);
                RouteElementNames routeNames = new RouteElementNames(currentWUP.getNodeFDN().getToken());
                String ingresProcessorIngresName = routeNames.getEndPointWUPContainerIngresProcessorIngres();
                if (ingresProcessorIngresName.startsWith("seda")) {
                    for(Endpoint currentEndpoint: currentEndpointCollection) {
                        if (currentEndpoint.getEndpointUri().equalsIgnoreCase(ingresProcessorIngresName)) {
                            SedaEndpoint seda = (SedaEndpoint) currentEndpoint;
                            int size = seda.getExchanges().size();
                            wupMetricsAgent.updateWUPIngresSEDAQueueSize(currentWUP.getComponentID(), size);
                            break;
                        }
                    }
                }
            }
        }
    }
}
