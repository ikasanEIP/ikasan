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
package org.ikasan.framework.error.filtering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.user.ExceptionCache;
import org.ikasan.framework.util.grouping.AttributedGroup;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ErrorOccurrenceFilteringRouterTest {

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
    
    private Event event = mockery.mock(Event.class);
    
    private Payload payload = mockery.mock(Payload.class);
    
    private List<Payload> payloads = new ArrayList<Payload>();
    {
    	payloads.add(payload);
    }
    
    private String payloadContent = "payloadContent";
    
    
    private Matcher<Document> errorOccurrenceMatcher = mockery.mock(Matcher.class);
    
    private ExceptionCache exceptionCache = mockery.mock(ExceptionCache.class);
    
    private AttributedGroup duplicateGrouping = mockery.mock(AttributedGroup.class);
    
    private String duplicateGroupingName = "duplicateGroupingName";
    
    private Long duplicatePeriod = new Long(1000);
    
    private ErrorOccurrenceFilteringRouter router;
    
    private DocumentBuilderFactory documentBuilderFactory = mockery.mock(DocumentBuilderFactory.class);
    
    private DocumentBuilder documentBuilder = mockery.mock(DocumentBuilder.class);
    
    private Document document = mockery.mock(Document.class);
    
    public ErrorOccurrenceFilteringRouterTest() throws ParserConfigurationException{
    	
    	mockery.checking(new Expectations()
        {
            {
            	one(documentBuilderFactory).newDocumentBuilder();will(returnValue(documentBuilder));

            }
        });	
    	
    	
    	router = new ErrorOccurrenceFilteringRouter(exceptionCache, documentBuilderFactory);
    }
	@Test
	public void testFilter_withErrorOccurrenceMatchedBySuppressionMatcher_willReturnTrue() throws ParserConfigurationException, SAXException, IOException {
		List<Matcher<Document>> suppressableMatchers = new ArrayList<Matcher<Document>>();
		
		suppressableMatchers.add(errorOccurrenceMatcher);
		router.setSupressableMatchers(suppressableMatchers);
		
		
		mockery.checking(new Expectations()
        {
            {
            	
            	
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));
                
                one(errorOccurrenceMatcher).matches(with(a(Document.class)));
                will(returnValue(true));
                
            }
        });	
		Assert.assertTrue("Event containing ErrorOccurrence that matches one of the suppress matchers, should result in the exclusion transition", router.filter(event));
	}
	
	@Test
	public void testFilter_withErrorOccurrenceUnmatched_willReturnFalse() throws SAXException, IOException {

		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));

            }
        });
		
		
		Assert.assertFalse("Event containing ErrorOccurrence that does not match any configured rules, should result in the inclusion transition", router.filter(event));
	}
	
	@Test
	public void testFilter_withErrorOccurrenceInDuplicateGroup_andKnownToDuplicateCache_willReturnTrue() throws SAXException, IOException {
		List<AttributedGroup> duplicteGroupings = new ArrayList<AttributedGroup>();
		
		duplicteGroupings.add(duplicateGrouping);
		router.setDuplicateGroupings(duplicteGroupings);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));
                
                one(duplicateGrouping).hasAsMember(document);
                will(returnValue(true));
                
                one(duplicateGrouping).getGroupName();
                will(returnValue(duplicateGroupingName));
                
                one(duplicateGrouping).getAttribute(ErrorOccurrenceFilteringRouter.DUPLICATE_PERIOD_ATTRIBUTE_NAME);
                will(returnValue(duplicatePeriod));
                
                one(exceptionCache).notifiedSince(duplicateGroupingName, duplicatePeriod);
                will(returnValue(true));
                
            }
        });	
		Assert.assertTrue("Event containing ErrorOccurrence that is a member of a duplicate group that is known to the cache within the duplicate period, should result in the exclusion transition", router.filter(event));
	}
	
	@Test
	public void testFilter_withErrorOccurrenceInDuplicateGroup_butUnknownToDuplicateCache_willReturnFalse() throws SAXException, IOException {
		List<AttributedGroup> duplicteGroupings = new ArrayList<AttributedGroup>();
		
		duplicteGroupings.add(duplicateGrouping);
		router.setDuplicateGroupings(duplicteGroupings);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));
                
                one(duplicateGrouping).hasAsMember(document);
                will(returnValue(true));
                
                one(duplicateGrouping).getGroupName();
                will(returnValue(duplicateGroupingName));
                
                one(duplicateGrouping).getAttribute(ErrorOccurrenceFilteringRouter.DUPLICATE_PERIOD_ATTRIBUTE_NAME);
                will(returnValue(duplicatePeriod));
                
                one(exceptionCache).notifiedSince(duplicateGroupingName, duplicatePeriod);
                will(returnValue(false));
                
                //because it wasnt know to the cache beforehand, the cache gets notified now
                one(exceptionCache).notify(duplicateGroupingName);
                
            }
        });	
		Assert.assertFalse("Event containing ErrorOccurrence that is a member of a duplicate group that is not known to the cache within the duplicate period, should not result in the exclusion transition", router.filter(event));
	}
	

}
