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
package org.ikasan.framework.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The controller for the various user views
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/exclusions/*.htm")

public class ExclusionsController  
{

    /** The user service to use */
    private ExcludedEventService excludedEventService;
    
    /** Error error logging service     */
    private ErrorLoggingService errorLoggingService;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(ExclusionsController.class);

    /**
     * Constructor
     * 
     * @param excludedEventService - The ExcludedEventService to use
     * @param errorLoggingService - The ErrorLoggingService to use
     */
    @Autowired
    public ExclusionsController(ExcludedEventService excludedEventService, ErrorLoggingService errorLoggingService)
    {
        super();
        this.excludedEventService = excludedEventService;
        this.errorLoggingService = errorLoggingService;
    }

    /**
     * List the excludedEvents
     * 
     * @param model - The model (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public String listExclusions(

    		@RequestParam(required=false) String moduleName, 
    		@RequestParam(required=false) String flowName,
    		HttpServletRequest request,
    		@RequestParam(required=false) Integer page, 
    		@RequestParam(required=false) String orderBy,
    		@RequestParam(required=false) Boolean orderAsc,
    		ModelMap model)
    {
    	//perform the paged search
        PagedSearchResult<ExcludedEvent> pagedResult = excludedEventService.getExcludedEvents(MasterDetailControllerUtil.defaultZero(page), 25, MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), MasterDetailControllerUtil.nullForEmpty(moduleName), MasterDetailControllerUtil.nullForEmpty(flowName ));

    	//search restriction params
    	Map<String, Object> searchParams = new HashMap<String, Object>();
    	MasterDetailControllerUtil.addParam(searchParams,"moduleName", moduleName);
    	MasterDetailControllerUtil.addParam(searchParams,"flowName", flowName);
    	
    	MasterDetailControllerUtil.addPagedModelAttributes(MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), model, MasterDetailControllerUtil.defaultZero(page), pagedResult,
				request, searchParams);
        return "admin/exclusions/exclusions";
    }


	/**
     * Display ExcludedEvent
     * 
     * @param model - The model (map)
     * @return excludedEvent view
     */
    @RequestMapping(value="exclusion.htm", method=RequestMethod.GET)
    public String view(@RequestParam String eventId,  
    		@RequestParam(required=false) String searchResultsUrl, 
    		ModelMap model)
    {
        ExcludedEvent excludedEvent = excludedEventService.getExcludedEvent(eventId);
//		if (excludedEvent.getEvent()!=null){
//	        List<ErrorOccurrence> errorOccurrences = errorLoggingService.getErrorOccurrences(excludedEvent.getEvent().getId());
//	        model.addAttribute("errorOccurrences", errorOccurrences);
//		}
		
		model.addAttribute("excludedEvent", excludedEvent);
        model.addAttribute("searchResultsUrl", searchResultsUrl);
        return "admin/exclusions/viewExclusion";
    }


    
	/**
     * Handle Resubmission request POST
     * 
     * @param model - The model (map)
     * @return excludedEvent view
     */
    @RequestMapping(value="exclusion.htm", method=RequestMethod.POST)
    public String requestResubmission(@RequestParam String eventId, ModelMap model)
    {
    	boolean success = true;
    	try{
    	excludedEventService.resubmit(eventId);
    	} catch(Throwable throwable){
    		logger.error("Exception caught trying to resubmit",throwable);
    		success=false;
    	}
    	logger.info("Resubmission "+(success?"successful":"failed")+"for excludedEvent id:"+eventId);

    	return success?"admin/exclusions/resubmissionSuccess":"admin/exclusions/resubmissionFailure";
    }
}
