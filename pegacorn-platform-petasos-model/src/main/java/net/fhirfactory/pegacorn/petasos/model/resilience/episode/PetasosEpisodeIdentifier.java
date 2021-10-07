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

package net.fhirfactory.pegacorn.petasos.model.resilience.episode;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import org.apache.commons.lang3.SerializationUtils;

/**
 * @author Mark A. Hunter
 * @since 2020-08-07
 */
public class PetasosEpisodeIdentifier implements Serializable {
	private String value;
	private Instant creationInstant;

	public PetasosEpisodeIdentifier(){
		this.value = null;
		this.creationInstant = null;
	}
	
    public PetasosEpisodeIdentifier(FDNToken originalToken) {
		this.value = SerializationUtils.clone(originalToken.getContent());
		this.creationInstant = Instant.now();
    }

	public PetasosEpisodeIdentifier(TopologyNodeFunctionFDN functionFDN){
		this.value = SerializationUtils.clone(functionFDN.toTypeBasedFDNWithVersion().getToken().getContent());
		this.creationInstant = Instant.now();
	}

	public PetasosEpisodeIdentifier(PetasosEpisodeIdentifier ori){
		if(ori != null) {
			if (ori.value != null) {
				this.value = SerializationUtils.clone(ori.getValue());
			} else {
				this.value = null;
			}
			if (ori.creationInstant != null) {
				this.creationInstant = SerializationUtils.clone(ori.getCreationInstant());
			} else {
				this.creationInstant = Instant.now();
			}
		} else {
			this.value = null;
			this.creationInstant = Instant.now();
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Instant getCreationInstant() {
		return creationInstant;
	}

	public void setCreationInstant(Instant creationInstant) {
		this.creationInstant = creationInstant;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PetasosEpisodeIdentifier that = (PetasosEpisodeIdentifier) o;
		return Objects.equals(value, that.value) && Objects.equals(creationInstant, that.creationInstant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, creationInstant);
	}

	@Override
	public String toString() {
		return "PetasosEpisodeIdentifier{" +
				"value='" + value + '\'' +
				", creationInstant=" + creationInstant +
				'}';
	}
}
