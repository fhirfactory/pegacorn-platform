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
package net.fhirfactory.pegacorn.petasos.model.uow;

import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.generalid.RDN;

import java.io.Serializable;

public class UoWIdentifier extends FDNToken implements Serializable {
	
    public UoWIdentifier(FDNToken originalToken) {
        this.setContent(new String(originalToken.getContent()));
    }
    
    public UoWIdentifier(){super();}

    @Override
    public String toString() {
        FDNToken tempToken = new FDNToken();
        tempToken.setContent(this.getContent());
        FDN tempFDN = new FDN(tempToken);
        String simpleString = "UoWIdentifier{";
        int setSize = tempFDN.getRDNSet().size();
        int counter = 0;
        for (RDN currentRDN: tempFDN.getRDNSet()) {
            String rdnValueEntry = currentRDN.getValue();
            if (rdnValueEntry.contains(".")) {
                String outputString = rdnValueEntry.replace(".", "_");
                simpleString = simpleString + outputString;
            } else {
                simpleString = simpleString + rdnValueEntry;
            }
            if (counter < (setSize - 1)) {
                simpleString = simpleString + ".";
            }
            counter++;
        }
        simpleString = simpleString + "}";
        return (simpleString);
    }

}
