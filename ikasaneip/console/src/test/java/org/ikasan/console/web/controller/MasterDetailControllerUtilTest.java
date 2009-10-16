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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ikasan.framework.management.search.PagedSearchResult;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.ui.ModelMap;

import junit.framework.TestCase;

/**
 * Test class for the MasterDetailControllerUtil class
 * 
 * @author Ikasan Development Team
 */
public class MasterDetailControllerUtilTest extends TestCase
{
    /**
     * Test the default constructor
     */
    public void testConstructor()
    {
        new MasterDetailControllerUtil();
    }

    /**
     * Test the defaultFalse method
     */
    public void testDefaultFalse()
    {
        boolean result = MasterDetailControllerUtil.defaultFalse(true);
        assertTrue(result);
        result = MasterDetailControllerUtil.defaultFalse(false);
        assertFalse(result);
        result = MasterDetailControllerUtil.defaultFalse(null);
        assertFalse(result);
    }

    /**
     * Test the defaultTrue method
     */
    public void testDefaultTrue()
    {
        boolean result = MasterDetailControllerUtil.defaultTrue(false);
        assertFalse(result);
        result = MasterDetailControllerUtil.defaultTrue(true);
        assertTrue(result);
        result = MasterDetailControllerUtil.defaultTrue(null);
        assertTrue(result);
    }

    /**
     * Test the resolveOrderBy method
     */
    public void testResolveOrderBy()
    {
        String result = MasterDetailControllerUtil.resolveOrderBy("foobar");
        assertEquals("foobar", result);
        result = MasterDetailControllerUtil.resolveOrderBy(null);
        assertEquals("id", result);
    }

    /**
     * Test the defaultZero method
     */
    public void testDefaultZero()
    {
        int result = MasterDetailControllerUtil.defaultZero(1);
        assertEquals(1, result);
        result = MasterDetailControllerUtil.defaultZero(null);
        assertEquals(0, result);
    }

    /**
     * Test the nullForEmpty method
     */
    public void testNullForEmpty()
    {
        String result = MasterDetailControllerUtil.nullForEmpty("");
        assertNull(result);
        result = MasterDetailControllerUtil.nullForEmpty(null);
        assertNull(result);
        result = MasterDetailControllerUtil.nullForEmpty("foobar");
        assertEquals("foobar", result);
    }

    /**
     * Test the addParam method
     */
    public void testAddParam()
    {
        Map<String, Object> searchParams = null;
        String paramName = "parameterName";
        Object paramValue = "parameterValue";
        MasterDetailControllerUtil.addParam(searchParams, paramName, paramValue);
        searchParams = new HashMap<String, Object>();
        paramName = null;
        paramValue = "parameterValue";
        MasterDetailControllerUtil.addParam(searchParams, paramName, paramValue);
        assertTrue(searchParams.isEmpty());
        searchParams = new HashMap<String, Object>();
        paramName = "parameterName";
        paramValue = null;
        MasterDetailControllerUtil.addParam(searchParams, paramName, paramValue);
        assertTrue(searchParams.isEmpty());
        searchParams = new HashMap<String, Object>();
        paramName = "parameterName";
        paramValue = "parameterValue";
        MasterDetailControllerUtil.addParam(searchParams, paramName, paramValue);
        String result = (String) searchParams.get("parameterName");
        assertEquals("parameterValue", result);
        assertEquals(1, searchParams.size());
    }

    /**
     * Test the addPagedModelAttributes method
     * 
     * TODO This unit test could cover the index page algorithm a little more thoroughly
     */
    public void testAddPagedModelAttributes()
    {
        // The context that the tests run in, allows for mocking actual concrete classes
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        
        ModelMap model = null;
        String orderBy = "id";
        Boolean orderAsc = true;
        String pointToPointFlowProfileSearch = "1";
        Boolean pointToPointFlowProfileSelectAll = false;
        Boolean moduleSelectAll = false;
        int pageNo = 1;
        int pageSize = 10;
        PagedSearchResult<?> pagedResult = null;
        HttpServletRequest request = null;
        Map<String, Object> searchParams = new HashMap<String, Object>();
        try 
        {
            MasterDetailControllerUtil.addPagedModelAttributes(orderBy, orderAsc, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll, moduleSelectAll, model, pageNo, pageSize, pagedResult, request, searchParams);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // Do Nothing, this is expected
        }

        // Test the cases where request is null and the pagedResults is null
        model = new ModelMap();
        MasterDetailControllerUtil.addPagedModelAttributes(orderBy, orderAsc, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll, moduleSelectAll, model, pageNo, pageSize, pagedResult, request, searchParams);
        assertNull(model.get("firstResultIndex"));
        assertNull(model.get("isLastPage"));
        assertNull(model.get("lastPage"));
        assertNull(model.get("size"));
        assertEquals(0, model.get("resultSize"));
        assertEquals("", model.get("searchResultsUrl"));

        // Test the case where the request and pagedResult aren't null
        final HttpServletRequest request2 = context.mock(HttpServletRequest.class);
        final PagedSearchResult<?> pagedResult2 = context.mock(PagedSearchResult.class);

        // Expectations
        context.checking(new Expectations()
        {
            {
                one(request2).getRequestURL();
                will(returnValue(new StringBuffer("http://www.ikasan.org")));
                one(request2).getQueryString();
                will(returnValue("?key=value"));
                one(pagedResult2).getFirstResultIndex();
                will(returnValue(2));
                one(pagedResult2).getResultSize();
                will(returnValue(1));
                one(pagedResult2).isLastPage();
                will(returnValue(true));
                one(pagedResult2).getResultSize();
                will(returnValue(1));
                one(pagedResult2).size();
                will(returnValue(1));
            }
        });

        // Test
        MasterDetailControllerUtil.addPagedModelAttributes(orderBy, orderAsc, pointToPointFlowProfileSearch, pointToPointFlowProfileSelectAll, moduleSelectAll, model, pageNo, pageSize, pagedResult2, request2, searchParams);
        assertEquals(2, model.get("firstResultIndex"));
        assertTrue((Boolean)model.get("isLastPage"));
        assertEquals(0, model.get("lastPage"));
        assertEquals(1, model.get("resultSize"));
        assertEquals(1, model.get("size"));
        assertEquals("http://www.ikasan.org??key=value#results", model.get("searchResultsUrl"));
        
        // The rest
        assertEquals("id", model.get("orderBy"));
        assertTrue((Boolean)model.get("orderAsc"));
        assertEquals("1", model.get("pointToPointFlowProfileSearch"));
        assertFalse((Boolean)model.get("pointToPointFlowProfileSelectAll"));
        assertFalse((Boolean)model.get("moduleSelectAll"));
        assertEquals(1, model.get("page"));
        assertEquals(10, model.get("pageSize"));
    }
}