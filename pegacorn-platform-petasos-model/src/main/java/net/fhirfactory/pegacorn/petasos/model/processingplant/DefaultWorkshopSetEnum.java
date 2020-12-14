package net.fhirfactory.pegacorn.petasos.model.processingplant;

public enum DefaultWorkshopSetEnum {
    INTERACT_WORKSHOP("InteractionWorkshop"),
    TRANSFORM_WORKSHOP("TransformationWorkshop"),
    EDGE_WORKSHOP("EdgeIPCWorkshop");

    private String workshop;

    private DefaultWorkshopSetEnum(String newWorkshop){
        this.workshop = newWorkshop;
    }

    public String getWorkshop(){
        return(this.workshop);
    }
}
