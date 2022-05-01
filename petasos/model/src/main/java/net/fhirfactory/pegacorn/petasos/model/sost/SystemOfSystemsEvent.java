package net.fhirfactory.pegacorn.petasos.model.sost;

import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;

import java.time.Instant;
import java.util.Date;

public abstract class SystemOfSystemsEvent {
    private Date eventInstant;
    private SystemOfSystemsEventTypeEnum eventType;
    private FDNToken associatedEndpoint;

    public SystemOfSystemsEvent(SystemOfSystemsEventTypeEnum newEventType, FDNToken endpointId){
        this.associatedEndpoint = endpointId;
        this.eventInstant = Date.from(Instant.now());
        this.eventType = newEventType;
    }

    public void setEventInstant(Date newInstant){this.eventInstant = newInstant;}

    public Date getEventInstant() {
        return eventInstant;
    }

    public SystemOfSystemsEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(SystemOfSystemsEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public FDNToken getAssociatedEndpoint() {
        return associatedEndpoint;
    }

}
