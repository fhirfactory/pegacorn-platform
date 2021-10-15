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
package net.fhirfactory.pegacorn.petasos.model.uow;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Mark A. Hunter
 * @since 08-Jun-2020
 */
public class UoWPayloadSet implements Serializable {
    HashSet<UoWPayload> payloadElements;

    public UoWPayloadSet() {
        payloadElements = new HashSet<UoWPayload>();
    }

    public UoWPayloadSet(UoWPayloadSet oldPayloadSet) {
        payloadElements = new HashSet<UoWPayload>();
        Iterator<UoWPayload> payloadIterator = oldPayloadSet.getPayloadElements().iterator();
        while (payloadIterator.hasNext()) {
            UoWPayload oldPayload = payloadIterator.next();
            UoWPayload newPayload = new UoWPayload(oldPayload);
            payloadElements.add(newPayload);
        }
    }

    public Set<UoWPayload> getPayloadElements() {
        return (payloadElements);
    }

    public void addPayloadElement(UoWPayload newPayload) {
        payloadElements.add(newPayload);
    }

    @Override
    public String toString() {
        String newString = new String();
        if (payloadElements.isEmpty()) {
            return (newString);
        }
        Iterator<UoWPayload> payloadIterator = payloadElements.iterator();
        newString = new String("UoWPayloadSet={");
        while (payloadIterator.hasNext()) {
            UoWPayload payload = payloadIterator.next();
            newString = newString + "(";
            newString = newString + "(PayloadTopicID=" + payload.getPayloadManifest() +"),";
            newString = newString + "(Payload=" + payload.getPayload() + ")";
            newString = newString + ")";
            if (payloadIterator.hasNext()) {
                newString = newString + ",";
            }
        }
        newString = newString + "}";
        return (newString);
    }
}
