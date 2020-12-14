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
package net.fhirfactory.pegacorn.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Objects;

public class FDNToken {

    private String content;

    public FDNToken() {
        content = new String();
    }

    public FDNToken(String tokenContent) {
        this.content = new String(tokenContent);
    }

    public FDNToken(FDNToken originalToken) {
        this.content = new String(originalToken.getContent());
    }

    public String getContent() {
        return (this.content);
    }

    public void setContent(String tokenContent) {
        this.content = new String(tokenContent);
    }

    @Override
    public String toString() {
        return (makeSimpleString());
    }

    private String makeSimpleString(){
        FDN tempFDN = new FDN(this);
        String simpleString = "SimpleFDN=";
        ArrayList<RDN> rdnSet = tempFDN.getRDNSet();
        int setSize = rdnSet.size();
        for (int counter = 0; counter < setSize; counter++) {
            RDN currentRDN = rdnSet.get(counter);
            String currentValue = currentRDN.getValue();
            if(currentValue.contains(".")){
                String outputString = currentValue.replace(".", "_");
                simpleString = simpleString + outputString;
            } else {
                simpleString = simpleString + currentValue;
            }
            if(counter < (setSize - 1)){
                simpleString = simpleString + ".";
            }
        }
        return(simpleString);
    }

    public String toFullString(){
        return(this.content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FDNToken fdnToken = (FDNToken) o;
        boolean equalityTest = fdnToken.getContent().contentEquals(this.getContent());
        return (equalityTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent());
    }

    public String toTag(){
        String tag = new String();
        FDNToken tempToken = new FDNToken();
        tempToken.setContent(this.getContent());
        FDN tempFDN = new FDN(tempToken);
        int setSize = tempFDN.getRDNSet().size();
        int counter = 0;
        for (RDN currentRDN: tempFDN.getRDNSet()){
            String rdnValueEntry = currentRDN.getValue();
            if (rdnValueEntry.contains(".")) {
                String outputString = rdnValueEntry.replace(".", "_");
                tag = tag + outputString;
            } else {
                tag = tag + rdnValueEntry;
            }
            if (counter < (setSize - 1)) {
                tag = tag + ".";
            }
            counter++;
        }
        return(tag);
    }

    @JsonIgnore
    public String getUnqualifiedToken(){
        FDNToken tempToken = new FDNToken();
        tempToken.setContent(this.getContent());
        FDN tempFDN = new FDN(tempToken);
        return(tempFDN.getUnqualifiedToken());
    }
}