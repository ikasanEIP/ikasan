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
package org.ikasan.framework.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.ui.ModelMap;

/**
 * Common utility functions used by MVC controllers that deal with paged Master/Detail 
 * search functions
 * 
 * 
 * @author The Ikasan Development Team
 *
 */
public class MasterDetailControllerUtil {

	/**
	 * Adds a parameter to the searchParams map, only if it has a value
	 * 
	 * @param searchParams
	 * @param paramName
	 * @param paramValue
	 */
	public static void addParam(Map<String, Object> searchParams, String paramName,
			Object paramValue) {
		if (paramValue != null) {
			searchParams.put(paramName, paramValue);
		}
	}

	/**
	 * Adds all standard control attributes to the model for displaying the search 
	 * results page
	 * 
	 * @param orderBy
	 * @param orderAsc
	 * @param model
	 * @param pageNo
	 * @param pagedResult
	 * @param request
	 * @param searchParams
	 */
	public static void addPagedModelAttributes(String orderBy, Boolean orderAsc,
			ModelMap model, int pageNo,
			PagedSearchResult<?> pagedResult, HttpServletRequest request,
			Map<String, Object> searchParams) {
		String requestUrl = request.getRequestURL()+"?"+request.getQueryString();
		
		model.addAttribute("searchParams", searchParams);

		model.addAttribute("orderBy", orderBy);
		model.addAttribute("orderAsc", orderAsc);
		model.addAttribute("page", pageNo);

		model.addAttribute("results", pagedResult);
		model.addAttribute("firstResultIndex", pagedResult
				.getFirstResultIndex());
		model.addAttribute("lastPage", pagedResult.isLastPage());
		model.addAttribute("resultSize", pagedResult.getResultSize());
		model.addAttribute("size", pagedResult.size());
		model.addAttribute("searchResultsUrl", requestUrl);
	}
	
	/**
	 * Standardises the orderAsc field to use false if otherwise not supplied
	 * 
	 * @param orderAsc
	 * @return
	 */
	public static boolean defaultFalse(Boolean input) {
		if (input==null){
    		return false; //default orderAsc to false if not specified
    	}
		return input;
	}

	/**
	 * Returns the input value if not null, otherwise "id"
	 * 
	 * @param orderBy
	 * @return orderBy or default to ids
	 */
	public static String resolveOrderBy(String orderBy) {
		return orderBy==null?"id":orderBy;
	}

	/**
	 * Returns the input value if not null, otherwise 0
	 * 
	 * @param input
	 * @return
	 */
	public static int defaultZero(Integer input) {
		return input!=null?input:0;
	}

	/**
	 * Returns the input value or null for an empty String
	 * 
	 * @param input
	 * @return
	 */
	public static String nullForEmpty(String input) {
		String result = input;
		if (input!=null && "".equals(input)){
			result = null;
		}
		return result;
	}

}
