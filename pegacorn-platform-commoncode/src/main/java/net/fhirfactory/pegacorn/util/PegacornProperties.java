package net.fhirfactory.pegacorn.util;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on ca.uhn.fhir.jpa.starter.HapiProperties, where properties can be overridden with environment variables
 * 
 * @author Jasen Schremmer
 */
public class PegacornProperties {
    private static final Logger LOG = LoggerFactory.getLogger(PegacornProperties.class);

    private static Properties getProperties() {
        return null; //TODO do we want to load defaults from somewhere?
    }
    
    public static String getProperty(String propertyName, String defaultValue) {
        String env = propertyName.toUpperCase(Locale.US);
        env = env.replace(".", "_");
        env = env.replace("-", "_");

        String logMsg = null;
        
        String value = System.getenv(env);
        if (StringUtils.isNotBlank(value)) {
            logMsg = "Found value in environment variable";
            if (! propertyName.equals(env)) {
                logMsg = logMsg + " " + env;
            }
        } else {
            Properties properties = getProperties();
            if (properties != null) {
                value = properties.getProperty(propertyName);
            }
            if (StringUtils.isNotBlank(value)) {
                logMsg = "Found value in properties";
            } else {
                value = defaultValue;
                logMsg = "Using default value";                
            }
        }

        value = value == null ? null : value.trim();
        
        //Log at warning level so the logs are always shown
        LOG.warn("In PegacornProperties.getProperty(" + propertyName + ") " + logMsg + " " + value);

        return value;
    }

    public static String getMandatoryProperty(String propertyName) {
        String value = getProperty(propertyName, StringUtils.EMPTY);
        if (StringUtils.isBlank(value)) {
            throw new IllegalStateException("A value for property " + propertyName + " must be provided");
        }
        return value;
    }
            
    public static Boolean getBooleanProperty(String propertyName, Boolean defaultValue) {
        String value = getProperty(propertyName, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public static boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        return getBooleanProperty(propertyName, Boolean.valueOf(defaultValue));
    }

    public static Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        String value = getProperty(propertyName, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }
    
    /**
     * Sample call is getPropertyEnum("elasticsearch.required_index_status", ElasticsearchIndexStatus.class, ElasticsearchIndexStatus.YELLOW)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends Enum> T getPropertyEnum(String thePropertyName, Class<T> theEnumType, T theDefaultValue) {
        String value = getProperty(thePropertyName, theDefaultValue.name());
        return (T) Enum.valueOf(theEnumType, value);
    }    
}
