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

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.components.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.components.mode.ConcurrencyModeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
public class NodeElement {

    private static final Logger LOG = LoggerFactory.getLogger(NodeElement.class);

    private String friendlyName;
    private Object friendlyNameLock;
    private NodeElementTypeEnum nodeArchetype;
    private Object nodeArchetypeLock;
    private ElementMapStatusEnum nodeMapStatus;
    private Object nodeMapStatusLock;
    private NodeElementIdentifier nodeInstanceID;
    private Object nodeInstanceIDLock;
    private FDNToken nodeFunctionID;
    private Object nodeFunctionIDLock;
    private HashSet<LinkElementIdentifier> links;
    private Object linksLock;
    private HashSet<EndpointElementIdentifier> endpoints;
    private Object endpointsLock;
    private HashSet<NodeElementIdentifier> containedElements;
    private Object containedElementsLock;
    private NodeElementIdentifier containingElementID;
    private Object containingElementIDLock;
    private String version;
    private Object versionLock;
    private ResilienceModeEnum resilienceMode;
    private Object resilienceModeLock;
    private ConcurrencyModeEnum concurrencyMode;
    private Object concurrencyModeLock;
    private HashMap<String, String> elementExtensions;
    private Object elementExtensionsLock;
    private boolean instanceInPlace;
    private Object instanceInPlaceLock;
    


    public NodeElement() {
        // 1st, clear the deck
        this.friendlyName = null;
        this.friendlyNameLock = new Object();
        this.nodeArchetype = null;
        this.nodeArchetypeLock = new Object();
        this.nodeMapStatus = null;
        this.nodeMapStatusLock = new Object();
        this.nodeInstanceID = null;
        this.nodeInstanceIDLock = new Object();
        this.nodeFunctionID = null;
        this.nodeFunctionIDLock = new Object();
        this.links = null;
        this.linksLock = new Object();
        this.endpoints = null;
        this.endpointsLock = new Object();
        this.containedElements = null;
        this.containedElementsLock = new Object();
        this.containingElementID = null;
        this.containingElementIDLock = new Object();
        this.version = null;
        this.versionLock = new Object();
        this.resilienceMode = null;
        this.resilienceModeLock = new Object();
        this.concurrencyMode = null;
        this.concurrencyModeLock = new Object();
        this.elementExtensions = null;
        this.elementExtensionsLock = new Object();
        this.instanceInPlace = false;
        this.instanceInPlaceLock = new Object();
        // Now establish the basic structure
        this.endpoints = new HashSet<EndpointElementIdentifier>();
        this.links = new HashSet<LinkElementIdentifier>();
        this.containedElements = new HashSet<NodeElementIdentifier>();
        this.elementExtensions = new HashMap<String, String>();
        this.nodeInstanceID = null;
        this.nodeMapStatus = null;
        this.nodeArchetype = null;
        this.instanceInPlace = false;
    }

    public NodeElement(NodeElementIdentifier elementInstanceID) {
        // 1st, clear the deck
        this.nodeArchetype = null;
        this.nodeArchetypeLock = new Object();
        this.nodeMapStatus = null;
        this.nodeMapStatusLock = new Object();
        this.nodeInstanceID = null;
        this.nodeInstanceIDLock = new Object();
        this.nodeFunctionID = null;
        this.nodeFunctionIDLock = new Object();
        this.links = null;
        this.linksLock = new Object();
        this.endpoints = null;
        this.endpointsLock = new Object();
        this.containedElements = null;
        this.containedElementsLock = new Object();
        this.containingElementID = null;
        this.containingElementIDLock = new Object();
        this.version = null;
        this.versionLock = new Object();
        this.resilienceMode = null;
        this.resilienceModeLock = new Object();
        this.concurrencyMode = null;
        this.concurrencyModeLock = new Object();
        this.elementExtensions = null;
        this.elementExtensionsLock = new Object();
        this.instanceInPlace = false;
        this.instanceInPlaceLock = new Object();
        // Now establish the basic structure
        this.endpoints = new HashSet<EndpointElementIdentifier>();
        this.links = new HashSet<LinkElementIdentifier>();
        this.containedElements = new HashSet<NodeElementIdentifier>();
        this.elementExtensions = new HashMap<String, String>();
        this.nodeInstanceID = elementInstanceID;
        this.concurrencyMode = ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE;
        this.resilienceMode = resilienceMode.RESILIENCE_MODE_STANDALONE;
        this.nodeMapStatus = ElementMapStatusEnum.NOT_INSTANTIATED;
        this.version = "0.0.0";
        this.instanceInPlace = false;
        FDN nodeFDN = new FDN(elementInstanceID);
        this.friendlyName = nodeFDN.getUnqualifiedRDN().getValue();
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public NodeElementIdentifier getContainingElementID() {
        return containingElementID;
    }

    public void setContainingElementID(NodeElementIdentifier containingElementID) {
        synchronized (this.containingElementIDLock) {
            this.containingElementID = containingElementID;
        }
    }

    public NodeElementTypeEnum getNodeArchetype() {
        return nodeArchetype;
    }

    public void setNodeArchetype(NodeElementTypeEnum nodeArchetype) {
        synchronized (this.nodeArchetypeLock) {
            this.nodeArchetype = nodeArchetype;
        }
    }

    public ElementMapStatusEnum getNodeMapStatus() {
        return nodeMapStatus;
    }

    public void setNodeMapStatus(ElementMapStatusEnum nodeMapStatus) {
        synchronized (nodeMapStatusLock) {
            this.nodeMapStatus = nodeMapStatus;
        }
    }

    public NodeElementIdentifier getNodeInstanceID() {
        return nodeInstanceID;
    }

    public void setNodeInstanceID(NodeElementIdentifier nodeInstanceID) {
        synchronized (nodeInstanceIDLock) {
            this.nodeInstanceID = nodeInstanceID;
        }
    }

    public Set<LinkElementIdentifier> getLinks() {
        return links;
    }

    public void setLinks(HashSet<LinkElementIdentifier> links) {
        synchronized (linksLock) {
            this.links = links;
        }
    }

    public Set<EndpointElementIdentifier> getEndpoints() {
        return endpoints;
    }

    public boolean hasEndpoints() {
        if (this.endpoints == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public void setEndpoints(HashSet<EndpointElementIdentifier> endpoints) {
        synchronized (endpointsLock) {
            this.endpoints = endpoints;
        }
    }

    public void addEndpoint(EndpointElementIdentifier newEndpoint) {
        synchronized (endpointsLock) {
            this.endpoints.add(newEndpoint);
        }
    }

    public void removeEndpoint(EndpointElementIdentifier containedEndpoint) {
        synchronized (endpointsLock) {
            if (hasEndpoints()) {
                this.endpoints.remove(containedEndpoint);
            }
        }
    }

    public ResilienceModeEnum getResilienceMode() {
        return resilienceMode;
    }

    public void setResilienceMode(ResilienceModeEnum resilienceMode) {
        synchronized (resilienceModeLock) {
            this.resilienceMode = resilienceMode;
        }
    }

    public ConcurrencyModeEnum getConcurrencyMode() {
        return concurrencyMode;
    }

    public void setConcurrencyMode(ConcurrencyModeEnum concurrencyMode) {
        synchronized (concurrencyModeLock) {
            this.concurrencyMode = concurrencyMode;
        }
    }

    public boolean hasContainedElements() {
        if (this.containedElements == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public Set<NodeElementIdentifier> getContainedElements() {
        return (this.containedElements);
    }

    public void setContainedElements(HashSet<NodeElementIdentifier> newContainedElementSet) {
        synchronized (containedElementsLock) {
            this.containedElements = newContainedElementSet;
        }
    }

    public void addContainedElement(NodeElementIdentifier newContainedElement) {
        synchronized (containedElementsLock) {
            this.containedElements.add(newContainedElement);
        }
    }

    public void removeContainedElement(NodeElementIdentifier containedElementToBeRemoved) {
        synchronized (containedElementsLock) {
            if (hasContainedElements()) {
                this.containedElements.remove(containedElementToBeRemoved);
            }
        }
    }

    public FDNToken getNodeFunctionID() {
        return nodeFunctionID;
    }

    public void setNodeFunctionID(FDNToken nodeFunctionID) {
        synchronized(nodeFunctionIDLock) {
            this.nodeFunctionID = nodeFunctionID;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        synchronized (versionLock) {
            this.version = version;
        }
    }

    public Map<String, String> getElementExtensions() {
        return elementExtensions;
    }

    public void setElementExtensions(HashMap<String, String> elementExtensions) {
        synchronized (elementExtensionsLock) {
            this.elementExtensions = elementExtensions;
        }
    }

    public boolean isInstanceInPlace() {
        return instanceInPlace;
    }

    public void setInstanceInPlace(boolean instanceInPlace) {
        this.instanceInPlace = instanceInPlace;
    }
    
    public NodeElementFunctionToken getNodeFunctionToken(){
        NodeElementFunctionToken token = new NodeElementFunctionToken();
        token.setFunctionID(this.getNodeFunctionID());
        token.setVersion(this.getVersion());
        return(token);        
    }

    @Override
    public String toString() {
        return "NodeElement{" +
                "(nodeArchetype=" + nodeArchetype + ")," +
                "(nodeMapStatus=" + nodeMapStatus + ")," +
                "(nodeInstanceID=" + nodeInstanceID + ")," +
                "(nodeFunctionID=" + nodeFunctionID + ")," +
                "(links=" + links + ")," +
                "(endpoints=" + endpoints + ")," +
                "(containedElements=" + containedElements + ")," +
                "(containingElementID=" + containingElementID + ")," +
                "(version='" + version + '\'' + ")," +
                "(resilienceMode=" + resilienceMode + ")," +
                "(concurrencyMode=" + concurrencyMode + ")," +
                "(elementExtensions=" + elementExtensions + ")," +
                "(instanceInPlace=" + instanceInPlace +")" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeElement that = (NodeElement) o;
        return isInstanceInPlace() == that.isInstanceInPlace() &&
                getNodeArchetype() == that.getNodeArchetype() &&
                getNodeMapStatus() == that.getNodeMapStatus() &&
                Objects.equals(getNodeInstanceID(), that.getNodeInstanceID()) &&
                Objects.equals(getNodeFunctionID(), that.getNodeFunctionID()) &&
                Objects.equals(getLinks(), that.getLinks()) &&
                Objects.equals(getEndpoints(), that.getEndpoints()) &&
                Objects.equals(getContainedElements(), that.getContainedElements()) &&
                Objects.equals(getContainingElementID(), that.getContainingElementID()) &&
                Objects.equals(getVersion(), that.getVersion()) &&
                getResilienceMode() == that.getResilienceMode() &&
                getConcurrencyMode() == that.getConcurrencyMode() &&
                Objects.equals(getElementExtensions(), that.getElementExtensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeArchetype(), getNodeMapStatus(), getNodeInstanceID(), getNodeFunctionID(), getLinks(), getEndpoints(), getContainedElements(), getContainingElementID(), getVersion(), getResilienceMode(), getConcurrencyMode(), getElementExtensions(), isInstanceInPlace());
    }
    
    public String extractNodeKey() {
    	String key =  this.getNodeInstanceID().toTag() + "." + getVersion();
    	return(key);
    }


    public List<String> debugPrint(String linePrefix) {
    	ArrayList<String> stringList = new ArrayList<String>();
		stringList.add(linePrefix + " (NodeElement).nodeArchetype (NodeElementTypeEnum) --> " + nodeArchetype);
		stringList.add(linePrefix + " (NodeElement).nodeMapStatus (ElementMapStatusEnum) --> " + nodeMapStatus);
		stringList.add(linePrefix + " (NodeElement).nodeInstanceID (NodeElementIdentifier) --> " + nodeInstanceID);
		stringList.add(linePrefix + " (NodeElement).nodeFunctionID (FDNToken) --> " + nodeFunctionID);
		stringList.add(linePrefix + " (NodeElement).links (Map<LinkElementIdentifier>) --> " + links);
		stringList.add(linePrefix + " (NodeElement).endpoints (Map<EndpointElementIdentifier>) --> " + endpoints);
		stringList.add(linePrefix + " (NodeElement).containedElements (Map<NodeElementIdentifier>) --> " + containedElements);
		stringList.add(linePrefix + " (NodeElement).containingElementID (NodeElementIdentifier) --> " + containingElementID);
		stringList.add(linePrefix + " (NodeElement).version (String) --> " + version);
		stringList.add(linePrefix + " (NodeElement).resilienceMode (ResilienceModeEnum) --> " + resilienceMode);
		stringList.add(linePrefix + " (NodeElement).concurrencyMode (ConcurrencyModeEnum) --> " + concurrencyMode);
		stringList.add(linePrefix + " (NodeElement).elementExtensions (HashMap<String, String>) --> " + "Not Printed"); // elementExtensions + "),"
		stringList.add(linePrefix + " (NodeElement).instanceInPlace (boolean) --> " + instanceInPlace);
		return(stringList);
    }
}
