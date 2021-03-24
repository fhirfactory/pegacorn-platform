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

package net.fhirfactory.pegacorn.petasos.wup.archetypes;

import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;

public abstract class EdgeEgressMessagingGatewayWUP extends GenericMessageBasedWUPTemplate {

    protected static final String IPC_FRAME_DECODER = "ipcFrameDecoder";
    protected static final String IPC_STRING_DECODER = "ipcStringDecoder";
    protected static final String IPC_STRING_ENCODER = "ipcStringEncoder";
    private static final String DEFAULT_NETTY_PARAMS = 
            "?allowDefaultCodec=false&decoders=#" + IPC_FRAME_DECODER + "&encoders=#" + IPC_STRING_ENCODER + 
            "&keepAlive=false&clientMode=true&sync=true&sendBufferSize=" + IPC_PACKET_MAXIMUM_FRAME_SIZE;
        
    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint(){
        getLogger().debug(".specifyIngresTopologyEndpoint(): Entry");
        GenericMessageBasedWUPEndpoint ingressEndpoint = new GenericMessageBasedWUPEndpoint();
        ingressEndpoint.setFrameworkEnabled(true);
        ingressEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPIngres());
        getLogger().debug(".specifyIngresTopologyEndpoint(): Exit");
        return(ingressEndpoint);
    }

}
