/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.rest.module;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.resubmission.ResubmissionService;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ikasan Development Team
 */
@RequestMapping("/rest/replay")
@RestController
public class ReplayApplication
{
    private static Logger logger = LoggerFactory.getLogger(ReplayApplication.class);

    /**
     * stopped state string constant
     */
    private static String STOPPED = "stopped";

    /**
     * stoppedInError state string constant
     */
    private static String STOPPED_IN_ERROR = "stoppedInError";

    @Autowired
    private ModuleContainer moduleContainer;

    /**
     * TODO: work out how to get annotation security working.
     *
     * @param moduleName
     * @param flowName
     * @param event
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = "/eventReplay/{moduleName}/{flowName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity replay(@PathVariable("moduleName") String moduleName, @PathVariable("flowName") String flowName,
            @RequestBody byte[] event)
    {
        try
        {
            Module<Flow> module = moduleContainer.getModule(moduleName);
            if (module == null)
            {
                throw new RuntimeException("Could not get module from module container using name:  " + moduleName);
            }
            Flow flow = module.getFlow(flowName);
            if (flow == null)
            {
                throw new RuntimeException("Could not get flow from module container using name:  " + flowName);
            }
            if (flow.getState().equals(STOPPED) || flow.getState().equals(STOPPED_IN_ERROR))
            {
                throw new RuntimeException("Events cannot be replayed when the flow that is being replayed to is in a " +
                        flow.getState() + " state.  Module[" + moduleName + "] Flow[" + flowName + "]");
            }
            FlowConfiguration flowConfiguration = flow.getFlowConfiguration();
            ResubmissionService resubmissionService = flowConfiguration.getResubmissionService();
            if (resubmissionService == null)
            {
                throw new RuntimeException("The resubmission service on the flow you are resubmitting to is null. This is most liekly due to " +
                        "the resubmission service not being set on the flow factory for the flow you are resubmitting to.");
            }
            Serialiser serialiser = flow.getSerialiserFactory().getDefaultSerialiser();
            Object deserialisedEvent = serialiser.deserialise(event);
            logger.debug("deserialisedEvent " + deserialisedEvent);
            resubmissionService.submit(deserialisedEvent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("An error has occurred trying to replay an event: ", e);
            return new ResponseEntity("An error has occurred on the server when trying to replay the event. " + e.getMessage(),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity("Event replayed!", HttpStatus.OK);
    }
}
