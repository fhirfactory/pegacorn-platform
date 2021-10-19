/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming;

import net.fhirfactory.pegacorn.common.model.componentid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
public class RouteElementNames {
    private static final Logger LOG = LoggerFactory.getLogger(RouteElementNames.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private ComponentIdType componentId;
    private boolean mustBeDirect;
    private String wupTypeName;
    private String wupVersion;
    private static final String INTRA_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String DIRECT_INTER_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String SEDA_INTER_FUNCTION_DIRECT_TYPE = "seda:";
    private static final String SEDA_INTER_FUNCTION_CONFIGURATION_PARAMETERS = "?blockWhenFull=true&size=10000";

    public RouteElementNames(ComponentIdType idType, boolean mustBeDirect){
        getLogger().debug(".RouteElementNames(): Entry, idType->{}, mustBeDirect->{}", idType, mustBeDirect);
        this.componentId = idType;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = mustBeDirect;
    }

    public RouteElementNames(ComponentIdType idType){
        getLogger().debug(".RouteElementNames(): Entry, idType->{}", idType);
        this.componentId = idType;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = false;
    }

    public String simplifyName(){
        getLogger().debug(".simplifyName(): Entry, this.nodeFDNToken --> {}", this.componentId);
        String nodeId = componentId.getDisplayName();
        String nodeVersion = componentId.getVersion();
        String nodeVersionSimplified = nodeVersion.replace(".","");
        String wupName = componentId.getDisplayName()+"."+nodeVersionSimplified;
        getLogger().trace(".simplifyName(): wupName (String) --> {}", wupName);
        return(wupName);
    }

    public String getWupTypeName(){
        return(this.wupTypeName);
    }
    
    public String getRouteCoreWUP(){
        return(this.wupTypeName+".WUP.Core");
    }

    public String getEndPointWUPContainerIngresProcessorIngres() {
        getLogger().debug(".getEndPointWUPContainerIngresProcessorIngres(): Entry");
        String endpointName;
        if(this.mustBeDirect){
            endpointName = DIRECT_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres";
        } else {
            endpointName = SEDA_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres" + SEDA_INTER_FUNCTION_CONFIGURATION_PARAMETERS;
        }
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresProcessorEgress() {
        getLogger().debug(".getEndPointWUPContainerIngresProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresGatekeeperIngres() {
        getLogger().debug(".getEndPointWUPContainerIngresGatekeeperIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresGatekeeper.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresGatekeeperEgress() {
        getLogger().debug(".getEndPointWUPContainerIngresGatekeeperEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresGatekeeper.Egress";
        return(endpointName);
    }

    public String getEndPointWUPIngresConduitIngres() {
        getLogger().debug(".getEndPointWUPIngresConduitIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.IngresConduit.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPIngres() {
        getLogger().debug(".getEndPointWUPIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPEgress() {
        getLogger().debug(".getEndPointWUPEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Egress";
        return(endpointName);
    }

    public String getEndPointWUPEgressConduitEgress() {
        getLogger().debug(".getEndPointWUPEgressConduitEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.EgressConduit.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressGatekeeperIngres() {
        getLogger().debug(".getEndPointWUPContainerEgressGatekeeperIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressGatekeeper.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorIngres() {
        getLogger().debug(".getEndPointWUPContainerEgressProcessorIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorEgress() {
        getLogger().debug(".getEndPointWUPContainerEgressProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Egress";
        return(endpointName);
    }

    public String getEndPointInterchangePayloadTransformerIngres() {
        getLogger().debug(".getEndPointInterchangePayloadTransformerIngres(): Entry");
        String endpointName = SEDA_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.PayloadTransformer.Ingres" + SEDA_INTER_FUNCTION_CONFIGURATION_PARAMETERS;
        return(endpointName);
    }

    public String getEndPointInterchangePayloadTransformerEgress() {
        getLogger().debug(".getEndPointInterchangePayloadTransformerEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.PayloadTransformer.Egress";
        return(endpointName);
    }

    public String getEndPointInterchangeRouterIngres() {
        getLogger().debug(".getEndPointInterchangeRouterIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.Router.Ingres";
        return(endpointName);
    }

    public String getEndPointInterchangeRouterEgress() {
        getLogger().debug(".getEndPointInterchangeRouterEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.Router.Egress";
        return(endpointName);
    }

    public String getRouteIngresProcessorEgress2IngresGatekeeperIngres() {
        getLogger().debug(".getRouteIngresProcessorEgress2IngresGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.E-To-" + wupTypeName +".WUPC.IG.I" ;
        return(endpointName);
    }

    public String getRouteIngresGatekeeperEgress2IngresConduitIngres() {
        getLogger().debug(".getRouteIngresGatekeeperEgress2IngresConduitIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IG-E-To-" + wupTypeName +".WUPC.IC.I" ;
        return(endpointName);
    }

    public String getRouteIngresConduitIngres2WUPIngres() {
        getLogger().debug(".getRouteIngresConduitIngres2WUPIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.IC.I-To-" + wupTypeName +".WUP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgress2WUPEgressConduitEgress() {
        getLogger().debug(".getRouteWUPEgress2WUPEgressConduitEgress(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.E-To-" + wupTypeName +".WUP.EC.E" ;
        return(endpointName);
    }

    public String getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres() {
        getLogger().debug(".getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EC.E-To-" + wupTypeName +".WUPC.EP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres() {
        getLogger().debug(".getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EP.E-To-" + wupTypeName +".WUPC.EG.I" ;
        return(endpointName);
    }

    public String getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres() {
        getLogger().debug(".getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.E-To-" + wupTypeName +".IC.R.I" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngressProcessor() {
        getLogger().debug(".getRouteWUPContainerIngressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.I-To-" + wupTypeName +".WUPC.IP.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngresGateway() {
        getLogger().debug(".getRouteWUPContainerIngresGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IG.I-To-" + wupTypeName +".WUPC.IG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressGateway() {
        getLogger().debug(".getRouteWUPContainerEgressGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EG.I-To-" + wupTypeName +".WUPC.EG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressProcessor() {
        getLogger().debug(".getRouteWUPContainerEgressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EP.I-To-" + wupTypeName +".WUPC.EP.E" ;
        return(endpointName);
    }

    public String getRouteInterchangePayloadTransformer(){
        getLogger().debug(".getRouteInterchangePayloadTransformer(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.I-To-" + wupTypeName +".IC.PT.E" ;
        return(endpointName);
    }

    public String getRouteInterchangeRouter(){
        getLogger().debug(".getRouteInterchangeRouter(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.R.I-To-" + wupTypeName +".IC.R.E" ;
        return(endpointName);
    }
}
