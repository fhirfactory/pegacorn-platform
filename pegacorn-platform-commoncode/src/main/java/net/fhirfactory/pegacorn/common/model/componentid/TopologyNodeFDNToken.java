package net.fhirfactory.pegacorn.common.model.componentid;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;

import java.io.Serializable;
import java.util.Objects;

public class TopologyNodeFDNToken implements Serializable {
    private String tokenValue;

    public TopologyNodeFDNToken(){
        tokenValue = new String();
    }

    public TopologyNodeFDNToken(TopologyNodeFDNToken ori){
        this.tokenValue = ori.getTokenValue();
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
        FDN genericFDN = topologyFDN.toTypeBasedFDNWithVersion();
        FDNToken genericFDNToken = genericFDN.getToken();
        return(genericFDNToken);
    }

    @Override
    public String toString() {
        return "TopologyNodeFDNToken{" +
                "tokenValue=" + tokenValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TopologyNodeFDNToken)) return false;
        TopologyNodeFDNToken that = (TopologyNodeFDNToken) o;
        return Objects.equals(getTokenValue(), that.getTokenValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTokenValue());
    }
}
