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

package net.fhirfactory.pegacorn.petasos.wup.archetypes;

import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class InteractAPIServletRESTfulPOSTGatewayWUP extends GenericMessageBasedWUPTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(InteractAPIServletRESTfulPOSTGatewayWUP.class);

    private IPCEndpoint ingresEndpointElement;

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_API_PUSH);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint(){
        getLogger().debug(".specifyIngresTopologyEndpoint(): Entry");
        GenericMessageBasedWUPEndpoint ingresEndpoint = new GenericMessageBasedWUPEndpoint();
        ingresEndpoint.setFrameworkEnabled(false);
        String ingresEndPoint = "direct:" + this.getWupInstanceName() + "-" + this.specifyIngresEndpointPath();
        ingresEndpoint.setEndpointSpecification(ingresEndPoint);
        getLogger().debug(".specifyIngresTopologyEndpoint(): Exit");
        return(ingresEndpoint);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyEgressTopologyEndpoint(){
        getLogger().debug(".specifyEgressTopologyEndpoint(): Entry");
        GenericMessageBasedWUPEndpoint egressEndpoint = new GenericMessageBasedWUPEndpoint();
        egressEndpoint.setFrameworkEnabled(true);
        egressEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPEgress());
        getLogger().debug(".specifyEgressTopologyEndpoint(): Exit");
        return(egressEndpoint);
    }
 
    /**
     * The Ingres Message Gateway doesn't subscribe to ANY topics as it receives it's 
     * input from an external system.
     * 
     * @return An empty Set<TopicToken>
     */
    @Override
    public Set<TopicToken> specifySubscriptionTopics() {
        HashSet<TopicToken> subTopics = new HashSet<TopicToken>();
        return(subTopics);
    }
    
    abstract protected String specifyIngresEndpointPath();

}
