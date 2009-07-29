/*
 * $Id$
 * $URL$
 * 
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
package org.ikasan.console.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.console.web.command.WiretapSearchCriteria;
import org.ikasan.console.web.command.WiretapSearchCriteriaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * This class is the Controller for the Wiretap search form
 * 
 * TODO Comment correctly, split out the search criteria etc
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/events/*.htm")
public class WiretapEventsSearchFormController
{
    /** The logger */
    private Logger logger = Logger.getLogger(WiretapEventsSearchFormController.class);

    /** The wiretap service */
    private WiretapService wiretapService;

    /** The module container (effectively holds the DTO) */
    private ModuleService moduleService;

    /** The search criteria validator to use */
    private WiretapSearchCriteriaValidator validator = new WiretapSearchCriteriaValidator();

    /**
     * Constructor
     * 
     * @param wiretapService - The wiretap service to use
     * @param moduleService - The module container to use
     */
    @Autowired
    public WiretapEventsSearchFormController(WiretapService wiretapService, ModuleService moduleService)
    {
        super();
        this.wiretapService = wiretapService;
        this.moduleService = moduleService;
    }

    /**
     * Get the module names
     * 
     * @return List of module names
     */
    @ModelAttribute("modules")
    public List<Module> getModuleNames()
    {
        return this.moduleService.getModules();
    }

    /**
     * Show the combined wiretap event search and search results view
     * 
     * @param request - Standard HttpRequest
     * @param newSearch - The newSearch flag
     * @param page - page index into the greater result set
     * @param orderBy - The field to order by
     * @param orderAsc - Ascending flag
     * @param selectAll - Select all boolean
     * @param moduleNames - Set of names of modules to include in search - must
     *            contain at least one moduleName
     * @param componentName - The name of the component
     * @param eventId - The Event Id
     * @param payloadId - The Payload Id
     * @param fromDateString - Include only events after fromDate
     * @param fromTimeString - Include only events after fromDate
     * @param untilDateString - Include only events before untilDate
     * @param untilTimeString - Include only events before untilDate
     * @param payloadContent - The Payload content
     * @param model - The model (map)
     * 
     * @return wiretap events view
     */
    @RequestMapping("list.htm")
    public String listWiretapEvents(HttpServletRequest request, @RequestParam(required = false) Boolean newSearch, 
            @RequestParam(required = false) Integer page, @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Boolean orderAsc, @RequestParam(required = false) Boolean selectAll,
            @RequestParam(required = false) Set<String> moduleNames, @RequestParam(required = false) String componentName,
            @RequestParam(required = false) String eventId, @RequestParam(required = false) String payloadId,
            @RequestParam(required = false) String fromDateString, @RequestParam(required = false) String fromTimeString,
            @RequestParam(required = false) String untilDateString, @RequestParam(required = false) String untilTimeString,
            @RequestParam(required = false) String payloadContent, ModelMap model)
    {
        boolean noErrors = true;
        
        // If it's a new search then automatically run the default search 
        if (newSearch != null && newSearch)
        {
            logger.debug("Redirecting to the Default Search");
            String newSearchURL = getNewSearchURL(); 
            return newSearchURL;
        }
        
        logger.info("Form values that came in:");
        logger.info("New Search Flag [" + newSearch + "]");
        logger.info("Page [" + page + "]");
        logger.info("Order By [" + orderBy + "]");
        logger.info("Order Ascending Flag [" + orderAsc + "]");
        logger.info("Select All Flag [" + selectAll + "]");
        logger.info("Module Names [" + moduleNames + "]");
        logger.info("Component Name [" + componentName + "]");
        logger.info("Event Id [" + eventId + "]");
        logger.info("Payload Id [" + payloadId + "]");
        logger.info("From Date String [" + fromDateString + "]");
        logger.info("From Time String [" + fromTimeString + "]");
        logger.info("Until Date String [" + untilDateString + "]");
        logger.info("Until Time String [" + untilTimeString + "]");
        logger.info("Payload Content [" + payloadContent + "]");
        
        // Set the search criteria
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(moduleNames);
        wiretapSearchCriteria.setComponentName(componentName);
        wiretapSearchCriteria.setEventId(eventId);
        wiretapSearchCriteria.setPayloadId(payloadId);
        wiretapSearchCriteria.setFromDate(fromDateString);
        wiretapSearchCriteria.setFromTime(fromTimeString);
        wiretapSearchCriteria.setUntilDate(untilDateString);
        wiretapSearchCriteria.setUntilTime(untilTimeString);
        wiretapSearchCriteria.setPayloadContent(payloadContent);

        // Validate the wiretap search criteria
        List<String> errors = new ArrayList<String>();
        this.validator.validate(wiretapSearchCriteria, errors);
        model.addAttribute("errors", errors);
        if (!errors.isEmpty())
        {
            noErrors = false;
        }
        
        // Setup the generic search criteria
        int pageNo = MasterDetailControllerUtil.defaultZero(page);
        // TODO Make pageSize a user driven variable
        int pageSize = 25;
        String orderByField = MasterDetailControllerUtil.resolveOrderBy(orderBy);
        boolean orderAscending = MasterDetailControllerUtil.defaultFalse(orderAsc);
        // boolean selAll = MasterDetailControllerUtil.defaultTrue(selectAll);
        // TODO Could move this into the wiretap search criteria, but have to
        // Make sure we don't magically validate someone's poor input
        String compName = MasterDetailControllerUtil.nullForEmpty(componentName);
        String evtId = MasterDetailControllerUtil.nullForEmpty(eventId);
        String ploadId = MasterDetailControllerUtil.nullForEmpty(payloadId);
        // TODO Shift this code into the wireTap search criteria when setting the date time there
        Date fromDate = null;
        Date untilDate = null;
        try
        {
            fromDate = wiretapSearchCriteria.getFromDateTime();
        }
        catch (ParseException e)
        {
            errors.add("From Date/Time was not parseable, please choose a valid date from the date picker");
            noErrors = false;
        }
        try
        {
            untilDate = wiretapSearchCriteria.getUntilDateTime();
        }
        catch (ParseException e)
        {
            errors.add("Until Date/Time was not parseable, please choose a valid date from the date picker");
            noErrors = false;
        }
        fromDate = MasterDetailControllerUtil.nullForEmpty(fromDate);
        untilDate = MasterDetailControllerUtil.nullForEmpty(untilDate);
        String content = MasterDetailControllerUtil.nullForEmpty(payloadContent);

        logger.info("******************************");
        logger.info("Executing a Search with:");
        logger.info("Page [" + pageNo + "]");
        logger.info("Page Size [" + pageSize + "]");
        logger.info("Order By [" + orderByField + "]");
        logger.info("Order Ascending Flag [" + orderAscending + "]");
        logger.info("Module Names [" + moduleNames + "]");
        logger.info("Component Name [" + compName + "]");
        logger.info("Event Id [" + evtId + "]");
        logger.info("Payload Id [" + ploadId + "]");
        logger.info("From Date/Time [" + fromDate + "]");
        logger.info("Until Date/Time [" + untilDate + "]");
        logger.info("Payload Content [" + content + "]");
        
        // Perform the paged search
        PagedSearchResult<WiretapEvent> pagedResult = null;
        if (noErrors)
        {
            pagedResult = this.wiretapService.findWiretapEvents(pageNo, pageSize, orderByField, orderAscending, moduleNames, compName,
                evtId, ploadId, fromDate, untilDate, content);
        }

        // Store the search parameters used
        Map<String, Object> searchParams = new HashMap<String, Object>();
        MasterDetailControllerUtil.addParam(searchParams, "moduleNames", moduleNames);
        MasterDetailControllerUtil.addParam(searchParams, "componentName", componentName);
        MasterDetailControllerUtil.addParam(searchParams, "eventId", eventId);
        MasterDetailControllerUtil.addParam(searchParams, "payloadId", payloadId);
        MasterDetailControllerUtil.addParam(searchParams, "fromDateString", fromDateString);
        MasterDetailControllerUtil.addParam(searchParams, "fromTimeString", fromTimeString);
        MasterDetailControllerUtil.addParam(searchParams, "untilDateString", untilDateString);
        MasterDetailControllerUtil.addParam(searchParams, "untilTimeString", untilTimeString);
        MasterDetailControllerUtil.addParam(searchParams, "payloadContent", payloadContent);
        // MasterDetailControllerUtil.addPagedModelAttributes(orderByField, orderAscending, selAll, model, pageNo, pagedResult, request, searchParams);        
        MasterDetailControllerUtil.addPagedModelAttributes(orderByField, orderAscending, selectAll, model, pageNo, pagedResult, request, searchParams);

        logger.info("******************************");
        logger.info("Storing the Search Parameters:");
        logger.info("Order By [" + orderByField + "]");
        logger.info("Order Ascending Flag [" + orderAscending + "]");
        // logger.info("Select All Flag [" + selAll + "]");
        logger.info("Select All Flag [" + selectAll + "]");
        Set<?> keys = model.keySet();
        Object value = null;
        for (Object key:keys)
        {
            value = model.get(key);
            // logger.info("Model Value [" + value.toString() + "]");
        }
        logger.info("Page [" + pageNo + "]");
        // logger.info("Paged Result [" + pagedResult + "]");
        logger.info("Request [" + request + "]");
        for (String key:searchParams.keySet())
        {
            logger.info("Search Parameter key [" + key + "] value [" + searchParams.get(key) + "]");    
        }
        logger.info("Search Parameters [" + searchParams + "]");
        logger.info("******************************");
        
        // Return back to the combined search / search results view
        return "events/wiretapEvents";
    }

    /**
     * Helper method that constructs the URL for the newSearch redirect
     * 
     * @return The redirect URL for the new search
     */
    private String getNewSearchURL()
    {
        String springRedirectCommand = "redirect:";
        String baseURL = "list.htm?";
        
        // Build the list of parameters
        List<Module> modules = this.moduleService.getModules();
        String parameters = "newSearch=false&page=0&orderBy=id&orderAsc=true&selectAll=true";
        for (Module module : modules)
        {
            parameters = parameters + "&moduleNames=" + module.getName();
        }
        
        String finalURL = springRedirectCommand + baseURL + parameters;
        return finalURL;
    }
    
    /**
     * View a specified WiretapEvent
     * 
     * @param eventId The id of the event to get
     * @param searchResultsUrl The Search Results Page we came from
     * @param modelMap The model
     * @return The model and view representing the wiretap event
     */
    @RequestMapping("viewEvent.htm")
    public ModelAndView viewEvent(@RequestParam("eventId") long eventId, @RequestParam(required = false) String searchResultsUrl, ModelMap modelMap)
    {
        this.logger.info("inside viewEvent, eventId=[" + eventId + "]");
        // TODO Make this occur purely in the view and not in the controller
        WiretapEvent wiretapEvent = this.wiretapService.getWiretapEvent(new Long(eventId));
        String payloadContent = wiretapEvent.getPayloadContent();
        String prettyXMLContent = "";
        if (payloadContentIsXML(payloadContent))
        {
            // Escape the HTML
            prettyXMLContent = StringEscapeUtils.escapeHtml(payloadContent);
            // Then add <br> instead of newline
            prettyXMLContent = prettyXMLContent.replaceAll(System.getProperty("line.separator"), "<br>");
            // Then add &nbsp; instead of ' '
            prettyXMLContent = prettyXMLContent.replaceAll(" ", "&nbsp;");
            payloadContent = prettyXMLContent;
        }
        modelMap.addAttribute("wiretapEvent", this.wiretapService.getWiretapEvent(new Long(eventId)));
        modelMap.addAttribute("payloadContent", payloadContent);
        modelMap.addAttribute("searchResultsUrl", searchResultsUrl);
        return new ModelAndView("events/viewWiretapEvent", modelMap);
    }

    /**
     * Helper method to determine if payload content is XML TODO Find formal way
     * of proving that it is XML
     * 
     * @param payloadContent - The content to check
     * @return true of the content is XML
     */
    private boolean payloadContentIsXML(String payloadContent)
    {
        if (payloadContent.startsWith("<?xml"))
        {
            return true;
        }
        return false;
    }

    /**
     * View a specific payload content in a best guess native format
     * 
     * @param eventId The id of the event to get
     * @param response - Standard response stream
     * @return null
     */
    @RequestMapping("viewPrettyPayloadContent.htm")
    public ModelAndView viewPrettyPayloadContent(@RequestParam("eventId") long eventId, HttpServletResponse response)
    {
        this.logger.info("inside viewPrettyPayloadContent, eventId=[" + eventId + "]");
        WiretapEvent wiretapEvent = this.wiretapService.getWiretapEvent(new Long(eventId));
        response.setContentType("text/xml");
        try
        {
            response.getOutputStream().write(wiretapEvent.getPayloadContent().getBytes());
        }
        catch (IOException e)
        {
            this.logger.error("Could not render payload content.", e);
        }
        return null;
    }
}
