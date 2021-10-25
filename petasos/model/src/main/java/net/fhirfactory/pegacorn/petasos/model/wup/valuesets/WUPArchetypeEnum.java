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

package net.fhirfactory.pegacorn.petasos.model.wup.valuesets;

/**
 *
 * @author Mark A. Hunter
 */
public enum WUPArchetypeEnum {
    WUP_NATURE_MESSAGE_WORKER("pegacorn.petasos.wup.nature.message.worker"),
    WUP_NATURE_STIMULI_TRIGGERED_WORKFLOW("pegacorn.petasos.wup.nature.workflow.stimuli_triggered"),
    WUP_NATURE_TIMER_TRIGGERED_WORKFLOW("pegacorn.petasos.wup.nature.workflow.timer_triggered"),
    WUP_NATURE_LADON_STANDARD_MOA("pegacorn.petasos.wup.nature.behaviour.ladon_moa_triggered"),
    WUP_NATURE_LADON_BEHAVIOUR_WRAPPER("pegacorn.petasos.wup.nature.statespace.behaviour_wrapper"),
    WUP_NATURE_IRIS_MATRIXCONTROL_PATHWAY("pegacorn.petasos.wup.nature.iris.matrixcontrol.pathway"),
    WUP_NATURE_IRIS_MATRIXCONTROL_BEHAVIOUR("pegacorn.petasos.wup.nature.iris.matrixcontrol.behaviour"),
    WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT("pegacorn.petasos.wup.nature.message.ingres_point"),
    WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT("pegacorn.petasos.wup.nature.message.egress_point"),
    WUP_NATURE_MESSAGE_EXTERNAL_CONCURRENT_INGRES_POINT("pegacorn.petasos.wup.nature.message.concurrent_ingres_point"),
    WUP_NATURE_API_CLIENT("pegacorn.petasos.wup.nature.api.client"),
    WUP_NATURE_API_ANSWER("pegacorn.petasos.wup.nature.api.answer"),
    WUP_NATURE_API_RECEIVE("pegacorn.petasos.wup.nature.api.receive"),
    WUP_NATURE_API_PUSH("pegacorn.petasos.wup.nature.api.receive");
    
    private String wupNature;

    private WUPArchetypeEnum(String wupNature){
        this.wupNature = wupNature;
    }

    public String getWUPNature(){
        return(this.wupNature);
    }    
}
