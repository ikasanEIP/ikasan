package org.ikasan.console.web.controller;

import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

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