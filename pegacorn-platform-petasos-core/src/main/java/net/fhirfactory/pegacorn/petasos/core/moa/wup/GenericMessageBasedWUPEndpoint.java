package net.fhirfactory.pegacorn.petasos.core.moa.wup;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PegacornStandardEndpoint;

public class GenericMessageBasedWUPEndpoint {
    private PegacornStandardEndpoint endpointTopologyNode;
    private String endpointSpecification;
    private boolean frameworkEnabled;

    public PegacornStandardEndpoint getEndpointTopologyNode() {
        return endpointTopologyNode;
    }

    public void setEndpointTopologyNode(PegacornStandardEndpoint endpointTopologyNode) {
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
