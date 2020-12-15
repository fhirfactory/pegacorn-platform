package net.fhirfactory.pegacorn.fhir.helpers;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.ResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BundleDecoder {

    public MessageHeader extractMessageHeader(Bundle bundleResource){
        if(bundleResource == null){
            return(null);
        }
        if(! bundleResource.getType().equals(Bundle.BundleType.MESSAGE)){
            return(null);
        }
        for(Bundle.BundleEntryComponent entry: bundleResource.getEntry()){
            if(entry.getResource().getResourceType().equals(ResourceType.MessageHeader)){
                return((MessageHeader)entry.getResource());
            }
        }
        return(null);
    }
}
