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
package net.fhirfactory.pegacorn.deployment.topology.map.standalone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.fhirfactory.pegacorn.deployment.names.PegacornLadonComponentNames;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.communicate.CommunicateIris;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.fhirpit.FHIRPitServices;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.fhirplace.FHIRPlaceServices;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.fhirview.FHIRViewServices;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.hestia.HestiaAudit;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.hestia.HestiaDAM;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.ladon.LadonServices;
import net.fhirfactory.pegacorn.deployment.topology.map.archetypes.SolutionDeploymentTopology;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapNodeElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter
 */

@ApplicationScoped
public class StandaloneSampleDeploymentSolution extends SolutionDeploymentTopology {
	private static final Logger LOG = LoggerFactory.getLogger(StandaloneSampleDeploymentSolution.class);
	
	private static String SOLUTION_VERSION = "1.0.0";
	private static String SOLUTION_NAME = "SampleStandaloneSolution";

	@Inject
	private PegacornLadonComponentNames subsystemComponentNames;
	
	public StandaloneSampleDeploymentSolution() {
		super();
	}

	@Override
	public void initialiseDeploymentTopology(){
		LOG.info(".initialiseDeploymentTopology(): Entry");
		initialiseServices();
		LOG.info(".initialiseDeploymentTopology(): Exit");
	}

	@Override
	protected String specifySolutionName() {
		return (SOLUTION_NAME);
	}

	@Override
	protected String specifySolutionVersion() {
		return (SOLUTION_VERSION);
	}

	@Override
	protected ResilienceModeEnum specifyResilienceMode() {
		return (ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
	}

	@Override
	protected void specifySubsystems(DeploymentMapNodeElement solutionNode) {
	//	buildCommunicate();
		buildLadon();
	}


	protected void buildCommunicate() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName("Communicate");
		subsystem.setFunctionName("Communicate");
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyCommunicateExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	protected void buildLadon() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName(subsystemComponentNames.getLadonSubsystemDefault());
		subsystem.setFunctionName(subsystemComponentNames.getLadonSubsystemDefault());
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyLadonExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	/*
	private void buildHestia() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName("Hestia");
		subsystem.setFunctionName("Hestia");
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyHestiaExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	private void buildFHIRPlace() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName("FHIRPlace");
		subsystem.setFunctionName("FHIRPlace");
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyFHIRPlaceExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	private void buildFHIRPit() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName("FHIRPit");
		subsystem.setFunctionName("FHIRPit");
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyFHIRPitExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	private void buildFHIRView() {
		DeploymentMapNodeElement subsystem = new DeploymentMapNodeElement();
		subsystem.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
		subsystem.setElementVersion(this.getSolutionVersion());
		subsystem.setInstanceName("FHIRView");
		subsystem.setFunctionName("FHIRView");
		subsystem.setResilienceMode(this.getSolutionMode());
		subsystem.setTopologyElementType(NodeElementTypeEnum.SUBSYSTEM);
		subsystem.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
		specifyFHIRViewExternalisedServices(subsystem);
		this.getSolution().getContainedElements().add(subsystem);
	}

	 */

	protected void specifyCommunicateExternalisedServices(DeploymentMapNodeElement subsystem) {
		/*
		CommunicateAVConfSvr communicateConfSvrExternalisedService = new CommunicateAVConfSvr();
		communicateConfSvrExternalisedService.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(communicateConfSvrExternalisedService.buildConnectedSystemSet());
		
		CommunicateEcho communicateEchoExternalisedService = new CommunicateEcho();
		communicateEchoExternalisedService.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(communicateEchoExternalisedService.buildConnectedSystemSet());	
		
		CommunicateGrpSvr communicateGrpSvrExternalisedService = new CommunicateGrpSvr();
		communicateGrpSvrExternalisedService.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(communicateGrpSvrExternalisedService.buildConnectedSystemSet());

		 */

		CommunicateIris communicateIrisExternalisedServce = new CommunicateIris();
		communicateIrisExternalisedServce.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(communicateIrisExternalisedServce.buildConnectedSystemSet());
		/*
		
		CommunicateVoIPBridge communicateVoIPBridgeExternalisedService = new CommunicateVoIPBridge();
		communicateVoIPBridgeExternalisedService.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(communicateVoIPBridgeExternalisedService.buildConnectedSystemSet());

		 */
		
	}

	protected void specifyMITaFExternalisedServices(DeploymentMapNodeElement subsystem) {

	}

	protected void specifyFHIRPlaceExternalisedServices(DeploymentMapNodeElement subsystem) {
		FHIRPlaceServices fhirplaceExternalisedServices = new FHIRPlaceServices();
		fhirplaceExternalisedServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(fhirplaceExternalisedServices.buildConnectedSystemSet());		
	}

	protected void specifyFHIRPitExternalisedServices(DeploymentMapNodeElement subsystem) {
		FHIRPitServices fhirpitExternalisedServices = new FHIRPitServices();
		fhirpitExternalisedServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(fhirpitExternalisedServices.buildConnectedSystemSet());			
	}

	protected void specifyFHIRViewExternalisedServices(DeploymentMapNodeElement subsystem) {
		FHIRViewServices fhirviewExternalisedServices = new FHIRViewServices();
		fhirviewExternalisedServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(fhirviewExternalisedServices.buildConnectedSystemSet());		
	}
	

	protected void specifyLadonExternalisedServices(DeploymentMapNodeElement subsystem) {
		LadonServices ladonExternalisedServices = new LadonServices();
		ladonExternalisedServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(ladonExternalisedServices.buildConnectedSystemSet());				
	}

	protected void specifyHestiaExternalisedServices(DeploymentMapNodeElement subsystem) {
		HestiaAudit hestiaAuditExternalisedServices = new HestiaAudit();
		hestiaAuditExternalisedServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(hestiaAuditExternalisedServices.buildConnectedSystemSet());	
		
		HestiaDAM hestiaDAMExternalServices = new HestiaDAM();
		hestiaDAMExternalServices.buildExternalisedServiceNode(subsystem);
		this.getConnectedSubsystems().addAll(hestiaDAMExternalServices.buildConnectedSystemSet());	
	}

	@Override
	protected Set<DeploymentMapNodeElement> specifyConnectedSystems() {
		
		HashSet<DeploymentMapNodeElement> connectedSystemSet = new HashSet<DeploymentMapNodeElement>();
		
		// Communicate Connected Systems
		//CommunicateVoIPBridge communicateVoIPBridgeExternalisedService = new CommunicateVoIPBridge();
		CommunicateIris communicateIrisExternalisedServce = new CommunicateIris();
		//CommunicateGrpSvr communicateGrpSvrExternalisedService = new CommunicateGrpSvr();
		//CommunicateEcho communicateEchoExternalisedService = new CommunicateEcho();
		//CommunicateAVConfSvr communicateConfSvrExternalisedService = new CommunicateAVConfSvr();
		//connectedSystemSet.addAll(communicateVoIPBridgeExternalisedService.buildConnectedSystemSet());
		connectedSystemSet.addAll(communicateIrisExternalisedServce.buildConnectedSystemSet());
		//connectedSystemSet.addAll(communicateGrpSvrExternalisedService.buildConnectedSystemSet());
		//connectedSystemSet.addAll(communicateEchoExternalisedService.buildConnectedSystemSet());
		//connectedSystemSet.addAll(communicateConfSvrExternalisedService.buildConnectedSystemSet());
		
		
		// MITaF Connected Systems


		// FHIRPlace Connected Systems
		//FHIRPlaceServices fhirplaceExternalisedServices = new FHIRPlaceServices();
		//connectedSystemSet.addAll(fhirplaceExternalisedServices.buildConnectedSystemSet());
		
		// FHIRPit Connected Systems
		//FHIRPitServices fhirpitExternalisedServices = new FHIRPitServices();
		//connectedSystemSet.addAll(fhirpitExternalisedServices.buildConnectedSystemSet());
		
		// FHIRView Connected Systems
		//FHIRViewServices fhirviewExternalisedServices = new FHIRViewServices();
		//connectedSystemSet.addAll(fhirviewExternalisedServices.buildConnectedSystemSet());

		//Ladon Connected Systems
		LadonServices ladonExternalisedServices = new LadonServices();
		connectedSystemSet.addAll(ladonExternalisedServices.buildConnectedSystemSet());

		// Hestia Connected Systems
		//HestiaDAM hestiaDAMExternalServices = new HestiaDAM();
		//HestiaAudit hestiaAuditExternalisedServices = new HestiaAudit();
		//connectedSystemSet.addAll(hestiaAuditExternalisedServices.buildConnectedSystemSet());
		//connectedSystemSet.addAll(hestiaDAMExternalServices.buildConnectedSystemSet());

		return(connectedSystemSet);
	}

        public boolean hasBeenInitialised(){
            return(this.getIsInitialised());
        }

}
