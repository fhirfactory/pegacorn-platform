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

package net.fhirfactory.pegacorn.common.model.componentid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Mark A. Hunter
 * @since 2020-08-07
 */
public class TopologyNodeFDN {
    private ArrayList<TopologyNodeRDN> hierarchicalNameSet;

    public TopologyNodeFDN(){
        hierarchicalNameSet = new ArrayList<>();
    }

    public TopologyNodeFDN(TopologyNodeFDN originalToken) {
        int size = originalToken.getHierarchicalNameSet().size();
        for(int counter = 0; counter < size; counter ++){
            this.hierarchicalNameSet.add(counter, originalToken.getHierarchicalNameSet().get(counter));
        }
    }

    public ArrayList<TopologyNodeRDN> getHierarchicalNameSet() {
        return hierarchicalNameSet;
    }

    public void setHierarchicalNameSet(ArrayList<TopologyNodeRDN> hierarchicalNameSet) {
        this.hierarchicalNameSet = hierarchicalNameSet;
    }

    public void appendTopologyNodeRDN(TopologyNodeRDN newRDN){
        int count = this.hierarchicalNameSet.size();
        this.hierarchicalNameSet.add(count, newRDN);
    }

    public void appendTopologyNodeFDN(TopologyNodeFDN additionalFDN){
        int additionalElementSize = additionalFDN.getHierarchicalNameSet().size();
        int currentElementSize = this.hierarchicalNameSet.size();
        for(int counter = 0; counter < additionalElementSize; counter ++){
            int position = currentElementSize + counter;
            this.hierarchicalNameSet.add(position, additionalFDN.getHierarchicalNameSet().get(counter));
        }
    }

    @Override
    public String toString() {
        String simpleString = "{TopologyNodeIdentifier:";
        for(TopologyNodeRDN nodeRDN: hierarchicalNameSet){
            simpleString += "("+nodeRDN.getNodeType()+"="+nodeRDN.getNodeName()+"-"+nodeRDN.getNodeVersion()+")";
        }
        simpleString += "}";
        return (simpleString);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TopologyNodeFDN)) {
            return false;
        }
        TopologyNodeFDN that = (TopologyNodeFDN) o;
        int thisSize = getHierarchicalNameSet().size();
        int thatSize = that.getHierarchicalNameSet().size();
        if(thisSize != thatSize){
            return(false);
        }
        for(int counter = 0; counter < thisSize; counter ++){
            TopologyNodeRDN thisCurrentRDN = getHierarchicalNameSet().get(counter);
            TopologyNodeRDN thatCurrentRDN = that.getHierarchicalNameSet().get(counter);
            boolean sameName = thisCurrentRDN.getNodeName().contentEquals(thatCurrentRDN.getNodeName());
            boolean sameVersion = thisCurrentRDN.getNodeVersion().contentEquals(thatCurrentRDN.getNodeVersion());
            boolean sameType = thisCurrentRDN.getNodeType().equals(thatCurrentRDN.getNodeType());
            if(!sameName || !sameVersion || !sameType){
                return(false);
            }
        }
        return (true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHierarchicalNameSet());
    }

    public String toTag(){
        String simpleTag = new String();
        int count = hierarchicalNameSet.size();
        int counter = 0;
        for(TopologyNodeRDN nodeRDN: hierarchicalNameSet){
            simpleTag += nodeRDN.getNodeName()+"("+nodeRDN.getNodeVersion()+")";
            if(counter < (count -1)){
                simpleTag += ".";
            }
        }
        return(simpleTag);
    }

    public TopologyNodeFDNToken getToken(){
        TopologyNodeRDNSet nodeRDNSet = new TopologyNodeRDNSet(this.hierarchicalNameSet);
        String tokenString = null;
        try{
            XmlMapper xmlMapper = new XmlMapper();
            tokenString = xmlMapper.writeValueAsString(nodeRDNSet);
        } catch(JsonProcessingException jsonException){
            jsonException.printStackTrace();
            tokenString = "";
        }
        TopologyNodeFDNToken newToken = new TopologyNodeFDNToken();
        return(newToken);
    }

    public TopologyNodeFDN(TopologyNodeFDNToken token){
        this.hierarchicalNameSet = new ArrayList<>();
        try{
            XmlMapper xmlMapper = new XmlMapper();
            TopologyNodeRDNSet nodeRDNSet = xmlMapper.readValue(token.getTokenValue(), TopologyNodeRDNSet.class);
            int rdnCount = nodeRDNSet.payload.size();
            for(int counter = 0; counter < rdnCount; counter ++){
                this.hierarchicalNameSet.set(counter, nodeRDNSet.getPayload().get(counter));
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public TopologyNodeFDN(String token){
        this.hierarchicalNameSet = new ArrayList<>();
        try{
            XmlMapper xmlMapper = new XmlMapper();
            TopologyNodeRDNSet nodeRDNSet = xmlMapper.readValue(token, TopologyNodeRDNSet.class);
            int rdnCount = nodeRDNSet.payload.size();
            for(int counter = 0; counter < rdnCount; counter ++){
                this.hierarchicalNameSet.set(counter, nodeRDNSet.getPayload().get(counter));
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public TopologyNodeRDN extractRDNForNodeType(TopologyNodeTypeEnum nodeType){
        for(TopologyNodeRDN nodeRDN: hierarchicalNameSet){
            if(nodeRDN.getNodeType().equals(nodeType)){
                return(nodeRDN);
            }
        }
        return(null);
    }

    public FDN toTypeBasedFDN(){
        FDN newFDN = new FDN();
        for(TopologyNodeRDN nodeRDN: hierarchicalNameSet){
            newFDN.appendRDN(new RDN(nodeRDN.getNodeType().getNodeElementType(),nodeRDN.getNodeName()));
        }
        return(newFDN);
    }

    public FDN toVersionBasedFDN(){
        FDN newFDN = new FDN();
        for(TopologyNodeRDN nodeRDN: hierarchicalNameSet){
            newFDN.appendRDN(new RDN(nodeRDN.getNodeName(), nodeRDN.getNodeVersion()));
        }
        return(newFDN);
    }
}
