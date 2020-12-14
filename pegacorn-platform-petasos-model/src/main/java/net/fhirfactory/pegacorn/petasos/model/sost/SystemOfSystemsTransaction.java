package net.fhirfactory.pegacorn.petasos.model.sost;

import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelIdentifier;

import java.util.ArrayList;

public class SystemOfSystemsTransaction {
    private SystemOfSystemsTransactionIdentifier transactionIdentifier;
    private ArrayList<SystemOfSystemsTriggerEvent> triggerSet;
    private ArrayList<SystemOfSystemsOutcomeEvent> outcomeSet;
    private ArrayList<ResilienceParcelIdentifier> associatedPetasosParcelSet;

    public SystemOfSystemsTransactionIdentifier getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public void setTransactionIdentifier(SystemOfSystemsTransactionIdentifier transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    public ArrayList<SystemOfSystemsTriggerEvent> getTriggerSet() {
        return triggerSet;
    }

    public void setTriggerSet(ArrayList<SystemOfSystemsTriggerEvent> triggerSet) {
        this.triggerSet = triggerSet;
    }

    public ArrayList<SystemOfSystemsOutcomeEvent> getOutcomeSet() {
        return outcomeSet;
    }

    public void setOutcomeSet(ArrayList<SystemOfSystemsOutcomeEvent> outcomeSet) {
        this.outcomeSet = outcomeSet;
    }

    public ArrayList<ResilienceParcelIdentifier> getAssociatedPetasosParcelSet() {
        return associatedPetasosParcelSet;
    }

    public void setAssociatedPetasosParcelSet(ArrayList<ResilienceParcelIdentifier> associatedPetasosParcelSet) {
        this.associatedPetasosParcelSet = associatedPetasosParcelSet;
    }
}
