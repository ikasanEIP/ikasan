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
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionSearchService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;

/**
 * Module application implementing the REST contract for Exclusion
 */
@RequestMapping("/rest/exclusion")
@RestController
public class ExclusionApplication
{

    @Autowired
    private ExclusionSearchService exclusionSearchService;

    @Autowired
    /** The module container (effectively holds the DTO) */
    private ModuleService moduleService;

    private DateTimeConverter dateTimeConverter = new DateTimeConverter();


    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(defaultValue = "timestamp") String orderBy,
        @RequestParam(defaultValue = "false") boolean orderAscending,
        @RequestParam(required = false) String flow,
        @RequestParam(required = false) String componentName,
        @RequestParam(required = false) String identifier,
        @RequestParam(required = false) String fromDateTime,
        @RequestParam(required = false) String untilDateTime
        ) {

        String moduleName = moduleService.getModules().get(0).getName();
        PagedSearchResult<ExclusionEvent> exclusions = null;
        try
        {
            exclusions = exclusionSearchService.find(
                pageNumber,
                pageSize,
                orderBy,
                orderAscending,
                moduleName,
                flow,
                componentName,
                identifier,
                dateTimeConverter.getDate(fromDateTime),
                dateTimeConverter.getDate(untilDateTime)
                );
            return new ResponseEntity(exclusions, HttpStatus.OK);
        }
        catch (ParseException e)
        {
            return new ResponseEntity(new ErrorDto("fromDateTime or untilDateTime has invalid dateTime format not following yyyy-MM-dd'T'HH:mm:ss."),
                HttpStatus.BAD_REQUEST);
        }

    }

}
