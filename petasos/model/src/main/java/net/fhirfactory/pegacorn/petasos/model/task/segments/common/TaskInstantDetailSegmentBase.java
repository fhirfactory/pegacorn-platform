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
package net.fhirfactory.pegacorn.petasos.model.task.segments.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import org.apache.commons.lang3.SerializationUtils;

import java.time.Instant;

public class TaskInstantDetailSegmentBase {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant readyInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant startInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant finishInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant finalisationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastCheckedInstant;

    //
    // Constructor
    //

    public TaskInstantDetailSegmentBase(){
        this.registrationInstant = null;
        this.readyInstant = null;
        this.startInstant = null;
        this.finishInstant = null;
        this.finalisationInstant = null;
        this.lastCheckedInstant = Instant.now();
    }

    public TaskInstantDetailSegmentBase(TaskInstantDetailSegmentBase ori){
        this.registrationInstant = null;
        this.readyInstant = null;
        this.startInstant = null;
        this.finishInstant = null;
        this.finalisationInstant = null;
        this.lastCheckedInstant = Instant.now();
        if(ori.hasFinalisationInstant()){
            setFinalisationInstant(SerializationUtils.clone(ori.getFinalisationInstant()));
        }
        if(ori.hasFinishInstant()){
            setFinishInstant(SerializationUtils.clone(ori.getFinishInstant()));
        }
        if(ori.hasReadyInstant()){
            setReadyInstant(SerializationUtils.clone(ori.getReadyInstant()));
        }
        if(ori.hasStartInstant()){
            setStartInstant(SerializationUtils.clone(ori.getStartInstant()));
        }
        if(ori.hasRegistrationInstant()){
            setRegistrationInstant(SerializationUtils.clone(ori.getRegistrationInstant()));
        }
        if(ori.hasLastCheckedInstant()){
            setLastCheckedInstant(SerializationUtils.clone(ori.getLastCheckedInstant()));
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasRegistrationInstant(){
        boolean hasValue = this.registrationInstant != null;
        return(hasValue);
    }

    public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(Instant registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    @JsonIgnore
    public boolean hasReadyInstant(){
        boolean hasValue = this.readyInstant != null;
        return(hasValue);
    }
    public Instant getReadyInstant() {
        return readyInstant;
    }

    public void setReadyInstant(Instant readyInstant) {
        this.readyInstant = readyInstant;
    }

    @JsonIgnore
    public boolean hasStartInstant(){
        boolean hasValue = this.startInstant != null;
        return(hasValue);
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    @JsonIgnore
    public boolean hasFinishInstant(){
        boolean hasValue = this.finishInstant != null;
        return(hasValue);
    }

    public Instant getFinishInstant() {
        return finishInstant;
    }

    public void setFinishInstant(Instant finishInstant) {
        this.finishInstant = finishInstant;
    }

    @JsonIgnore
    public boolean hasFinalisationInstant(){
        boolean hasValue = this.finalisationInstant != null;
        return(hasValue);
    }

    public Instant getFinalisationInstant() {
        return finalisationInstant;
    }

    public void setFinalisationInstant(Instant finalisationInstant) {
        this.finalisationInstant = finalisationInstant;
    }

    @JsonIgnore
    public boolean hasLastCheckedInstant(){
        boolean hasValue = this.lastCheckedInstant != null;
        return(hasValue);
    }

    public Instant getLastCheckedInstant() {
        return lastCheckedInstant;
    }

    public void setLastCheckedInstant(Instant lastCheckedInstant) {
        this.lastCheckedInstant = lastCheckedInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.petasos.model.task.segments.common.TaskInstantDetailSegmentBase{" +
                "registrationInstant=" + registrationInstant +
                ", readyInstant=" + readyInstant +
                ", startInstant=" + startInstant +
                ", finishInstant=" + finishInstant +
                ", finalisationInstant=" + finalisationInstant +
                ", lastCheckedInstant=" + lastCheckedInstant +
                '}';
    }
}
