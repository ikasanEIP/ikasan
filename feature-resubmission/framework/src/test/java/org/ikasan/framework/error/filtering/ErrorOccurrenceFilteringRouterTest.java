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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.hamcrest.TypeSafeMatcher;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.grouping.ErrorOccurrenceGroup;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.serialisation.ErrorOccurrenceXmlSerialiser;
import org.ikasan.framework.exception.user.ExceptionCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

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
	
    private ErrorOccurrenceXmlSerialiser errorOccurrenceXmlSerialiser = mockery.mock(ErrorOccurrenceXmlSerialiser.class);
    
    private Event event = mockery.mock(Event.class);
    
    private Payload payload = mockery.mock(Payload.class);
    
    private List<Payload> payloads = new ArrayList<Payload>();
    {
    	payloads.add(payload);
    }
    
    private String payloadContent = "payloadContent";
    
    private ErrorOccurrence errorOccurrence = mockery.mock(ErrorOccurrence.class);
    
    private TypeSafeMatcher<ErrorOccurrence> errorOccurrenceMatcher = mockery.mock(TypeSafeMatcher.class);
    
    private ExceptionCache exceptionCache = mockery.mock(ExceptionCache.class);
    
    private ErrorOccurrenceGroup duplicateGrouping = mockery.mock(ErrorOccurrenceGroup.class);
    
    private String duplicateGroupingName = "duplicateGroupingName";
    
    private Long duplicatePeriod = new Long(1000);
    
    private ErrorOccurrenceFilteringRouter router = new ErrorOccurrenceFilteringRouter(errorOccurrenceXmlSerialiser, exceptionCache);
    
	@Test
	public void testEvaluate_withErrorOccurrenceMatchedBySuppressionMatcher_willReturnExclusionTransition() {
		List<TypeSafeMatcher<ErrorOccurrence>> suppressableMatchers = new ArrayList<TypeSafeMatcher<ErrorOccurrence>>();
		
		suppressableMatchers.add(errorOccurrenceMatcher);
		router.setSupressableMatchers(suppressableMatchers);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(errorOccurrenceXmlSerialiser).toObject(payloadContent);
                will(returnValue(errorOccurrence));
                
                one(errorOccurrenceMatcher).matchesSafely(errorOccurrence);
                will(returnValue(true));
                
            }
        });	
		Assert.assertTrue("Event containing ErrorOccurrence that matches one of the suppress matchers, should result in the exclusion transition", ErrorOccurrenceFilteringRouter.EXCLUSION_TRANSITION.equals(router.evaluate(event)));
	}
	
	@Test
	public void testEvaluate_withErrorOccurrenceUnmatched_willReturnInclusionTransition() {

		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(errorOccurrenceXmlSerialiser).toObject(payloadContent);
                will(returnValue(errorOccurrence));

            }
        });
		
		
		Assert.assertTrue("Event containing ErrorOccurrence that does not match any configured rules, should result in the inclusion transition", ErrorOccurrenceFilteringRouter.INCLUSION_TRANSITION.equals(router.evaluate(event)));
	}
	
	@Test
	public void testEvaluate_withErrorOccurrenceInDuplicateGroup_andKnownToDuplicateCache_willReturnExclusionTransition() {
		List<ErrorOccurrenceGroup> duplicteGroupings = new ArrayList<ErrorOccurrenceGroup>();
		
		duplicteGroupings.add(duplicateGrouping);
		router.setDuplicateGroupings(duplicteGroupings);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(errorOccurrenceXmlSerialiser).toObject(payloadContent);
                will(returnValue(errorOccurrence));
                
                one(duplicateGrouping).hasAsMember(errorOccurrence);
                will(returnValue(true));
                
                one(duplicateGrouping).getGroupName();
                will(returnValue(duplicateGroupingName));
                
                one(duplicateGrouping).getAttribute(ErrorOccurrenceFilteringRouter.DUPLICATE_PERIOD_ATTRIBUTE_NAME);
                will(returnValue(duplicatePeriod));
                
                one(exceptionCache).notifiedSince(duplicateGroupingName, duplicatePeriod);
                will(returnValue(true));
                
            }
        });	
		Assert.assertTrue("Event containing ErrorOccurrence that is a member of a duplicate group that is known to the cache within the duplicate period, should result in the exclusion transition", ErrorOccurrenceFilteringRouter.EXCLUSION_TRANSITION.equals(router.evaluate(event)));
	}
	
	@Test
	public void testEvaluate_withErrorOccurrenceInDuplicateGroup_butUnknownToDuplicateCache_willReturnInclusionTransition() {
		List<ErrorOccurrenceGroup> duplicteGroupings = new ArrayList<ErrorOccurrenceGroup>();
		
		duplicteGroupings.add(duplicateGrouping);
		router.setDuplicateGroupings(duplicteGroupings);
		
		
		mockery.checking(new Expectations()
        {
            {
                one(event).getPayloads();will(returnValue(payloads));
                one(payload).getContent();
                will(returnValue(payloadContent.getBytes()));
                
                one(errorOccurrenceXmlSerialiser).toObject(payloadContent);
                will(returnValue(errorOccurrence));
                
                one(duplicateGrouping).hasAsMember(errorOccurrence);
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
		Assert.assertTrue("Event containing ErrorOccurrence that is a member of a duplicate group that is not known to the cache within the duplicate period, should not result in the exclusion transition", ErrorOccurrenceFilteringRouter.INCLUSION_TRANSITION.equals(router.evaluate(event)));
	}
	

}
