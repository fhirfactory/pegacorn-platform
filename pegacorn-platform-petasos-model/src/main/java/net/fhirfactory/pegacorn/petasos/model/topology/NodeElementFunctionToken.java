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
package net.fhirfactory.pegacorn.petasos.model.topology;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.common.model.FDN;
import net.fhirfactory.pegacorn.common.model.RDN;

/**
 *
 * @author Mark Hunter
 */
public class NodeElementFunctionToken {

    private net.fhirfactory.pegacorn.common.model.FDNToken functionID;
    private String version;

    public NodeElementFunctionToken(net.fhirfactory.pegacorn.common.model.FDNToken nodeFunctionID, String nodeVersion) {
        this.functionID = nodeFunctionID;
        this.version = nodeVersion;
    }

    public NodeElementFunctionToken() {
        this.functionID = null;
        this.version = null;
    }

    public NodeElementFunctionToken(NodeElementFunctionToken originalToken) {
        this.functionID = originalToken.getFunctionID();
        this.version = originalToken.getVersion();
    }

    public net.fhirfactory.pegacorn.common.model.FDNToken getFunctionID() {
        return functionID;
    }

    public void setFunctionID(net.fhirfactory.pegacorn.common.model.FDNToken identifier) {
        this.functionID = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String toString = "{NodeElementFunctionToken=("
                + this.functionID.toString() + "),("
                + this.version + ")}";
        return (toString);
    }

    @JsonIgnore
    public net.fhirfactory.pegacorn.common.model.FDNToken getAsSingleFDNToken() {
        FDN functionFDN = new FDN(this.functionID);
        functionFDN.appendRDN(new RDN("Version", this.version));
        return (functionFDN.getToken());
    }
}
