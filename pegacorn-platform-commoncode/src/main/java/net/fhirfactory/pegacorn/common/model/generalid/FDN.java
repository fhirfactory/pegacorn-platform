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
package net.fhirfactory.pegacorn.common.model.generalid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 * @since 01-June-2020
 * 
 * Note that the LOG'ing level within this class is set to TRACE only, as
 * this set of activities should only be logged if we are really trying to
 * "dig amongst the weeds"!!!
 *
 */
public class FDN implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(FDN.class);
    private ArrayList<RDN> rdnSet;
    private String FDNType;
    private FDNToken token;
    private String fdnToString;
    private String unqualifiedToken;

    private static final String RDN_TO_STRING_ENTRY_SEPERATOR = ".";
    private static final String FDN_TO_STRING_PREFIX = "FDN(";
    private static final String FDN_TO_STRING_SUFFIX = ")";

    private static final String FDN_TOKEN_ID = "FDNToken";

    /**
     * Default Constructor
     */
    public FDN() {
        LOG.trace(".FDN(): Default constructor invoked.");
        this.rdnSet = new ArrayList<RDN>();
        LOG.trace(".FDN(): this.rdnElementSet intialised.");
        this.token = new FDNToken();
        this.fdnToString = new String();
        this.unqualifiedToken = new String();
    }

    /**
     * The Copy Constructor: It creates a duplicate of the original FDN
     * (instantiating new containing elements).
     *
     * @param originalFDN The original FDN
     */
    public FDN(FDN originalFDN) {
        LOG.trace(".FDN( FDN originalFDN ): Constructor invoked, originalFDN --> {}", originalFDN);
        if (originalFDN == null) {
            throw (new IllegalArgumentException("Empty FDN passed to copy Constructor"));
        }
        // Essentially, we iterate through the HashMap from the OriginalFDN, 
        // create new RDN's from the content, and append these RDN's (in the 
        // appropriate order) into the new FDN.
        this.rdnSet = new ArrayList<RDN>();
        ArrayList<RDN> otherRDNSet = originalFDN.getRDNSet();
        if (otherRDNSet.size() != originalFDN.getRDNCount()) {
            throw (new IllegalArgumentException("Malformed FDN passed to copy Constructor"));
        }
        for (int counter = 0; counter < originalFDN.getRDNCount(); counter++) {
            RDN currentRDN = otherRDNSet.get(counter);
            RDN clonedRDN = SerializationUtils.clone(currentRDN);
            this.rdnSet.add(counter, clonedRDN);
        }
        // We need to pre-build the toString() and getToken() content so we don't re-do it 
        // every time we do some comparison etc.
        generateToString();
        generateToken();
        generateUnqualifiedToken();
        LOG.trace(".FDN( FDN originalFDN ): generatedFDN = {}", this.fdnToString);
    }

    /**
     * This constructor uses an FDNToken to construct a new FDN.
     *
     * @param token An FDNToken from which the FDN may be instantiated.
     */
    public FDN(FDNToken token) {
        LOG.debug(".FDN( FDNToken token ): Constructor invoked, token --> {}", token);
        if (token == null) {
            throw (new IllegalArgumentException("Empty parameter passed to Constructor"));
        }
        String tokenContent = token.getContent();
        LOG.trace(".FDN( FDNToken token ): tokenContent --> {}", tokenContent);
        String[] rdnStringEntries = tokenContent.split("><");
        if(rdnStringEntries.length <= 0){
            throw (new IllegalArgumentException("Badly formed FDNToken passed to Constructor, cannot parse -> " + token.getContent()));
        }
        LOG.trace(".FDN(FDNToken token): We have a valid JSONObject for the FDNToken!, now extract content & process");
        this.rdnSet = new ArrayList<RDN>();
        for (int counter = 0; counter < rdnStringEntries.length; counter++) {
            LOG.trace(".FDN( FDNToken token ): Iterating through the extracted Token, attempting to extract RDN[{}]", counter);
            String currentCounterEntry = null;
            for(int loopCounter = 0; counter < rdnStringEntries.length; loopCounter += 1){
                if(rdnStringEntries[loopCounter].startsWith("<"+counter+":")){
                    currentCounterEntry = rdnStringEntries[loopCounter];
                    break;
                }
                if(currentCounterEntry == null){
                    if(rdnStringEntries[loopCounter].startsWith(counter+":")){
                        currentCounterEntry = rdnStringEntries[loopCounter];
                        break;
                    }
                }
            }
            LOG.trace(".FDN( FDNToken token ): processing ->{}", currentCounterEntry);
            // Extract the RDN Type/Qualifier
            String rdnQualifierWorking = null;
            if(currentCounterEntry.startsWith("<"+counter+":")){
                rdnQualifierWorking = currentCounterEntry.replace("<"+counter+":", "");
            } else if(currentCounterEntry.startsWith(counter+":")) {
                rdnQualifierWorking = currentCounterEntry.replace(counter + ":", "");
            }
            int rdnQualifierEnd = rdnQualifierWorking.indexOf(">");
            String rdnQualifier = rdnQualifierWorking.substring(0,rdnQualifierEnd);
            // Extract the RDN Value
            String rdnValueWorking = null;
            if(currentCounterEntry.startsWith("<")) {
                rdnValueWorking = currentCounterEntry.substring(1, currentCounterEntry.length() - 1);
            } else {
                rdnValueWorking = currentCounterEntry;
            }
            int startPoint = rdnValueWorking.indexOf(">");
            int endPoint = rdnValueWorking.indexOf("<");
            String rdnValue = rdnValueWorking.substring(startPoint+1, endPoint);
            LOG.trace(".FDN( FDNToken token ): creating RDN, rdnQualifier->{}, rdnValue->{}", rdnQualifier, rdnValue);
            RDN currentRDN = new RDN(rdnQualifier, rdnValue);
            LOG.trace(".FDN( FDNToken token ): Iterating through the extracted RDNs, current RDN --> {}", currentRDN);
            this.rdnSet.add(counter, currentRDN);
        }

        // We need to pre-build the toString() and getToken() content so we don't re-do it 
        // every time we do some comparison etc.
        generateToString();
        generateToken();
        generateUnqualifiedToken();
    }

    /**
     * This method appends an RDN (Relative Distinguished Name) to an existing
     * FDN. This makes the RDN the "Least Significant" member.
     *
     * @param toBeAddedRDN An RDN that should be appended (injected as the
     * "Least Significant" member of the FDN.
     */
    public void appendRDN(RDN toBeAddedRDN) {
        LOG.trace(".appendRDN(): Entry, toBeAddedRDN --> {}", toBeAddedRDN);
        if (toBeAddedRDN == null) {
            throw (new IllegalArgumentException("Empty RDN passed to appendRDN"));
        }
        RDN newRDN = new RDN(toBeAddedRDN);
        int existingSetSize = this.getRDNCount();
        this.rdnSet.add(existingSetSize, newRDN);
        // We need to pre-build the toString() and getToken() content so we don't re-do it 
        // every time we do some comparison etc.
        generateToString();
        generateToken();
        generateUnqualifiedToken();
        LOG.trace(".appendRDN(): Exit");
    }

    @Override
    public String toString() {
        LOG.trace(".toString(): Entry/Exit");
        return (this.fdnToString);
    }

    /**
     * This method pre-builds the FDN::toString() value. This value
     * (this.fdnToString) cannot be used as input into an FDN constructor and is
     * made available only for the purposes of documentation and/or reporting.
     */
    private void generateToString() {
        LOG.trace(".generateToString(): Entry");
        String toString = FDN_TO_STRING_PREFIX;
        generateToken();
        toString = toString + getToken();
        toString = toString + FDN_TO_STRING_SUFFIX;
        this.fdnToString = toString;
        LOG.trace(".generateToString(): Exit");
    }

    /**
     * FDNs are used to support hierarchical/containment models - where a Parent
     * FDN it total contained within the child FDN. For exmaple, for the
     * following FDN: FDN = (Campus=CHS).(Building=Bulding10),(Floor=3)
     *
     * then the Campus element is the Parent of the Building element. Note that
     * the Floor=? element is the "Least Significant" element.
     *
     * @return Returns the "Parent" FDN of this FDN. The "Parent" FDN is one
     * that has the current "Least Significant" member removed from it.
     */
    public FDN getParentFDN() {
        LOG.trace(".getParentFDN(): Entry");
        if (this.getRDNCount() <= 1) {
            return null;
        }
        FDN newParentFDN = new FDN();
        for (int counter = 0; counter < (this.getRDNCount() - 2); counter++) {
            RDN currentRDN = this.rdnSet.get(counter);
            newParentFDN.appendRDN(currentRDN);
        }
        LOG.trace(".getParentFDN(): Exit");
        return (newParentFDN);
    }

    public RDN getUnqualifiedRDN() {
        LOG.trace(".getUnqualifiedRDN(): Entry");
        if (this.getRDNCount() <= 0) {
            LOG.trace(".getUnqualifiedRDN(): Exit, no RDNs");
            return (null);
        }
        RDN leastSignificantRDN = this.rdnSet.get((this.getRDNCount() - 1));
        LOG.trace(".getUnqualifiedRDN(): Exit, least signifcant RDN --> {}", leastSignificantRDN);
        return (leastSignificantRDN);
    }

    public boolean isEmpty() {
        LOG.trace(".isEmpty(): Entry");
        if (this.getRDNCount() <= 0) {
            LOG.trace(".isEmpty(): Exit, returned --> true");
            return (true);
        } else {
            LOG.trace(".isEmpty(): Exit, returned --> false");
            return (false);
        }
    }

    public ArrayList<RDN> getRDNSet() {
        LOG.trace(".getRDNSet(): Entry/Exit");
        return (this.rdnSet);
    }

    public int getRDNCount() {
        LOG.trace(".getRDNCount(): Entry/Exit");
        return (this.rdnSet.size());
    }

    public FDNToken getToken() {
        LOG.trace(".getToken(): Entry/Exit");
        return (this.token);
    }

    private void generateToken() {
        LOG.trace(".generateToken(): Entry");
        StringBuilder tokenBuilder = new StringBuilder();
        for (int counter = 0; counter < this.getRDNCount(); counter++) {
            RDN currentRDN = this.rdnSet.get(counter);
            String currentEntry = pseudoXMLAttribute(counter, currentRDN.getQualifier(), currentRDN.getValue());
            tokenBuilder.append(currentEntry);
        }
        this.token = new FDNToken(tokenBuilder.toString());
        LOG.trace(".generateToken(): Exit");
    }

    private String pseudoXMLAttribute(int order, String attributeName, String attributeValue){
        StringBuilder xmlAttributeBuilder = new StringBuilder();
        xmlAttributeBuilder.append("<");
        xmlAttributeBuilder.append(order);
        xmlAttributeBuilder.append(":");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        xmlAttributeBuilder.append(attributeValue);
        xmlAttributeBuilder.append("</");
        xmlAttributeBuilder.append(order);
        xmlAttributeBuilder.append(":");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        return(xmlAttributeBuilder.toString());
    }

    private void generateUnqualifiedToken() {
        LOG.trace(".generateUnqualifiedToken(): Entry");
        String newUnqualifiedToken = new String();
        for (int counter = 0; counter < this.getRDNCount(); counter++) {
            RDN currentRDN = this.rdnSet.get(counter);
            String value = currentRDN.getValue().replace(".", "_");
            newUnqualifiedToken = newUnqualifiedToken + value;
            if(counter < (this.getRDNCount()-1)){
                newUnqualifiedToken = newUnqualifiedToken + ".";
            }
        }
        this.unqualifiedToken = newUnqualifiedToken;
        LOG.trace(".generateUnqualifiedToken(): Exit");
    }
    
    public String getID() {
        LOG.trace(".getID(): Entry");
        String id = new String();
        int depthCount = this.getRDNCount();
        for (int counter = 0; counter < depthCount; counter++) {
            RDN currentRDN = this.rdnSet.get(counter);
            id = id + currentRDN.getQualifier() + "=" + currentRDN.getValue();
            if(counter < (depthCount - 1)) {
            	id = id + ".";
            }
        }
        LOG.trace(".generateUnqualifiedToken(): Exit, Id --> {}", id);
        return(id);
    }

    public String getUnqualifiedToken() {
        LOG.trace(".getUnqualifiedToken(): Entry/Exit");
        return unqualifiedToken;
    }

    public String getFDNType() {
        LOG.trace(".getFDNType(): Entry/Exit");
        return FDNType;
    }

    public void setFDNType(String FDNType) {
        LOG.trace(".setFDNType(): Entry/Exit");
        this.FDNType = FDNType;
    }

    public void appendFDN(FDN additionalFDN) {
        LOG.trace(".appendFDN(): Entry, additionalFDN --> {}", additionalFDN );
        if (additionalFDN == null) {
            LOG.trace(".appendFDN(): Exit, nothing to add, additionFDN is null");
            return;
        }
        int additionalFDNSize = additionalFDN.getRDNCount();
        ArrayList<RDN> additionalRDNSet = additionalFDN.getRDNSet();
        for (int counter = 0; counter < additionalFDNSize; counter++) {
            this.appendRDN(additionalRDNSet.get(counter));
        }
        generateToString();
        generateToken();
        generateUnqualifiedToken();
        LOG.trace(".appendFDN: Exit");
    }
    
    public RDN extractRDNViaQualifier(String qualifier){
        LOG.trace(".extractRDNViaQualifier(): Entry, qualifier --> {}",qualifier );
        for (RDN currentRDN: this.rdnSet) {
            boolean matches = currentRDN.getQualifier().contentEquals(qualifier);
            if(matches){
                return(currentRDN);
            }
        }
        return(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FDN fdn = (FDN) o;
        String thisFDNToken = this.getToken().toFullString();
        String otherFDNToken = fdn.getToken().toFullString();
        boolean equalityTest = thisFDNToken.contentEquals(otherFDNToken);
        return (equalityTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rdnSet);
    }
}
