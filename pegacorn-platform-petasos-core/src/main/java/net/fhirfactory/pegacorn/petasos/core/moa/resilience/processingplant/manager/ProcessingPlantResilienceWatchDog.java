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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class ProcessingPlantResilienceWatchDog {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantResilienceWatchDog.class);

    private boolean initialised;
    private boolean taskScheduled;

    private static Long RESILIENCE_ACTIVITY_INITIAL_DELAY = 60000L;
    private static Long RESILIENCE_ACTIVITY_WATCHDOG_PERIOD = 10000L;

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
        }
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

    protected void scheduleAsynchronousWriterTask(){
        getLogger().debug(".scheduleAsynchronousWriterTask(): Entry");
        if(isTaskScheduled()){
            // do nothing
        } else {
            TimerTask ProcessingPlantResilienceWatchDogTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".ProcessingPlantResilienceWatchDogTask(): Entry");
                    checkResilienceServices();
                    getLogger().debug(".ProcessingPlantResilienceWatchDogTask(): Exit");
                }
            };
            String timerName = "ProcessingPlantResilienceWatchDogTask";
            Timer timer = new Timer(timerName);
            timer.schedule(ProcessingPlantResilienceWatchDogTask, getResilienceActivityInitialDelay(), getResilienceActivityWatchdogPeriod());
            this.taskScheduled = true;
        }
        getLogger().debug(".scheduleAsynchronousWriterTask(): Exit");
    }

    protected void checkResilienceServices(){

    }
}
