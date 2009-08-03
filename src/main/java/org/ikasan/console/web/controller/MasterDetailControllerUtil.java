/*
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

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.ui.ModelMap;

/**
 * Common utility functions used by MVC controllers that deal with paged
 * Master/Detail search functions
 * 
 * @author The Ikasan Development Team
 */
public class MasterDetailControllerUtil
{
    /**
     * Adds a parameter to the searchParams map, only if it has a value
     * 
     * @param searchParams - The search parameters map to add to
     * @param paramName - The name of the parameter to add
     * @param paramValue - The value of the parameter to add
     */
    public static void addParam(Map<String, Object> searchParams, String paramName, Object paramValue)
    {
        if (paramValue != null)
        {
            searchParams.put(paramName, paramValue);
        }
    }

    /**
     * Adds all standard control attributes to the model for displaying the
     * search results page
     * 
     * @param orderBy - The field we are ordering by
     * @param orderAsc - Flag to determine if we are ordering by ascending value
     *            or not
     * @param selectAll - Select All flag  
     * @param model - The model we are displaying (the data)
     * @param pageNo - The page number we are on
     * @param pagedResult - The paged result page
     * @param request - standard HttpRequest
     * @param searchParams - The search parameters we're using
     */
    public static void addPagedModelAttributes(String orderBy, Boolean orderAsc, Boolean selectAll, 
            ModelMap model, int pageNo, PagedSearchResult<?> pagedResult,
            HttpServletRequest request, Map<String, Object> searchParams)
    {
        String requestUrl = request.getRequestURL() + "?" + request.getQueryString() + "#results";
        model.addAttribute("searchParams", searchParams);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderAsc", orderAsc);
        model.addAttribute("selectAll", selectAll);
        model.addAttribute("page", pageNo);
        model.addAttribute("results", pagedResult);
        // pagedResult can be null if we're returning errors to the user
        if (pagedResult != null)
        {
            model.addAttribute("firstResultIndex", pagedResult.getFirstResultIndex());
            model.addAttribute("lastPage", pagedResult.isLastPage());
            model.addAttribute("resultSize", pagedResult.getResultSize());
            model.addAttribute("size", pagedResult.size());
        }
        // Set a default value for the JSP to cleanly deal with errors coming back
        else
        {
            model.addAttribute("resultSize", 0);
        }

        model.addAttribute("searchResultsUrl", requestUrl);
    }

    /**
     * Standardises the input field to use false if otherwise not supplied
     * 
     * @param input - The ascending order flag
     * @return false by default
     */
    public static boolean defaultFalse(Boolean input)
    {
        if (input == null)
        {
            return false;
        }
        return input;
    }

    /**
     * Standardises the input field to use true if otherwise not supplied
     * 
     * @param input - The ascending order flag
     * @return true by default
     */
    public static boolean defaultTrue(Boolean input)
    {
        if (input == null)
        {
            return true;
        }
        return input;
    }
    
    /**
     * Returns the input value if not null, otherwise "id"
     * 
     * @param orderBy - Field to order by
     * @return orderBy or default to 'id'
     */
    public static String resolveOrderBy(String orderBy)
    {
        if (orderBy == null)
        {
            return "id";
        }
        return orderBy;
    }

    /**
     * Returns the input value if not null, otherwise 0
     * 
     * @param input - input
     * @return 0 as a default
     */
    public static int defaultZero(Integer input)
    {
        if (input != null)
        {
            return input;
        }
        return 0;
    }

    /**
     * Returns the input value or null for an empty String
     * 
     * @param input - input
     * @return null for an empty String as default
     */
    public static String nullForEmpty(String input)
    {
        String result = input;
        if (input != null && "".equals(input))
        {
            result = null;
        }
        return result;
    }

}
