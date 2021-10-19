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
package net.fhirfactory.pegacorn.common.model.componentid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class ComponentIdType implements Serializable {
    private String id;
    private String displayName;
    private String version;

    //
    // Constructor(s)
    //

    public ComponentIdType(){
        this.id = null;
        this.version = null;
        this.displayName = null;
    }

    public ComponentIdType(ComponentIdType ori){
        this.id = null;
        this.version = null;
        this.displayName = null;
        if(ori.hasId()){
            setId(SerializationUtils.clone(ori.getId()));
        }
        if(ori.hasVersion()){
            setVersion(SerializationUtils.clone(ori.getVersion()));
        }
        if(ori.hasDisplayName()){
            setDisplayName(SerializationUtils.clone(ori.getDisplayName()));
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
    public boolean hasDisplayName(){
        boolean hasValue = this.displayName != null;
        return(hasValue);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "ComponentIdType{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
