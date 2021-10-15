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
package net.fhirfactory.pegacorn.common.model.dates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hl7.fhir.r4.model.Period;

import java.time.Instant;
import java.util.Date;

public class EffectivePeriod {
    private Date effectiveStartDate;
    private Date effectiveEndDate;

    public EffectivePeriod(){
        this.effectiveEndDate = null;
        this.effectiveStartDate = null;
    }

    public EffectivePeriod(Date startDate){
        this.effectiveStartDate = startDate;
        this.effectiveEndDate = null;
    }

    public EffectivePeriod(Date startDate, Date endDate){
        this.effectiveEndDate = endDate;
        this.effectiveStartDate = startDate;
    }

    public EffectivePeriod(EffectivePeriod ori){
        this.effectiveStartDate = ori.getEffectiveStartDate();
        this.effectiveEndDate = ori.getEffectiveEndDate();
    }

    @JsonIgnore
    public boolean hasEffectiveStartDate(){
        if(this.effectiveStartDate == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasEffectiveEndDate(){
        if(this.effectiveEndDate == null){
            return(false);
        } else {
            return(true);
        }
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    @JsonIgnore
    public boolean isEffective(Date currentDate) {
        boolean isAfterStartDate = true;
        if(effectiveStartDate != null) {
            isAfterStartDate = currentDate.after(effectiveStartDate);
        }
        boolean isBeforeEndDate = true;
        if (effectiveEndDate != null){
            isBeforeEndDate = currentDate.before(effectiveEndDate);
        }
        if(isBeforeEndDate && isAfterStartDate){
            return(true);
        } else {
            return(false);
        }
    }

    @JsonIgnore
    public boolean isEffectiveNow(){
        Date currentDate = Date.from(Instant.now());
        boolean isEffectiveNow = isEffective(currentDate);
        return(isEffectiveNow);
    }

    @Override
    public String toString() {
        return "EffectivePeriod{" +
                "effectiveStartDate=" + effectiveStartDate +
                ", effectiveEndDate=" + effectiveEndDate +
                '}';
    }

    @JsonIgnore
    public Period getPeriod(){
        Period fhirPeriod = new Period();
        if(this.hasEffectiveStartDate()) {
            fhirPeriod.setStart(this.getEffectiveStartDate());
        }
        if(this.hasEffectiveEndDate()) {
            fhirPeriod.setEnd(this.getEffectiveEndDate());
        }
        return(fhirPeriod);
    }
}
