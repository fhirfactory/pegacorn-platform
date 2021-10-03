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
package net.fhirfactory.pegacorn.deployment.topology.map.common.standalone.hestia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.deployment.topology.map.common.archetypes.common.PegacornTopologyFactoryBase;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.map.common.model.DeploymentMapNodeElement;

/**
 *
 * @author Mark A. Hunter
 */
public class HestiaAudit extends PegacornTopologyFactoryBase {

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement hestiaAuditNode = new DeploymentMapNodeElement();
        hestiaAuditNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        hestiaAuditNode.setElementVersion("0.0.1");
        hestiaAuditNode.setInstanceName("Hestia-Audit");
        hestiaAuditNode.setFunctionName("Hestia-Audit");
        hestiaAuditNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        hestiaAuditNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(hestiaAuditNode);
        buildExternalisedServiceNode(hestiaAuditNode);
    }
   	public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {

   		DeploymentMapNodeElement hestiaAuditExternalisedService = new DeploymentMapNodeElement();
        hestiaAuditExternalisedService.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        hestiaAuditExternalisedService.setElementVersion(subsystem.getElementVersion());
        hestiaAuditExternalisedService.setInstanceName("Hestia-DigitalAssetManager");
        hestiaAuditExternalisedService.setFunctionName("Hestia-DigitalAssetManager");
        hestiaAuditExternalisedService.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        hestiaAuditExternalisedService.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
        hestiaAuditExternalisedService.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(hestiaAuditExternalisedService);
    }

	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}
}
