/*
 * Copyright (c) 2020 MAHun
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
package net.fhirfactory.pegacorn.petasos.model.task.segments.status.valuesets;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum OutcomeStatusEnum {
    OUTCOME_STATUS_UNKNOWN("pegacorn.outcome.status.unknown"),
    OUTCOME_STATUS_CANCELLED("pegacorn.outcome.status.cancelled"),
    OUTCOME_STATUS_ACTIVE("pegacorn.outcome.status.active"),
    OUTCOME_STATUS_FINISHED("pegacorn.outcome.status.finished"),
    OUTCOME_STATUS_FINALISED("pegacorn.outcome.status.finalised"),
    OUTCOME_STATUS_FAILED("pegacorn.outcome.status.failed");

    private String outcomeStatus;
    
    private OutcomeStatusEnum(String executionStatus){
        this.outcomeStatus = executionStatus;
    }
    
    public String getOutcomeStatus(){
        return(this.outcomeStatus);
    }    
}
