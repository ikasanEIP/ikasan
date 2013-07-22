/*
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
    public String view(@RequestParam long excludedEventId,  
    		@RequestParam(required=false) String searchResultsUrl, 
    		ModelMap model)
    {
        ExcludedEvent excludedEvent = excludedEventService.getExcludedEvent(excludedEventId);
		if (excludedEvent.getEvent()!=null){
	        List<ErrorOccurrence> errorOccurrences = errorLoggingService.getErrorOccurrences(excludedEvent.getEvent().getId());
	        model.addAttribute("errorOccurrences", errorOccurrences);
		}
		
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
    public String requestResubmission(@RequestParam long excludedEventId, ModelMap model)
    {
    	boolean success = true;
    	try{
    	excludedEventService.resubmit(excludedEventId);
    	} catch(Throwable throwable){
    		logger.error("Exception caught trying to resubmit",throwable);
    		success=false;
    	}
    	logger.info("Resubmission "+(success?"successful":"failed")+"for excludedEvent id:"+excludedEventId);

    	return "redirect:list.htm";
    }
}
