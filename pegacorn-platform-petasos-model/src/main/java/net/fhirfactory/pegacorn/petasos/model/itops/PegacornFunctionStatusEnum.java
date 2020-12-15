package net.fhirfactory.pegacorn.petasos.model.itops;

public enum PegacornFunctionStatusEnum {
    FUNCTION_STATUS_OK("pegacorn.function.state.ok"),
    FUNCTION_STATUS_FAILED("pegacorn.function.state.failed");

    private String functionState;

    private PegacornFunctionStatusEnum(String state ){
        this.functionState = state;
    }

    public String getFunctionState(){
        return(this.functionState);
    }
}
