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
package net.fhirfactory.pegacorn.petasos.model.task.segments.reporting.valuesets;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum TaskOutcomeReportingEnum {
    TASK_OUTCOME_SUCCESS("pegacorn.task.outcome_report.success"),
    TASK_OUTCOME_FAILED("pegacorn.task.outcome_report.failed"),
    TASK_OUTCOME_INCOMPLETE("pegacorn.task.outcome_report.incomplete"),
    TASK_OUTCOME_NO_PROCESSING_REQUIRED("pegacorn.task.outcome_report.nonerequired"),
    TASK_OUTCOME_NOTSTARTED("pegacorn.task.outcome_report.not_started");
    
    private String outcome;
    
    private TaskOutcomeReportingEnum(String processingOutcome ){
        this.outcome = processingOutcome;
    }
    
    public String getUoWProcessingOutcome(){
        return(this.outcome);
    }
    
}
