package net.fhirfactory.pegacorn.common.model.componentid;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;

public class TopologyNodeFDNToken {
    private String tokenValue;

    public TopologyNodeFDNToken(){
        tokenValue = new String();
    }

    public TopologyNodeFDNToken(TopologyNodeFDNToken ori){
        this.tokenValue = new String(ori.getTokenValue());
    }

    public TopologyNodeFDNToken(String token){
        this.tokenValue = token;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public FDNToken toTypeBasedFDNToken(){
        TopologyNodeFDN topologyFDN = new TopologyNodeFDN(this);
        FDN genericFDN = topologyFDN.toTypeBasedFDN();
        FDNToken genericFDNToken = genericFDN.getToken();
        return(genericFDNToken);
    }

    public FDNToken toVersionBasedFDNToken(){
        TopologyNodeFDN topologyFDN = new TopologyNodeFDN(this);
        FDN genericFDN = topologyFDN.toVersionBasedFDN();
        FDNToken genericFDNToken = genericFDN.getToken();
        return(genericFDNToken);
    }
}
