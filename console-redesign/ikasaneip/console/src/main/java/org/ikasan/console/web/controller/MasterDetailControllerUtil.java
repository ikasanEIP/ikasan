/*
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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
	
	private static Logger logger = Logger.getLogger(MasterDetailControllerUtil.class);
    /**
     * Adds a parameter to the searchParams map, only if it has a value
     * 
     * @param searchParams - The search parameters map to add to
     * @param paramName - The name of the parameter to add
     * @param paramValue - The value of the parameter to add
     */
    public static void addParam(Map<String, Object> searchParams, String paramName, Object paramValue)
    {
        if (searchParams != null && paramName != null && paramValue != null)
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
     * @param pointToPointFlowProfileSearch - Flag to indicate what type of search            
     * @param pointToPointFlowProfileSelectAll - Select all boolean for
     *            pointToPointFlowProfile based search
     * @param moduleSelectAll - Select all boolean for pointToPointFlowProfile
     *            based search
     * @param model - The model we are displaying (the data)
     * @param pageNo - The page number we are on
     * @param pageSize - The page Size (how many results to display per page)
     * @param pagedResult - The paged result page
     * @param request - standard HttpRequest
     * @param searchParams - The search parameters we're using
     */
    public static void addPagedModelAttributes(String orderBy, Boolean orderAsc, String pointToPointFlowProfileSearch, Boolean pointToPointFlowProfileSelectAll, Boolean moduleSelectAll,
            ModelMap model, int pageNo, int pageSize, PagedSearchResult<?> pagedResult, HttpServletRequest request, Map<String, Object> searchParams)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model should not be NULL at this point.");
        }
        String requestUrl = "";
        if (request != null)
        {
            // TODO We are not checking that the getRequestURL and getQuery String are not null
            requestUrl = request.getRequestURL() + "?" + request.getQueryString() + "#results";
        }
        model.addAttribute("searchParams", searchParams);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderAsc", orderAsc);
        model.addAttribute("pointToPointFlowProfileSearch", pointToPointFlowProfileSearch);
        model.addAttribute("pointToPointFlowProfileSelectAll", pointToPointFlowProfileSelectAll);
        model.addAttribute("moduleSelectAll", moduleSelectAll);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("page", pageNo);
        model.addAttribute("results", pagedResult);
        // pagedResult can be null if we're returning errors to the user
        if (pagedResult != null)
        {
        	logger.info("resultSize = [" + pagedResult.getResultSize() + "]");
            model.addAttribute("firstResultIndex", pagedResult.getFirstResultIndex());
            // Calculate the last page (divide total results by page size, round
            // up but take off 1 as indexes are 0 based)
            int lastPage = (int) Math.ceil(((double) pagedResult.getResultSize() / (double) pageSize)) - 1;
            model.addAttribute("isLastPage", pagedResult.isLastPage());
            model.addAttribute("lastPage", lastPage);
            model.addAttribute("resultSize", pagedResult.getResultSize());
            model.addAttribute("size", pagedResult.size());
        }
        // Set a default value for the JSP to cleanly deal with errors coming
        // back
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
