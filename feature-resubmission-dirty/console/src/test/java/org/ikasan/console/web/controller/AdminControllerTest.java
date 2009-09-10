package org.ikasan.console.web.controller;

import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

/**
 * Test class for the AdminController
 * 
 * @author Ikasan Development Team
 */
public class AdminControllerTest extends TestCase
{
    /**
     * Test the handleRequest method
     */
    public void testHandleRequestView()
    {
        AdminController controller = new AdminController();
        ModelAndView modelAndView = controller.handleRequest(null, null);
        assertEquals("admin/admin", modelAndView.getViewName());
    }
}