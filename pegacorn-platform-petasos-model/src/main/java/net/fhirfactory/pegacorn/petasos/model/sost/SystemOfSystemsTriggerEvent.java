package net.fhirfactory.pegacorn.petasos.model.sost;

import net.fhirfactory.pegacorn.common.model.FDNToken;

public class SystemOfSystemsTriggerEvent extends SystemOfSystemsEvent{


    public SystemOfSystemsTriggerEvent(SystemOfSystemsEventTypeEnum newEventType, FDNToken endpointId) {
        super(newEventType, endpointId);
    }
}
