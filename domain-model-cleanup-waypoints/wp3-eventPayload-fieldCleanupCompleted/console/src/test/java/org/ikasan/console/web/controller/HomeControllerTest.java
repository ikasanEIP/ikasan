package org.ikasan.console.web.controller;

import junit.framework.TestCase;

import org.springframework.web.servlet.ModelAndView;

/**
 * Test class for the HomeController
 * 
 * @author Ikasan Development Team
 */
public class HomeControllerTest extends TestCase
{
    /**
     * Test the handleRequest method
     * 
     * @throws Exception - General Exception
     */
    public void testHandleRequestView() throws Exception
    {
        HomeController controller = new HomeController();
        ModelAndView modelAndView = controller.handleRequest(null, null);
        assertEquals("home", modelAndView.getViewName());
    }
}