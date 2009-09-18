package org.ikasan.console.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring MVC controller class for dealing with a request to go to the home page
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/home.htm")
public class HomeController
{
    /**
     * Standard handleRequest Method, in this case simply returns the view that the 
     * user requested.
     * 
     * @param request - Standard HttpServletRequest, not used
     * @param response - Standard HttpServletResponse, not used
     * @return ModelAndView, in this case logical mapping to the home view
     * @throws ServletException - Servlet based Exception
     * @throws IOException - IO based Exception
     * 
     * Suppress unused warnings as handleRequest is called by Spring framework
     */
    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        return new ModelAndView("home");
    }

}