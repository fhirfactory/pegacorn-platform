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
package net.fhirfactory.pegacorn.deployment.topology.map.standalone.communicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.deployment.topology.map.archetypes.CommunicatePegacornSubsystem;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapNodeElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;

/**
 *
 * @author Mark A. Hunter
 */
public class CommunicateGrpSvr extends CommunicatePegacornSubsystem {

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement communicateGrpSvrNode = new DeploymentMapNodeElement();
        communicateGrpSvrNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        communicateGrpSvrNode.setElementVersion("0.0.1");
        communicateGrpSvrNode.setInstanceName("Communicate-GrpSvr");
        communicateGrpSvrNode.setFunctionName("Communicate-GrpSvr");
        communicateGrpSvrNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        communicateGrpSvrNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(communicateGrpSvrNode);
        buildExternalisedServiceNode(communicateGrpSvrNode);
    }
	public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {
		DeploymentMapNodeElement nodeGroupServer = new DeploymentMapNodeElement();
        nodeGroupServer.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeGroupServer.setElementVersion(subsystem.getElementVersion());
        nodeGroupServer.setInstanceName("Communicate-GroupServer");
        nodeGroupServer.setFunctionName("Communicate-GroupServer");
        nodeGroupServer.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeGroupServer.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
        nodeGroupServer.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(nodeGroupServer);
    }

 	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}

}
