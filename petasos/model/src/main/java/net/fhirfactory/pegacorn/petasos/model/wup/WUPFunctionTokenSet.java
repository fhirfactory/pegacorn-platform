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
package net.fhirfactory.pegacorn.petasos.model.wup;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class WUPFunctionTokenSet {
    private LinkedHashSet<WUPFunctionToken> elements;
    private Object updateLock;

    public WUPFunctionTokenSet() {
        elements = new LinkedHashSet<>();
        updateLock = new Object();
    }

    public WUPFunctionTokenSet(WUPFunctionTokenSet originalSet) {
        elements = new LinkedHashSet<>();
        updateLock = new Object();
        for( WUPFunctionToken token: originalSet.getElements()){
            elements.add(token);
        }
    }

    public Set<WUPFunctionToken> getElements() {
        return (elements);
    }

    public void setElements(Set<WUPFunctionToken> newElementSet) {
        if (newElementSet == null) {
            return;
        }
        synchronized (updateLock) {
            elements.clear();
            elements.addAll(newElementSet);
        }
    }

    public void addElement(WUPFunctionToken newFDNToken) {
        synchronized (updateLock) {
            Iterator<WUPFunctionToken> setIterator = elements.iterator();
            boolean isAlreadyPresent = false;
            while (setIterator.hasNext()) {
                WUPFunctionToken currentFDN = setIterator.next();
                if (currentFDN.equals(newFDNToken)) {
                    isAlreadyPresent = true;
                    break;
                }
            }
            if (!isAlreadyPresent) {
                elements.add(newFDNToken);
            }
        }
    }

    public void removeElement(WUPFunctionToken theFDNToken) {
        synchronized (updateLock) {
            Iterator<WUPFunctionToken> setIterator = elements.iterator();
            while (setIterator.hasNext()) {
                WUPFunctionToken currentFDN = setIterator.next();
                if (currentFDN.equals(theFDNToken)) {
                    elements.remove(currentFDN);
                    break;
                }
            }
        }
    }

    public boolean isEmpty() {
        if (elements.isEmpty()) {
            return (true);
        } else {
            return (false);
        }
    }
}
