package net.fhirfactory.pegacorn.petasos.core.moa.wup;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCEndpoint;

public class GenericMessageBasedWUPEndpoint {
    private IPCEndpoint endpointTopologyNode;
    private String endpointSpecification;
    private boolean frameworkEnabled;

    public IPCEndpoint getEndpointTopologyNode() {
        return endpointTopologyNode;
    }

    public void setEndpointTopologyNode(IPCEndpoint endpointTopologyNode) {
        this.endpointTopologyNode = endpointTopologyNode;
    }

    public String getEndpointSpecification() {
        return endpointSpecification;
    }

    public void setEndpointSpecification(String endpointSpecification) {
        this.endpointSpecification = endpointSpecification;
    }

    public boolean isFrameworkEnabled() {
        return frameworkEnabled;
    }

    public void setFrameworkEnabled(boolean frameworkEnabled) {
        this.frameworkEnabled = frameworkEnabled;
    }
}
