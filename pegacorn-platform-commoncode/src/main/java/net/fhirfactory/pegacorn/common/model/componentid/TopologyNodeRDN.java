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
package net.fhirfactory.pegacorn.common.model.componentid;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class TopologyNodeRDN implements Serializable {
    private String nodeName;
    private String nodeVersion;
    private TopologyNodeTypeEnum nodeType;

    public TopologyNodeRDN(){
        this.nodeVersion = null;
        this.nodeName = null;
        this.nodeType = null;
    }

    public TopologyNodeRDN(TopologyNodeTypeEnum newNodeType, String newNodeName, String newNodeVersion){
        this.nodeName = newNodeName;
        this.nodeType = newNodeType;
        this.nodeVersion = newNodeVersion;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(String nodeVersion) {
        this.nodeVersion = nodeVersion;
    }

    public TopologyNodeTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(TopologyNodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    @JsonIgnore
    public String getTag(){
        String newTag = getNodeType().getNodeElementType() + "[" + getNodeName() + "(" + getNodeVersion() +")]";
        return(newTag);
    }

    @Override
    public String toString() {
        return "TopologyNodeRDN{" +
                "nodeName=" + nodeName +
                ", nodeVersion=" + nodeVersion +
                ", nodeType=" + nodeType +
                '}';
    }
}
