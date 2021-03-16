/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.deployment.topology.map.common.standalone.fhirpit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.deployment.topology.map.common.archetypes.FHIRPitSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.deployment.topology.map.common.model.DeploymentMapNodeElement;
import net.fhirfactory.pegacorn.components.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.components.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;

/**
 *
 * @author Mark A. Hunter
 */
public class FHIRPitServices extends FHIRPitSubsystemTopologyFactory {

	// TODO Fix the port number allocations for Services 

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement fhirpitNode = new DeploymentMapNodeElement();
        fhirpitNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        fhirpitNode.setElementVersion("0.0.1");
        fhirpitNode.setInstanceName("FHIRPit");
        fhirpitNode.setFunctionName("FHIRPit");
        fhirpitNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        fhirpitNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(fhirpitNode);
        buildExternalisedServiceNode(fhirpitNode);
    }
	public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {
        DeploymentMapNodeElement externalisedService = new DeploymentMapNodeElement();
        externalisedService.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        externalisedService.setElementVersion(subsystem.getElementVersion());
        externalisedService.setInstanceName("FHIRPit-Default");
        externalisedService.setFunctionName("FHIRPit-Default");
        externalisedService.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        externalisedService.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        externalisedService.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(externalisedService);
    }

	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}
}
