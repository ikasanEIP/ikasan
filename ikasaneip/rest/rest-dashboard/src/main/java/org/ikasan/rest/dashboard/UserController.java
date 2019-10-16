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
package org.ikasan.rest.dashboard;

import org.ikasan.rest.dashboard.model.dto.ErrorDto;
import org.ikasan.rest.dashboard.model.user.IkasanPrincipal;
import org.ikasan.rest.dashboard.model.user.Policy;
import org.ikasan.rest.dashboard.model.user.Role;
import org.ikasan.rest.dashboard.model.user.UserDto;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST contract of User Service interface available on dashboard.
 * This Endpoints are required by modules to be able to access blue console.
 */
@RequestMapping("/rest")
@RestController
public class UserController
{
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
        if (this.userService == null)
        {
            throw new IllegalArgumentException("userService cannot be null!");
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/user")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin','WriteBlueConsole','ReadBlueConsole')")
    public ResponseEntity getUser(@RequestParam(name = "username") String username)
    {
        try
        {
            User user = this.userService.loadUserByUsername(username);
            if (user == null)
            {
                return new ResponseEntity(new ErrorDto("User [" + username + "] not found."), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(convert(user), HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity(new ErrorDto("User [" + username + "] not found."), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/users")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin','WriteBlueConsole','ReadBlueConsole')")
    public ResponseEntity getUsers()
    {
        try
        {
            List<User> users = this.userService.getUsers();
            if (users != null && !users.isEmpty())
            {
                return new ResponseEntity(users.stream().map(this::convert).collect(Collectors.toSet()), HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity(new ArrayList<UserDto>(), HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity(new ErrorDto("Issue when not found."), HttpStatus.NOT_FOUND);
        }
    }

    private UserDto convert(User user)
    {
        UserDto dto = new UserDto();
        dto.setDepartment(user.getDepartment());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setSurname(user.getSurname());
        dto.setPreviousAccessTimestamp(user.getPreviousAccessTimestamp());
        dto.setPrincipals(user.getPrincipals().stream().map(p -> convert(p)).collect(Collectors.toSet()));
        return dto;
    }

    private IkasanPrincipal convert(org.ikasan.security.model.IkasanPrincipal ikasanPrincipal)
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName(ikasanPrincipal.getName());
        principal.setDescription(ikasanPrincipal.getDescription());
        principal.setRoles(ikasanPrincipal.getRoles().stream().map(r -> convert(r)).collect(Collectors.toSet()));
        return principal;
    }

    private Role convert(org.ikasan.security.model.Role role)
    {
        Role r = new Role();
        r.setName(role.getName());
        r.setDescription(role.getDescription());
        r.setPolicies(role.getPolicies().stream().map(p -> convert(p)).collect(Collectors.toSet()));
        return r;
    }

    private Policy convert(org.ikasan.security.model.Policy policy)
    {
        Policy p = new Policy();
        p.setName(policy.getName());
        p.setDescription(policy.getDescription());
        return p;
    }
}
