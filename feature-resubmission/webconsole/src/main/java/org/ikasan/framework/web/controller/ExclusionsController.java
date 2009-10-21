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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
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
    


    /** Logger for this class */
    private Logger logger = Logger.getLogger(ExclusionsController.class);

    /**
     * Constructor
     * 
     * @param excludedEventService - The ExcludedEventService to use
     */
    @Autowired
    public ExclusionsController(ExcludedEventService excludedEventService)
    {
        super();
        this.excludedEventService = excludedEventService;
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
    	try{
    		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    		excludedEventService.resubmit(eventId, currentUser);
    	} catch(AbortTransactionException abortTransactionException){
    		logger.info("Resubmission failed for excludedEvent id:"+eventId);
    		logger.error("Exception caught trying to resubmit",abortTransactionException);
    		model.addAttribute("resubmissionError", abortTransactionException.getCause());
    		return view(eventId, null, model);
    	}
    	logger.info("Resubmission succesful for excludedEvent id:"+eventId);

    	
    	
    	return "redirect:exclusion.htm?eventId="+eventId;
    }
}
