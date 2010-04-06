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

import org.ikasan.framework.security.model.User;
import org.ikasan.framework.security.service.UserService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

/**
 * Test class for the UsersController
 * 
 * @author Ikasan Development Team
 */
public class UsersControllerTest extends TestCase
{
    /** The context that the tests run in, allows for mocking actual concrete classes */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** A mocked UserService for use with testing */
    UserService userService = context.mock(UserService.class);
    
    /**
     * Test the constructor
     */
    public void testConstructor()
    {
        try 
        {
            new UsersController(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Do nothing
        }
        new UsersController(userService);
    }

    /**
     * Test the list Users method
     */
    public void testListUsers()
    {
        UsersController controller = new UsersController(userService);
        ModelMap model = null;
        ModelAndView result = controller.listUsers(model);
        assertEquals("admin/users/users", result.getViewName());
        
        controller = new UsersController(userService);
        model = new ModelMap();
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(userService).getUsers();
                will(returnValue(new ArrayList<User>()));
            }
        });
        // Test
        result = controller.listUsers(model);
        // Verify
        assertNotNull(model.get("user"));
        List<User> users = (List<User>)model.get("users");
        assertTrue(users.isEmpty());
        assertEquals("admin/users/users", result.getViewName());

        model = new ModelMap();
        // Expectations
        context.checking(new Expectations()
        {
            {
                one(userService).getUsers();
                will(returnValue(new ArrayList<User>()));
            }
        });
        model.addAttribute("user", new User(null, null, false));
        result = controller.listUsers(model);
        assertNotNull(model.get("user"));
        users = (List<User>)model.get("users");
        assert(users.isEmpty());
        assertEquals("admin/users/users", result.getViewName());
    }

}