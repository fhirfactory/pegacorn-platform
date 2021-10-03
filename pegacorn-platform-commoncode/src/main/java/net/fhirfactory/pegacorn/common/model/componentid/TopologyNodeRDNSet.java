package net.fhirfactory.pegacorn.common.model.componentid;

import java.util.ArrayList;
import java.util.HashMap;

public class TopologyNodeRDNSet {
    private HashMap<Integer, TopologyNodeRDN> payload;

    public TopologyNodeRDNSet(ArrayList<TopologyNodeRDN> rdnArrayList){
        this.payload = new HashMap<>();
        if(rdnArrayList == null){
            return;
        }
        int counter = 0;
        for(TopologyNodeRDN currentRDN: rdnArrayList){
            payload.put(counter, currentRDN);
            counter += 1;
        }
    }

    public TopologyNodeRDNSet(){
        this.payload = new HashMap<>();
    }

    public HashMap<Integer, TopologyNodeRDN> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<Integer, TopologyNodeRDN> payload) {
        this.payload = payload;
    }
}
