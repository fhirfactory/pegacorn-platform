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

package net.fhirfactory.pegacorn.petasos.model.topology;

import java.util.Objects;

public class LinkElement {
    private EndpointElementIdentifier endpointA;
    private EndpointElementIdentifier endpointB;
    private LinkElementIdentifier linkID;
    private String friendlyName;
    private LinkElementStatusEnum status;
    private LinkElementTypeEnum linkType;
    private String toStringString;

    public LinkElement(){
        this.endpointA = null;
        this.endpointB = null;
        this.linkID = null;
        this.friendlyName = null;
        this.status = null;
        this.linkType = null;
        this.toStringString = null;
    }

    public EndpointElementIdentifier getEndpointA() {
        return endpointA;
    }

    public void setEndpointA(EndpointElementIdentifier endpointA) {
        this.endpointA = endpointA;
        generateToString();
    }

    public EndpointElementIdentifier getEndpointB() {
        return endpointB;
    }

    public void setEndpointB(EndpointElementIdentifier endpointB) {
        this.endpointB = endpointB;
        generateToString();
    }

    public LinkElementIdentifier getLinkID() {
        return linkID;
    }

    public void setLinkID(LinkElementIdentifier linkID) {
        this.linkID = linkID;
        generateToString();
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        generateToString();
    }

    public LinkElementStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LinkElementStatusEnum status) {
        this.status = status;
        generateToString();
    }

    public LinkElementTypeEnum getLinkType() {
        return linkType;
    }

    @Override
    public String toString() {
        return (this.toStringString);
    }

    private void generateToString(){
        this.toStringString =  "LinkElement{" +
                "(endpointA:" + endpointA + ")," +
                "(endpointB:" + endpointB + ")," +
                "(linkID:" + linkID + ")," +
                "(friendlyName:" + friendlyName + ")," +
                "(status:" + status + ")," +
                "(linkType:" + linkType + ")" +
                '}';
    }

    public boolean equals(LinkElement otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        boolean endpointAIsEqual = this.endpointA.equals(otherObject.getEndpointA());
        boolean endpointBIsEqual = this.endpointB.equals(otherObject.getEndpointB());
        boolean linkIDIsEqual = this.linkID.equals(otherObject.getLinkID());
        boolean friendlyNameIsEqual = this.friendlyName.equals(otherObject.getFriendlyName());
        boolean statusIsEqual = this.status.equals(otherObject.getStatus());
        boolean linkTypeIsEqual = this.linkType.equals(otherObject.getLinkType());
        boolean isEqual = endpointAIsEqual && endpointBIsEqual && linkIDIsEqual && friendlyNameIsEqual && statusIsEqual && linkTypeIsEqual;
        return (isEqual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpointA, endpointB, linkID, friendlyName, status, linkType);
    }
}
