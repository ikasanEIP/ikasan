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

import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Module application implementing the REST contract for System Events
 */
@RequestMapping("/rest/systemEvent")
@RestController
public class SystemEventApplication
{
    @Autowired
    private SystemEventService systemEventService;

    /**
     * Retrieves a paginated list of system events based on the specified parameters.
     *
     * @param pageNumber The page number to retrieve (default is 0)
     * @param pageSize The number of items per page (default is 25)
     * @param orderBy The field by which to order the results (default is timestamp)
     * @param orderAscending Flag indicating whether the results should be ordered in ascending order (default is false)
     * @param subject The subject to filter the results by, can be null
     * @param action The action to filter the results by, can be null
     * @param actor The actor to filter the results by, can be null
     * @param fromDateTime The starting date and time as epoch milliseconds, can be null
     * @param untilDateTime The ending date and time as epoch milliseconds, can be null
     * @return ResponseEntity containing a paginated list of system events
     */
    @RequestMapping(method = RequestMethod.GET,
        value = "/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "25") int pageSize,
        @RequestParam(value = "orderBy", defaultValue = "timestamp") String orderBy,
        @RequestParam(value = "orderAscending", defaultValue = "false") boolean orderAscending,
        @RequestParam(value = "subject", required = false) String subject,
        @RequestParam(value = "action", required = false) String action,
        @RequestParam(value = "actor", required = false) String actor,
        @RequestParam(value = "fromDateTime", required = false) Long fromDateTime,
        @RequestParam(value = "untilDateTime", required = false) Long untilDateTime
    )
    {
        Date fromDate = fromDateTime != null ? new Date(fromDateTime) : null;
        Date untilDate = untilDateTime != null ? new Date(untilDateTime) : null;

        PagedSearchResult<SystemEvent> pagedResult = systemEventService.listSystemEvents(
            pageNumber,
            pageSize,
            orderBy,
            orderAscending,
            subject,
            action,
            fromDate,
            untilDate,
            actor
        );
        return new ResponseEntity(pagedResult, HttpStatus.OK);
    }
}
