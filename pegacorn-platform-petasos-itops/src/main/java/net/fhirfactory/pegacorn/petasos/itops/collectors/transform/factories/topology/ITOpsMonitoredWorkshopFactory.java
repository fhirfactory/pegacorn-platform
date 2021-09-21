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
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.common.ITOpsMonitoredNodeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredWUP;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsMonitoredWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ITOpsMonitoredWorkshopFactory  extends ITOpsMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMonitoredWorkshopFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ITOpsMonitoredWUPFactory wupFactory;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ITOpsMonitoredWorkshop newWorkshop(WorkshopTopologyNode workshopNode){
        getLogger().debug(".newWorkshop(): Entry, workshopNode->{}", workshopNode);
        ITOpsMonitoredWorkshop workshop = new ITOpsMonitoredWorkshop();
        workshop = (ITOpsMonitoredWorkshop) newITOpsMonitoredNode(workshop, workshopNode);
        for(TopologyNodeFDN currentWUPFDN: workshopNode.getWupSet()){
            WorkUnitProcessorTopologyNode wupTopologyNode = (WorkUnitProcessorTopologyNode) topologyIM.getNode(currentWUPFDN);
            ITOpsMonitoredWUP currentWUP = wupFactory.newWorkUnitProcessor(wupTopologyNode);
            workshop.addWorkUnitProcessor(currentWUP);
        }
        getLogger().debug(".newWorkshop(): Exit, workshop->{}", workshop);
        return(workshop);
    }

}
