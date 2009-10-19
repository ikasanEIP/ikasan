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

import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.console.module.Module;
import org.ikasan.console.module.service.ModuleService;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.ikasan.console.pointtopointflow.service.PointToPointFlowProfileService;
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

    /** The module service */
    private ModuleService moduleService;

    /** The point to point flow profile service */
    private PointToPointFlowProfileService pointToPointFlowProfileService;

    /** The search criteria validator to use */
    private WiretapSearchCriteriaValidator validator = new WiretapSearchCriteriaValidator();

    /**
     * Constructor
     * 
     * @param wiretapService - The wiretap service to use
     * @param moduleService - The module service to use
     * @param pointToPointFlowProfileService - The point to point flow profile
     *            container to use
     */
    @Autowired
    public WiretapEventsSearchFormController(WiretapService wiretapService, ModuleService moduleService,
            PointToPointFlowProfileService pointToPointFlowProfileService)
    {
        super();
        this.wiretapService = wiretapService;
        this.moduleService = moduleService;
        this.pointToPointFlowProfileService = pointToPointFlowProfileService;
    }

    /**
     * Get the modules
     * 
     * @return Set of modules
     */
    @ModelAttribute("modules")
    public Set<Module> getModules()
    {
        Set<Module> modules = this.moduleService.getAllModules();
        return modules;
    }

    /**
     * Get the point to point flow profiles
     * 
     * @return List of point to point flow profiles
     */
    @ModelAttribute("pointToPointFlowProfiles")
    public Set<PointToPointFlowProfile> getPointToPointFlowProfiles()
    {
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = this.pointToPointFlowProfileService.getAllPointToPointFlowProfiles();
        return pointToPointFlowProfiles;
    }

    /**
     * Show the combined wiretap event search and search results view
     * 
     * @return wiretap events view
     */
    @RequestMapping("newSearch.htm")
    public String initialiseWiretapEventSearch()
    {
        String springRedirectCommand = "redirect:";
        String baseURL = "list.htm?";
        String parameters = "page=0&orderBy=id&orderAsc=true&pointToPointFlowProfileSearch=true&pointToPointFlowProfileSelectAll=true&moduleSelectAll=false&pageSize=10";
        // Build the list of parameters
        Set<Long> pointToPointFlowProfileIds = this.pointToPointFlowProfileService.getAllPointToPointFlowProfileIds();
        for (Long pointToPointFlowProfileId : pointToPointFlowProfileIds)
        {
            parameters = parameters + "&pointToPointFlowProfileIds=" + pointToPointFlowProfileId;
        }
        String finalURL = springRedirectCommand + baseURL + parameters;
        return finalURL;
    }

    /**
     * Show the combined wiretap event search and search results view
     * 
     * @param request - Standard HttpRequest
     * @param page - page index into the greater result set
     * @param orderBy - The field to order by
     * @param orderAsc - Ascending flag
     * @param pointToPointFlowProfileSearch - Flag to indicate what type of
     *            search
     * @param pointToPointFlowProfileSelectAll - Select all boolean for
     *            pointToPointFlowProfile based search
     * @param moduleSelectAll - Select all boolean for module based search
     * @param pageSize - Number of search results to display per page
     * @param moduleIds - Set of ids of modules to include in search - must
     *            contain at least one id
     * @param pointToPointFlowProfileIds - Set of ids of point to point flow
     *            profiles to include in search - must contain at least one
     *            pointToPointFlowProfileId
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
    public String listWiretapEvents(HttpServletRequest request, @RequestParam(required = false) Integer page, @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Boolean orderAsc, @RequestParam(required = false) String pointToPointFlowProfileSearch,
            @RequestParam(required = false) Boolean pointToPointFlowProfileSelectAll, @RequestParam(required = false) Boolean moduleSelectAll,
            @RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Set<Long> moduleIds,
            @RequestParam(required = false) Set<Long> pointToPointFlowProfileIds, @RequestParam(required = false) String componentName,
            @RequestParam(required = false) String eventId, @RequestParam(required = false) String payloadId,
            @RequestParam(required = false) String fromDateString, @RequestParam(required = false) String fromTimeString,
            @RequestParam(required = false) String untilDateString, @RequestParam(required = false) String untilTimeString,
            @RequestParam(required = false) String payloadContent, ModelMap model)
    {
        Integer pageSizeToReturn = pageSize;
        Set<Long> moduleIdsToSearchOn = moduleIds;
        List<String> errors = new ArrayList<String>();
        boolean noErrors = true;
        // Execute a pointToPointFlowProfile based search, so get the module ids
        // from those.
        if (pointToPointFlowProfileSearch.equals("true"))
        {
            logger.debug("This is a PointToPointFlowProfile Based Search.");
            moduleIdsToSearchOn = getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
        }
        // If a search is executed from a page that has no search results
        // defined then the pageSize is null, we therefore default it to 10
        if (pageSize == null)
        {
            pageSizeToReturn = 10;
        }
        // Log the search criteria coming in
        // TODO Fix log4j config with Jboss in order to be able to log.debug
        if (logger.isDebugEnabled())
        {
            logger.debug("Form values that came in:");
            logSearch(page, orderBy, orderAsc, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll, moduleSelectAll, pageSizeToReturn,
                moduleIdsToSearchOn, pointToPointFlowProfileIds, componentName, eventId, payloadId, fromDateString, fromTimeString, untilDateString,
                untilTimeString, payloadContent);
        }
        // Set the search criteria from the values that came in and then
        // validate them
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(moduleIdsToSearchOn);
        wiretapSearchCriteria.setComponentName(componentName);
        wiretapSearchCriteria.setEventId(eventId);
        wiretapSearchCriteria.setPayloadId(payloadId);
        wiretapSearchCriteria.setFromDate(fromDateString);
        wiretapSearchCriteria.setFromTime(fromTimeString);
        wiretapSearchCriteria.setUntilDate(untilDateString);
        wiretapSearchCriteria.setUntilTime(untilTimeString);
        wiretapSearchCriteria.setPayloadContent(payloadContent);
        // Validate the criteria and add any errors to the model
        this.validator.validate(wiretapSearchCriteria, errors);
        model.addAttribute("errors", errors);
        if (!errors.isEmpty())
        {
            noErrors = false;
        }
        // Setup the generic search criteria
        int pageNo = MasterDetailControllerUtil.defaultZero(page);
        String orderByField = MasterDetailControllerUtil.resolveOrderBy(orderBy);
        boolean orderAscending = MasterDetailControllerUtil.defaultFalse(orderAsc);
        // boolean pointToPointFlowProfileSearchFlag =
        // MasterDetailControllerUtil.defaultTrue(pointToPointFlowProfileSearch);
        Date fromDate = wiretapSearchCriteria.getFromDateTime();
        Date untilDate = wiretapSearchCriteria.getUntilDateTime();
        // Log the search criteria we're sending down
        if (logger.isDebugEnabled())
        {
            logger.debug("Executing Search with:");
            logSearch(pageNo, orderByField, orderAscending, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll, moduleSelectAll,
                pageSizeToReturn, moduleIdsToSearchOn, pointToPointFlowProfileIds, componentName, eventId, payloadId, fromDateString, fromTimeString,
                untilDateString, untilTimeString, payloadContent);
            logger.debug("From Date/Time [" + fromDate + "]");
            logger.debug("Until Date/Time [" + untilDate + "]");
        }
        // Perform the paged search
        PagedSearchResult<WiretapEvent> pagedResult = null;
        if (noErrors)
        {
            Set<String> moduleNames = this.moduleService.getModuleNames(moduleIdsToSearchOn);
            pagedResult = this.wiretapService.findWiretapEvents(pageNo, pageSizeToReturn, orderByField, orderAscending, moduleNames, componentName, eventId,
                payloadId, fromDate, untilDate, payloadContent);
        }
        // Store the search parameters used
        Map<String, Object> searchParams = new HashMap<String, Object>();
        if (pointToPointFlowProfileSearch.equals("true"))
        {
            MasterDetailControllerUtil.addParam(searchParams, "pointToPointFlowProfileIds", pointToPointFlowProfileIds);
        }
        else
        {
            MasterDetailControllerUtil.addParam(searchParams, "moduleIds", moduleIdsToSearchOn);
        }
        MasterDetailControllerUtil.addParam(searchParams, "componentName", componentName);
        MasterDetailControllerUtil.addParam(searchParams, "eventId", eventId);
        MasterDetailControllerUtil.addParam(searchParams, "payloadId", payloadId);
        MasterDetailControllerUtil.addParam(searchParams, "fromDateString", fromDateString);
        MasterDetailControllerUtil.addParam(searchParams, "fromTimeString", fromTimeString);
        MasterDetailControllerUtil.addParam(searchParams, "untilDateString", untilDateString);
        MasterDetailControllerUtil.addParam(searchParams, "untilTimeString", untilTimeString);
        MasterDetailControllerUtil.addParam(searchParams, "payloadContent", payloadContent);
        MasterDetailControllerUtil.addPagedModelAttributes(orderByField, orderAscending, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll,
            moduleSelectAll, model, pageNo, pageSizeToReturn, pagedResult, request, searchParams);
        // Return back to the combined search / search results view
        return "events/wiretapEvents";
    }

    /**
     * Get a Set of module ids from the list of given pointToPointFlowProfileIds
     * 
     * @param pointToPointFlowProfileIds - The list of
     *            pointToPointFlowProfileIds to get the Module Ids from
     * @return Set of module ids
     */
    private Set<Long> getModuleIdsFromPointToPointFlowProfiles(Set<Long> pointToPointFlowProfileIds)
    {
        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
        return moduleIds;
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
        this.logger.debug("inside viewEvent, eventId=[" + eventId + "]");
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
        this.logger.debug("inside viewPrettyPayloadContent, eventId=[" + eventId + "]");
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
     * @param response - The HttpServletResponse object, content is streamed to
     *            this
     */
    @RequestMapping("downloadPayloadContent.htm")
    public void outputFile(@RequestParam("eventId") long eventId, final HttpServletResponse response)
    {
        this.logger.debug("inside downloadPayloadContent, eventId=[" + eventId + "]");
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
     * @param page - page index into the greater result set
     * @param orderBy - The field to order by
     * @param orderAsc - Ascending flag
     * @param pointToPointFlowProfileSearch - Flag to indicate what type of
     *            search
     * @param pointToPointFlowProfileSelectAll - Select all boolean for
     *            pointToPointFlowProfile based search
     * @param moduleSelectAll - Select all boolean for module based search
     * @param pageSize - Page Size, number of search results per page
     * @param moduleIds - Set of ids of modules to include in search
     * @param pointToPointFlowProfileIds - Set of ids of
     *            pointToPointFlowProfiles to include in search
     * @param componentName - The name of the component
     * @param eventId - The Event Id
     * @param payloadId - The Payload Id
     * @param fromDateString - fromDate String
     * @param fromTimeString - fromTime String
     * @param untilDateString - untilDate String
     * @param untilTimeString - untilTime String
     * @param payloadContent - The Payload content
     */
    private void logSearch(Integer page, String orderBy, Boolean orderAsc, String pointToPointFlowProfileSearch,
            Boolean pointToPointFlowProfileSelectAll, Boolean moduleSelectAll, Integer pageSize, Set<Long> moduleIds, Set<Long> pointToPointFlowProfileIds,
            String componentName, String eventId, String payloadId, String fromDateString, String fromTimeString, String untilDateString,
            String untilTimeString, String payloadContent)
    {
        logger.debug("Page [" + page + "]");
        logger.debug("Order By [" + orderBy + "]");
        logger.debug("Order Ascending Flag [" + orderAsc + "]");
        logger.debug("Point To Point Flow Profile Search Flag [" + pointToPointFlowProfileSearch + "]");
        logger.debug("Point To Point Flow Profile Select All Flag [" + pointToPointFlowProfileSelectAll + "]");
        logger.debug("Module Select All Flag [" + moduleSelectAll + "]");
        logger.debug("Number of search results per page [" + pageSize + "]");
        logger.debug("Module Ids [" + moduleIds + "]");
        logger.debug("PointToPointFlowProfile Ids [" + pointToPointFlowProfileIds + "]");
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
