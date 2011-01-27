/*
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
