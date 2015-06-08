package org.ikasan.spec.rest;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.Module;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Spec for the module's REST interface
 */
@Path("/modules")
@Produces( MediaType.APPLICATION_JSON )
public interface ModuleResource
{
    @GET
    List<Module> getModules();

    @GET
    @Path("{moduleName}")
    Module getModule(@PathParam("moduleName") String moduleName);

    @GET
    @Path("{moduleName}/flows")
    List<? extends Flow> getFlows(@PathParam("moduleName") String moduleName);

    @GET
    @Path("{moduleName}/flow/{flowName}")
    List<? extends FlowElement<?>> getFlowElements(@PathParam("moduleName") String moduleName,
                                                @PathParam("flowName") String flowName);

    @POST
    @Path("{moduleName}/flow/{flowName}/state")
    void controlFlowState(@Context SecurityContext context,
                                       @PathParam("moduleName") String moduleName,
                                       @PathParam("flowName") String flowName,
                                       @QueryParam("action") String action);

    @POST
    @Path("{moduleName}/flow/{flowName}/mode")
    void controlFlowStartupMode(@Context SecurityContext context,
                                             @PathParam("moduleName") String moduleName,
                                             @PathParam("flowName") String flowName,
                                             @QueryParam("startupType") String startupType,
                                             @QueryParam("startupComment") String startupComment);
}
