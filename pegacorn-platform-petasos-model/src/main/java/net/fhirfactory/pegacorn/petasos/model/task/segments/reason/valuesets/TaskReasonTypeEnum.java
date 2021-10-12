package net.fhirfactory.pegacorn.petasos.model.task.segments.reason.valuesets;

public enum TaskReasonTypeEnum {
    TASK_REASON_MESSAGE_PROCESSING("MessageProcessingTask", "petasos.task_reason.message_processing"),
    TASK_REASON_API_REQUEST("APIProcessingTask", "petasos.task_reason.api_request");

    private String taskReasonType;
    private String taskReasonDisplayName;

    private TaskReasonTypeEnum(String name, String reason){
        this.taskReasonDisplayName = name;
        this.taskReasonType = reason;
    }

    public String getTaskReasonType(){
        return(taskReasonType);
    }

    public String getTaskReasonDisplayName(){
        return(taskReasonDisplayName);
    }
}
