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
package org.ikasan.framework.exception.matching;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class MatchingExceptionHandlerTest {
	
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    final Throwable throwable = mockery.mock(Throwable.class);
    
	final IkasanExceptionAction anIkasanExceptionAction = mockery.mock(IkasanExceptionAction.class, "anIkasanExceptionAction");

	final IkasanExceptionAction anotherIkasanExceptionAction = mockery.mock(IkasanExceptionAction.class, "anotherIkasanExceptionAction");

	final ExceptionGroup anExceptionGroup = mockery.mock(ExceptionGroup.class, "anExceptionGroup");

	final ExceptionGroup anotherExceptionGroup = mockery.mock(ExceptionGroup.class, "anotherExceptionGroup");
	


	
	
	@Test
	public void testHandleThrowable_withUnmatchedThrowable_willReturnRollBackStopAction() {
		MatchingExceptionHandler matchingExceptionHandler = new MatchingExceptionHandler(null);
		assertEquals("handling an unmatched exception should result in a RollbackStop Action", IkasanExceptionActionType.ROLLBACK_STOP, matchingExceptionHandler.handleThrowable(null, new RuntimeException()).getType());
	}
	
	
	@Test
	public void testHandleThrowable_withModuleMatchedThrowable_willReturnMappedAction() {

		List<ExceptionGroup> moduleExceptionGroup = new ArrayList<ExceptionGroup>();
		moduleExceptionGroup.add(anExceptionGroup);
		
		MatchingExceptionHandler matchingExceptionHandler = new MatchingExceptionHandler(moduleExceptionGroup);
    	mockery.checking(new Expectations()
        {
            {
            	one(anExceptionGroup).includes(throwable);will(returnValue(true));
            	one(anExceptionGroup).getAction();will(returnValue(anIkasanExceptionAction));
            }
        });
		assertEquals("handling an exception that matches at a module level should return the mapped action", anIkasanExceptionAction, matchingExceptionHandler.handleThrowable(null, throwable));
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testHandleThrowable_withComponentMatchedThrowable_willReturnMappedAction() {
		final String componentName = "componentName";
		List<ExceptionGroup> moduleExceptionGroupings = new ArrayList<ExceptionGroup>();
		moduleExceptionGroupings.add(anExceptionGroup);
		
		List<ExceptionGroup> thisComponentsExceptionGroupings = new ArrayList<ExceptionGroup>();
		thisComponentsExceptionGroupings.add(anotherExceptionGroup);
		
		Map<String, List<ExceptionGroup>> componentExceptionGroups = new HashMap<String, List<ExceptionGroup>>();
		componentExceptionGroups.put(componentName, thisComponentsExceptionGroupings);
		
		MatchingExceptionHandler matchingExceptionHandler = new MatchingExceptionHandler(moduleExceptionGroupings, componentExceptionGroups);
    	mockery.checking(new Expectations()
        {
            {
            	one(anotherExceptionGroup).includes(throwable);will(returnValue(true));
            	one(anotherExceptionGroup).getAction();will(returnValue(anotherIkasanExceptionAction));
            }
        });
		assertEquals("handling an exception that matches at a component level should return the mapped action", anotherIkasanExceptionAction, matchingExceptionHandler.handleThrowable(componentName, throwable));
		mockery.assertIsSatisfied();
	}

}
