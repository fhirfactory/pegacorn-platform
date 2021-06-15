/*
 * Copyright (c) 2020 Mark A. Hunter
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
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
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

    
    abstract protected Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private WorkUnitProcessorTopologyNode wupTopologyNode;
    private WUPJobCard wupJobCard;
    private RouteElementNames nameSet;
    private WUPArchetypeEnum wupArchetype;
    private Set<DataParcelToken> topicSubscriptionSet;

    @Inject
    private PetasosMOAServicesBroker servicesBroker;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRElementTopicFactory fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantInterface processingPlantServices;

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
    
    protected abstract Set<DataParcelToken> specifySubscriptionTopics();
    protected abstract WUPArchetypeEnum specifyWUPArchetype();
    protected abstract String specifyWUPInstanceName();
    protected abstract String specifyWUPInstanceVersion();

    protected abstract WorkshopInterface specifyWorkshop();
    protected abstract GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint();
    protected abstract GenericMessageBasedWUPEndpoint specifyEgressTopologyEndpoint();

    protected WorkshopInterface getWorkshop(){
        return(specifyWorkshop());
    }
    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlantServices);
    }
    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlantServices.getTopologyFactory());
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

    protected SolutionTopologyNode getSolutionTopology(){return(processingPlantServices.getSolutionNode());}

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

    public Set<DataParcelToken> getTopicSubscriptionSet() {
        return topicSubscriptionSet;
    }

    public void setTopicSubscriptionSet(Set<DataParcelToken> topicSubscriptionSet) {
        this.topicSubscriptionSet = topicSubscriptionSet;
    }

    public String getVersion() {
        return wupTopologyNode.getNodeRDN().getNodeVersion();
    }

    public FHIRElementTopicFactory getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }


    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
        WorkUnitProcessorTopologyNode wupNode = getTopologyFactory()
                .addWorkUnitProcessor(specifyWUPInstanceName(),specifyWUPInstanceVersion(), getWorkshop().getWorkshopNode(), TopologyNodeTypeEnum.WUP);
        getTopologyIM().addTopologyNode(specifyWorkshop().getWorkshopNode().getNodeFDN(), wupNode);
        setWupTopologyNode(wupNode);
        wupNode.setResilienceMode(specifyWorkshop().getWorkshopNode().getResilienceMode());
        wupNode.setConcurrencyMode(specifyWorkshop().getWorkshopNode().getConcurrencyMode());
    }

    protected IPCTopologyEndpoint deriveServerEndpoint(IPCInterfaceDefinition interfaceDefinition){
        getLogger().debug(".deriveServerEndpoint(): Entry, interfaceDefinition->{}", interfaceDefinition);
        ProcessingPlantTopologyNode processingPlantTopologyNode = processingPlantServices.getProcessingPlantNode();
        getLogger().trace(".deriveServerEndpoint(): Parse through all endpoints and their IPC Definitions");
        for(TopologyNodeFDN endpointFDN: processingPlantTopologyNode.getEndpoints()){
            IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint)topologyIM.getNode(endpointFDN);
            getLogger().trace(".deriveServerEndpoint(): endpoint->{}", endpoint);
            for(IPCInterface currentInterface: endpoint.getSupportedInterfaceSet()){
                getLogger().trace(".deriveServerEndpoint(): currentInterface->{}", currentInterface);
                for(IPCInterfaceDefinition currentInterfaceDef: currentInterface.getSupportedInterfaceDefinitions()){
                    getLogger().trace(".deriveServerEndpoint(): currentInterfaceDef->{}", currentInterfaceDef);
                    if(currentInterfaceDef.equals(interfaceDefinition)){
                        getLogger().trace(".deriveServerEndpoint(): currentInterfaceDef matches");
                        return(endpoint);
                    }
                }
            }
        }
        return(null);
    }

    protected class NodeDetailInjector implements Processor{
        @Override
        public void process(Exchange exchange) throws Exception {
            getLogger().debug("NodeDetailInjector.process(): Entry");
            boolean alreadyInPlace = false;
            if(exchange.hasProperties()) {
                WorkUnitProcessorTopologyNode wupTN = exchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
                if (wupTN != null) {
                    alreadyInPlace = true;
                }
            }
            if(!alreadyInPlace) {
                exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getWupTopologyNode());
            }
        }
    }

    public WorkUnitProcessorTopologyNode getWupTopologyNode() {
        return wupTopologyNode;
    }

    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    protected RouteDefinition fromIncludingPetasosServices(String uri) {
        NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();
        RouteDefinition route = fromWithStandardExceptionHandling(uri);
        route
                .process(nodeDetailInjector)
        ;
        return route;
    }
}
