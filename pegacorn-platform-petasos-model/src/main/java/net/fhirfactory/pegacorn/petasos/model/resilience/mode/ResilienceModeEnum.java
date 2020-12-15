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

package net.fhirfactory.pegacorn.petasos.model.resilience.mode;

public enum ResilienceModeEnum {
    RESILIENCE_MODE_STANDALONE("pegacorn.platform.petasos.resilience_mode.standalone"),
    RESILIENCE_MODE_KUBERNETES_STANDALONE("pegacorn.platform.petasos.resilience_mode.kubernetes_standalone"),
    RESILIENCE_MODE_CLUSTERED("pegacorn.platform.petasos.resilience_mode.clustered"),
    RESILIENCE_MODE_KUBERNETES_CLUSTERED("pegacorn.platform.petasos.resilience_mode.kubernetes_clustered"),
    RESILIENCE_MODE_MULTISITE("pegacorn.platform.petasos.resileince_mode.multisite"),
    RESILIENCE_MODE_KUBERNETES_MULTISITE("pegacorn.platform.petasos.resilience_mode.kubernetes_multisite"),;

    private String petasosNodeMode;

    private ResilienceModeEnum(String nodeMode){
        this.petasosNodeMode = nodeMode;
    }

    public String getResilienceMode(){
        return(this.petasosNodeMode);
    }
}
