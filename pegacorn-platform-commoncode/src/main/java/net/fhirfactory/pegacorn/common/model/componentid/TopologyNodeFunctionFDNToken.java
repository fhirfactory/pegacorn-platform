package net.fhirfactory.pegacorn.common.model.componentid;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;

import java.io.Serializable;

public class TopologyNodeFunctionFDNToken implements Serializable {
    public String token;

    public TopologyNodeFunctionFDNToken(){
        token = new String();
    }

    public TopologyNodeFunctionFDNToken(TopologyNodeFunctionFDNToken ori){
        this.token = new String(ori.getToken());
    }

    public TopologyNodeFunctionFDNToken(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FDNToken toTypeBasedFDNToken(){
        TopologyNodeFunctionFDN topologyFDN = new TopologyNodeFunctionFDN(this);
        FDN genericFDN = topologyFDN.toTypeBasedFDN();
        FDNToken genericFDNToken = genericFDN.getToken();
        return(genericFDNToken);
    }

    public FDNToken toVersionBasedFDNToken(){
        TopologyNodeFunctionFDN topologyFDN = new TopologyNodeFunctionFDN(this);
        FDN genericFDN = topologyFDN.toVersionBasedFDN();
        FDNToken genericFDNToken = genericFDN.getToken();
        return(genericFDNToken);
    }

    @Override
    public String toString() {
        return "TopologyNodeFunctionFDNToken{" +
                "token='" + token + '\'' +
                '}';
    }
}
