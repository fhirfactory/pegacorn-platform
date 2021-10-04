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
package net.fhirfactory.pegacorn.petasos.itops.collectors;

import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsMetricsLocalDM;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.ITOpsMetric;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.valuesets.ITOpsMetricNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class ITOpsMetricsCollectionAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMetricsCollectionAgent.class);

    @Inject
    private ITOpsMetricsLocalDM metricsDM;

    //
    // Metrics Collectors and Helpers
    //

    public void updateLastActivityInstant(String componentID){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_LAST_ACTIVITY_IT_OPS_METRIC);
        newUpdateInstantMetric.setUnit("Date/Time");
        newUpdateInstantMetric.setValue(Instant.now().toString());
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

    public void updateLastActivitySuccess(String componentID, boolean success){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_LAST_ACTIVITY_SUCCESSFUL_IT_OPS_METRIC);
        newUpdateInstantMetric.setUnit("boolean");
        newUpdateInstantMetric.setValue(Boolean.toString(success));
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

    public void updatePresentEpisodeID(String componentID, String episodeID){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_PRESENT_EPISODE_ID_IT_OPS_METRIC);
        newUpdateInstantMetric.setUnit("Task ID");
        newUpdateInstantMetric.setValue(episodeID);
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

    public void updatePreviousEpisodeID(String componentID, String episodeID){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_PREVIOUS_EPISODE_ID_IT_OPS_METRIC);
        newUpdateInstantMetric.setUnit("Task ID");
        newUpdateInstantMetric.setValue(episodeID);
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

    public void updateWorkUnitProcessorStatus(String componentID, String status){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_STATUS_IT_OPS_METRIC);
        newUpdateInstantMetric.setUnit("WorkUnitProcessor.Status");
        newUpdateInstantMetric.setValue(status);
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }
    
    public void updateRemoteEndpointDetail(String componentID, String endpointDetail ){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_REMOTE_ENDPOINT_DETAIL);
        newUpdateInstantMetric.setUnit("RemoteEndpoint.Detail");
        newUpdateInstantMetric.setValue(endpointDetail);
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

    public void updateWUPIngresSEDAQueueSize(String componentID, int queueSize){
        ITOpsMetric newUpdateInstantMetric = new ITOpsMetric();
        newUpdateInstantMetric.setName(ITOpsMetricNameEnum.METRIC_REMOTE_ENDPOINT_DETAIL);
        newUpdateInstantMetric.setUnit("WorkUnitProcessor.IncomingQueueSize");
        newUpdateInstantMetric.setValue(Integer.toString(queueSize));
        metricsDM.addMetric(componentID, newUpdateInstantMetric);
    }

}
