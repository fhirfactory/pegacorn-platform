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
package net.fhirfactory.pegacorn.petasos.model.itops.metrics.valuesets;

public enum ITOpsMetricNameEnum {
    METRIC_STATUS_IT_OPS_METRIC("Status", "itops.metric_type.status"),
    METRIC_LAST_ACTIVITY_IT_OPS_METRIC ("LastActivity", "itops.metric_type.last_activity"),
    METRIC_LAST_ACTIVITY_SUCCESSFUL_IT_OPS_METRIC("LastActivitySucessful", "itops.metric_type.last_activity_succesful"),
    METRIC_IS_CLUSTERED_IT_OPS_METRIC("IsClustered", "itops.metric_type.is_clustered"),
    METRIC_PRESENT_EPISODE_ID_IT_OPS_METRIC("PresentEpisodeID", "itops.metric_type.present_episode_id"),
    METRIC_PREVIOUS_EPISODE_ID_IT_OPS_METRIC("PreviousEpisodeID", "itops.metric_type.previous_episode_id"),
    METRIC_REMOTE_ENDPOINT_DETAIL("RemoteEndpoint", "itops.metric_type.remote_endpoinit_detail"),
    METRIC_WORK_UNIT_PROCESSOR_INCOMING_QUEUE_SIZE("WUPIncomingQueueSize", "itops.metric_type.wup_incoming_queue_size");

    private String displayName;
    private String metricType;

    private ITOpsMetricNameEnum(String displayName, String metricType){
        this.displayName = displayName;
        this.metricType = metricType;
    }

    public String getMetricType() {
        return metricType;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return (displayName);
    }
}
