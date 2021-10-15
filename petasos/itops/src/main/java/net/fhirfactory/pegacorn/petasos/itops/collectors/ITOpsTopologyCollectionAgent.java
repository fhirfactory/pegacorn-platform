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
package net.fhirfactory.pegacorn.petasos.itops.collectors;

import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsTopologyLocalDM;
import net.fhirfactory.pegacorn.petasos.itops.collectors.transform.factories.topology.ITOpsTopologyGraphFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsTopologyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ITOpsTopologyCollectionAgent {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsTopologyCollectionAgent.class);

    @Inject
    private ITOpsTopologyLocalDM itOpsTopologyDM;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ITOpsTopologyGraphFactory topologyGraphFactory;


    public void refreshLocalTopologyGraph(){
        LOG.debug(".refreshLocalTopologyGraph(): Entry");
        List<TopologyNode> nodeList = getTopologyElementList();
        if(nodeList.isEmpty()){
            LOG.debug(".refreshLocalTopologyGraph(): Exit, nodeList is empty");
            return;
        }
        ITOpsTopologyGraph builtGraph = buildTopologyGraph(nodeList);
        itOpsTopologyDM.setCurrentState(builtGraph);
        LOG.debug(".refreshLocalTopologyGraph(): Exit");
    }

    private List<TopologyNode> getTopologyElementList() {
        LOG.debug(".getTopologyElementList(): Entry");
        Set<TopologyNode> nodeElementSet = topologyIM.getNodeElementSet();
        List<TopologyNode> nodeList = new ArrayList<>();
        if (nodeElementSet.isEmpty()) {
            LOG.debug(".getTopologyElementList(): Exit, topologyIM NodeSet is empty");
            return(nodeList);
        }
        for(TopologyNode currentNode: nodeElementSet) {
            switch (currentNode.getComponentType()) {
                case ENDPOINT:
                case WUP:
                case OAM_WORK_UNIT_PROCESSOR:
                case PROCESSING_PLANT:
                case OAM_WORKSHOP:
                case WORKSHOP:{
                    nodeList.add(currentNode);
                    break;
                }
                default: {
                    // don't add
                }
            }
        }
        LOG.debug(".getTopologyElementList(): Exit");
        return(nodeList);
    }

    private ITOpsTopologyGraph buildTopologyGraph(List<TopologyNode> nodeList){
        LOG.debug(".buildTopologyGraph(): Entry");
        if(nodeList.isEmpty()){
            LOG.debug(".buildTopologyGraph(): Exit, nodeList is empty");
            ITOpsTopologyGraph emptyGraph = new ITOpsTopologyGraph();
            return(emptyGraph);
        }

        ITOpsTopologyGraph topologyGraph = topologyGraphFactory.newTopologyGraph(nodeList);

        LOG.debug(".buildTopologyGraph(): Exit, topologyGraph->{}",topologyGraph);
        return(topologyGraph);
    }
}
