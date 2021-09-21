package net.fhirfactory.pegacorn.petasos.itops.valuesets;

public enum ITOpsCapabilityNamesEnum {
    IT_OPS_METRICS_REPORT_COLLATOR ("ITOpsMetricsReportCollator"),
    IT_OPS_PUBSUB_REPORT_COLLATOR ("ITOpsPubSubReportCollator"),
    IT_OPS_TOPOLOGY_REPORT_COLLATOR("ITOpsTopologyReportCollator");

    private String capabilityName;

    private ITOpsCapabilityNamesEnum(String name){
        this.capabilityName = name;
    }

    public String getCapabilityName() {
        return capabilityName;
    }

    public static ITOpsCapabilityNamesEnum fromCapabilityName(String given){
        for(ITOpsCapabilityNamesEnum currentName: ITOpsCapabilityNamesEnum.values()){
            if(currentName.getCapabilityName().equalsIgnoreCase(given)){
                return(currentName);
            }
        }
        return(null);
    }
}
