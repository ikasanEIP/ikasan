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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.framework.web.command.WiretapSearchCriteria;
import org.ikasan.framework.web.command.WiretapSearchCriteriaValidator;
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
        return moduleService.getModules();
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
            searchCriteria = new WiretapSearchCriteria();
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
        validator.validate(searchCriteria, result);
        // ValidationUtils.rejectIfEmpty(result, "modules", "field.required", "You need to select at least one module");
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
        logger.info("inside viewEvent, eventId=[" + eventId + "]");
        modelMap.addAttribute("wiretapEvent", wiretapService.getWiretapEvent(new Long(eventId)));
        return new ModelAndView("events/viewWiretapEvent", modelMap);
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
        logger.info("inside viewPrettyPayloadContent, eventId=[" + eventId + "]");
        WiretapEvent wiretapEvent = wiretapService.getWiretapEvent(new Long(eventId));
        response.setContentType("text/xml");
        try
        {
            response.getOutputStream().write(wiretapEvent.getPayloadContent().getBytes());
        }
        catch (IOException e)
        {
            logger.error("Could not render payload content.", e);
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
        PagedWiretapSearchResult pagedResult = wiretapService.findWiretapEvents(searchCriteria.getModules(),
            null, searchCriteria.getComponentName(), searchCriteria.getEventId(), searchCriteria.getPayloadId(),
            searchCriteria.getFromDateTime(), searchCriteria.getUntilDateTime(), searchCriteria.getPayloadContent(),
            20, pageNo);
        modelMap.addAttribute("searchResults", pagedResult);
        return "events/wiretapEventResults";
    }
}
