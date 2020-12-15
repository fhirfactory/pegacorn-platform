package net.fhirfactory.pegacorn.petasos.model.restfulapi;

import org.json.JSONObject;

import java.util.HashSet;

public class RESTfulActionRequest {
    private String fromIPAddress;
    private String fromPort;
    private String action;
    private String invocation;
    private HashSet<String> parameters;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInvocation() {
        return invocation;
    }

    public void setInvocation(String invocation) {
        this.invocation = invocation;
    }

    public HashSet<String> getParameters() {
        return parameters;
    }

    public void setParameters(HashSet<String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String newParameter){
        this.parameters.add(newParameter);
    }

    public String asJSONString(){
        JSONObject thisAsJSON = new JSONObject(this);
        String asAString = thisAsJSON.toString();
        return(asAString);
    }

    public String getFromIPAddress() {
        return fromIPAddress;
    }

    public void setFromIPAddress(String fromIPAddress) {
        this.fromIPAddress = fromIPAddress;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }
}
