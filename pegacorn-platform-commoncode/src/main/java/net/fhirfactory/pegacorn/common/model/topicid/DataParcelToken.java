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
public class DataParcelToken {
    private FDNToken token;
    private String version;
    private DataParcelNormalisationStatusEnum normalisationStatus;
    private DataParcelValidationStatusEnum validationStatus;

    public static final String WILDCARD_QUALIFIER = "Wildcard";
    public static final String WILDCARD_VALUE = "*";

    public DataParcelToken(FDNToken datasetToken, String datasetVersion) {
        this.token = new FDNToken(datasetToken);
        this.version = new String(datasetVersion);
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
    }

    public DataParcelToken() {
        this.token = null;
        this.version = null;
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
    }

    public DataParcelToken(DataParcelToken originalToken){
        this.token = new FDNToken(originalToken.getToken());
        this.version = new String(originalToken.getVersion());
        this.normalisationStatus = originalToken.getNormalisationStatus();
        this.validationStatus = originalToken.getValidationStatus();
    }

    public DataParcelNormalisationStatusEnum getNormalisationStatus() {
        return normalisationStatus;
    }

    public void setNormalisationStatus(DataParcelNormalisationStatusEnum normalisationStatus) {
        this.normalisationStatus = normalisationStatus;
    }

    public DataParcelValidationStatusEnum getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(DataParcelValidationStatusEnum validationStatus) {
        this.validationStatus = validationStatus;
    }

    public FDNToken getToken() {
        return token;
    }

    public void setToken(FDNToken token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addDiscriminator(String descriminatorType, String descriminatorValue){
        FDN existingIdentifierFDN = new FDN(this.token);
        existingIdentifierFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType(), descriminatorType));
        existingIdentifierFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType(), descriminatorValue));
        this.token = existingIdentifierFDN.getToken();
    }

    /**
     * This function removes the Discriminator from a TopicToken Identifier.
     */
    public void removeDiscriminator(){
        FDN existingIdentifierFDN = new FDN(this.token);
        ArrayList<RDN> rdnSet = existingIdentifierFDN.getRDNSet();
        FDN newFDN = new FDN();
        int counter = 0;
        for(RDN rdn: rdnSet){
            boolean hasDataSetQualifierValue = rdn.getQualifier().contentEquals(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType());
            boolean hasDataSetQualifierType = rdn.getQualifier().contentEquals(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType());
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
        this.token = newFDN.getToken();
    }

    @Override
    public String toString() {
        return "DataParcelToken{" +
                "token=" + token +
                ", version='" + version + '\'' +
                ", normalisationStatus=" + normalisationStatus +
                ", validationStatus=" + validationStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataParcelToken)) return false;
        DataParcelToken that = (DataParcelToken) o;
        return getToken().equals(that.getToken()) && getVersion().equals(that.getVersion()) && getNormalisationStatus() == that.getNormalisationStatus() && getValidationStatus() == that.getValidationStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getVersion(), getNormalisationStatus(), getValidationStatus());
    }

    public static String getWildcardQualifier() {
        return WILDCARD_QUALIFIER;
    }

    public static String getWildcardValue() {
        return WILDCARD_VALUE;
    }
}
