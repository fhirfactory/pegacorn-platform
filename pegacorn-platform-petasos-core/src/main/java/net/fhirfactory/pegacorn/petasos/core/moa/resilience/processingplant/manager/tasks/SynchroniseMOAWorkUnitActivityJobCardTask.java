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

package net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.manager.tasks;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.resilience.processingplant.cache.ProcessingPlantWUAEpisodeActivityMatrixDM;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SynchroniseMOAWorkUnitActivityJobCardTask {
	private static final Logger LOG = LoggerFactory.getLogger(SynchroniseMOAWorkUnitActivityJobCardTask.class);

	@Inject
	ProcessingPlantWUAEpisodeActivityMatrixDM activityMatrixDM;

	@Inject
	TopologyIM topologyIM;
	
	//
	// Getters (and Setters)
	//
	
	protected Logger getLogger(){
		return(LOG);
	}

	//
	// Business Methods
	//

	public void synchroniseJobCard(WUPJobCard submittedJobCard) {
		getLogger().debug(".synchroniseJobCard(): Entry"); 
		if (submittedJobCard == null) {
			throw (new IllegalArgumentException(".doTask(): submittedJobCard is null"));
		}
		ActivityID activityID = submittedJobCard.getActivityID();
        TopologyNodeFDN nodeFDN = new TopologyNodeFDN(activityID.getPresentWUPIdentifier());
		WorkUnitProcessorTopologyNode wup = (WorkUnitProcessorTopologyNode)topologyIM.getNode(nodeFDN);
		switch (wup.getResilienceMode()) {
			case RESILIENCE_MODE_MULTISITE: {
				getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_MULTISITE");
				switch (wup.getConcurrencyMode()) {
					case CONCURRENCY_MODE_CONCURRENT: // Woo hoo - we are full-on highly available
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_MULTISITE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_CONCURRENT");
						break;
					case CONCURRENCY_MODE_STANDALONE: // WTF - why bother!
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_MULTISITE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_STANDALONE");
						break;
					case CONCURRENCY_MODE_ONDEMAND: // make it reliable, scalable
					default:
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_MULTISITE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_ONDEMAND (default concurrency mode)");

				}
				break;
			}
			case RESILIENCE_MODE_CLUSTERED: {
				getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_CLUSTERED");
				switch (wup.getConcurrencyMode()) {
					case CONCURRENCY_MODE_CONCURRENT: // Not possible
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_CLUSTERED, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_CONCURRENT");
					case CONCURRENCY_MODE_STANDALONE: // A waste, we can have multiple - but only want one!
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_CLUSTERED, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_STANDALONE");
					case CONCURRENCY_MODE_ONDEMAND: // OK, preferred & MVP
					default:
						getLogger().trace(
								".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_CLUSTERED, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_ONDEMAND (default concurrency mode)");
				}
				break;
			}
			case RESILIENCE_MODE_STANDALONE:
				getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_STANDALONE");
			default: {
				switch (wup.getConcurrencyMode()) {
					case CONCURRENCY_MODE_CONCURRENT: // Not possible!
						getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_STANDALONE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_CONCURRENT (not possible)");
					case CONCURRENCY_MODE_ONDEMAND: // Not possible!
						getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_STANDALONE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_ONDEMAND (not possible)");
					case CONCURRENCY_MODE_STANDALONE: // Really only good for PoCs and Integration Testing
					default:
						getLogger().trace(".synchroniseJobCard(): Deployment Mode --> PETASOS_MODE_STANDALONE, Concurrency Mode --> PETASOS_WUA_CONCURRENCY_MODE_STANDALONE (default concurrent mode)");
						standaloneModeSynchroniseJobCard(submittedJobCard);
				}
			}
		}
	}

	/**
	 *
	 * @param actionableJobCard
	 */
	public void standaloneModeSynchroniseJobCard(WUPJobCard actionableJobCard) {
		getLogger().debug(".standaloneModeSynchroniseJobCard(): Entry"); 
		if (actionableJobCard == null) {
			throw (new IllegalArgumentException(".doTask(): actionableJobCard is null"));
		}
		ResilienceParcelIdentifier parcelInstanceID = actionableJobCard.getActivityID().getPresentParcelIdentifier();
		PetasosEpisodeIdentifier wuaEpisodeID = actionableJobCard.getActivityID().getPresentEpisodeIdentifier();
		getLogger().trace(".standaloneModeSynchroniseJobCard(): Retrieve the ParcelStatusElement from the Cache for ParcelInstanceID --> {}", parcelInstanceID);
		ParcelStatusElement statusElement = activityMatrixDM.getParcelStatusElement(parcelInstanceID);
		getLogger().trace(".standaloneModeSynchroniseJobCard(): Retrieved ParcelStatusElement --> {}", statusElement);
		getLogger().trace(".standaloneModeSynchroniseJobCard(): Retrieve the ParcelInstanceSet for the wuaEpisodeID --> {}", wuaEpisodeID);
		List<ParcelStatusElement> parcelSet = activityMatrixDM.getEpisodeElementSet(wuaEpisodeID);
		if (getLogger().isTraceEnabled()) {
			getLogger().trace(".standaloneModeSynchroniseJobCard(): The ParcelSet associated with the ParcelEpisodeID --> {} contains {} elements", wuaEpisodeID, parcelSet.size());
		}
		ResilienceParcelIdentifier systemWideFocusedParcelInstanceID = activityMatrixDM.getSiteWideFocusElement(wuaEpisodeID);
		getLogger().trace(".standaloneModeSynchroniseJobCard(): The Parcel with systemWideFocusedParcel --> {}", systemWideFocusedParcelInstanceID);
		ResilienceParcelIdentifier clusterFocusedParcelInstanceID = activityMatrixDM.getClusterFocusElement(wuaEpisodeID);
		getLogger().trace(".standaloneModeSynchroniseJobCard(): The Parcel with clusterFocusedParcel --> {}", clusterFocusedParcelInstanceID);
		if (parcelSet.isEmpty()) {
			throw (new IllegalArgumentException(".synchroniseJobCard(): There are no ResilienceParcels for the given ParcelEpisodeID --> something is very wrong!"));
		}
		getLogger().trace( ".standaloneModeSynchroniseJobCard(): Now, again, for the standalone mode - there should only be a single thread per WUA Episode ID, so set it to have FOCUS");
		statusElement.setHasSystemWideFocus(true);
		statusElement.setHasClusterFocus(true);
		getLogger().trace(".standaloneModeSynchroniseJobCard(): Now, lets update the JobCard based on the ActivityMatrix");
		actionableJobCard.setGrantedStatus(actionableJobCard.getRequestedStatus());
		actionableJobCard.setUpdateDate(Date.from(Instant.now()));

	}
}
