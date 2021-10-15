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

package net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes;

import java.io.Serializable;
import java.util.ArrayList;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;

public class FulfillmentTrackingIdType extends FDNToken implements Serializable {
	
    public FulfillmentTrackingIdType(FDNToken originalToken) {
        this.setContent(new String(originalToken.getContent()));
    }

    public FulfillmentTrackingIdType(){
    	super();
	}

	public FulfillmentTrackingIdType(TopologyNodeFunctionFDNToken nodeFunctionToken){
    	super();
		TopologyNodeFunctionFDN nodeFunctionFDN = new TopologyNodeFunctionFDN(nodeFunctionToken);
		FDN newFDN = new FDN();
		for(TopologyNodeRDN currentRDN: nodeFunctionFDN.getHierarchicalNameSet()) {
			newFDN.appendRDN(new RDN(currentRDN.getNodeName(), currentRDN.getNodeVersion()));
		}
		setContent(newFDN.getToken().getContent());
	}
    

	@Override
	public String toString() {
	        FDN tempFDN = new FDN(this);
	        String simpleString = "ResilienceParcelIdentifier{";
	        ArrayList<RDN> rdnSet = tempFDN.getRDNSet();
	        int setSize = rdnSet.size();
	        for (int counter = 0; counter < setSize; counter++) {
	            RDN currentRDN = rdnSet.get(counter);
	            String currentNameValue = currentRDN.getValue();
	            if(currentNameValue.contains(".")){
	                String outputString = currentNameValue.replace(".", "_");
	                simpleString = simpleString + outputString;
	            } else {
	                simpleString = simpleString + currentNameValue;
	            }
	            if(counter < (setSize - 1)){
	                simpleString = simpleString + ".";
	            }
	        }
	        simpleString = simpleString + "}";
	        return(simpleString);
	}
}
