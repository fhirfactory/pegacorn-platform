/*
 * Copyright (c) 2020 MAHun
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
package net.fhirfactory.pegacorn.petasos.itops.caches;

import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.ITOpsPubSubReport;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.ProcessingPlantSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.WorkUnitProcessorSubscriptionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ITOpsPubSubMapLocalDM extends ITOpsLocalDMRefreshBase {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsPubSubMapLocalDM.class);

    // ConcurrentHashMap<componentID, ProcessingPlantSubscriptionSummary>
    private ConcurrentHashMap<String, ProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet;
    private Object processingPlantMapLock;
    // ConcurrentHashMap<componentID, WorkUnitProcessorSubscriptionSummary>
    private ConcurrentHashMap<String, WorkUnitProcessorSubscriptionSummary> workUnitProcessorSubscriptionSummarySet;
    private Object wupMapLock;

    public ITOpsPubSubMapLocalDM(){
        this.processingPlantSubscriptionSummarySet = new ConcurrentHashMap<>();
        this.workUnitProcessorSubscriptionSummarySet = new ConcurrentHashMap<>();
        this.processingPlantMapLock = new Object();
        this.wupMapLock = new Object();
    }

    //
    // Publisher Subscription Traceability
    //

    public void addProcessingPlantSubscriptionSummary(ProcessingPlantSubscriptionSummary summary){
        LOG.debug(".addProcessingPlantSubscriptionSummary(): Entry");
        synchronized (processingPlantMapLock) {
            if (processingPlantSubscriptionSummarySet.containsKey(summary.getComponentID())) {
                processingPlantSubscriptionSummarySet.remove(summary.getComponentID());
            }
            processingPlantSubscriptionSummarySet.put(summary.getComponentID(), summary);
        }
        refreshCurrentStateUpdateInstant();
        LOG.debug(".addProcessingPlantSubscriptionSummary(): Exit");
    }

    public void addWorkUnitProcessorSubscriptionSummary(WorkUnitProcessorSubscriptionSummary summary){
        LOG.debug(".addWorkUnitProcessorSubscriptionSummary(): Entry");
        synchronized (wupMapLock) {
            if (workUnitProcessorSubscriptionSummarySet.containsKey(summary.getSubscriber())) {
                workUnitProcessorSubscriptionSummarySet.remove(summary.getSubscriber());
            }
            workUnitProcessorSubscriptionSummarySet.put(summary.getSubscriber(), summary);
        }
        refreshCurrentStateUpdateInstant();
        LOG.debug(".addWorkUnitProcessorSubscriptionSummary(): Exit");
    }

    public ITOpsPubSubReport getPubSubReport(){
        LOG.debug(".getPubSubReport(): Entry");
        ITOpsPubSubReport report = new ITOpsPubSubReport();
        synchronized (wupMapLock) {
            for (WorkUnitProcessorSubscriptionSummary currentSummary : this.workUnitProcessorSubscriptionSummarySet.values()) {
                LOG.trace(".getPubSubReport(): Adding summary->{}", currentSummary);
                report.addWorkUnitProcessorSubscriptionSummary(currentSummary);
            }
        }
        synchronized(processingPlantMapLock){
            for(ProcessingPlantSubscriptionSummary currentSummary: this.processingPlantSubscriptionSummarySet.values()){
                report.addProcessingPlantSubscriptionSummary(currentSummary);
            }
        }
        report.setTimestamp(getCurrentStateUpdateInstant());
        LOG.debug(".getPubSubReport(): Eixt");
        return(report);
    }
}
