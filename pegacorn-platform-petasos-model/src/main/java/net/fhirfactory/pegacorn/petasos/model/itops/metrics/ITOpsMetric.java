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
package net.fhirfactory.pegacorn.petasos.model.itops.metrics;

import net.fhirfactory.pegacorn.petasos.model.itops.metrics.valuesets.ITOpsMetricNameEnum;

import java.io.Serializable;
import java.util.Objects;

public class ITOpsMetric implements Serializable {
    private ITOpsMetricNameEnum name;
    private String unit;
    private String value;

    public ITOpsMetric(){
        name = null;
        unit = null;
        value = null;
    }

    public ITOpsMetric(ITOpsMetricNameEnum name, String unit, String value){
        this.name = name;
        this.unit = unit;
        this.value = value;
    }

    public ITOpsMetricNameEnum getName() {
        return name;
    }

    public void setName(ITOpsMetricNameEnum name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ITOpsMetric)) return false;
        ITOpsMetric that = (ITOpsMetric) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getUnit(), that.getUnit()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUnit(), getValue());
    }

    @Override
    public String toString() {
        return "ITOpsMetric{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
