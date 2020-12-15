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
package net.fhirfactory.pegacorn.deployment.topology.map.standalone.hestia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.deployment.topology.map.archetypes.HestiaPegacornSubsystem;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapNodeElement;

/**
 *
 * @author Mark A. Hunter
 */
public class HestiaDAM extends HestiaPegacornSubsystem {


    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement hestiaDAMNode = new DeploymentMapNodeElement();
        hestiaDAMNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        hestiaDAMNode.setElementVersion("0.0.1");
        hestiaDAMNode.setInstanceName("Hestia-DAM");
        hestiaDAMNode.setFunctionName("Hestia-DAM");
        hestiaDAMNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        hestiaDAMNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(hestiaDAMNode);
        buildExternalisedServiceNode(hestiaDAMNode);
    }
   	public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {

   		DeploymentMapNodeElement hestiaDAMInstance = new DeploymentMapNodeElement();
        hestiaDAMInstance.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        hestiaDAMInstance.setElementVersion(subsystem.getElementVersion());
        hestiaDAMInstance.setInstanceName("Hestia-DigitalAssetManager");
        hestiaDAMInstance.setFunctionName("Hestia-DigitalAssetManager");
        hestiaDAMInstance.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        hestiaDAMInstance.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
        hestiaDAMInstance.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(hestiaDAMInstance);
    }

	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}
}
