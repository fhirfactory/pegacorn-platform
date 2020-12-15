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

package net.fhirfactory.pegacorn.common.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class FDNTokenSet {
	private LinkedHashSet<FDNToken> elements;
	private String fdnSetAsString;
	private Object updateLock;

	public FDNTokenSet() {
		elements = new LinkedHashSet<>();
		fdnSetAsString = new String();
		updateLock = new Object();
	}

	public FDNTokenSet(FDNTokenSet originalSet) {
		elements = new LinkedHashSet<>();
		updateLock = new Object();
		if (originalSet != null) {
			Iterator<FDNToken> originalSetIterator = originalSet.getElements().iterator();
			while (originalSetIterator.hasNext()) {
				FDNToken newFDN = new FDNToken(originalSetIterator.next());
				elements.add(newFDN);
			}
		}
		generateString();
	}

	public Set<FDNToken> getElements() {
		return (elements);
	}

	public void setElements(Set<FDNToken> newElementSet) {
		if (newElementSet == null) {
			return;
		}
		synchronized (updateLock) {
			elements.clear();
			Iterator<FDNToken> fdnIterator = newElementSet.iterator();
			while (fdnIterator.hasNext()) {
				FDNToken fdnCopy = new FDNToken(fdnIterator.next());
				elements.add(fdnCopy);
			}
			generateString();
		}
	}

	public void addElement(FDNToken newFDNToken) {
		synchronized (updateLock) {
			Iterator<FDNToken> setIterator = elements.iterator();
			boolean isAlreadyPresent = false;
			while (setIterator.hasNext()) {
				FDNToken currentFDN = setIterator.next();
				if (currentFDN.equals(newFDNToken)) {
					isAlreadyPresent = true;
					break;
				}
			}
			if (!isAlreadyPresent) {
				FDNToken toBeAddedFDN = new FDNToken(newFDNToken);
				elements.add(toBeAddedFDN);
				generateString();
			}
		}
	}

	public void removeElement(FDNToken theFDNToken) {
		synchronized (updateLock) {
			Iterator<FDNToken> setIterator = elements.iterator();
			while (setIterator.hasNext()) {
				FDNToken currentFDN = setIterator.next();
				if (currentFDN.equals(theFDNToken)) {
					elements.remove(currentFDN);
					generateString();
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

	private void generateString() {
		if(elements.isEmpty()) {
			this.fdnSetAsString = new String();
		}
		String newString = new String("{FDNSet=(");
		int counter = 0;
		Iterator<FDNToken> setIterator = elements.iterator();	
		while (setIterator.hasNext()) {
			FDNToken currentFDN = setIterator.next();
			newString = newString + "[" + Integer.toString(counter) + "][" + currentFDN.toString() + "]";
			counter += 1;
		}
		newString = newString + ")}";
		this.fdnSetAsString = newString;
	}

	@Override
	public String toString() {
		return (this.fdnSetAsString);
	}

}
