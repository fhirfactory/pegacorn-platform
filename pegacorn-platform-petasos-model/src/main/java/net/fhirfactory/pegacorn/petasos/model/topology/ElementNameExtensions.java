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


import javax.inject.Singleton;

/**
 * @author Mark A. Hunter
 */

@Singleton
public class ElementNameExtensions {
    private static String END_POINT_WUP_CONTAINER_INGRES_PROCESSOR_INGRES = ".WUPContainer.IngresProcessor.Ingres";
    private static String END_POINT_WUP_CONTAINER_INGRES_PROCESSOR_EGRESS = ".WUPContainer.IngresProcessor.Egress";
    private static String END_POINT_WUP_INGRES = ".Ingres";
    private static String END_POINT_WUP_EGRESS = ".Egress";
    private static String END_POINT_WUP_CONTAINER_EGRESS_PROCESSOR_INGRES = ".WUPContainer.EgressProcessor.Ingres";
    private static String END_POINT_WUP_CONTAINER_EGRESS_PROCESSOR_EGRESS = ".WUPContainer.EgressProcessor.Egress";
    private static String END_POINT_INTERCHANGE_PAYLOAD_TRANSFORMER_INGRES = ".Interchange.PayloadTransformer.Ingres";
    private static String END_POINT_INTERCHANGE_PAYLOAD_TRANSFORMER_EGRESS = ".Interchange.PayloadTransformer.Egress";
    private static String END_POINT_INTERCHANGE_ROUTER_INGRES = ".Interchange.Router.Ingres";
    private static String END_POINT_INTERCHANGE_ROUTER_EGRESS = ".Interchange.Router.Egress";

    private static String PROCESSOR_WUP_CONTAINER_INGRES_PROCESSOR = ".WUPContainer.IngresProcessor";
    private static String PROCESSOR_WUP_CONTAINER_EGRESS_PROCESSOR = ".WUPContainer.EgressProcessor";
    private static String PROCESSOR_INTERCHANGE_PAYLOAD_TRANSFORMER = ".Interchange.PayloadTransformer";
    private static String PROCESSOR_INTERCHANGE_ROUTER = ".Interchange.Router";

    public static String getEndPointWupContainerIngresProcessorIngres() {
        return END_POINT_WUP_CONTAINER_INGRES_PROCESSOR_INGRES;
    }

    public static String getEndPointWupContainerIngresProcessorEgress() {
        return END_POINT_WUP_CONTAINER_INGRES_PROCESSOR_EGRESS;
    }

    public static String getEndPointWupIngres() {
        return END_POINT_WUP_INGRES;
    }

    public static String getEndPointWupEgress() {
        return END_POINT_WUP_EGRESS;
    }

    public static String getEndPointWupContainerEgressProcessorIngres() {
        return END_POINT_WUP_CONTAINER_EGRESS_PROCESSOR_INGRES;
    }

    public static String getEndPointWupContainerEgressProcessorEgress() {
        return END_POINT_WUP_CONTAINER_EGRESS_PROCESSOR_EGRESS;
    }

    public static String getEndPointInterchangePayloadTransformerIngres() {
        return END_POINT_INTERCHANGE_PAYLOAD_TRANSFORMER_INGRES;
    }

    public static String getEndPointInterchangePayloadTransformerEgress() {
        return END_POINT_INTERCHANGE_PAYLOAD_TRANSFORMER_EGRESS;
    }

    public static String getEndPointInterchangeRouterIngres() {
        return END_POINT_INTERCHANGE_ROUTER_INGRES;
    }

    public static String getEndPointInterchangeRouterEgress() {
        return END_POINT_INTERCHANGE_ROUTER_EGRESS;
    }

    public static String getProcessorWupContainerIngresProcessor() {
        return PROCESSOR_WUP_CONTAINER_INGRES_PROCESSOR;
    }

    public static String getProcessorWupContainerEgressProcessor() {
        return PROCESSOR_WUP_CONTAINER_EGRESS_PROCESSOR;
    }

    public static String getProcessorInterchangePayloadTransformer() {
        return PROCESSOR_INTERCHANGE_PAYLOAD_TRANSFORMER;
    }

    public static String getProcessorInterchangeRouter() {
        return PROCESSOR_INTERCHANGE_ROUTER;
    }
}
