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
package net.fhirfactory.pegacorn.petasos.model.configuration;

/**
 *
 * @author Mark A. Hunter
 */

public final class PetasosPropertyConstants {
    
    // For assisting in monitoring expected end time in parcels, this period in milliseconds
    // can be used when monitoring a UoW. For example it can be added to the expected completion
    // time to provide an extra buffer to avoid classing a UoW as failed too early
    public final static long EXPECTED_COMPLETION_TIME_BUFFER_MILLIS = 200;

    // How often to send a heartbeat between Nodes
    public final static long HEARTBEAT_FREQUENCY_MILLIS = 100;
    
    // Every N heartbeats, include a status update with the heartbeat message
    public final static long HEARTBEAT_STATUS_UPDATE_FREQUENCY = 10;
    
    // If a heartbeat fails, try to reconnect N times before failing and marking
    // the Node as unavailable
    public final static long HEARTBEAT_NUM_RETRIES = 3;
    
    // If the cache is full, this is the location where the overflow is persisted
    public final static String CACHE_OVERFLOW_DIRECTORY = "/tmp";
    
    // Size of the cache in bytes, once exceeded entries will be written to the overflow
    // directory
    public final static long CACHE_SIZE_IN_BYTES = 1000000000;
    
    // How long should completed content be kept within the caches?
    public final static long CACHE_ENTRY_RETENTION_PERIOD_SECONDS = 60;
    
    // How long should a WUP take to complete a task - worst case?
    public final static long WUP_ACTIVITY_DURATION_SECONDS = 10;
    
    // How long should a WUP sleep between scans for activity?
    public final static long WUP_SLEEP_INTERVAL_MILLISECONDS = 250;

    public final static String WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME = "WUPTopologyNode";
    public final static String WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME = "WUPJobCard";
    public final static String WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME = "WUPPetasosParcel";

}