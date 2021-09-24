package net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.valuesets;

public enum SubscriptionSummaryType {
    PROCESSING_PLANT_SUBSCRIPTION_SUMMARY("ProcessingPlantSubscriptionSummary", "itops.report.subscription_summary_type.processing_plant"),
    WORK_UNIT_PROCESSOR_SUMMARY("WorkUnitProcessorSubscriptionSummary", "itops.report.subscription_summary_type.work_unit_processor");

    private String displayName;
    private String typeValue;

    private SubscriptionSummaryType(String displayName, String typeValue){
        this.displayName = displayName;
        this.typeValue = typeValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTypeValue() {
        return typeValue;
    }
}
