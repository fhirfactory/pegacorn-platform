/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.common.ITOpsMonitoredNodeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredProcessingPlant;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ITOpsMonitoredProcessingPlantFactory extends ITOpsMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMonitoredProcessingPlantFactory.class);

    @Inject
    private ITOpsMonitoredWorkshopFactory workshopFactory;

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ITOpsMonitoredProcessingPlant newProcessingPlant(ProcessingPlantTopologyNode topologyNode){
        LOG.debug(".newProcessingPlant(): Entry, topologyNode->{}", topologyNode);
        ITOpsMonitoredProcessingPlant processingPlant = new ITOpsMonitoredProcessingPlant();
        processingPlant = (ITOpsMonitoredProcessingPlant) newITOpsMonitoredNode(processingPlant, topologyNode);
        processingPlant.setSecurityZone(topologyNode.getSecurityZone().getNetworkSecurityZone());
        processingPlant.setActualHostIP(topologyNode.getActualHostIP());
        processingPlant.setActualPodIP(topologyNode.getActualPodIP());
        TopologyNodeFDN nodeFDN = topologyNode.getNodeFDN();
        processingPlant.setTopologyNodeFDN(nodeFDN);
        String platformNodeName = "Unknown";
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(ComponentTypeTypeEnum.PLATFORM)){
                platformNodeName = currentRDN.getNodeName();
                break;
            }
        }
        String siteName = "Unknown";
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(ComponentTypeTypeEnum.SITE)){
                siteName = currentRDN.getNodeName();
                break;
            }
        }
        processingPlant.setSite(siteName);
        processingPlant.setPlatformID(platformNodeName);
        for(TopologyNodeFDN currentWorkshopFDN: topologyNode.getWorkshops()){
            WorkshopTopologyNode workshopTopologyNode = (WorkshopTopologyNode) topologyIM.getNode(currentWorkshopFDN);
            ITOpsMonitoredWorkshop currentWorkshop = workshopFactory.newWorkshop(workshopTopologyNode);
            processingPlant.addWorkshop(currentWorkshop);
        }
        LOG.debug(".newProcessingPlant(): Exit, processingPlant->{}", processingPlant);
        return(processingPlant);
    }
}
