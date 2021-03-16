package net.fhirfactory.pegacorn.common.model.componentid;

public class TopologyNodeFunctionFDNToken {
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
}
