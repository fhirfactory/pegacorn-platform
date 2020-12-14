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
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapEndpointElement;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapNodeElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.EndpointElementTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;

/**
 *
 * @author Mark A. Hunter
 */

public class CommunicateIris extends CommunicatePegacornSubsystem {

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement communicateIris = new DeploymentMapNodeElement();
        communicateIris.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        communicateIris.setElementVersion("0.0.1");
        communicateIris.setInstanceName("Communicate-Iris");
        communicateIris.setFunctionName("Communicate-Iris");
        communicateIris.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        communicateIris.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(communicateIris);
        buildExternalisedServiceNode(communicateIris);
    }
   	public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {
    	DeploymentMapNodeElement irisInstance = new DeploymentMapNodeElement();
        irisInstance.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        irisInstance.setElementVersion(subsystem.getElementVersion());
        irisInstance.setInstanceName("Communicate-Iris-ProcessingPlant");
        irisInstance.setFunctionName("Communicate-Iris-ProcessingPlant");
        irisInstance.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        irisInstance.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        irisInstance.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(irisInstance);
        createIrisSites(irisInstance);
    }

    public void createIrisSites(DeploymentMapNodeElement irisServiceNode) {
    	DeploymentMapNodeElement nodeSiteA = new DeploymentMapNodeElement();
        nodeSiteA.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeSiteA.setElementVersion(irisServiceNode.getFunctionName());
        nodeSiteA.setInstanceName("___");
        nodeSiteA.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeSiteA.setTopologyElementType(NodeElementTypeEnum.SITE);
        irisServiceNode.getContainedElements().add(nodeSiteA);
        createIrisServices(nodeSiteA);
        /*        
        ConfigMapNodeElement nodeSiteB = new ConfigMapNodeElement();
        nodeSiteB.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeSiteB.setElementVersion("0.0.1");
        nodeSiteB.setInstanceName("SiteB");
        nodeSiteB.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeSiteB.setTopologyElementType(NodeElementTypeEnum.SITE);
        irisServiceNode.getContainedElements().add(nodeSiteB);
        createIrisPlatforms(nodeSiteB); */
    }

    public void createIrisServices(DeploymentMapNodeElement irisSites) {
    	DeploymentMapNodeElement nodeIrisServiceSiteA = new DeploymentMapNodeElement();
        nodeIrisServiceSiteA.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeIrisServiceSiteA.setElementVersion(irisSites.getElementVersion());
        nodeIrisServiceSiteA.setInstanceName("Iris");
        nodeIrisServiceSiteA.setFunctionName("Iris");
        nodeIrisServiceSiteA.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeIrisServiceSiteA.setTopologyElementType(NodeElementTypeEnum.SERVICE);
        irisSites.getContainedElements().add(nodeIrisServiceSiteA);
        createIrisServiceEndpoints(nodeIrisServiceSiteA);
        createIrisPlatforms(nodeIrisServiceSiteA);
    }

    public void createIrisServiceEndpoints(DeploymentMapNodeElement irisServiceNode) {
        DeploymentMapEndpointElement endpointMatrixNotificationReceiver = new DeploymentMapEndpointElement();
        endpointMatrixNotificationReceiver.setEndpointInstanceID("MatrixNotificationsReceiver");
        endpointMatrixNotificationReceiver.setEndpointFunctionID("MatrixNotificationsReceiver");
        endpointMatrixNotificationReceiver.setEndpointType(EndpointElementTypeEnum.INGRES_MESSAGING);
        endpointMatrixNotificationReceiver.setExternalDNSEntry("gen0-iris");
        endpointMatrixNotificationReceiver.setExternalPortNumber("10700");
        endpointMatrixNotificationReceiver.setInternalPortNumber("10700");
        endpointMatrixNotificationReceiver.setIsServer(true);
        endpointMatrixNotificationReceiver.setRequiresEncryption(false);
        irisServiceNode.getEndpoints().add(endpointMatrixNotificationReceiver);

        DeploymentMapEndpointElement endpointIrisPetasosTopologySync = new DeploymentMapEndpointElement();
        endpointIrisPetasosTopologySync.setEndpointInstanceID("PetasosTopologySyncSvr");
        endpointIrisPetasosTopologySync.setEndpointFunctionID("PetasosTopologySyncSvr");
        endpointIrisPetasosTopologySync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointIrisPetasosTopologySync.setExternalDNSEntry("petasos-gen0-iris");
        endpointIrisPetasosTopologySync.setExternalPortNumber("10870");
        endpointIrisPetasosTopologySync.setInternalPortNumber("10870");
        endpointIrisPetasosTopologySync.setIsServer(true);
        endpointIrisPetasosTopologySync.setRequiresEncryption(false);
        irisServiceNode.getEndpoints().add(endpointIrisPetasosTopologySync);

        DeploymentMapEndpointElement endpointIrisPetasosParcelSync = new DeploymentMapEndpointElement();
        endpointIrisPetasosParcelSync.setEndpointInstanceID("PetasosResilienceParcelSyncSvr");
        endpointIrisPetasosParcelSync.setEndpointFunctionID("PetasosResilienceParcelSyncSvr");
        endpointIrisPetasosParcelSync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointIrisPetasosParcelSync.setExternalDNSEntry("petasos-gen0-iris");
        endpointIrisPetasosParcelSync.setExternalPortNumber("10872");
        endpointIrisPetasosParcelSync.setInternalPortNumber("10872");
        endpointIrisPetasosParcelSync.setIsServer(true);
        endpointIrisPetasosParcelSync.setRequiresEncryption(false);
        irisServiceNode.getEndpoints().add(endpointIrisPetasosParcelSync);

        DeploymentMapEndpointElement endpointIrisPetasosHeartbeatSync = new DeploymentMapEndpointElement();
        endpointIrisPetasosHeartbeatSync.setEndpointInstanceID("PetasosHeartbeatSvr");
        endpointIrisPetasosHeartbeatSync.setEndpointFunctionID("PetasosHeartbeatSvr");
        endpointIrisPetasosHeartbeatSync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointIrisPetasosHeartbeatSync.setExternalDNSEntry("petasos-gen0-iris");
        endpointIrisPetasosHeartbeatSync.setExternalPortNumber("10871");
        endpointIrisPetasosHeartbeatSync.setInternalPortNumber("10871");
        endpointIrisPetasosHeartbeatSync.setIsServer(true);
        endpointIrisPetasosHeartbeatSync.setRequiresEncryption(true);
        irisServiceNode.getEndpoints().add(endpointIrisPetasosHeartbeatSync);

    }

    public void createIrisPlatforms(DeploymentMapNodeElement irisSiteNode) {
    	DeploymentMapNodeElement nodePlatformA = new DeploymentMapNodeElement();
        nodePlatformA.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodePlatformA.setElementVersion(irisSiteNode.getElementVersion());
        nodePlatformA.setInstanceName("___");
        nodePlatformA.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodePlatformA.setTopologyElementType(NodeElementTypeEnum.PLATFORM);
        irisSiteNode.getContainedElements().add(nodePlatformA);
        createIrisProcessingPlatform(nodePlatformA);
    }

    private static String IRIS_SERVICE_MODULE = "1.0.0";
    
    public void createIrisProcessingPlatform(DeploymentMapNodeElement nodeNode) {
    	DeploymentMapNodeElement nodeIrisProcessingPlant = new DeploymentMapNodeElement();
        nodeIrisProcessingPlant.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeIrisProcessingPlant.setElementVersion(IRIS_SERVICE_MODULE);
        nodeIrisProcessingPlant.setInstanceName("Matrix2FHIRServices");
        nodeIrisProcessingPlant.setFunctionName("Matrix2FHIRServices");
        nodeIrisProcessingPlant.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeIrisProcessingPlant.setTopologyElementType(NodeElementTypeEnum.PROCESSING_PLANT);
        nodeNode.getContainedElements().add(nodeIrisProcessingPlant);

        DeploymentMapEndpointElement endpointMatrixNotificationReceiver = new DeploymentMapEndpointElement();
        endpointMatrixNotificationReceiver.setEndpointInstanceID("MatrixNotificationReceiver");
        endpointMatrixNotificationReceiver.setEndpointFunctionID("MatrixNotificationReceiver");
        endpointMatrixNotificationReceiver.setEndpointType(EndpointElementTypeEnum.INGRES_MESSAGING);
        endpointMatrixNotificationReceiver.setInternalDNSEntry("___");
        endpointMatrixNotificationReceiver.setExternalDNSEntry("___");
        endpointMatrixNotificationReceiver.setExternalPortNumber("10700");
        endpointMatrixNotificationReceiver.setInternalPortNumber("10700");
        endpointMatrixNotificationReceiver.setIsServer(true);
        endpointMatrixNotificationReceiver.setRequiresEncryption(true);
        nodeNode.getEndpoints().add(endpointMatrixNotificationReceiver);
        
    }

	@Override
	public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
		return(new HashSet<DeploymentMapNodeElement>());
	}

}
