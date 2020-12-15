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
public class CommunicateEcho extends CommunicatePegacornSubsystem {

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement communicateEcho = new DeploymentMapNodeElement();
        communicateEcho.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        communicateEcho.setElementVersion("0.0.1");
        communicateEcho.setInstanceName("Communicate-Echo");
        communicateEcho.setFunctionName("Communicate-Echo");
        communicateEcho.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        communicateEcho.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(communicateEcho);
        buildExternalisedServiceNode(communicateEcho);
    }

	public void buildExternalisedServiceNode(DeploymentMapNodeElement communicateNode) {
    	DeploymentMapNodeElement echoServer = new DeploymentMapNodeElement();
        echoServer.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        echoServer.setElementVersion(communicateNode.getElementVersion());
        echoServer.setInstanceName("Communicate-Echo");
        echoServer.setFunctionName("Communicate-Echo");
        echoServer.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        echoServer.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        echoServer.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        communicateNode.getContainedElements().add(echoServer);
    }

	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}

}
