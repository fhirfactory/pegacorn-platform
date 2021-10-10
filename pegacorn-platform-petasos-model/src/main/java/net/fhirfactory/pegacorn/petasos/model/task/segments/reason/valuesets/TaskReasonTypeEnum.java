package net.fhirfactory.pegacorn.petasos.model.task.segments.reason.valuesets;

public enum TaskReasonTypeEnum {
    TASK_REASON_MESSAGE_PROCESSING("petasos.task_reason.message_processing"),
    TASK_REASON_API_REQUEST("petasos.task_reason.api_request");

    private String taskReasonType;

    private TaskReasonTypeEnum(String reason){
        this.taskReasonType = reason;
    }

    public String getTaskReasonType(){
        return(taskReasonType);
    }
}
