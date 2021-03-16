package net.fhirfactory.pegacorn.common.model.componentid;

public class TopologyNodeFDNToken {
    public String token;

    public TopologyNodeFDNToken(){
        token = new String();
    }

    public TopologyNodeFDNToken(TopologyNodeFDNToken ori){
        this.token = new String(ori.getToken());
    }

    public TopologyNodeFDNToken(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
