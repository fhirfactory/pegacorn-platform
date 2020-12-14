package net.fhirfactory.pegacorn.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
@ApplicationScoped
public class FHIRContextUtility {

    private FhirContext fhirContext;

    public FHIRContextUtility() {
        fhirContext = FhirContext.forR4();
    }
    
    /**
     * NOTE: the result is thread safe.
     * 
     * @see {FhirContext}
     */
    public FhirContext getFhirContext() {
        return fhirContext;
    }

    /**
     * NOTE: the result is NOT thread safe.
     * 
     * @see {IParser.newJsonParser()}
     */
    @Produces
    public IParser getJsonParser() {
        return getFhirContext().newJsonParser();
    }
}
