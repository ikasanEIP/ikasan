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

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the error log 
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/errors/*.htm")
public class ErrorLogController {

	/**
	 * Error logging service
	 */
	private ErrorLoggingService errorLoggingService;
	

    
    /**
	 * @param errorLoggingService
	 */
	@Autowired
	public ErrorLogController(ErrorLoggingService errorLoggingService, ExcludedEventService excludedEventService) {
		super();
		this.errorLoggingService = errorLoggingService;
	}
	
    
    
	/**
     * List all errorOccurrences
     * 
     * @param model - The model (map)
     * @return errors view
     */
    @RequestMapping("list.htm")
    public String listErrors(
    		@RequestParam(required=false) String moduleName, 
    		@RequestParam(required=false) String flowName,
    		HttpServletRequest request,
    		@RequestParam(required=false) Integer page, 
    		@RequestParam(required=false) String orderBy,
    		@RequestParam(required=false) Boolean orderAsc,
    		ModelMap model)
    {


    	//perform the paged search
         PagedSearchResult<ErrorOccurrence> pagedResult = errorLoggingService.getErrors(MasterDetailControllerUtil.defaultZero(page), 25,MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), MasterDetailControllerUtil.nullForEmpty(moduleName), MasterDetailControllerUtil.nullForEmpty(flowName));

    	//search restriction params
    	Map<String, Object> searchParams = new HashMap<String, Object>();
    	MasterDetailControllerUtil.addParam(searchParams,"moduleName", moduleName);
    	MasterDetailControllerUtil.addParam(searchParams,"flowName", flowName);
    	
    	MasterDetailControllerUtil.addPagedModelAttributes(MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), model, MasterDetailControllerUtil.defaultZero(page), pagedResult,
				request, searchParams);
        return "admin/errors/errors";
    }
    
	/**
     * Display ErrorOccurrence
     * 
     * @param model - The model (map)
     * @return errors view
     */
    @RequestMapping("viewError.htm")
    public String view(@RequestParam long errorId, 
    		@RequestParam(required=false) String searchResultsUrl, 
    		ModelMap model)
    {
        model.addAttribute("error", errorLoggingService.getErrorOccurrence(errorId));
        model.addAttribute("searchResultsUrl", searchResultsUrl);
        return "admin/errors/viewError";
    }
    
    
    
}
