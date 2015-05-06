package org.ikasan.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.ikasan.rest.configuration.ConfigurationApplication;
import org.ikasan.rest.module.ModuleApplication;

/**
 * Registers the components used by Jersey
 */
public class IkasanRestApplication extends ResourceConfig
{
    /**
     * Registers the applications we implement and the Spring-Jersey glue
     */
    public IkasanRestApplication()
    {
        // Spring glue
        register(RequestContextFilter.class);

        // Jackson for JSON marshalling
        register(JacksonFeature.class);

        // Ikasan rest applications
        register(ConfigurationApplication.class);
        register(ModuleApplication.class);

    }
}
