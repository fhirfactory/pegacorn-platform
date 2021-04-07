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
package net.fhirfactory.pegacorn.petasos.core.moa.wup;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.model.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.model.WorkshopInterface;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Set;

/**
 * Generic Message Orientated Architecture (MOA) Work Unit Processor (WUP) Template
 * 
 * @author Mark A. Hunter
 * @since 2020-07-01
 */

public abstract class GenericMessageBasedWUPTemplate extends BaseRouteBuilder {

    public static final Integer IPC_PACKET_MAXIMUM_FRAME_SIZE = 25 * 1024 * 1024; // 25 MB
    
    abstract protected Logger getLogger();

    private WorkUnitProcessorTopologyNode wupTopologyNode;
    private WUPJobCard wupInstanceJobCard;
    private RouteElementNames nameSet;
    private WUPArchetypeEnum wupArchetype;
    private Set<TopicToken> topicSubscriptionSet;

    @Inject
    private PetasosMOAServicesBroker servicesBroker;
    
    @Inject
    private SolutionTopologyNode solutionTopology;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRElementTopicIDBuilder fhirTopicIDBuilder;

//    @Inject
//    private ProcessingPlantInterface processingPlantServices;

    public GenericMessageBasedWUPTemplate() {
        super();
    }

    /**
     * This function essentially establishes the WUP itself, by first calling all the (abstract classes realised within subclasses)
     * and setting the core attributes of the WUP. Then, it executes the buildWUPFramework() function, which invokes the Petasos
     * framework around this WUP.
     *
     * It is automatically called by the CDI framework following Constructor invocation (see @PostConstruct tag).
     */
    @PostConstruct
    protected void initialise(){
        getLogger().debug(".initialise(): Entry, Default Post Constructor function to setup the WUP");
        getLogger().trace(".initialise(): WUP Instance Name --> {}", specifyWUPInstanceName());
        getLogger().trace(".initialise(): WUP Instance Version --> {}", specifyWUPInstanceVersion());
        getLogger().trace(".initialise(): Setting if the WUP uses the Petasos generated Ingres/Egress Endpoints");
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        buildWUPNodeElement();
        getLogger().trace(".initialise(): Setting the WUP Archetype - which is used by the WUP Framework to ascertain what wrapping this WUP needs");
        this.wupArchetype =  specifyWUPArchetype();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        nameSet = new RouteElementNames(getWUPTopologyNode().getNodeFunctionFDN().getFunctionToken());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().trace(".initialise(): Setting the Topic Subscription Set (i.e. the list of Data Sets we will process)");
        this.topicSubscriptionSet = specifySubscriptionTopics();
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());
        getLogger().debug(".initialise(): Exit");
    }
    
    // To be implemented methods (in Specialisations)
    
    protected abstract Set<TopicToken> specifySubscriptionTopics();
    protected abstract WUPArchetypeEnum specifyWUPArchetype();
    protected abstract String specifyWUPInstanceName();
    protected abstract String specifyWUPInstanceVersion();

    protected abstract WorkshopInterface specifyWorkshop();
    protected abstract ProcessingPlantInterface specifyProcessingPlant();
    protected abstract GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint();
    protected abstract GenericMessageBasedWUPEndpoint specifyEgressTopologyEndpoint();

    protected WorkshopInterface getWorkshop(){
        return(specifyWorkshop());
    }
    protected ProcessingPlantInterface getProcessingPlant(){
        return(specifyProcessingPlant());
    }

    protected GenericMessageBasedWUPEndpoint getIngresTopologyEndpoint(){
        return(specifyIngresTopologyEndpoint());
    }
    protected GenericMessageBasedWUPEndpoint getEgressTopologyEndpoint(){
        return(specifyEgressTopologyEndpoint());
    }

    protected boolean getUsesWUPFrameworkGeneratedIngresEndpoint(){
        return(getIngresTopologyEndpoint().isFrameworkEnabled());
    }
    protected boolean getUsesWUPFrameworkGeneratedEgressEndpoint(){
        return(getEgressTopologyEndpoint().isFrameworkEnabled());
    }

    protected void executePostInitialisationActivities(){
        // Subclasses can optionally override
    }

    protected SolutionTopologyNode getSolutionTopology(){return(solutionTopology);}

    public void buildWUPFramework(CamelContext routeContext) {
        getLogger().debug(".buildWUPFramework(): Entry");
        servicesBroker.registerWorkUnitProcessor(this.wupTopologyNode, this.getTopicSubscriptionSet(), this.getWupArchetype());
        getLogger().debug(".buildWUPFramework(): Exit");
    }
    
    public String getEndpointHostName(){
        String dnsName = getProcessingPlant().getProcessingPlantNode().getDefaultDNSName();
        return(dnsName);
    }
    
    public PetasosMOAServicesBroker getServicesBroker(){
        return(this.servicesBroker);
    }
    
    public TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public WorkUnitProcessorTopologyNode getWUPTopologyNode() {
        return wupTopologyNode;
    }

    public void setWupTopologyNode(WorkUnitProcessorTopologyNode wupTopologyNode) {
        this.wupTopologyNode = wupTopologyNode;
    }

    public RouteElementNames getNameSet() {
        return nameSet;
    }

    public String getWupInstanceName() {
        return getWUPTopologyNode().getNodeRDN().getTag();
    }

    public WUPArchetypeEnum getWupArchetype() {
        return wupArchetype;
    }

    public Set<TopicToken> getTopicSubscriptionSet() {
        return topicSubscriptionSet;
    }

    public void setTopicSubscriptionSet(Set<TopicToken> topicSubscriptionSet) {
        this.topicSubscriptionSet = topicSubscriptionSet;
    }

    public String getVersion() {
        return wupTopologyNode.getNodeRDN().getNodeVersion();
    }

    public FHIRElementTopicIDBuilder getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }


    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
 //       WorkUnitProcessorTopologyNode wupNode = getWorkshop()
 //               .getTopologyFactory()
 //               .addWorkUnitProcessor(specifyWUPInstanceName(),specifyWUPInstanceVersion(), getWorkshop().getWorkshopNode(), TopologyNodeTypeEnum.WUP);
 //       getTopologyIM().addTopologyNode(specifyWorkshop().getWorkshopNode(), wupNode);
 //       setWupTopologyNode(wupNode);
 //       wupNode.setResilienceMode(specifyWorkshop().getWorkshopNode().getResilienceMode());
 //       wupNode.setConcurrencyMode(specifyWorkshop().getWorkshopNode().getConcurrencyMode());
    }
}
