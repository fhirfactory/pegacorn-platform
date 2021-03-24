/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.common.model.topicid;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Mark A. Hunter
 * @since 2020-08-01
 */
public class TopicToken {
    private FDNToken identifier;
    private String version;

    public static final String WILDCARD_QUALIFIER = "Wildcard";
    public static final String WILDCARD_VALUE = "*";

    public TopicToken(FDNToken datasetToken, String datasetVersion) {
        this.identifier = datasetToken;
        this.version = datasetVersion;
    }

    public TopicToken() {
        this.identifier = null;
        this.version = null;
    }

    public TopicToken(TopicToken originalToken){
        this.identifier = originalToken.getIdentifier();
        this.version = originalToken.getVersion();
    }

    public FDNToken getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FDNToken identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addDescriminator(String descriminatorType, String descriminatorValue){
        FDN existingIdentifierFDN = new FDN(this.identifier);
        existingIdentifierFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType(), descriminatorType));
        existingIdentifierFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType(), descriminatorValue));
        this.identifier = existingIdentifierFDN.getToken();
    }

    /**
     * This function removes the Descriminator from a TopicToken Identifier.
     */
    public void removeDescriminator(){
        FDN existingIdentifierFDN = new FDN(this.identifier);
        ArrayList<RDN> rdnSet = existingIdentifierFDN.getRDNSet();
        FDN newFDN = new FDN();
        int counter = 0;
        for(RDN rdn: rdnSet){
            boolean hasDataSetQualifierValue = rdn.getQualifier().contentEquals(TopicTypeEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType());
            boolean hasDataSetQualifierType = rdn.getQualifier().contentEquals(TopicTypeEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType());
            if(!(hasDataSetQualifierValue || hasDataSetQualifierType)){
                if(counter < 4) {
                    newFDN.appendRDN(rdn);
                    counter += 1;
                }
                if(counter >= 4){
                    break;
                }
            }
        }
        this.identifier = newFDN.getToken();
        /*
        boolean hasDataSetQualifierValue =  existingIdentifierFDN.getUnqualifiedRDN().getQualifier().contentEquals(TopicTypeEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType());
        boolean hasDataSetQualifierType = existingIdentifierFDN.getParentFDN().getUnqualifiedRDN().getQualifier().contentEquals(TopicTypeEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType());
        if(!hasDataSetQualifierValue || !hasDataSetQualifierType){
            return;
        }
        if(existingIdentifierFDN.getRDNCount() == 5){
            FDN newFDN = existingIdentifierFDN.getParentFDN();
            this.setIdentifier(newFDN.getToken());
        }
        if(existingIdentifierFDN.getRDNCount() == 6){
            FDN newFDN1 = existingIdentifierFDN.getParentFDN();
            FDN newFDN2 = newFDN1.getParentFDN();
            this.setIdentifier(newFDN2.getToken());
        }
        */
    }

    @Override
    public String toString() {
        return "TopicToken{(identifier=" + identifier.toTag() + "),(version=" + version + ")}";
    }

    @Override
    public boolean equals(Object otherToken) {
        if (this == otherToken) return true;
        if ((otherToken == null) || (getClass() != otherToken.getClass())) return false;
        TopicToken otherTopicToken = (TopicToken)otherToken;
        if (!this.getIdentifier().equals(otherTopicToken.getIdentifier())) return false;
        if (!this.getVersion().contentEquals(otherTopicToken.getVersion())) return false;
        return(true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), getVersion());
    }

    public boolean wildcardMatch(TopicToken otherTopicToken){
        boolean versionMatches = false;
        if((otherTopicToken.getVersion() == null) || (otherTopicToken.getVersion().contentEquals("*")) || (otherTopicToken.getVersion().contentEquals(getVersion()))){
            versionMatches = true;
        }
        if(getIdentifier().equals(otherTopicToken.getIdentifier()) && versionMatches){
            return(true);
        }
        FDN thisFDN = new FDN(getIdentifier());
        FDN thatFDN = new FDN(otherTopicToken.getIdentifier());
        int fdnSize = thisFDN.getRDNCount();
        if(fdnSize > thatFDN.getRDNCount()){
            fdnSize = thatFDN.getRDNCount();
        }
        ArrayList<RDN> thisRDNArray = thisFDN.getRDNSet();
        ArrayList<RDN> thatRDNArray = thatFDN.getRDNSet();
        for(int counter = 0; counter < fdnSize; counter ++){
            boolean contentSame = thisRDNArray.get(counter).getQualifier().contentEquals(thatRDNArray.get(counter).getQualifier());
            boolean hasWildcard = thatRDNArray.get(counter).getQualifier().contentEquals(getWildcardQualifier())
                    || thatRDNArray.get(counter).getValue().equals(getWildcardValue())
                    || thisRDNArray.get(counter).getQualifier().contentEquals(getWildcardQualifier())
                    || thisRDNArray.get(counter).getValue().contentEquals(getWildcardValue());
            if(!contentSame && !hasWildcard){
                return(false);
            }
        }
        return(true);
    }

    public static String getWildcardQualifier() {
        return WILDCARD_QUALIFIER;
    }

    public static String getWildcardValue() {
        return WILDCARD_VALUE;
    }
}
