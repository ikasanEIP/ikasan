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

import org.apache.log4j.Logger;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String listExclusions(@RequestParam(required=false) Integer page, ModelMap model)
    {

    	int pageNo = page!=null?page:0;
    	
    	model.addAttribute("page", pageNo);
        PagedSearchResult<ExcludedEvent> pagedResult = excludedEventService.getExcludedEvents(pageNo, 25);
		model.addAttribute("exclusions", pagedResult);
		model.addAttribute("firstResultIndex", pagedResult.getFirstResultIndex());
		model.addAttribute("lastPage", pagedResult.isLastPage());
		model.addAttribute("resultSize", pagedResult.getResultSize());
		model.addAttribute("size", pagedResult.size());
        return "admin/exclusions/exclusions";
    }

	/**
     * Display ExcludedEvent
     * 
     * @param model - The model (map)
     * @return excludedEvent view
     */
    @RequestMapping("viewExcludedEvent.htm")
    public String view(@RequestParam long excludedEventId, ModelMap model)
    {
        model.addAttribute("excludedEvent", excludedEventService.getExcludedEvent(excludedEventId));
        return "admin/exclusions/viewExclusion";
    }
}
