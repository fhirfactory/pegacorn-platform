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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;

/**
 * @author Mark A. Hunter
 * @since 2020-08-07
 */
@Deprecated
public class PetasosEpisodeIdentifier extends TaskIdType implements Serializable {


	//
	// Constructor(s)
	//

	@JsonIgnore @Deprecated
	public PetasosEpisodeIdentifier(PetasosEpisodeIdentifier originalId) {
		super(originalId);
	}

	@JsonIgnore @Deprecated
	public PetasosEpisodeIdentifier(TaskIdType taskId){
		super(taskId);
	}

	@JsonIgnore @Deprecated
    public PetasosEpisodeIdentifier(FDNToken originalToken) {
		super();
		this.setId(originalToken.getContent());
    }

	public PetasosEpisodeIdentifier(){
		super();
	}

	public PetasosEpisodeIdentifier(TopologyNodeFunctionFDN functionFDN){
		super();
		this.setContent(functionFDN.toTypeBasedFDNWithVersion().getToken().getContent());
	}

	//
	// Getters and Setters
	//

	@JsonIgnore @Deprecated
	public String getContent(){
		return(getId());
	}

	@JsonIgnore @Deprecated
	public void setContent(FDNToken token){
		setId(token.getContent());
	}

	@JsonIgnore @Deprecated
	public void setContent(String content){
		setId(content);
	}

	@JsonIgnore @Deprecated
	public void setValue(FDNToken token){
		setId(token.getContent());
	}

	@JsonIgnore @Deprecated
	public void setValue(String content){
		setId(content);
	}

	@JsonIgnore @Deprecated
	public FDNToken getFDNToken(){
		FDNToken token = new FDNToken(getId());
		return(token);
	}

	@JsonIgnore @Deprecated
	public String getUnqualifiedToken(){
		String unqualifiedToken = getFDNToken().getUnqualifiedToken();
		return(unqualifiedToken);
	}

	//
	// To String
	//

	@Override
	public String toString() {
		return "PetasosEpisodeIdentifier{" +
				"id='" + getId() + '\'' +
				", version='" + getVersion() + '\'' +
				", creationInstant=" + getCreationInstant() +
				'}';
	}
}
