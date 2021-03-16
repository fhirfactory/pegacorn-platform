/*
 * Copyright (c) 2020 MAHun
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointElement {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointElement.class);

    private EndpointElementTypeEnum endpointType;
    private Object endpointTypeLock;
    private ElementMapStatusEnum topologyElementStatus;
    private Object topologyElementStatusLock;
    private EndpointElementIdentifier endpointInstanceID;
    private Object endpointInstanceIDLock;
    private FDNToken endpointFunctionID;
    private Object endpointFunctionIDLock;
    private NodeElementIdentifier containingNodeID;
    private Object containingNodeIDLock;
    private String friendlyName;
    private Object friendlyNameLock;
    private String internalHostname;
    private Object internalHostnameLock;
    private String exposedHostname;
    private Object exposedHostnameLock;
    private String internalPort;
    private Object internalPortLock;
    private String exposedPort;
    private Object exposedPortLock;
    private boolean isServer;
    private Object isServerLock;
    private String toStringString;
    private Object toStringStringLock;
    private ElementDerivationTypeEnum derivedType;
    private Object derivedTypeLock;
    private String version;
    private Object versionLock;
    private boolean utilisingSecurity;
    private Object utilisingSecurityLock;

    public EndpointElement() {
        this.endpointFunctionID = null;
        this.toStringString = null;
        this.containingNodeID = null;
        this.endpointInstanceID = null;
        this.endpointType = null;
        this.friendlyName = null;
        this.internalHostname = null;
        this.exposedHostname = null;
        this.isServer = false;
        this.internalPort = null;
        this.topologyElementStatus = null;
        this.derivedType = null;
        this.exposedPort = null;
        this.version = null;
        this.utilisingSecurity = false;
        this.endpointTypeLock = new Object();
        this.endpointFunctionIDLock = new Object();
        this.topologyElementStatusLock = new Object();
        this.endpointInstanceIDLock = new Object();
        this.containingNodeIDLock = new Object();
        this.friendlyNameLock = new Object();
        this.internalHostnameLock = new Object();
        this.exposedHostnameLock = new Object();
        this.internalPortLock = new Object();
        this.isServerLock = new Object();
        this.toStringStringLock = new Object();
        this.derivedTypeLock = new Object();
        this.exposedPortLock = new Object();
        this.versionLock = new Object();
        this.utilisingSecurityLock = new Object();
    }

    public EndpointElementTypeEnum getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointElementTypeEnum endpointType) {
        synchronized (endpointTypeLock) {
            this.endpointType = endpointType;
            generateToString();
        }
    }

    public ElementMapStatusEnum getTopologyElementStatus() {
        return topologyElementStatus;
    }

    public void setTopologyElementStatus(ElementMapStatusEnum topologyElementStatus) {
        synchronized (topologyElementStatusLock) {
            this.topologyElementStatus = topologyElementStatus;
            generateToString();
        }
    }

    public EndpointElementIdentifier getEndpointInstanceID() {
        return endpointInstanceID;
    }

    public void setEndpointInstanceID(EndpointElementIdentifier endpointInstanceID) {
        synchronized (endpointInstanceIDLock) {
            this.endpointInstanceID = endpointInstanceID;
            FDN endpointFDN = new FDN(endpointInstanceID);
            this.setFriendlyName(endpointFDN.getUnqualifiedRDN().getValue());
            generateToString();
        }
    }

    public FDNToken getEndpointFunctionID() {
        return endpointFunctionID;
    }

    public void setEndpointFunctionID(FDNToken endpointFunctionID) {
        synchronized (endpointFunctionIDLock) {
            this.endpointFunctionID = endpointFunctionID;
            generateToString();
        }
    }

    public NodeElementIdentifier getContainingNodeID() {
        return containingNodeID;
    }

    public void setContainingNodeID(NodeElementIdentifier containingNodeID) {
        synchronized (containingNodeIDLock) {
            this.containingNodeID = containingNodeID;
            generateToString();
        }
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        synchronized (friendlyNameLock) {
            this.friendlyName = friendlyName;
            generateToString();
        }
    }

    public String getInternalHostname() {
        return internalHostname;
    }

    public void setInternalHostname(String internalHostname) {
        synchronized (internalHostnameLock) {
            this.internalHostname = internalHostname;
            generateToString();
        }
    }

    public String getExposedHostname() {
        return exposedHostname;
    }

    public void setExposedHostname(String hostname) {
        synchronized (exposedHostnameLock) {
            this.exposedHostname = hostname;
            generateToString();
        }
    }

    public String getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(String port) {
        synchronized (internalPortLock) {
            this.internalPort = port;

            generateToString();
        }
    }
    
    public String getExposedPort() {
    	return(this.exposedPort);
    }
    
    public void setExposedPort(String port) {
    	synchronized(exposedPortLock) {
    		this.exposedPort = port;
    		generateToString();
    	}
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        synchronized (isServerLock) {
            isServer = server;
            generateToString();
        }
    }

    @Override
    public String toString() {
        return (this.toStringString);
    }

    private void generateToString() {
        synchronized (this.toStringStringLock) {
            this.toStringString = "EndpointElement{" +
                    "(endpointType=" + endpointType + ")," +
                    "(topologyElementStatus=" + topologyElementStatus + ")," +
                    "(endpointInstanceID=" + endpointInstanceID + ")," +
                    "(endpointFunctionID=" + endpointFunctionID + ")," +
                    "(containingNodeID=" + containingNodeID + ")," +
                    "(friendlyName=" + friendlyName + ")," +
                    "(hostname=" + internalHostname + ")," +
                    "(internalPort=" + internalPort + ")," +
                    "(exposedPort=" + exposedPort + ")," +
                    "(isServer=" + isServer + ")" +
                    '}';
        }
    }

    public boolean isUtilisingSecurity() {
        return utilisingSecurity;
    }

    public void setUtilisingSecurity(boolean utilisingSecurity) {
        synchronized (this.utilisingSecurityLock) {
            this.utilisingSecurity = utilisingSecurity;
        }
    }

    public ElementDerivationTypeEnum getDerivedType() {
        return derivedType;
    }

    public void setDerivedType(ElementDerivationTypeEnum derivedType) {
        synchronized (this.derivedTypeLock) {
            this.derivedType = derivedType;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        synchronized (this.versionLock) {
            this.version = version;
        }
    }
}
