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

package org.ikasan.framework.event.wiretap.model;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>PagedWiretapSearchResult</code> class.
 * 
 * @author Ikasan Development Team
 */
public class PagedWiretapSearchResultTest 
{	
	
	/**
	 * setUp() runs before each test  
	 */
	@Before
	public void setup()
	{
		// nothing		
	}	
	
	/**
	 * tearDown() runs after each test
	 */
    @After
	public void tearDown()
	{
		// nothing				
	}

	/**
	 * Tests a PagedWiretapSearchResult instance
	 * Method mocks some WiretapEventHeader instances and
	 * make sure all the getter methods behave as expected. 
	 */
	@Test	
	public void test_onePagedWiretapSearchResult()
	{	
		// defines number of instances to be added to the list
		final int size = new Integer(3);  
		
		// random values for the resultSize and firsResult
		final int resultSize = 40;
		final int firstResult = 15;
		
		// create a list to be used for WiretapEventHeader instances  
		List<WiretapEventHeader> wiretapEventHeaders = new ArrayList<WiretapEventHeader>();
		
		// mock some WiretapEventHeader instances
		WiretapEventHeader wiretapEventHeader1 = mock(WiretapEventHeader.class);
		WiretapEventHeader wiretapEventHeader2 = mock(WiretapEventHeader.class);
		WiretapEventHeader wiretapEventHeader3 = mock(WiretapEventHeader.class);
		
		// add the header instances to the list
		wiretapEventHeaders.add(wiretapEventHeader1);
		wiretapEventHeaders.add(wiretapEventHeader2);
		wiretapEventHeaders.add(wiretapEventHeader3);								
		
		// create an instance to be tested 
		PagedWiretapSearchResult pagedWiretapSearchResult = new PagedWiretapSearchResult(wiretapEventHeaders, resultSize, firstResult);		
		
		assertNotNull(pagedWiretapSearchResult.getWiretapEventHeaders());		
		assertEquals(pagedWiretapSearchResult.getResultSize(), resultSize);
		assertEquals(pagedWiretapSearchResult.getFirstResult(), firstResult);		
		assertEquals(pagedWiretapSearchResult.getFirstIndex(), firstResult + 1);		 
		assertEquals(pagedWiretapSearchResult.getLastIndex(), firstResult + size);		
	}	
	
	/**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(PagedWiretapSearchResultTest.class);
    }	
}
