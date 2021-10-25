/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.Instant;

public class TaskIdType implements Serializable {
    private String id;
    private String version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant creationInstant;

    private static final String DEFAULT_VERSION="1.0";

    //
    // Constructors
    //

    public TaskIdType(){
        this.id = null;
        this.version = DEFAULT_VERSION;
        this.creationInstant = Instant.now();
    }

    public TaskIdType(TaskIdType ori){
        this.id = null;
        this.version = DEFAULT_VERSION;
        this.creationInstant = Instant.now();
        if(ori.hasId()) {
            setId(SerializationUtils.clone(ori.getId()));
        }
        if(ori.hasVersion()) {
            setVersion(SerializationUtils.clone(ori.getVersion()));
        }
        if(ori.hasCreationInstant()){
            setCreationInstant(SerializationUtils.clone(ori.getCreationInstant()));
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasId(){
        boolean hasValue = this.id != null;
        return(hasValue);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean hasVersion(){
        boolean hasValue = this.version != null;
        return(hasValue);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonIgnore
    public boolean hasCreationInstant(){
        boolean hasValue = this.creationInstant != null;
        return(hasValue);
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public void setCreationInstant(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "IdentitySegment{" +
                "id=" + id +
                ", version=" + version +
                ", creationInstant=" + creationInstant +
                '}';
    }
}
