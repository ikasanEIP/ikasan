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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.console.web.command.WiretapSearchCriteria;
import org.ikasan.console.web.command.WiretapSearchCriteriaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * This class is the Controller for the Wiretap search form
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/events/*.htm")
@SessionAttributes( { "searchCriteria", "pageNo" })
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
     * Setup the search form
     * 
     * @param request The standard HttpServletRequest
     * @param model The model
     * @return "events/wiretapEvents"
     */
    @RequestMapping(value = "search.htm", method = RequestMethod.GET)
    public String setupForm(HttpServletRequest request, ModelMap model)
    {
        WiretapSearchCriteria searchCriteria = (WiretapSearchCriteria) request.getSession().getAttribute(
            "searchCriteria");
        if (searchCriteria == null)
        {
            List<Module> modules = this.moduleService.getModules();
            Set<String> moduleNames = new HashSet<String>();
            for (Module module:modules)
            {
                moduleNames.add(module.getName());
            }
            moduleNames.add("(de)select all"); 
            searchCriteria = new WiretapSearchCriteria(moduleNames);
        }
        model.addAttribute("searchCriteria", searchCriteria);
        return "events/wiretapEvents";
    }

    /**
     * Handle submission of the search form
     * 
     * @param modelMap The model
     * @param searchCriteria The criteria to search on
     * @param result The place holder for the result
     * @return The model and its corresponding view (search results)
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap modelMap,
            @ModelAttribute("searchCriteria") WiretapSearchCriteria searchCriteria, BindingResult result)
    {
        this.validator.validate(searchCriteria, result);
        // TODO? ValidationUtils.rejectIfEmpty(result, "modules", "field.required", "You need to select at least one module");
        if (result.hasErrors())
        {
            return "events/wiretapEvents";
        }
        return displaySearchResults(modelMap, searchCriteria, 1);
    }

    /**
     * Search for the next page of results
     * 
     * @param request The standard HttpServletRequest
     * @param modelMap The model
     * @return The next page of search results (as a Model/View pairing)
     */
    @RequestMapping("next.htm")
    public String next(HttpServletRequest request, ModelMap modelMap)
    {
        WiretapSearchCriteria searchCriteria = (WiretapSearchCriteria) request.getSession().getAttribute(
            "searchCriteria");
        if (searchCriteria == null)
        {
            // must have timed out, need to go back to specify new search criteria
            return "redirect:/events/search.htm";
        }
        return displaySearchResults(modelMap, searchCriteria, getSessionPageNo(request) + 1);
    }

    /**
     * Get the page no from the session. If it does not exist, returns 1
     * 
     * @param request - The request that we're getting the page number out of
     * @return 1 or the session cached page no
     */
    private int getSessionPageNo(HttpServletRequest request)
    {
        int pageNo = 1;
        Integer sessionPageNo = (Integer) request.getSession().getAttribute("pageNo");
        if (sessionPageNo != null)
        {
            pageNo = sessionPageNo;
        }
        return pageNo;
    }

    /**
     * Return to the search results page
     * 
     * @param request The standard HttpServletRequest
     * @param modelMap The model
     * @return The next page of search results (as a Model/View pairing)
     */
    @RequestMapping("searchResults.htm")
    public String searchResults(HttpServletRequest request, ModelMap modelMap)
    {
        WiretapSearchCriteria searchCriteria = (WiretapSearchCriteria) request.getSession().getAttribute(
            "searchCriteria");
        if (searchCriteria == null)
        {
            // must have timed out, need to go back to specify new search criteria
            return "redirect:/events/search.htm";
        }
        return displaySearchResults(modelMap, searchCriteria, getSessionPageNo(request));
    }

    /**
     * Search for the previous page of results
     * 
     * @param request The standard HttpServletRequest
     * @param modelMap The model
     * @return The previous page of search results (as a Model/View pairing)
     */
    @RequestMapping("previous.htm")
    public String previous(HttpServletRequest request, ModelMap modelMap)
    {
        WiretapSearchCriteria searchCriteria = (WiretapSearchCriteria) request.getSession().getAttribute(
            "searchCriteria");
        if (searchCriteria == null)
        {
            // must have timed out, need to go back to specify new search criteria
            return "redirect:/events/search.htm";
        }
        return displaySearchResults(modelMap, searchCriteria, getSessionPageNo(request) - 1);
    }

    /**
     * View a specified WiretapEvent
     * 
     * @param eventId The id of the event to get
     * @param modelMap The model
     * @return The model and view representing the wiretap event
     */
    @RequestMapping("viewEvent.htm")
    public ModelAndView viewEvent(@RequestParam("eventId") long eventId, ModelMap modelMap)
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
        return new ModelAndView("events/viewWiretapEvent", modelMap);
    }

    /**
     * Helper method to determine if payload content is XML
     * TODO Find formal way of proving that it is XML
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
     * Search for and display a page of search results
     * 
     * @param modelMap The model
     * @param searchCriteria The criteria to search on
     * @param pageNo The page number to display (for paged results)
     * @return Model and View representing a page of search results
     */
    private String displaySearchResults(ModelMap modelMap, WiretapSearchCriteria searchCriteria, int pageNo)
    {
        modelMap.addAttribute("pageNo", pageNo);
        PagedWiretapSearchResult pagedResult = this.wiretapService.findWiretapEvents(searchCriteria.getModules(),
            searchCriteria.getComponentName(), searchCriteria.getEventId(), searchCriteria.getPayloadId(),
            searchCriteria.getFromDateTime(), searchCriteria.getUntilDateTime(), searchCriteria.getPayloadContent(),
            20, pageNo);
        modelMap.addAttribute("searchResults", pagedResult);
        return "events/wiretapEventResults";
    }
}
