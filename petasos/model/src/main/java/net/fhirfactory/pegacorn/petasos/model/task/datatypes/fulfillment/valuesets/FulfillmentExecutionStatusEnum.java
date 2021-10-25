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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.fulfillment.valuesets;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum FulfillmentExecutionStatusEnum {
    FULFILLMENT_EXECUTION_STATUS_UNREGISTERED("Unregistered", "petasos.fulfillment.execution_status.unregistered"),
    FULFILLMENT_EXECUTION_STATUS_REGISTERED("Registered", "petasos.fulfillment.execution_status.registered"),
    FULFILLMENT_EXECUTION_STATUS_CANCELLED("Cancelled", "petasos.fulfillment.execution_status.cancelled"),
    FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED("NoActionRequired", "petasos.fulfillment.execution_status.no_action_required"),
    FULFILLMENT_EXECUTION_STATUS_INITIATED("Initiated", "petasos.fulfillment.execution_status.initiated"),
    FULFILLMENT_EXECUTION_STATUS_ACTIVE("Active", "petasos.fulfillment.execution_status.active"),
    FULFILLMENT_EXECUTION_STATUS_ACTIVE_ELSEWHERE("ActiveElsewhere","petasos.fulfillment.execution_status.active_elsewhere"),
    FULFILLMENT_EXECUTION_STATUS_FINISHED("Finished","petasos.fulfillment.execution_status.finished"),
    FULFILLMENT_EXECUTION_STATUS_FINALISED("Finalised","petasos.fulfillment.execution_status.finalised"),
    FULFILLMENT_EXECUTION_STATUS_FAILED("Failed", "petasos.petasos.agent.parcel.status.failed"),
	FULFILLMENT_EXECUTION_STATUS_FINISHED_ELSEWHERE("FinishedElsewhere", "petasos.fulfillment.execution_status.finished_elsewhere"),
	FULFILLMENT_EXECUTION_STATUS_FINALISED_ELSEWHERE("FinalisedElsewhere", "petasos.fulfillment.execution_status.finalised_elsewhere");
    
    private String token;
    private String displayName;
    
    private FulfillmentExecutionStatusEnum(String name, String token){
        this.token = token;
        this.displayName = name;
    }
    
    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }
}
