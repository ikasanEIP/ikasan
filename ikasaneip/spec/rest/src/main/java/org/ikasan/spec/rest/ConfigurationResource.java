package org.ikasan.spec.rest;

import org.ikasan.spec.configuration.Configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Configuration spec for the REST services.
 */
@Path("/config")
public interface ConfigurationResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Configuration getConfiguration(@QueryParam("moduleName") String moduleName,
                                          @QueryParam("flowName") String flowName,
                                          @QueryParam("flowElementName") String flowElementName);

    @GET
    @Path("{configurationId}")
    @Produces(MediaType.APPLICATION_JSON)
    Configuration getConfigurationById(@PathParam("configurationId") String configurationId);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    void saveConfiguration(Configuration configuration);

    @DELETE
    void deleteConfiguration(@QueryParam("moduleName") String moduleName,
                                    @QueryParam("flowName") String flowName,
                                    @QueryParam("flowElementName") String flowElementName);

    @DELETE
    @Path("{configurationId}")
    void deleteConfigurationById(@PathParam("configurationId") String configurationId);
}
