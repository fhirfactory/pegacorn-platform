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
package net.fhirfactory.pegacorn.deployment.topology.map.standalone.ladon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.deployment.names.PegacornLadonComponentNames;
import net.fhirfactory.pegacorn.deployment.topology.map.archetypes.common.EdgeMessagingPortsCreator;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.petasos.model.resilience.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.EndpointElementTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.map.archetypes.LadonExternalisedServices;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapEndpointElement;
import net.fhirfactory.pegacorn.deployment.topology.map.model.DeploymentMapNodeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter
 */

public class LadonServices extends LadonExternalisedServices {

    private static final Logger LOG = LoggerFactory.getLogger(LadonServices.class);

    private static final String DEFAULT_VERSION = "1.0.0";

    PegacornLadonComponentNames subsystemComponentNames = new PegacornLadonComponentNames();

    @Override
    public void buildSubsystemNode(DeploymentMapNodeElement solutionNode) {
        DeploymentMapNodeElement ladonNode = new DeploymentMapNodeElement();
        ladonNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        ladonNode.setElementVersion("0.0.1");
        ladonNode.setInstanceName(subsystemComponentNames.getLadonSubsystemDefault());
        ladonNode.setFunctionName(subsystemComponentNames.getLadonSubsystemDefault());
        ladonNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        ladonNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        solutionNode.getContainedElements().add(ladonNode);
        buildExternalisedServiceNode(ladonNode);
    }

    public void buildExternalisedServiceNode(DeploymentMapNodeElement subsystem) {
        LOG.debug(".buildExternalisedServiceNode(): Entry");
        DeploymentMapNodeElement gen0LadonServiceNode = new DeploymentMapNodeElement();
        gen0LadonServiceNode.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        gen0LadonServiceNode.setElementVersion(subsystem.getElementVersion());
        gen0LadonServiceNode.setInstanceName(subsystemComponentNames.getLadonExternalisedServiceDefault());
        gen0LadonServiceNode.setFunctionName(subsystemComponentNames.getLadonExternalisedServiceDefault());
        gen0LadonServiceNode.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        gen0LadonServiceNode.setTopologyElementType(NodeElementTypeEnum.EXTERNALISED_SERVICE);
        gen0LadonServiceNode.setContainedElements(new ArrayList<DeploymentMapNodeElement>());
        subsystem.getContainedElements().add(gen0LadonServiceNode);
        createLadonSites(gen0LadonServiceNode);
    }

    public void createLadonSites(DeploymentMapNodeElement nodeLadonExternalService) {
        LOG.debug(".createLadonSites(): Entry");
        DeploymentMapNodeElement nodeSiteA = new DeploymentMapNodeElement();
        nodeSiteA.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeSiteA.setElementVersion(nodeLadonExternalService.getElementVersion());
        nodeSiteA.setInstanceName("___");
        nodeSiteA.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeSiteA.setTopologyElementType(NodeElementTypeEnum.SITE);
        nodeLadonExternalService.getContainedElements().add(nodeSiteA);
        createLadonServices(nodeSiteA);
    }

    public void createLadonServices(DeploymentMapNodeElement nodeLadonSite) {
        LOG.debug(".createLadonServices(): Entry");
        DeploymentMapNodeElement nodeLadonService = new DeploymentMapNodeElement();
        nodeLadonService.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeLadonService.setElementVersion(nodeLadonSite.getElementVersion());
        nodeLadonService.setInstanceName(subsystemComponentNames.getLadonServiceDefault());
        nodeLadonService.setFunctionName(subsystemComponentNames.getLadonServiceDefault());
        nodeLadonService.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeLadonService.setTopologyElementType(NodeElementTypeEnum.SERVICE);
        nodeLadonSite.getContainedElements().add(nodeLadonService);
        createLadonPlatforms(nodeLadonService);
        createLadonServiceEndpoints(nodeLadonService);
    }

    public void createLadonServiceEndpoints(DeploymentMapNodeElement nodeLadonService) {
        LOG.debug(".createLadonServiceEndpoints(): Entry");
        DeploymentMapEndpointElement endpointPetasosTopologySync = new DeploymentMapEndpointElement();
        endpointPetasosTopologySync.setEndpointInstanceID("PetasosTopologySyncSvr");
        endpointPetasosTopologySync.setEndpointFunctionID("PetasosTopologySyncSvr");
        endpointPetasosTopologySync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointPetasosTopologySync.setExternalDNSEntry("___");
        endpointPetasosTopologySync.setExternalPortNumber("10810");
        endpointPetasosTopologySync.setInternalPortNumber("10810");
        endpointPetasosTopologySync.setIsServer(true);
        endpointPetasosTopologySync.setVersion(DEFAULT_VERSION);
        endpointPetasosTopologySync.setRequiresEncryption(false);
        nodeLadonService.getEndpoints().add(endpointPetasosTopologySync);

        DeploymentMapEndpointElement endpointPetasosParcelSync = new DeploymentMapEndpointElement();
        endpointPetasosParcelSync.setEndpointInstanceID("PetasosTopologySyncSvr");
        endpointPetasosParcelSync.setEndpointFunctionID("PetasosTopologySyncSvr");
        endpointPetasosParcelSync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointPetasosParcelSync.setExternalDNSEntry("___");
        endpointPetasosParcelSync.setExternalPortNumber("10811");
        endpointPetasosParcelSync.setInternalPortNumber("10811");
        endpointPetasosParcelSync.setVersion(DEFAULT_VERSION);
        endpointPetasosParcelSync.setIsServer(true);
        endpointPetasosParcelSync.setRequiresEncryption(false);
        nodeLadonService.getEndpoints().add(endpointPetasosParcelSync);

        DeploymentMapEndpointElement endpointPetasosHeartbeatSync = new DeploymentMapEndpointElement();
        endpointPetasosHeartbeatSync.setEndpointInstanceID("PetasosTopologySyncSvr");
        endpointPetasosHeartbeatSync.setEndpointFunctionID("PetasosTopologySyncSvr");
        endpointPetasosHeartbeatSync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointPetasosHeartbeatSync.setExternalDNSEntry("___");
        endpointPetasosHeartbeatSync.setExternalPortNumber("10812");
        endpointPetasosHeartbeatSync.setInternalPortNumber("10812");
        endpointPetasosHeartbeatSync.setIsServer(true);
        endpointPetasosHeartbeatSync.setVersion(DEFAULT_VERSION);
        endpointPetasosHeartbeatSync.setRequiresEncryption(true);
        nodeLadonService.getEndpoints().add(endpointPetasosHeartbeatSync);

        EdgeMessagingPortsCreator messagingPorts = new EdgeMessagingPortsCreator();
        messagingPorts.addEdgeReceiverPorts(nodeLadonService, 10250,DEFAULT_VERSION);
    }

    public void createLadonPlatforms(DeploymentMapNodeElement nodeLadonServices) {
        LOG.debug(".createLadonPlatforms(): Entry");
        DeploymentMapNodeElement nodePlatformA = new DeploymentMapNodeElement();
        nodePlatformA.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodePlatformA.setElementVersion(nodeLadonServices.getElementVersion());
        nodePlatformA.setInstanceName("___");
        nodePlatformA.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodePlatformA.setTopologyElementType(NodeElementTypeEnum.PLATFORM);
        nodeLadonServices.getContainedElements().add(nodePlatformA);
        createLadonProcessingPlant(nodePlatformA);
    }

    private static String LADON_DEFAULT_VERSION = "1.0.0";

    public void createLadonProcessingPlant(DeploymentMapNodeElement nodeNode) {
        LOG.debug(".createLadonProcessingPlant(): Entry");
        DeploymentMapNodeElement nodeLadonProcessingPlant = new DeploymentMapNodeElement();
        nodeLadonProcessingPlant.setConcurrencyMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        nodeLadonProcessingPlant.setElementVersion(LADON_DEFAULT_VERSION);
        nodeLadonProcessingPlant.setInstanceName(subsystemComponentNames.getLadonProcessingPlantDefault());
        nodeLadonProcessingPlant.setFunctionName(subsystemComponentNames.getLadonProcessingPlantDefault());
        nodeLadonProcessingPlant.setResilienceMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        nodeLadonProcessingPlant.setTopologyElementType(NodeElementTypeEnum.PROCESSING_PLANT);
        nodeNode.getContainedElements().add(nodeLadonProcessingPlant);

        DeploymentMapEndpointElement endpointEndEdgeAnswer = new DeploymentMapEndpointElement();
        endpointEndEdgeAnswer.setEndpointInstanceID(subsystemComponentNames.getEdgeAnswerEndpointName());
        endpointEndEdgeAnswer.setEndpointFunctionID(subsystemComponentNames.getEdgeAnswerEndpointName());
        endpointEndEdgeAnswer.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointEndEdgeAnswer.setExternalDNSEntry("___");
        endpointEndEdgeAnswer.setExternalPortNumber("8080");
        endpointEndEdgeAnswer.setIsServer(true);
        endpointEndEdgeAnswer.setVersion(LADON_DEFAULT_VERSION);
        endpointEndEdgeAnswer.setRequiresEncryption(false);
        nodeLadonProcessingPlant.getEndpoints().add(endpointEndEdgeAnswer);

        LOG.debug(".createLadonServiceEndpoints(): Entry");
        DeploymentMapEndpointElement endpointPetasosTopologySync = new DeploymentMapEndpointElement();
        endpointPetasosTopologySync.setEndpointInstanceID(subsystemComponentNames.getPetasosWatchdogFinalisationService());
        endpointPetasosTopologySync.setEndpointFunctionID(subsystemComponentNames.getPetasosWatchdogFinalisationService());
        endpointPetasosTopologySync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointPetasosTopologySync.setExternalDNSEntry("___");
        endpointPetasosTopologySync.setExternalPortNumber("10810");
        endpointPetasosTopologySync.setInternalPortNumber("10810");
        endpointPetasosTopologySync.setIsServer(true);
        endpointPetasosTopologySync.setVersion(LADON_DEFAULT_VERSION);
        endpointPetasosTopologySync.setRequiresEncryption(false);
        nodeLadonProcessingPlant.getEndpoints().add(endpointPetasosTopologySync);

        DeploymentMapEndpointElement endpointPetasosParcelSync = new DeploymentMapEndpointElement();
        endpointPetasosParcelSync.setEndpointInstanceID(subsystemComponentNames.getPetasosWatchdogHeartbeatService());
        endpointPetasosParcelSync.setEndpointFunctionID(subsystemComponentNames.getPetasosWatchdogHeartbeatService());
        endpointPetasosParcelSync.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointPetasosParcelSync.setExternalDNSEntry("___");
        endpointPetasosParcelSync.setExternalPortNumber("10811");
        endpointPetasosParcelSync.setInternalPortNumber("10811");
        endpointPetasosParcelSync.setIsServer(true);
        endpointPetasosParcelSync.setVersion(LADON_DEFAULT_VERSION);
        endpointPetasosParcelSync.setRequiresEncryption(false);
        nodeLadonProcessingPlant.getEndpoints().add(endpointPetasosParcelSync);

        DeploymentMapEndpointElement endFromIrisFHIRAll = new DeploymentMapEndpointElement();
        endFromIrisFHIRAll.setEndpointInstanceID(subsystemComponentNames.getLadonEdgeReceiveFhirAllFromIris());
        endFromIrisFHIRAll.setEndpointFunctionID(subsystemComponentNames.getLadonEdgeReceiveFhirAllFromIris());
        endFromIrisFHIRAll.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endFromIrisFHIRAll.setExternalDNSEntry("___");
        endFromIrisFHIRAll.setExternalPortNumber("12500");
        endFromIrisFHIRAll.setInternalPortNumber("12500");
        endFromIrisFHIRAll.setIsServer(true);
        endFromIrisFHIRAll.setVersion(LADON_DEFAULT_VERSION);
        endFromIrisFHIRAll.setRequiresEncryption(false);
        nodeLadonProcessingPlant.getEndpoints().add(endFromIrisFHIRAll);

        DeploymentMapEndpointElement endpointToIrisFHIRAll = new DeploymentMapEndpointElement();
        endpointToIrisFHIRAll.setEndpointInstanceID(subsystemComponentNames.getLadonEdgeForwardFhirAllToIris());
        endpointToIrisFHIRAll.setEndpointFunctionID(subsystemComponentNames.getLadonEdgeForwardFhirAllToIris());
        endpointToIrisFHIRAll.setEndpointType(EndpointElementTypeEnum.API_SERVER);
        endpointToIrisFHIRAll.setExternalDNSEntry("___");
        endpointToIrisFHIRAll.setIsServer(false);
        endpointToIrisFHIRAll.setVersion(LADON_DEFAULT_VERSION);
        endpointToIrisFHIRAll.setRequiresEncryption(false);
        nodeLadonProcessingPlant.getEndpoints().add(endpointToIrisFHIRAll);
    }

    @Override
    public Set<DeploymentMapNodeElement> buildConnectedSystemSet() {
        return (new HashSet<DeploymentMapNodeElement>());
    }
}
