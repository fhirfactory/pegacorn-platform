package net.fhirfactory.pegacorn.referencevalues;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PegacornSystemReference {
	
	private static final String PEGACORN_COMMUNICATE_GROUPSERVER_IDENTIFIER_SYSTEM = "http://pegacorn.fhirfactory.net/pegacorn/R1/communicate/iris/roomserver";
	private static final String PEGACORN_DEFAULT_IDENTIFIER_SYSTEM = "\"http://pegacorn.fhirfactory.net/pegacorn/R1/identifier";
        
        
        public String getDefaultIdentifierSystem(){
            return(PEGACORN_DEFAULT_IDENTIFIER_SYSTEM);
        }
        
	public String getDefaultIdentifierSystemForCommunicateGroupServer() {
		return(PEGACORN_COMMUNICATE_GROUPSERVER_IDENTIFIER_SYSTEM);
	}

}
