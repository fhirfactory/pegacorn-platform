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

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class ComponentID implements Serializable {
    private String uniqueID;
    private String displayID;
    private TopologyNodeFDN contextualID;
    private TopologyNodeFunctionFDN contextualFunctionID;
    private String version;

    public ComponentID(){
        this.uniqueID = null;
        this.displayID = null;
        this.contextualID = null;
        this.contextualFunctionID = null;
        this.version = null;
    }

    public ComponentID(ComponentID ori){
        if(ori.hasUniqueID()){
            this.uniqueID = SerializationUtils.clone(ori.getUniqueID());
        } else {
            this.uniqueID = null;
        }
        if(ori.hasDisplayID()){
            this.displayID = SerializationUtils.clone(ori.getDisplayID());
        } else {
            this.displayID = null;
        }
        if(ori.hasVersion()){
            this.version = SerializationUtils.clone(ori.getVersion());
        } else {
            this.version = null;
        }
        if(ori.hasContextualID()){
            this.contextualID = SerializationUtils.clone(ori.getContextualID());
        } else {
            this.contextualID = null;
        }
        if(ori.hasContextualFunctionID()){
            this.contextualFunctionID = SerializationUtils.clone(ori.getContextualFunctionID());
        } else {
            this.contextualFunctionID = null;
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public boolean hasUniqueID(){
        boolean hasValue = this.uniqueID != null;
        return(hasValue);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public boolean hasDisplayID(){
        boolean hasValue = this.uniqueID != null;
        return(hasValue);
    }

    public String getDisplayID() {
        return displayID;
    }

    public void setDisplayID(String displayID) {
        this.displayID = displayID;
    }

    public boolean hasContextualID(){
        boolean hasValue = this.contextualID != null;
        return(hasValue);
    }

    public TopologyNodeFDN getContextualID() {
        return contextualID;
    }

    public void setContextualID(TopologyNodeFDN contextualID) {
        this.contextualID = contextualID;
    }

    public boolean hasContextualFunctionID(){
        boolean hasValue = this.contextualFunctionID != null;
        return(hasValue);
    }

    public TopologyNodeFunctionFDN getContextualFunctionID() {
        return contextualFunctionID;
    }

    public void setContextualFunctionID(TopologyNodeFunctionFDN contextualFunctionID) {
        this.contextualFunctionID = contextualFunctionID;
    }

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
        return "net.fhirfactory.pegacorn.common.model.componentid.ComponentID{" +
                "uniqueID='" + uniqueID + '\'' +
                ", displayID='" + displayID + '\'' +
                ", contextualID=" + contextualID +
                ", contextualFunctionID=" + contextualFunctionID +
                ", version='" + version + '\'' +
                '}';
    }
}
