package net.fhirfactory.pegacorn.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

/**
 * @author Jasen Schremmer
 */
public abstract class BaseRouteBuilder extends RouteBuilder {

    public BaseRouteBuilder() {
        super();
    }

    public BaseRouteBuilder(CamelContext context) {
        super(context);
    }
    
    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    protected RouteDefinition fromWithStandardExceptionHandling(String uri) {
        RouteDefinition route = from(uri);
        route
            .onException(Exception.class)
                .log(LoggingLevel.ERROR, getClass().getName() + ":: Exception occurred ${exception.message}, Body=${body}")
                .handled(false)
            .end()
            ;
        return route;
    }
}
