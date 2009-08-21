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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.model.User;
import org.ikasan.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * The controller for the various user views
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/users/*.htm")
@SessionAttributes("user")
public class UsersController
{
    /** username parameter */
    private static final String USERNAME_PARAMETER_NAME = "username";

    /** authority parameter */
    private static final String AUTHORITY_PARAMETER_NAME = "authority";

    /** The user service to use */
    private UserService userService;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(UsersController.class);

    /**
     * Constructor
     * 
     * @param userService - The user service to use
     */
    @Autowired
    public UsersController(UserService userService)
    {
        super();
        this.userService = userService;
    }

    /**
     * List the users known to the system
     * 
     * @param model - The model (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public String listUsers(ModelMap model)
    {
        if (model.get("user") == null)
        {
            model.addAttribute("user", new User(null, null, true));
        }
        model.addAttribute("users", this.userService.getUsers());
        return "admin/users/users";
    }

    /**
     * Accepts submission of the createUser form
     * 
     * @param model - The model (map)
     * @param user - The user we're trying to create
     * @param result - The result
     * @return view the user
     */
    @RequestMapping(value = "createUser.htm", method = RequestMethod.POST)
    public String createUser(ModelMap model, @ModelAttribute("user") User user, BindingResult result)
    {
        // check that user doesn't already exist, and the password has been supplied
        ValidationUtils.rejectIfEmpty(result, "username", "field.required", "Username cannot be empty");
        ValidationUtils.rejectIfEmpty(result, "password", "field.required", "Password cannot be empty");
        if (this.userService.userExists(user.getUsername()))
        {
            result.addError(new FieldError("user", "username", "User with this username already exists"));
        }
        if (result.hasErrors())
        {
            return listUsers(model);
        }
        this.userService.createUser(user);
        this.logger.info("Created new user, with id:" + user.getId());
        return viewUser(user.getUsername(), model);
    }

    /**
     * List the users known to the system
     * 
     * @param username - The name of the user we're trying to view
     * @param model - The model (map)
     * @return path to user view
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String viewUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        User user = this.userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("nonGrantedAuthorities", getNonGrantedAuthrities(user.getAuthorities()));
        return "admin/users/viewUser";
    }

    /**
     * Accepts submission of the changePassword form
     * 
     * @param user - The user we're changing the password for
     * @param model - The model (map)
     * @return view the user
     */
    @RequestMapping(value = "changePassword.htm", method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("user") User user, ModelMap model)
    {
        this.userService.changeUsersPassword(user.getUsername(), user.getPassword());
        return viewUser(user.getUsername(), model);
    }

    /**
     * Grant a new authority to an existing user
     * 
     * @param username - The name of the user we're granting authority to 
     * @param authority - The authority we're granting the user
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="grantAuthority.htm", method = RequestMethod.POST)
    public String grantAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
            @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model)
    {
        this.userService.grantAuthority(username, authority);
        return viewUser(username, model);
    }

    /**
     * Revoke an authority from a existing user
     * 
     * @param username - The name of the user we're revoking authority from 
     * @param authority - The authority we're revoking from the user
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="revokeAuthority.htm", method = RequestMethod.POST)
    public String revokeAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
            @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model)
    {
        this.userService.revokeAuthority(username, authority);
        return viewUser(username, model);
    }

    /**
     * Delete a existing user
     * 
     * @param username - The name of the user we're deleting
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="delete.htm", method = RequestMethod.POST)
    public String deleteUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.deleteUser(username);
        return listUsers(model);
    }

    /**
     * Disable an existing user
     * 
     * @param username - The name of the user we're disabling 
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="disable.htm", method = RequestMethod.POST)
    public String disableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.disableUser(username);
        return viewUser(username, model);
    }

    /**
     * Enable an existing user
     * 
     * @param username - The name of the user we're enabling
     * @param model - The model (map)
     * @return to the view users
     */
    @RequestMapping(value="enable.htm", method = RequestMethod.POST)
    public String enableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model)
    {
        this.userService.enableUser(username);
        return viewUser(username, model);
    }

    /**
     * Get the list of non granted authorities
     * 
     * @param authorities - Full list of authorities
     * @return list of non granted authorities
     */
    private List<Authority> getNonGrantedAuthrities(GrantedAuthority[] authorities)
    {
        // start with a list of all the authorities
        List<Authority> nonGrantedAuthorities = new ArrayList<Authority>(this.userService.getAuthorities());
        // remove all that are granted
        for (GrantedAuthority authority : authorities)
        {
            nonGrantedAuthorities.remove(authority);
        }
        return nonGrantedAuthorities;
    }
}
