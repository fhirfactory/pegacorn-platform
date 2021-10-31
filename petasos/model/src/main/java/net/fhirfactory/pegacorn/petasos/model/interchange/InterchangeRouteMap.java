/*
 * Copyright (c) 2020 MAHun
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
package net.fhirfactory.pegacorn.petasos.model.interchange;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.Tree;
import net.fhirfactory.pegacorn.core.model.generalid.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark A. Hunter (ACT Health)
 * <p>
 * The Pegacorn::Petasos::Interchange needs to maintain a map of all the
 * PetasosParcels entering it and all the WUPs that should be consuming the UoWs
 * extracted from the ingres PetasosParcels.
 * </p>
 */
public class InterchangeRouteMap {
    private static final String ROUTE_ENTRY_SEPERATOR = " ---> ";
    private LinkedHashMap<FDN, Tree<FDN>> producingWUPInterchangeRouteSet;
    private static final Logger LOG = LoggerFactory.getLogger(InterchangeRouteMap.class);
    protected Logger getLogger(){
        return(LOG);
    }

    public InterchangeRouteMap() {
        producingWUPInterchangeRouteSet = new LinkedHashMap<FDN, Tree<FDN>>();
    }

    public void addProducingWUP(FDN producingWUPFDN) {
        getLogger().debug("addProducingWUP(): Entry, producingWUPFDN --> {}", producingWUPFDN);
        if (producingWUPFDN == null) {
            return;
        }
        if (producingWUPFDN.isEmpty()) {
            return;
        }
        if (producingWUPInterchangeRouteSet.containsKey(producingWUPFDN)) {
            return;
        }
        TreeNode<FDN> newTreeRoot = new TreeNode<FDN>(producingWUPFDN);
        Tree tempParcelConsumerTree = new Tree(newTreeRoot);
        this.producingWUPInterchangeRouteSet.put(producingWUPFDN, tempParcelConsumerTree);
        getLogger().debug("addProducingWUP(): Exit, producingWUPFDN --> {}", producingWUPFDN);
    }

    public void linkOutboundUoWType2ProducingWUP(FDN producingWUPFDN, FDN outboundUoWTypeFDN) {
        getLogger().debug("linkOutboundUoWType2ProducingWUP(): Entry, producingWUPFDN --> {}, outboundUoWTypeFDN --> {}", producingWUPFDN, outboundUoWTypeFDN);
        if (producingWUPFDN == null || outboundUoWTypeFDN == null) {
            return;
        }
        if (producingWUPFDN.isEmpty() || outboundUoWTypeFDN.isEmpty()) {
            return;
        }
        this.addProducingWUP(producingWUPFDN);
        Tree<FDN> tempTreeRoot = this.producingWUPInterchangeRouteSet.get(producingWUPFDN);
        TreeNode<FDN> tempTreeRootNode = tempTreeRoot.getTreeNode();
        if (tempTreeRootNode.getChildren().isEmpty()) {
            TreeNode<FDN> newChildTreeNode = new TreeNode<FDN>(outboundUoWTypeFDN);
            tempTreeRootNode.addChild(newChildTreeNode);
            return;
        }
        Iterator<TreeNode<FDN>> tmpTreeNodeChildIterator = tempTreeRootNode.getChildren().iterator();
        while (tmpTreeNodeChildIterator.hasNext()) {
            TreeNode<FDN> tmpChildNode = tmpTreeNodeChildIterator.next();
            if (tmpChildNode.getNodeData().toString().contentEquals(outboundUoWTypeFDN.toString())) {
                return;
            }
        }
        TreeNode<FDN> newChildTreeNode = new TreeNode<FDN>(outboundUoWTypeFDN);
        tempTreeRootNode.addChild(newChildTreeNode);
        getLogger().debug("linkOutboundUoWType2ProducingWUP(): Exit, TreeNode added onto TreeRoot --> {}, TreeNode (Leaf) --> {}", producingWUPFDN, outboundUoWTypeFDN);

    }

    public void linkConsumingWUP2OutboundUoWType2ProducingWUP(FDN producingWUPFDN, FDN outboundUoWTypeFDN, FDN consumingWUPFDN) {
        getLogger().debug("linkConsumingWUP2OutboundUoWType2ProducingWUP(): Entry, producingWUPFDN --> {}, egresUoWTypeFDN --> {}, consumingWUPFDN --> {}", producingWUPFDN, outboundUoWTypeFDN, consumingWUPFDN);
        if (producingWUPFDN == null || outboundUoWTypeFDN == null || consumingWUPFDN == null) {
            return;
        }
        if (producingWUPFDN.isEmpty() || outboundUoWTypeFDN.isEmpty() || consumingWUPFDN.isEmpty()) {
            return;
        }
        this.addProducingWUP(producingWUPFDN);
        this.linkOutboundUoWType2ProducingWUP(producingWUPFDN, outboundUoWTypeFDN);
        Tree<FDN> tempTreeRoot = this.producingWUPInterchangeRouteSet.get(producingWUPFDN);
        TreeNode<FDN> tempTreeRootNode = tempTreeRoot.getTreeNode();
        Iterator<TreeNode<FDN>> tmpTreeNodeChildIterator = tempTreeRootNode.getChildren().iterator();
        TreeNode<FDN> tmpChildNode = null;
        boolean ingresParcelFDNIsFound = false;
        while (tmpTreeNodeChildIterator.hasNext()) {
            tmpChildNode = tmpTreeNodeChildIterator.next();
            if (tmpChildNode.getNodeData().toString().contentEquals(outboundUoWTypeFDN.toString())) {
                ingresParcelFDNIsFound = true;
                break;
            }
        }
        if (ingresParcelFDNIsFound && (tmpChildNode != null)) {
            TreeNode<FDN> newEgressUoWNode = new TreeNode<FDN>(consumingWUPFDN);
            tmpChildNode.addChild(newEgressUoWNode);
        }
        getLogger().debug("linkConsumingWUP2OutboundUoWType2ProducingWUP(): Exit, TreeNode added onto TreeRoot --> {}, TreeNode (Leaf) --> {}", producingWUPFDN, consumingWUPFDN);
    }

    public Set<InterchangeRoute> getInterchangeRouteSet() {
        getLogger().debug("getInterchangeRouteSet(): Entry");
        LinkedHashSet<InterchangeRoute> routeSet = new LinkedHashSet<InterchangeRoute>();
        if (producingWUPInterchangeRouteSet.isEmpty()) {
            return (routeSet);
        }
        Set<FDN> rootNodes = producingWUPInterchangeRouteSet.keySet();
        Iterator<FDN> rootNodeIterator = rootNodes.iterator();
        while (rootNodeIterator.hasNext()) {
            FDN producerWUPInstance = rootNodeIterator.next();
            Tree<FDN> rootNodeInstance = producingWUPInterchangeRouteSet.get(producerWUPInstance);
            TreeNode<FDN> rootTreeNodeInstance = rootNodeInstance.getTreeNode();
            Iterator<TreeNode<FDN>> outboundUoWTypeIterator = rootTreeNodeInstance.getChildren().iterator();
            while (outboundUoWTypeIterator.hasNext()) {
                TreeNode<FDN> tempOutboundUoWTypeNode = outboundUoWTypeIterator.next();
                FDN outboundUoWTypeInstance = tempOutboundUoWTypeNode.getNodeData();
                Iterator<TreeNode<FDN>> consumingWUPIterator = tempOutboundUoWTypeNode.getChildren().iterator();
                while (consumingWUPIterator.hasNext()) {
                    TreeNode<FDN> tempConsumingWUPNode = consumingWUPIterator.next();
                    FDN consumingWUPInstance = tempConsumingWUPNode.getNodeData();
                    InterchangeRoute newInterchangeRoute = new InterchangeRoute(producerWUPInstance, outboundUoWTypeInstance, consumingWUPInstance);
                    routeSet.add(newInterchangeRoute);
                }

            }
        }
        if (getLogger().isDebugEnabled()) {
            if (routeSet.isEmpty()) {
                getLogger().debug("getInterchangeRouteSet(): Route Set is Empty!");
            } else {
                Iterator<InterchangeRoute> trailSetIterator = routeSet.iterator();
                int counter = 0;
                while (trailSetIterator.hasNext()) {
                    String trailSetEntry = trailSetIterator.next().toString();
                    getLogger().debug("getInterchangeRouteSet(): TrailSet Entry {} --> {}", counter, trailSetEntry);
                    counter += 1;
                }
            }
        }
        return (routeSet);
    }
}
