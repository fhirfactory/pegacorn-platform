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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Mark A. Hunter
 * @since 2020-07-05
 */
@Dependent
public class WUPIngresConduit {

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;

    private static final Logger LOG = LoggerFactory.getLogger(WUPIngresConduit.class);
    /**
     * This function strips the WUPJobCard and ParcelStatusElement from the ingresParcel, and injects them into the
     * Camel Exchange element for extraction by the WUPEgressConduit module. It then extracts the actual UoW and
     * returns this for forwarding into the WUP itself. This way, the only thing the Business Logic developer need
     * worry about is the UoW on which they are acting.
     *
     * @param ingresPacket The WorkUnitTransportPacket for the associated UoW - containing the WUPJobCard & ParcelStatusElement for the activity
     * @param camelExchange The Apache Camel Exchange object, for injecting the WUPJobCard & ParcelStatusElement into
     * @return A UoW (Unit of Work) object for injection into the WUP for processing by the Business Logic
     */
    public UoW forwardIntoWUP(WorkUnitTransportPacket ingresPacket, Exchange camelExchange){
        LOG.debug(".forwardIntoWUP(): Entry, ingresParcel->{}", ingresPacket);
        UoW theUoW = ingresPacket.getPayload();
        camelExchange.setProperty(PetasosPropertyConstants.WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME, ingresPacket.getCurrentJobCard());
        camelExchange.setProperty(PetasosPropertyConstants.WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME, ingresPacket.getCurrentParcelStatus());
        camelExchange.setProperty(PetasosPropertyConstants.WUP_CURRENT_UOW_EXCHANGE_PROPERTY_NAME, theUoW);
        //
        // Because auditing is not running yet
        // Remove once Auditing is in place
        //
        LOG.debug("ProcessingMessage->{}", theUoW.getIngresContent().getPayload());
        //
        //
        //

        LOG.debug(".forwardIntoWUP(): Exit, returning the UoW --> {}", theUoW);
        return(theUoW);
    }
}
