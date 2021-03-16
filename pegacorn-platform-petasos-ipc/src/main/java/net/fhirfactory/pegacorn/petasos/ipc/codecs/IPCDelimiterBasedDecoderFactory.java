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
package net.fhirfactory.pegacorn.petasos.ipc.codecs;

import org.apache.camel.component.netty.DefaultChannelHandlerFactory;
import org.apache.camel.component.netty.codec.DelimiterBasedFrameDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.util.CharsetUtil;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;

public class IPCDelimiterBasedDecoderFactory extends DefaultChannelHandlerFactory {
    private static final String IPC_PACKET_FRAME_END = "<|><ETX><|>";

    @Override
    public ChannelHandler newChannelHandler() {
       ByteBuf ipcFrameEnd = Unpooled.copiedBuffer(IPC_PACKET_FRAME_END, CharsetUtil.UTF_8);
        ByteBuf[] delimiterSet = new ByteBuf[1];
        delimiterSet[0] = ipcFrameEnd;
        DelimiterBasedFrameDecoder ipcFrameBasedDecoder = new DelimiterBasedFrameDecoder(
                GenericMessageBasedWUPTemplate.IPC_PACKET_MAXIMUM_FRAME_SIZE, true, delimiterSet);
        return(ipcFrameBasedDecoder);
    }

    public IPCDelimiterBasedDecoderFactory(){
    }
}
