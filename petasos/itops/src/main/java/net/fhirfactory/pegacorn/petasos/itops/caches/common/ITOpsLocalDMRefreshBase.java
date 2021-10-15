/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.itops.caches.common;

import java.time.Instant;

public abstract class ITOpsLocalDMRefreshBase {
    private Instant currentStateUpdateInstant;
    private Instant reportedStateUpdateInstant;

    public ITOpsLocalDMRefreshBase(){
        refreshCurrentStateUpdateInstant();
        refreshReportedStateUpdateInstant();
    }

    public Instant getCurrentStateUpdateInstant() {
        return currentStateUpdateInstant;
    }

    public void setCurrentStateUpdateInstant(Instant currentStateUpdateInstant) {
        this.currentStateUpdateInstant = currentStateUpdateInstant;
    }

    public Instant getReportedStateUpdateInstant() {
        return reportedStateUpdateInstant;
    }

    public void setReportedStateUpdateInstant(Instant reportedStateUpdateInstant) {
        this.reportedStateUpdateInstant = reportedStateUpdateInstant;
    }

    public void refreshCurrentStateUpdateInstant(){
        this.currentStateUpdateInstant = Instant.now();
    }

    public void refreshReportedStateUpdateInstant(){
        this.reportedStateUpdateInstant = Instant.now();
    }

    public boolean isUpdated(){
        if(getCurrentStateUpdateInstant().isAfter(getReportedStateUpdateInstant())){
            return(true);
        } else {
            return(false);
        }
    }
}
