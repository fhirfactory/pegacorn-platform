package net.fhirfactory.pegacorn.petasos.model.processingplant;

import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;

public interface ProcessingPlantServicesInterface {
    public NodeElement getProcessingPlantNodeElement();
    public NodeElementIdentifier getProcessingPlantNodeId();
    public NodeElement getWorkshop(String workshopName);
    public void initialisePlant();
}
