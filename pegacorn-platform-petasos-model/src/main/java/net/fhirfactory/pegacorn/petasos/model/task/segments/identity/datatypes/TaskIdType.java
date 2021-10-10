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
package net.fhirfactory.pegacorn.petasos.model.task.segments.identity.datatypes;

import java.io.Serializable;

public class TaskIdType implements Serializable {
    private String implementationIdentifier;
    private String version;

    private static final String DEFAULT_VERSION="1";

    //
    // Constructors
    //

    public TaskIdType(){
        this.implementationIdentifier = null;
        this.version = DEFAULT_VERSION;
    }

    public TaskIdType(TaskIdType ori){
        if(ori.hasLocalIdentifier()) {
            this.implementationIdentifier = ori.getImplementationIdentifier();
        } else {
            this.implementationIdentifier = null;
        }
        if(ori.hasVersion()) {
            this.version = ori.getVersion();
        } else {
            this.version = DEFAULT_VERSION;
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public boolean hasLocalIdentifier(){
        boolean hasValue = this.implementationIdentifier != null;
        return(hasValue);
    }

    public String getImplementationIdentifier() {
        return implementationIdentifier;
    }

    public void setImplementationIdentifier(String implementationIdentifier) {
        this.implementationIdentifier = implementationIdentifier;
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
        return "IdentitySegment{" +
                "implementationIdentifier=" + implementationIdentifier +
                ", version=" + version +
                '}';
    }
}
