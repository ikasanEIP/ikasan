package org.ikasan.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

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
        register(RolesAllowedDynamicFeature.class);
    }
}
