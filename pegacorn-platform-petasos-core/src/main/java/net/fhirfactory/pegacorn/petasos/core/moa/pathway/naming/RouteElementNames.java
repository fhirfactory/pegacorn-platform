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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
public class RouteElementNames {
    private static final Logger LOG = LoggerFactory.getLogger(RouteElementNames.class);

    private TopologyNodeFunctionFDNToken nodeFunctionToken;
    private boolean mustBeDirect;
    private String wupTypeName;
    private String wupVersion;
    private static final String INTRA_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String DIRECT_INTER_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String SEDA_INTER_FUNCTION_DIRECT_TYPE = "seda:";

    public RouteElementNames(TopologyNodeFunctionFDNToken functionToken, boolean mustBeDirect){
        LOG.debug(".RouteElementNames(): Entry, functionToken->{}, mustBeDirect->{}", functionToken, mustBeDirect);
        this.nodeFunctionToken = functionToken;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = mustBeDirect;
    }

    public RouteElementNames(TopologyNodeFunctionFDNToken functionToken){
        LOG.debug(".RouteElementNames(): Entry, functionToken->{}", functionToken);
        this.nodeFunctionToken = functionToken;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = false;
    }

    public String simplifyName(){
        LOG.debug(".simplifyName(): Entry, this.nodeFunctionToken --> {}", this.nodeFunctionToken);
        TopologyNodeFunctionFDN wupFunctionFDN = new TopologyNodeFunctionFDN(this.nodeFunctionToken);
        LOG.trace(".simplifyName(): wupFunctionFDN --> {}", wupFunctionFDN);
        TopologyNodeRDN processingPlantRDN = wupFunctionFDN.extractRDNForNodeType(TopologyNodeTypeEnum.PROCESSING_PLANT);
        LOG.trace(".simplifyName(): processingPlantRDN (RDN) --> {} ", processingPlantRDN);
        TopologyNodeRDN workshopRDN = wupFunctionFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP);
        LOG.trace(".simplifyName(): workshopRDN (RDN) --> {} ", workshopRDN);
        TopologyNodeRDN wupFunctionRDN = wupFunctionFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WUP);
        LOG.trace(".simplifyName(): wupFunctionRDN (RDN) --> {}", wupFunctionRDN);
        String nodeVersion = wupFunctionRDN.getNodeVersion();
        String nodeVersionSimplified = nodeVersion.replace(".","");
        String wupName = processingPlantRDN.getNodeName()+"."+workshopRDN.getNodeName()+"."+wupFunctionRDN.getNodeName()+"."+nodeVersionSimplified;
        LOG.trace(".simplifyName(): wupName (String) --> {}", wupName);
        return(wupName);
    }

    public String getWupTypeName(){
        return(this.wupTypeName);
    }
    
    public String getRouteCoreWUP(){
        return(this.wupTypeName+".WUP.Core");
    }

    public String getEndPointWUPContainerIngresProcessorIngres() {
        LOG.debug(".getEndPointWUPContainerIngresProcessorIngres(): Entry");
        String endpointName;
        if(this.mustBeDirect){
            endpointName = DIRECT_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres";
        } else {
            endpointName = SEDA_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres";
        }
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresProcessorEgress() {
        LOG.debug(".getEndPointWUPContainerIngresProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresGatekeeperIngres() {
        LOG.debug(".getEndPointWUPContainerIngresGatekeeperIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresGatekeeper.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPIngresConduitIngres() {
        LOG.debug(".getEndPointWUPIngresConduitIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.IngresConduit.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPIngres() {
        LOG.debug(".getEndPointWUPIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPEgress() {
        LOG.debug(".getEndPointWUPEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Egress";
        return(endpointName);
    }

    public String getEndPointWUPEgressConduitEgress() {
        LOG.debug(".getEndPointWUPEgressConduitEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.EgressConduit.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressGatekeeperIngres() {
        LOG.debug(".getEndPointWUPContainerEgressGatekeeperIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressGatekeeper.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorIngres() {
        LOG.debug(".getEndPointWUPContainerEgressProcessorIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorEgress() {
        LOG.debug(".getEndPointWUPContainerEgressProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Egress";
        return(endpointName);
    }

    public String getEndPointInterchangePayloadTransformerIngres() {
        LOG.debug(".getEndPointInterchangePayloadTransformerIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.PayloadTransformer.Ingres";
        return(endpointName);
    }

    public String getEndPointInterchangePayloadTransformerEgress() {
        LOG.debug(".getEndPointInterchangePayloadTransformerEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.PayloadTransformer.Egress";
        return(endpointName);
    }

    public String getEndPointInterchangeRouterIngres() {
        LOG.debug(".getEndPointInterchangeRouterIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.Router.Ingres";
        return(endpointName);
    }

    public String getEndPointInterchangeRouterEgress() {
        LOG.debug(".getEndPointInterchangeRouterEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.Router.Egress";
        return(endpointName);
    }

    public String getRouteIngresProcessorEgress2IngresGatekeeperIngres() {
        LOG.debug(".getRouteIngresProcessorEgress2IngresGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.E-To-" + wupTypeName +".WUPC.IG.I" ;
        return(endpointName);
    }

    public String getRouteIngresConduitIngres2WUPIngres() {
        LOG.debug(".getRouteIngresConduitIngres2WUPIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.IC.I-To-" + wupTypeName +".WUP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgress2WUPEgressConduitEgress() {
        LOG.debug(".getRouteWUPEgress2WUPEgressConduitEgress(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.E-To-" + wupTypeName +".WUP.EC.E" ;
        return(endpointName);
    }

    public String getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres() {
        LOG.debug(".getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EC.E-To-" + wupTypeName +".WUPC.EP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres() {
        LOG.debug(".getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EP.E-To-" + wupTypeName +".WUPC.EG.I" ;
        return(endpointName);
    }

    public String getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres() {
        LOG.debug(".getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.E-To-" + wupTypeName +".IC.R.I" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngressProcessor() {
        LOG.debug(".getRouteWUPContainerIngressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.I-To-" + wupTypeName +".WUPC.IP.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngresGateway() {
        LOG.debug(".getRouteWUPContainerIngresGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IG.I-To-" + wupTypeName +".WUPC.IG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressGateway() {
        LOG.debug(".getRouteWUPContainerEgressGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EG.I-To-" + wupTypeName +".WUPC.EG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressProcessor() {
        LOG.debug(".getRouteWUPContainerEgressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EP.I-To-" + wupTypeName +".WUPC.EP.E" ;
        return(endpointName);
    }

    public String getRouteInterchangePayloadTransformer(){
        LOG.debug(".getRouteInterchangePayloadTransformer(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.I-To-" + wupTypeName +".IC.PT.E" ;
        return(endpointName);
    }

    public String getRouteInterchangeRouter(){
        LOG.debug(".getRouteInterchangeRouter(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.R.I-To-" + wupTypeName +".IC.R.E" ;
        return(endpointName);
    }
}
