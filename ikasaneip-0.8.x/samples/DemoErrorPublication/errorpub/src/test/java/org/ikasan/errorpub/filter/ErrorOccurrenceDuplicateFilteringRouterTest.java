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
package org.ikasan.errorpub.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.ikasan.attributes.AttributeResolver;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceDuplicateFilteringRouterTest {

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
    
    private ErrorOccurrenceDuplicateFilteringRouter router;
    
    private NotificationCache notificationCache = mockery.mock(NotificationCache.class);
    
    private String duplicateGroupingName = "duplicateGroupingName";
    
    private Long duplicatePeriod = new Long(1000);
    
    private DocumentBuilderFactory documentBuilderFactory = mockery.mock(DocumentBuilderFactory.class);
    
    private DocumentBuilder documentBuilder = mockery.mock(DocumentBuilder.class);
    
    private Document document = mockery.mock(Document.class);
    
    private AttributeResolver attributeResolver = mockery.mock(AttributeResolver.class);
    
    public ErrorOccurrenceDuplicateFilteringRouterTest() throws ParserConfigurationException{
    	
    	mockery.checking(new Expectations()
        {
            {
            	one(documentBuilderFactory).newDocumentBuilder();will(returnValue(documentBuilder));

            }
        });	
    	
    	
    	router = new ErrorOccurrenceDuplicateFilteringRouter(attributeResolver, notificationCache, documentBuilderFactory);
    }
	@Test
	public void testFilter_withErrorOccurrenceUnmatched_willReturnFalse() throws SAXException, IOException {
		final Map<String, Object> resolvedAttributes = new HashMap<String, Object>();

		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));

                one(attributeResolver).resolveAttributes(document);
                will(returnValue(resolvedAttributes));
            }
        });
		
		
		Assert.assertFalse("Event containing ErrorOccurrence that does not match any configured rules, should result in the inclusion transition", router.filter(event));
	
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testFilter_withDuplicateAttributes_andKnownToDuplicateCache_willReturnTrue() throws SAXException, IOException {

		final Map<String, Object> resolvedAttributes = new HashMap<String, Object>();
		resolvedAttributes.put(ErrorOccurrenceDuplicateFilteringRouter.DUPLICATE_GROUPING_ID_ATTRIBUTE_NAME, duplicateGroupingName);
		resolvedAttributes.put(ErrorOccurrenceDuplicateFilteringRouter.DUPLICATE_PERIOD_ATTRIBUTE_NAME, duplicatePeriod);
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));
                
                one(attributeResolver).resolveAttributes(document);
                will(returnValue(resolvedAttributes));
                
                one(notificationCache).notifiedSince(duplicateGroupingName, duplicatePeriod);
                will(returnValue(true));
                
            }
        });	
		Assert.assertTrue("Event containing ErrorOccurrence that is a member of a duplicate group that is known to the cache within the duplicate period, should result in the exclusion transition", router.filter(event));
	
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testFilter_withErrorOccurrenceInDuplicateGroup_butUnknownToDuplicateCache_willReturnFalse() throws SAXException, IOException {
		final Map<String, Object> resolvedAttributes = new HashMap<String, Object>();
		resolvedAttributes.put(ErrorOccurrenceDuplicateFilteringRouter.DUPLICATE_GROUPING_ID_ATTRIBUTE_NAME, duplicateGroupingName);
		resolvedAttributes.put(ErrorOccurrenceDuplicateFilteringRouter.DUPLICATE_PERIOD_ATTRIBUTE_NAME, duplicatePeriod);
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(documentBuilder).parse((InputSource) with(an(InputSource.class)));will(returnValue(document));
                
                one(attributeResolver).resolveAttributes(document);
                will(returnValue(resolvedAttributes));
                
                one(notificationCache).notifiedSince(duplicateGroupingName, duplicatePeriod);
                will(returnValue(false));
                
                //because it wasnt know to the cache beforehand, the cache gets notified now
                one(notificationCache).notify(duplicateGroupingName);
                
            }
        });	
		Assert.assertFalse("Event containing ErrorOccurrence that is a member of a duplicate group that is not known to the cache within the duplicate period, should not result in the exclusion transition", router.filter(event));
	
		mockery.assertIsSatisfied();
	}

}
