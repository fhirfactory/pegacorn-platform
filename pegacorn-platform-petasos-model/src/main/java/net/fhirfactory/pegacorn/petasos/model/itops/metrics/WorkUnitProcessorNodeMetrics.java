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
package net.fhirfactory.pegacorn.petasos.model.itops.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.common.NodeMetricsBase;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;

import java.time.Instant;
import java.util.HashMap;

public class WorkUnitProcessorNodeMetrics extends NodeMetricsBase {

    public static final String WORK_UNIT_PROCESSOR_METRICS_TYPE = "WorkUnitProcessorBasedMetrics";

    private int ingresMessageCount;
    private int egressMessageCount;
    private int distributedMessageCount;
    private HashMap<String, Integer> distributionCountMap;
    private double averageEventProcessingDuration;
    private double rollingEventProcessingDuration;
    private double lastEventProcessingDuration;
    private double averageEventDistributionDuration;
    private double rollingAverageEventDistributionDuration;
    private Instant eventDistributionStartInstant;
    private Instant eventDistributionFinishInstant;
    private Instant eventProcessingStartInstant;
    private Instant eventProcessingFinishInstant;
    private int finalisedTasks;
    private int finishedTasks;
    private int failedTasks;
    private int startedTasks;
    private int registeredTasks;
    private int cancelledTasks;
    private PetasosEpisodeIdentifier currentEpisode;
    private PetasosEpisodeIdentifier lastEpisode;
    private int nodeIngresQueueSize;


    private static Long ROLLING_AVERAGE_COUNT = 10L;

    public WorkUnitProcessorNodeMetrics(){
        super();
        this.finalisedTasks = 0;
        this.finishedTasks = 0;
        this.failedTasks = 0;
        this.startedTasks = 0;
        this.registeredTasks = 0;
        this.cancelledTasks = 0;
        this.ingresMessageCount = 0;
        this.egressMessageCount = 0;
        this.distributedMessageCount = 0;
        this.averageEventDistributionDuration = 0L;
        this.rollingAverageEventDistributionDuration = 0L;
        this.eventDistributionFinishInstant = null;
        this.eventDistributionStartInstant = null;
        this.averageEventProcessingDuration = 0L;
        this.rollingEventProcessingDuration = 0L;
        this.lastEventProcessingDuration = 0L;
        this.eventProcessingStartInstant = Instant.EPOCH;
        this.eventProcessingFinishInstant = null;
        this.currentEpisode = null;
        this.lastEpisode = null;
        this.nodeIngresQueueSize = 0;
        this.distributionCountMap = new HashMap<>();
        this.setMetricsType(WORK_UNIT_PROCESSOR_METRICS_TYPE);
    }

    public WorkUnitProcessorNodeMetrics(String componentID){
        super(componentID);
        this.finalisedTasks = 0;
        this.finishedTasks = 0;
        this.failedTasks = 0;
        this.startedTasks = 0;
        this.registeredTasks = 0;
        this.cancelledTasks = 0;
        this.ingresMessageCount = 0;
        this.egressMessageCount = 0;
        this.distributedMessageCount = 0;
        this.averageEventDistributionDuration = 0L;
        this.rollingAverageEventDistributionDuration = 0L;
        this.eventDistributionFinishInstant = null;
        this.eventDistributionStartInstant = null;
        this.averageEventProcessingDuration = 0L;
        this.rollingEventProcessingDuration = 0L;
        this.lastEventProcessingDuration = 0L;
        this.eventProcessingStartInstant = Instant.EPOCH;
        this.eventProcessingFinishInstant = null;
        this.currentEpisode = null;
        this.lastEpisode = null;
        this.nodeIngresQueueSize = 0;
        this.distributionCountMap = new HashMap<>();
        this.setMetricsType(WORK_UNIT_PROCESSOR_METRICS_TYPE);
    }

    //
    // Some Helper Methods
    //

    @JsonIgnore
    public void incrementDistributedMessageEndpointCount(String targetName){
        if(!distributionCountMap.containsKey(targetName)){
            distributionCountMap.put(targetName, 1);
        } else {
            Integer currentValue = distributionCountMap.get(targetName);
            currentValue += 1;
            distributionCountMap.remove(targetName);
            distributionCountMap.put(targetName, currentValue);
        }
    }

    @JsonIgnore
    public void incrementIngresMessageCount(){
        this.ingresMessageCount += 1;
    }

    @JsonIgnore
    public void incrementEgressMessageCount(){
        this.egressMessageCount += 1;
    }

    @JsonIgnore
    public void incrementDistributedMessageCount(){
        this.distributedMessageCount += 1;
    }

    @JsonIgnore
    public void incrementFinalisedTasks(){
        this.finalisedTasks += 1;
    }

    @JsonIgnore
    public void incrementFinishedTasks(){
        this.finishedTasks += 1;
    }

    @JsonIgnore
    public void incrementFailedTasks(){
        this.failedTasks += 1;
    }

    @JsonIgnore
    public void incrementStartedTasks(){
        this.startedTasks += 1;
    }

    @JsonIgnore
    public void incrementRegisteredTasks(){
        this.registeredTasks += 1;
    }

    @JsonIgnore
    public void incrementCancelledTasks(){
        this.cancelledTasks  += 1;
    }

    @JsonIgnore
    public void updateAverageEventProcessingDuration(double newDuration){
        double currentAverage = this.averageEventProcessingDuration;
        if(currentAverage <= 0){
            this.averageEventProcessingDuration = newDuration;
        } else {
            double aggregateSum = (this.ingresMessageCount * currentAverage) + newDuration;
            double newAverage = aggregateSum / (this.ingresMessageCount + 1.0);
            this.averageEventProcessingDuration = newAverage;
        }
    }

    @JsonIgnore
    public void updateRollingEventEventProcessingDuration(double newDuration){
        double currentAverage = this.rollingEventProcessingDuration;
        if(currentAverage <= 0){
            this.rollingEventProcessingDuration = newDuration;
        } else {
            double aggregateSum = (currentAverage * (getRollingAverageCount()-1.0)) + newDuration;
            double newAverage = aggregateSum / (getRollingAverageCount());
            this.rollingEventProcessingDuration = newAverage;
        }
    }

    @JsonIgnore
    public void touchLastActivityStartInstant(){
        this.eventProcessingStartInstant = Instant.now();
    }

    @JsonIgnore
    public void touchEventDistributionStartInstant(){
        this.eventDistributionStartInstant = Instant.now();
    }

    @JsonIgnore
    public void touchEventDistributionFinishInstant(){
        this.eventDistributionFinishInstant = Instant.now();
        double finishTime = (double)this.eventDistributionFinishInstant.getEpochSecond() + (double)this.eventDistributionFinishInstant.getNano()/1000000000.0;
        double startTime = (double)this.eventDistributionStartInstant.getEpochSecond() + (double)this.eventDistributionStartInstant.getNano()/1000000000.0;
        double duration = finishTime - startTime;
        updateRollingAverageEventEventDistributionDuration(duration);
        updateAverageEventDistributionDuration(duration);
    }

    @JsonIgnore
    public void updateRollingAverageEventEventDistributionDuration(double newDuration){
        double currentAverage = this.rollingAverageEventDistributionDuration;
        if(currentAverage <= 0){
            this.rollingAverageEventDistributionDuration = newDuration;
        } else {
            double aggregateSum = (currentAverage * ((double)getRollingAverageCount()-1.0)) + newDuration;
            double newAverage = aggregateSum / (getRollingAverageCount());
            this.rollingAverageEventDistributionDuration = newAverage;
        }
    }

    @JsonIgnore
    public void updateAverageEventDistributionDuration(double newDuration){
        double currentAverage = this.averageEventDistributionDuration;
        double currentDistributionCount = (double)this.distributedMessageCount;
        if((currentDistributionCount <= 0) || (currentAverage == 0)){
            this.averageEventDistributionDuration = newDuration;
        } else {
            double aggregateSum = (currentAverage * (currentDistributionCount-1.0)) + newDuration;
            double newAverage = aggregateSum / (currentDistributionCount);
            this.averageEventDistributionDuration = newAverage;
        }
    }

    @JsonIgnore
    public void touchLastActivityFinishInstant(){
        this.eventProcessingFinishInstant = Instant.now();
        double finishTime = (double)this.eventProcessingFinishInstant.getEpochSecond() + (double)this.eventProcessingFinishInstant.getNano()/1000000000.0;
        double startTime = (double)this.eventProcessingStartInstant.getEpochSecond() + (double)this.eventProcessingStartInstant.getNano()/1000000000.0;
        double duration = finishTime - startTime;
        this.lastEventProcessingDuration = duration;
        setLastEventProcessingDuration(duration);
        updateAverageEventProcessingDuration(duration);
        updateRollingEventEventProcessingDuration(duration);
    }

    //
    // Getters (and Setters)
    //


    public double getAverageEventDistributionDuration() {
        return averageEventDistributionDuration;
    }

    public void setAverageEventDistributionDuration(double averageEventDistributionDuration) {
        this.averageEventDistributionDuration = averageEventDistributionDuration;
    }

    public double getRollingAverageEventDistributionDuration() {
        return rollingAverageEventDistributionDuration;
    }

    public void setRollingAverageEventDistributionDuration(double rollingAverageEventDistributionDuration) {
        this.rollingAverageEventDistributionDuration = rollingAverageEventDistributionDuration;
    }

    public Instant getEventDistributionStartInstant() {
        return eventDistributionStartInstant;
    }

    public void setEventDistributionStartInstant(Instant eventDistributionStartInstant) {
        this.eventDistributionStartInstant = eventDistributionStartInstant;
    }

    public Instant getEventDistributionFinishInstant() {
        return eventDistributionFinishInstant;
    }

    public void setEventDistributionFinishInstant(Instant eventDistributionFinishInstant) {
        this.eventDistributionFinishInstant = eventDistributionFinishInstant;
    }

    public int getFinalisedTasks() {
        return finalisedTasks;
    }

    public void setFinalisedTasks(int finalisedTasks) {
        this.finalisedTasks = finalisedTasks;
    }

    public int getFinishedTasks() {
        return finishedTasks;
    }

    public void setFinishedTasks(int finishedTasks) {
        this.finishedTasks = finishedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public void setFailedTasks(int failedTasks) {
        this.failedTasks = failedTasks;
    }

    public int getStartedTasks() {
        return startedTasks;
    }

    public void setStartedTasks(int startedTasks) {
        this.startedTasks = startedTasks;
    }

    public int getRegisteredTasks() {
        return registeredTasks;
    }

    public void setRegisteredTasks(int registeredTasks) {
        this.registeredTasks = registeredTasks;
    }

    public int getCancelledTasks() {
        return cancelledTasks;
    }

    public void setCancelledTasks(int cancelledTasks) {
        this.cancelledTasks = cancelledTasks;
    }

    public PetasosEpisodeIdentifier getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(PetasosEpisodeIdentifier currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public PetasosEpisodeIdentifier getLastEpisode() {
        return lastEpisode;
    }

    public void setLastEpisode(PetasosEpisodeIdentifier lastEpisode) {
        this.lastEpisode = lastEpisode;
    }

    public int getNodeIngresQueueSize() {
        return nodeIngresQueueSize;
    }

    public void setNodeIngresQueueSize(int nodeIngresQueueSize) {
        this.nodeIngresQueueSize = nodeIngresQueueSize;
    }

    public int getIngresMessageCount() {
        return ingresMessageCount;
    }

    public void setIngresMessageCount(int ingresMessageCount) {
        this.ingresMessageCount = ingresMessageCount;
    }

    public int getEgressMessageCount() {
        return egressMessageCount;
    }

    public void setEgressMessageCount(int egressMessageCount) {
        this.egressMessageCount = egressMessageCount;
    }

    public double getAverageEventProcessingDuration() {
        return averageEventProcessingDuration;
    }

    public void setAverageEventProcessingDuration(double averageEventProcessingDuration) {
        this.averageEventProcessingDuration = averageEventProcessingDuration;
    }

    public double getRollingEventProcessingDuration() {
        return rollingEventProcessingDuration;
    }

    public void setRollingEventProcessingDuration(double rollingEventProcessingDuration) {
        this.rollingEventProcessingDuration = rollingEventProcessingDuration;
    }

    public double getLastEventProcessingDuration() {
        return lastEventProcessingDuration;
    }

    public void setLastEventProcessingDuration(double lastEventProcessingDuration) {
        this.lastEventProcessingDuration = lastEventProcessingDuration;
    }

    public Instant getEventProcessingStartInstant() {
        return eventProcessingStartInstant;
    }

    public void setEventProcessingStartInstant(Instant eventProcessingStartInstant) {
        this.eventProcessingStartInstant = eventProcessingStartInstant;
    }

    public Instant getEventProcessingFinishInstant() {
        return eventProcessingFinishInstant;
    }

    public void setEventProcessingFinishInstant(Instant eventProcessingFinishInstant) {
        this.eventProcessingFinishInstant = eventProcessingFinishInstant;
    }

    public static Long getRollingAverageCount() {
        return ROLLING_AVERAGE_COUNT;
    }

    public static void setRollingAverageCount(Long rollingAverageCount) {
        ROLLING_AVERAGE_COUNT = rollingAverageCount;
    }

    public int getDistributedMessageCount() {
        return distributedMessageCount;
    }

    public void setDistributedMessageCount(int distributedMessageCount) {
        this.distributedMessageCount = distributedMessageCount;
    }

    public HashMap<String, Integer> getDistributionCountMap() {
        return distributionCountMap;
    }

    public void setDistributionCountMap(HashMap<String, Integer> distributionCountMap) {
        this.distributionCountMap = distributionCountMap;
    }

    //
    // To String Method(s)
    //

    @Override
    public String toString() {
        return "NodeMetrics{" +
                "componentID=" + getComponentID() +
                ", ingresMessageCount=" + ingresMessageCount +
                ", egressMessageCount=" + egressMessageCount +
                ", distributedMessageCount=" + distributedMessageCount +
                ", averageEventProcessingDuration=" + averageEventProcessingDuration +
                ", rollingEventProcessingDuration=" + rollingEventProcessingDuration +
                ", lastEventProcessingDuration=" + lastEventProcessingDuration +
                ", averageEventDistributionDuration=" + averageEventDistributionDuration +
                ", rollingAverageEventDistributionDuration=" + rollingAverageEventDistributionDuration +
                ", eventDistributionStartInstant=" + eventDistributionStartInstant +
                ", eventDistributionFinishInstant=" + eventDistributionFinishInstant +
                ", eventProcessingStartInstant=" + eventProcessingStartInstant +
                ", eventProcessingFinishInstant=" + eventProcessingFinishInstant +
                ", nodeStartupInstant=" + getNodeStartupInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", finalisedTasks=" + finalisedTasks +
                ", finishedTasks=" + finishedTasks +
                ", failedTasks=" + failedTasks +
                ", startedTasks=" + startedTasks +
                ", registeredTasks=" + registeredTasks +
                ", cancelledTasks=" + cancelledTasks +
                ", currentEpisode=" + currentEpisode +
                ", lastEpisode=" + lastEpisode +
                ", nodeIngresQueueSize=" + nodeIngresQueueSize +
                ", nodeStatus=" + getNodeStatus() +
                ", metricsType=" + getMetricsType()+
                ", distributionCountMap=" + distributionCountMap +
                '}';
    }
}
