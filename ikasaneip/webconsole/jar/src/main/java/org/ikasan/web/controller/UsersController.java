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
package org.ikasan.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.security.model.*;
import org.ikasan.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
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
@RequestMapping("/admin/users")
@SessionAttributes("user")
public class UsersController {
    /**
     * username parameter
     */
    private static final String USERNAME_PARAMETER_NAME = "username";

    /**
     * authority parameter
     */
    private static final String AUTHORITY_PARAMETER_NAME = "authority";

    /**
     * The user service to use
     */
    private UserService userService;

    /**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory.getLogger(UsersController.class);

    /**
     * Constructor
     *
     * @param userService - The user service to use
     */
    @Autowired
    public UsersController(UserService userService) {
        super();
        this.userService = userService;
    }

    /**
     * List the users known to the system
     *
     * @param model - The window (map)
     * @return "modules/modules"
     */
    @RequestMapping("list.htm")
    public String listUsers(ModelMap model) {
        if (model.get("user") == null) {
            model.addAttribute("user", new User(null, null, null, true));
        }
        model.addAttribute("users", this.userService.getUsers());
        return "admin/users/users";
    }

    /**
     * Accepts submission of the createUser form
     *
     * @param model  - The window (map)
     * @param user   - The user we're trying to create
     * @param result - The result
     * @return view the user
     */
    @RequestMapping(value = "createUser.htm", method = RequestMethod.POST)
    public String createUser(ModelMap model, @ModelAttribute("user") User user, BindingResult result) {
        // check that user doesn't already exist, and the password has been supplied
        ValidationUtils.rejectIfEmpty(result, "username", "field.required", "Username cannot be empty");
        ValidationUtils.rejectIfEmpty(result, "password", "field.required", "Password cannot be empty");
        ValidationUtils.rejectIfEmpty(result, "email", "field.required", "Email Address cannot be empty");
        if (this.userService.userExists(user.getUsername())) {
            result.addError(new FieldError("user", "username", "User with this username already exists"));
        }
        if (result.hasErrors()) {
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
     * @param model    - The window (map)
     * @return path to user view
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String viewUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model) {
        User user = this.userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("nonGrantedAuthorities", getNonGrantedAuthrities(user.getAuthorities()));
        return "admin/users/viewUser";
    }

    /**
     * Accepts submission of the changePassword form
     *
     * @param user               - The user we're changing the password for
     * @param confirmNewPassword - The password again, for confirmation
     * @param model              - The window (map)
     * @param result             - The binding result
     * @return view the user
     */
    @RequestMapping(value = "changePassword.htm", method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("user") User user, @RequestParam("confirm_password") String confirmNewPassword, ModelMap model, BindingResult result) {
        try {
            this.userService.changeUsersPassword(user.getUsername(), user.getPassword(), confirmNewPassword);
        } catch (IllegalArgumentException e) {
            result.addError(new FieldError("password", "password", e.getMessage()));
        }
        return viewUser(user.getUsername(), model);
    }

    /**
     * Grant a new authority to an existing user
     *
     * @param username  - The name of the user we're granting authority to
     * @param authority - The authority we're granting the user
     * @param model     - The window (map)
     * @return to the view users
     */
    @RequestMapping(value = "grantAuthority.htm", method = RequestMethod.POST)
    public String grantAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
                                 @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model) {
        this.userService.grantAuthority(username, authority);
        return viewUser(username, model);
    }

    /**
     * Revoke an authority from a existing user
     *
     * @param username  - The name of the user we're revoking authority from
     * @param authority - The authority we're revoking from the user
     * @param model     - The window (map)
     * @return to the view users
     */
    @RequestMapping(value = "revokeAuthority.htm", method = RequestMethod.POST)
    public String revokeAuthority(@RequestParam(USERNAME_PARAMETER_NAME) String username,
                                  @RequestParam(AUTHORITY_PARAMETER_NAME) String authority, ModelMap model) {
        this.userService.revokeAuthority(username, authority);
        return viewUser(username, model);
    }

    /**
     * Delete a existing user
     *
     * @param username - The name of the user we're deleting
     * @param model    - The window (map)
     * @return to the view users
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public String deleteUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model) {
        this.userService.deleteUser(username);
        return listUsers(model);
    }

    /**
     * Disable an existing user
     *
     * @param username - The name of the user we're disabling
     * @param model    - The window (map)
     * @return to the view users
     */
    @RequestMapping(value = "disable.htm", method = RequestMethod.POST)
    public String disableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model) {
        this.userService.disableUser(username);
        return viewUser(username, model);
    }

    /**
     * Enable an existing user
     *
     * @param username - The name of the user we're enabling
     * @param model    - The window (map)
     * @return to the view users
     */
    @RequestMapping(value = "enable.htm", method = RequestMethod.POST)
    public String enableUser(@RequestParam(USERNAME_PARAMETER_NAME) String username, ModelMap model) {
        this.userService.enableUser(username);
        return viewUser(username, model);
    }

    /**
     * Get the list of non granted authorities
     *
     * @param authorities - Full list of authorities
     * @return list of non granted authorities
     */
    private List<Policy> getNonGrantedAuthrities(Collection<? extends GrantedAuthority> authorities) {
        // start with a list of all the authorities

        List<Policy> nonGrantedAuthorities = this.userService.getAuthorities();

        // remove all that are granted
        for (GrantedAuthority authority : authorities) {
            nonGrantedAuthorities.remove(authority);
        }
        return nonGrantedAuthorities;
    }
}
