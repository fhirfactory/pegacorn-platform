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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Mark A. Hunter
 * @since 2020-08-07
 */
public class TopologyNodeFunctionFDN extends TopologyNodeFDN {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyNodeFunctionFDN.class);

    public TopologyNodeFunctionFDN(TopologyNodeFunctionFDN oriFDN){
        super(oriFDN);
    }

    public TopologyNodeFunctionFDN(){
        super();
    }

    public TopologyNodeFunctionFDN(TopologyNodeFunctionFDNToken token){
        LOG.debug(".TopologyNodeFunctionFDN(): Entry, token->{}", token);
        ArrayList<TopologyNodeRDN> nodeSet= new ArrayList<>();
        try{
            JsonMapper mapper = new JsonMapper();
            TopologyNodeRDNSet nodeRDNSet = mapper.readValue(token.getToken(), TopologyNodeRDNSet.class);
            int rdnCount = nodeRDNSet.getPayload().size();
            LOG.trace(".TopologyNodeFunctionFDN: RDN Count --> {}", rdnCount);
            for(int counter = 0; counter < rdnCount; counter ++){
                TopologyNodeRDN currentRDN = nodeRDNSet.getPayload().get(counter);
                nodeSet.add(currentRDN);
            }
            LOG.trace(".TopologyNodeFunctionFDN: Built nodeSet");
            setHierarchicalNameSet(nodeSet);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JsonIgnore
    public TopologyNodeFunctionFDNToken getFunctionToken(){
        LOG.debug(".getFunctionToken(): Entry");
        TopologyNodeRDNSet nodeRDNSet = new TopologyNodeRDNSet(this.getHierarchicalNameSet());
        String tokenString = null;
        try{
            JsonMapper mapper = new JsonMapper();
            tokenString = mapper.writeValueAsString(nodeRDNSet);
            LOG.trace(".getFunctionToken(): Generated tokenString, value->{}", tokenString);
        } catch(JsonProcessingException jsonException){
            jsonException.printStackTrace();
            tokenString = "";
        }
        TopologyNodeFunctionFDNToken newToken = new TopologyNodeFunctionFDNToken(tokenString);
        return(newToken);
    }
}
