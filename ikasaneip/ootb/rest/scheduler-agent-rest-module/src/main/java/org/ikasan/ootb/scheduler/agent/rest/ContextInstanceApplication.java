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
package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.ErrorDto;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rest/contextInstance")
@RestController
public class ContextInstanceApplication {
    private final Logger logger = LoggerFactory.getLogger(ContextInstanceApplication.class);
    @Autowired
    private ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;

    @RequestMapping(path = "/save", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity save(@RequestBody ContextInstance contextInstance) {
        try {
            logger.info("Requested to save correlationId [" + contextInstance.getId() + "]");
            contextInstanceIdentifierProvisionService.provision(contextInstance);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to save correlationId " + contextInstance.getId() + " Error message ["
                    + e.getMessage() + "]"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/remove", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity remove(@RequestParam("correlationId") String correlationId) {
        try {
            logger.info("Requested to remove correlationId [" + correlationId + "]");
            contextInstanceIdentifierProvisionService.remove(correlationId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto(
                    "An error has occurred attempting to remove correlationId [%s] Error message [%s]".formatted(correlationId, e.getMessage())),
                HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "/removeAll", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity removeAll() {
        try {
            logger.info("Requested to remove all correlationIds");
            contextInstanceIdentifierProvisionService.removeAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(
                new ErrorDto(
                    ("An error has occurred attempting to remove all correlationIds. " +
                        "Error message [%s]").formatted(e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/cache/identifiers",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity contextInstanceCacheIdentifiers() {
        try {
            return new ResponseEntity(ContextInstanceCache.getCorrelationIds(), HttpStatus.OK);

        } catch (Exception e) {
            String message = ("An exception has occurred while requesting job plan instance cache " +
                "identifiers. Error [%s]").formatted(e.getMessage());
            logger.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/cache/{identifier}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getContextInstance(@PathVariable("identifier") String identifier) {
        try {
            return new ResponseEntity(ContextInstanceCache.instance().getByCorrelationId(identifier), HttpStatus.OK);

        } catch (Exception e) {
            String message = ("An error has occurred requesting job plan instance with identifier [%s]." +
                " Error [%s]").formatted(identifier, e.getMessage());
            logger.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }
}
