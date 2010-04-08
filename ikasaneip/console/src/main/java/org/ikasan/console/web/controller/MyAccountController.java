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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ikasan.console.service.ConsoleService;
import org.ikasan.framework.security.model.User;
import org.ikasan.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The controller for the various my account views
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/users/*.htm")
@SessionAttributes("user")
public class MyAccountController
{
    /** The user service to use */
    private UserService userService;

    /** The console service to use */
    private ConsoleService consoleService;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(MyAccountController.class);

    /**
     * Constructor
     * 
     * @param userService - The user service to use
     * @param consoleService - The console service to use
     */
    @Autowired
    public MyAccountController(UserService userService, ConsoleService consoleService)
    {
        super();
        if (userService == null)
        {
            throw new IllegalArgumentException("UserService cannot be NULL");
        }
        this.userService = userService;
        if (consoleService == null)
        {
            throw new IllegalArgumentException("ConsoleService cannot be NULL");
        }
        this.consoleService = consoleService;
    }

    /**
     * Navigate to the change password page for a user (not an administrator)
     * 
     * @param request - Standard HTTP Request
     * @param response - Standard HTTP Response
     * @return - Model and View for changePassword
     */
    @RequestMapping(value = "changePassword.htm", method = RequestMethod.GET)
    public ModelAndView changePassword(@SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response)
    {
        return new ModelAndView("users/changePassword");
    }

    /**
     * Navigate to the my account page for a user (not an administrator)
     * 
     * @param request - Standard HTTP Request
     * @param response - Standard HTTP Response- The model, contains the user amongst other things
     * @param model - The model, will need to contain the user amongst other things
     * @return - Model and View for myAccount
     */
    @RequestMapping(value = "myAccount.htm", method = RequestMethod.GET)
    public ModelAndView myAccount(@SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response, ModelMap model)
    {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userService.loadUserByUsername(currentUser);
        model.addAttribute("user", user);
        return new ModelAndView("users/myAccount", model);
    }

    /**
     * Accepts submission of the changePassword form
     * 
     * @param user - The user we're changing the password for
     * @param confirmNewPassword - The password again, for confirmation
     * @param model - The model to add any errors too if required
     * @return Back to the changePassword screen in case of error, else back to the myAccount screen
     */
    @RequestMapping(value = "userChangePassword.htm", method = RequestMethod.POST)
    public ModelAndView userChangePassword(@ModelAttribute("user") User user,
            @RequestParam(required = false) String confirmNewPassword, ModelMap model)
    {
        List<String> errors = new ArrayList<String>();
        try
        {
            this.userService.changeUsersPassword(user.getUsername(), user.getPassword(), confirmNewPassword);
        }
        catch (IllegalArgumentException e)
        {
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty())
        {
            model.addAttribute("errors", errors);
            return new ModelAndView("users/changePassword");
        }
        logger.info("User [" + user.getUsername() + "] has successfully changed their password");
        return new ModelAndView("users/myAccount");
    }

    /**
     * Takes the user to the forgotPassword screen
     * 
     * @return forgotPassword screen
     */
    @RequestMapping(value = "forgotPassword.htm", method = RequestMethod.GET)
    public ModelAndView forgotPassword()
    {
        return new ModelAndView("users/forgotPassword");
    }

    /**
     * Send the new Password to the user
     * 
     * TODO Split tasks into individual methods
     * 
     * @param username - User to send the new password to
     * @param model - The model to hold errors
     * 
     * @return - On success, return the user to the login screen, else return back to the forgot password screen
     */
    @RequestMapping(value = "sendPassword.htm", method = RequestMethod.POST)
    public ModelAndView sendPassword(@RequestParam(required = false) String username, ModelMap model)
    {
        List<String> errors = new ArrayList<String>();
        // Load the user
        User user = null;
        try
        {
            user = userService.loadUserByUsername(username);
        }
        catch (UsernameNotFoundException e)
        {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return new ModelAndView("users/forgotPassword", model);
        }

        // Check the Email Address, TODO Could be an RFC based check here
        if (user != null)
        {
            if (user.getEmail() == null || user.getEmail() == "")
            {
                errors.add("User's email was empty, cannot send the password.");
                model.addAttribute("errors", errors);
                return new ModelAndView("users/forgotPassword", model);
            }

            try
            {
                // TODO Have a proper password generator
                // TODO Need to wrap this in a user txn
                userService.changeUsersPassword(user.getUsername(), "password", "password");
                consoleService.sendNewPassword(user);
            }
            catch (IllegalArgumentException e)
            {
                errors.add(e.getMessage());
                model.addAttribute("errors", errors);
                return new ModelAndView("users/forgotPassword", model);
            }
        }
        // Else case is unreachable

        // TODO Return nice success message then redirect them
        return new ModelAndView(new RedirectView("/console/login.jsp"));
    }
}
