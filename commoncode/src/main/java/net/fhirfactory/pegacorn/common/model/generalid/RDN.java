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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 *
 * @author Mark A. Hunter
 */
public class RDN implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(RDN.class);
    protected Logger getLogger(){
        return(LOG);
    }

	private String qualifier;
	private String value;

	@JsonIgnore
	public static String RDN_SEPARATOR = "=";

	private static final String tokenEntryQualifierForQualifier = "Qualifier";
	private static final String tokenEntryQualifierForValue = "Value";

	@JsonIgnore
	public RDNToken token;

	@JsonIgnore
	private String rdnToString;
	
	private String rdnAsConciseString;

	@JsonIgnore
	public RDN(String qualifier, String value) {
		getLogger().debug(".RND(String, String): Entry, Qualifier --> {}, Value --> {}", qualifier, value);
		if ((qualifier == null) || (value == null)) {
			throw (new IllegalArgumentException("null name or nameValue passed to Constructor"));
		}
		if ((qualifier.isEmpty()) || (value.isEmpty())) {
			throw (new IllegalArgumentException("Empty name or nameValue passed to Constructor"));
		}
		this.qualifier = qualifier;
		this.value = value;
		convertToString();
		createToken();
		convertToConciseString();
	}

	@JsonIgnore
	public RDN(RDN otherRDN) {
		if (otherRDN == null) {
			throw (new IllegalArgumentException("null otherRDN passed to copy Constructor"));
		}
		this.value = new String(otherRDN.getValue());
		this.qualifier = new String(otherRDN.getQualifier());
		convertToString();
		createToken();
		convertToConciseString();
	}

	@JsonIgnore
	public RDN(RDNToken token) {
		getLogger().debug(".RND(RDNToken): Entry, token --> {}", token);
		if (token == null) {
			throw (new IllegalArgumentException("null RDNToken passed to Constructor"));
		}
		String tokenContent = token.getContent();
		String[] tokenSplit = tokenContent.split("><");
		String qualifierWorking = tokenSplit[0];
		String qualifier = qualifierWorking.substring(1,qualifierWorking.length()-1);
		setQualifier(qualifier);
		String valueWorking = tokenSplit[1];
		String value = valueWorking.substring(0, valueWorking.length()-2);
		setValue(value);
		getLogger().trace(".RND(RDNToken): new RDN created, now building different String values!");
		convertToString();
		createToken();
		convertToConciseString();
		getLogger().trace(".RND(RDNToken): new RDN --> {}", this.rdnToString);
	}

	public String getValue() {
		return (this.value);
	}

	@JsonIgnore
	public void setValue(String newValue) {
		this.value = newValue;
		convertToString();
		createToken();
	}

	public String getQualifier() {
		return (this.qualifier);
	}

	@JsonIgnore
	public void setQualifier(String newQaulifier) {
		this.qualifier = newQaulifier;
		convertToString();
		createToken();
	}

	@Override
	public String toString() {
		return (this.rdnToString);
	}

	@JsonIgnore
	private void convertToString() {
		this.rdnToString = "[RDN=(" + this.getQualifier() + RDN_SEPARATOR + this.getValue() + ")]";
	}

	@JsonIgnore
	private void createToken() {
		this.token= new RDNToken(pseudoXMLAttribute(getQualifier(), getValue()));
	}

	@JsonIgnore
	public RDNToken getToken() {
		return (this.token);
	}
	
	public String getConciseString() {
		return(this.rdnAsConciseString);
	}
	
	private void convertToConciseString() {
		this.rdnAsConciseString = "("+ this.getQualifier() + RDN_SEPARATOR + this.getValue() + ")";
	}

	public String getUnqualifiedValue(){
		return(getValue());
	}

	private String pseudoXMLAttribute(String attributeName, String attributeValue){
		StringBuilder xmlAttributeBuilder = new StringBuilder();
		xmlAttributeBuilder.append("<");
		xmlAttributeBuilder.append(attributeName);
		xmlAttributeBuilder.append(">");
		xmlAttributeBuilder.append(attributeValue);
		xmlAttributeBuilder.append("</");
		xmlAttributeBuilder.append(attributeName);
		xmlAttributeBuilder.append(">");
		return(xmlAttributeBuilder.toString());
	}
}
