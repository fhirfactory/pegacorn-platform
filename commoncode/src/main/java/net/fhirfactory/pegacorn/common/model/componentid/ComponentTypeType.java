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

public class ComponentTypeType implements Serializable {
    private String typeName;
    private String displayTypeName;
    private ComponentTypeTypeEnum componentArchetype;
    private String version;

    //
    // Constructor(s)
    //

    public ComponentTypeType(){
        this.typeName = null;
        this.displayTypeName = null;
        this.version = null;
        this.componentArchetype = null;
    }

    public ComponentTypeType(ComponentTypeType ori){
        this.typeName = null;
        this.displayTypeName = null;
        this.version = null;
        this.componentArchetype = null;
        if(ori.hasComponentArchetype()){
            setComponentArchetype(ori.getComponentArchetype());
        }
        if(ori.hasTypeName()){
            setTypeName(SerializationUtils.clone(ori.getTypeName()));
        }
        if(ori.hasDisplayTypeName()){
            setDisplayTypeName(SerializationUtils.clone(ori.getDisplayTypeName()));
        }
        if(ori.hasVersion()){
            setVersion(SerializationUtils.clone(ori.getVersion()));
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasTypeName(){
        boolean hasValue = this.typeName != null;
        return(hasValue);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @JsonIgnore
    public boolean hasDisplayTypeName(){
        boolean hasValue = this.displayTypeName != null;
        return(hasValue);
    }

    public String getDisplayTypeName() {
        return displayTypeName;
    }

    public void setDisplayTypeName(String displayTypeName) {
        this.displayTypeName = displayTypeName;
    }

    @JsonIgnore
    public boolean hasComponentArchetype(){
        boolean hasValue = this.componentArchetype != null;
        return(hasValue);
    }

    public ComponentTypeTypeEnum getComponentArchetype() {
        return componentArchetype;
    }

    public void setComponentArchetype(ComponentTypeTypeEnum componentArchetype) {
        this.componentArchetype = componentArchetype;
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
        return "ComponentTypeType{" +
                "typeName=" + typeName +
                ", displayTypeName=" + displayTypeName +
                ", componentArchetype=" + componentArchetype +
                ", version=" + version +
                '}';
    }
}
