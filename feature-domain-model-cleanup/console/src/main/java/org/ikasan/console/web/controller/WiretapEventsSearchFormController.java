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
package org.ikasan.console.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.ikasan.console.web.command.WiretapSearchCriteria;
import org.ikasan.console.web.command.WiretapSearchCriteriaValidator;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class is the Controller for the WiretapEvent Search Form
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
            @RequestParam(required = false) Integer page, @RequestParam(required = false) String orderBy, @RequestParam(required = false) Boolean orderAsc,
            @RequestParam(required = false) Boolean selectAll, @RequestParam(required = false) Set<String> moduleNames,
            @RequestParam(required = false) String componentName, @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String payloadId, @RequestParam(required = false) String fromDateString,
            @RequestParam(required = false) String fromTimeString, @RequestParam(required = false) String untilDateString,
            @RequestParam(required = false) String untilTimeString, @RequestParam(required = false) String payloadContent, ModelMap model)
    {
        boolean noErrors = true;
        // If it's a new search then automatically run the default search
        if (newSearch != null && newSearch)
        {
            logger.debug("Redirecting to the Default Search");
            String newSearchURL = getNewSearchURL();
            return newSearchURL;
        }
        // Log the search criteria coming in
        if (logger.isDebugEnabled())
        {
            logger.debug("Form values that came in:");
            logSearch(newSearch, page, orderBy, orderAsc, selectAll, moduleNames, componentName, eventId, payloadId, fromDateString, fromTimeString,
                untilDateString, untilTimeString, payloadContent);
        }
        // Set the search criteria from the values that came in
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
        int pageSize = 25;
        String orderByField = MasterDetailControllerUtil.resolveOrderBy(orderBy);
        boolean orderAscending = MasterDetailControllerUtil.defaultFalse(orderAsc);
        Date fromDate = wiretapSearchCriteria.getFromDateTime();
        Date untilDate = wiretapSearchCriteria.getUntilDateTime();
        // Log the search criteria we're sending down
        if (logger.isDebugEnabled())
        {
            logger.debug("Executing Search with:");
            logSearch(newSearch, pageNo, orderByField, orderAscending, selectAll, moduleNames, componentName, eventId, payloadId, fromDateString,
                fromTimeString, untilDateString, untilTimeString, payloadContent);
            logger.debug("Page Size [" + pageSize + "]");
            logger.debug("From Date/Time [" + fromDate + "]");
            logger.debug("Until Date/Time [" + untilDate + "]");
        }
        // Perform the paged search
        PagedSearchResult<WiretapEvent> pagedResult = null;
        if (noErrors)
        {
            pagedResult = this.wiretapService.findWiretapEvents(pageNo, pageSize, orderByField, orderAscending, moduleNames, componentName, eventId, payloadId,
                fromDate, untilDate, payloadContent);
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
        MasterDetailControllerUtil
            .addPagedModelAttributes(orderByField, orderAscending, selectAll, model, pageNo, pageSize, pagedResult, request, searchParams);
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
     * Helper method to determine if payload content is XML
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

    /**
     * Download the payload content as a file
     * 
     * TODO Improve Error handling?
     * 
     * @param eventId - The Event id of the wiretapped event to download
     * @param response - The HttpServletResponse object, content is streamed to this
     */
    @RequestMapping("downloadPayloadContent.htm")
    public void outputFile(@RequestParam("eventId") long eventId, final HttpServletResponse response)
    {
        this.logger.info("inside downloadPayloadContent, eventId=[" + eventId + "]");        
        WiretapEvent wiretapEvent = this.wiretapService.getWiretapEvent(new Long(eventId));        
        String outgoingFileName = wiretapEvent.getEventId();
        response.setContentType("application/download");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + outgoingFileName + "\"");
        try
        {
            ServletOutputStream op = response.getOutputStream();
            op.write(wiretapEvent.getPayloadContent().getBytes());
            op.flush();
        }
        catch (IOException e)
        {
            this.logger.error("Could not download payload.", e);
        }
    }

    /**
     * Log the search
     * 
     * @param newSearch - The newSearch flag
     * @param page - page index into the greater result set
     * @param orderBy - The field to order by
     * @param orderAsc - Ascending flag
     * @param selectAll - Select all boolean
     * @param moduleNames - Set of names of modules to include in search
     * @param componentName - The name of the component
     * @param eventId - The Event Id
     * @param payloadId - The Payload Id
     * @param fromDateString - fromDate String
     * @param fromTimeString - fromTime String
     * @param untilDateString - untilDate String
     * @param untilTimeString - untilTime String
     * @param payloadContent - The Payload content
     */
    private void logSearch(Boolean newSearch, Integer page, String orderBy, Boolean orderAsc, Boolean selectAll, Set<String> moduleNames, String componentName,
            String eventId, String payloadId, String fromDateString, String fromTimeString, String untilDateString, String untilTimeString,
            String payloadContent)
    {
        logger.debug("New Search Flag [" + newSearch + "]");
        logger.debug("Page [" + page + "]");
        logger.debug("Order By [" + orderBy + "]");
        logger.debug("Order Ascending Flag [" + orderAsc + "]");
        logger.debug("Select All Flag [" + selectAll + "]");
        logger.debug("Module Names [" + moduleNames + "]");
        logger.debug("Component Name [" + componentName + "]");
        logger.debug("Event Id [" + eventId + "]");
        logger.debug("Payload Id [" + payloadId + "]");
        logger.debug("From Date String [" + fromDateString + "]");
        logger.debug("From Time String [" + fromTimeString + "]");
        logger.debug("Until Date String [" + untilDateString + "]");
        logger.debug("Until Time String [" + untilTimeString + "]");
        logger.debug("Payload Content [" + payloadContent + "]");
    }
}

