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

import org.apache.log4j.Logger;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Spring MVC controller class for dealing with a request to go to the home page
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/setup")
public class PersistenceController
{
    private static Logger logger = Logger.getLogger(PersistenceController.class);

    PersistenceServiceFactory persistenceServiceFactory;

    @Autowired
    public PersistenceController(PersistenceServiceFactory persistenceServiceFactory)
    {
        this.persistenceServiceFactory = persistenceServiceFactory;
        if(persistenceServiceFactory == null)
        {
            throw new IllegalArgumentException("persistenceServiceFactory cannot be 'null'");
        }
    }

    /**
     * Standard handleRequest Method, in this case simply returns the view that the
     * user requested.
     *
     * @param request - Standard HttpServletRequest, not used
     * @param response - Standard HttpServletResponse, not used
     * @return ModelAndView, in this case logical mapping to the home view
     * @throws javax.servlet.ServletException - Servlet based Exception
     * @throws java.io.IOException - IO based Exception
     * 
     * Suppress unused warnings as handleRequest is called by Spring framework
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "provider.htm", method = RequestMethod.GET)
    public ModelAndView requestProviderSetup(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException
    {

//        if(persistenceSetupRequired())
        if(true)
        {
            Set<String> providers = this.persistenceServiceFactory.getProviders();
            model.addAttribute("providers", providers);
            return new ModelAndView("admin/setup/getProviders", model);
        }

        // return without provider setup
        return new ModelAndView("accessDenied");
    }

    private boolean persistenceSetupRequired()
    {
        Set<String> providers = this.persistenceServiceFactory.getProviders();
        for(String provider:providers)
        {
            try
            {
                PersistenceService persistenceService = this.persistenceServiceFactory.getPersistenceService(provider);
                if(persistenceService.adminAccountExists())
                {
                    return false;
                }
            }
            catch(RuntimeException e)
            {
                logger.info("Provider [" + provider + "] not supported.", e);
            }
        }

        return true;
    }

    @RequestMapping(value = "completed.htm", method = RequestMethod.GET)
    public ModelAndView completed(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException
    {
        return new ModelAndView("/home");
    }

    // FIXME - added as a workaround for redirection after persistence provider setup steps
    @RequestMapping(value = "home.htm", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException
    {
        return new ModelAndView("/home");
    }

    @RequestMapping(value = "createProviderPersistence.htm", method = RequestMethod.POST)
    public ModelAndView createProviderPersistence(@RequestParam("provider") String provider, ModelMap model)
    {
        PersistenceService persistenceService = this.persistenceServiceFactory.getPersistenceService(provider);

        try
        {
            if(false)
            {
                return new ModelAndView("accessDenied");
            }

            persistenceService.createPersistence();
            persistenceService.createAdminAccount();
        }
        catch(RuntimeException e)
        {
            logger.error("Failed to create provider persistence", e);
            model.addAttribute("errors", e.getMessage());
        }

        return new ModelAndView("admin/setup/createProviderPersistence");
    }

}