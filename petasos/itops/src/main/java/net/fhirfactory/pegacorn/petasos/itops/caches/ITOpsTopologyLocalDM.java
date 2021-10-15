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
package net.fhirfactory.pegacorn.petasos.itops.caches;

import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsTopologyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ITOpsTopologyLocalDM extends ITOpsLocalDMRefreshBase {

    private static final Logger LOG = LoggerFactory.getLogger(ITOpsTopologyLocalDM.class);

    private ITOpsTopologyGraph currentState;

    private ITOpsTopologyGraph reportedState;


    public ITOpsTopologyLocalDM() {
        super();
        LOG.debug(".ITOpsCollatedNodesDM(): Constructor initialisation");
        this.currentState = null;
        this.reportedState = null;
    }

    public ITOpsTopologyGraph getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ITOpsTopologyGraph currentState) {
        this.currentState = currentState;
        refreshCurrentStateUpdateInstant();
    }

    public void stateReported(){
        this.reportedState = this.currentState;
        refreshReportedStateUpdateInstant();
    }

    public ITOpsTopologyGraph getReportedState() {
        return reportedState;
    }

    public void setReportedState(ITOpsTopologyGraph reportedState) {
        this.reportedState = reportedState;
        refreshCurrentStateUpdateInstant();
    }
}
