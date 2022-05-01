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
package net.fhirfactory.pegacorn.petasos.core.moa.pathway.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.petasos.audit.brokers.MOAServicesAuditBroker;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.OnExceptionDefinition;

public abstract class BasePetasosContainerRoute extends BaseRouteBuilder {

    private MOAServicesAuditBroker auditTrailBroker;

    //
    // Constructor(s)
    //

    public BasePetasosContainerRoute(CamelContext camelContext, MOAServicesAuditBroker auditTrailBroker){
        super(camelContext);
        this.auditTrailBroker = auditTrailBroker;
    }

    //
    // Exception Handlers
    //

    protected OnExceptionDefinition specifyCamelExecutionExceptionHandler() {
        OnExceptionDefinition exceptionDef = onException(CamelExecutionException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "Camel Execution Handler...")
                .bean(auditTrailBroker, "logCamelExecutionException(*, Exchange)");
        return(exceptionDef);
    }

    //
    // Getters (and Setters)
    //

    protected MOAServicesAuditBroker getAuditTrailBroker() {
        return auditTrailBroker;
    }
}
