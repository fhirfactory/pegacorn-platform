package net.fhirfactory.pegacorn.petasos.model.sost;

import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;

public class SystemOfSystemsOutcomeEvent extends SystemOfSystemsEvent {

    public SystemOfSystemsOutcomeEvent(SystemOfSystemsEventTypeEnum newEventType, FDNToken endpointId) {
        super(newEventType, endpointId);
    }
}
