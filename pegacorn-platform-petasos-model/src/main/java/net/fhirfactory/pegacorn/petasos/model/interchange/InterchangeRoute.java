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
package net.fhirfactory.pegacorn.petasos.model.interchange;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;

import java.time.Instant;

/**
 *
 * @author Mark A. Hunter
 * @since 07-Jun-2020
 * 
 */
public class InterchangeRoute {
    private FDN producingWUPFDN;
    private FDN outboundUoWTypeFDN;
    private FDN consumingWUPFDN;
    private String instanceDiscriminator;

    public InterchangeRoute(){
        this.consumingWUPFDN = new FDN();
        this.outboundUoWTypeFDN = new FDN();
        this.producingWUPFDN = new FDN();
        Long rightNow = Instant.now().getEpochSecond();
        this.instanceDiscriminator = Long.toHexString(rightNow);
    }

    public InterchangeRoute(FDN producingWUP, FDN outboundUoWType, FDN consumingWUP){
        this.consumingWUPFDN = new FDN(consumingWUP);
        this.outboundUoWTypeFDN = new FDN(outboundUoWType);
        this.producingWUPFDN = new FDN(producingWUP);
        Long rightNow = Instant.now().getEpochSecond();
        this.instanceDiscriminator = Long.toHexString(rightNow);
    }

    public String getFriendlyName() {
        String newString = "[" + this.producingWUPFDN.getUnqualifiedRDN().getValue() + "->" +
                this.outboundUoWTypeFDN.getUnqualifiedRDN().getValue() + "->" +
                this.consumingWUPFDN.getUnqualifiedRDN().getValue() + "][" +
                this.instanceDiscriminator + "]";
        return(newString);
    }

    public FDN getProducingWUPFDN() {
        return producingWUPFDN;
    }

    public void setProducingWUPFDN(FDN producingWUPFDN) {
        this.producingWUPFDN = new FDN(producingWUPFDN);
    }

    public FDN getOutboundUoWTypeFDN() {
        return outboundUoWTypeFDN;
    }

    public void setOutboundUoWTypeFDN(FDN outboundUoWTypeFDN) {
        this.outboundUoWTypeFDN = new FDN(outboundUoWTypeFDN);
    }

    public FDN getConsumingWUPFDN() {
        return consumingWUPFDN;
    }

    public void setConsumingWUPFDN(FDN consumingWUPFDN) {
        this.consumingWUPFDN = new FDN(consumingWUPFDN);
    }

    @Override
    public String toString(){
        String newString = "{InterchangeRoute:[friendlyName=" + this.getFriendlyName()
                + ", producingWUPFDN=" + this.producingWUPFDN.toString()
                + ", outboundUoWTypeFDN=" + this.outboundUoWTypeFDN.toString()
                + ", consumingWUPFDN=" + this.consumingWUPFDN.toString() + "]";
        return(newString);
    }
}
