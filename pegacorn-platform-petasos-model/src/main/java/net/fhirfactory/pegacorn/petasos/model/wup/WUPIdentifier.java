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

package net.fhirfactory.pegacorn.petasos.model.wup;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Mark A. Hunter
 * @since 2020-08-07
 */
public class WUPIdentifier extends TopologyNodeFDNToken implements Serializable {
	
    public WUPIdentifier(TopologyNodeFDNToken originalToken) {
        this.setTokenValue(SerializationUtils.clone(originalToken.getTokenValue()));
    }
    public WUPIdentifier(){super();}
	
	@Override
	public String toString() {

		TopologyNodeFDN tempFDN = new TopologyNodeFDN(this.getTokenValue());
		String simpleString = "WUPIdentifier{";
		ArrayList<TopologyNodeRDN> rdnSet = tempFDN.getHierarchicalNameSet();
		int setSize = rdnSet.size();
		for (int counter = 0; counter < setSize; counter++) {
			TopologyNodeRDN currentRDN = rdnSet.get(counter);
			String currentNameValue = currentRDN.getNodeName();
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
