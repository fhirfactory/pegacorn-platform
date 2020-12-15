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
package net.fhirfactory.pegacorn.petasos.model.audit;

public enum AuditLevelEnum {
    AUDIT_LEVEL_BLACK_BOX("pegacorn.platform.petasos.audit_level.systemofsystems"),
	AUDIT_LEVEL_COARSE("pegacorn.platform.petasos.audit_level.processingplant"),
    AUDIT_LEVEL_FINE("pegacorn.platform.petasos.audit_level.workshop"),
    AUDIT_LEVEL_VERY_FINE("pegacorn.platform.petasos.audit_level.wup"),
    AUDIT_LEVEL_EXTREME("pegacorn.platform.petasos.audit_level.thread");

    private String auditLevel;

    private AuditLevelEnum(String auditLevel){
        this.auditLevel = auditLevel;
    }

    public String getAuditLevel(){
        return(this.auditLevel);
    }
}
