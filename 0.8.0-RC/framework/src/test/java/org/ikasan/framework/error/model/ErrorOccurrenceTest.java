/*
 * $Id$
 * $URL$
 * 
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
package org.ikasan.framework.error.model;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceTest {

	/**
	 * Test method for {@link org.ikasan.framework.error.model.ErrorOccurrence#ErrorOccurrence(java.lang.Throwable, org.ikasan.framework.component.Event, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testErrorOccurrence_constructorWithEvent() {
		String moduleName = "moduleName";
		String flowName = "flowName";
		String flowElementName = "flowElementName";
		String actionTaken = "actionTaken";
		Date expiry = new Date();
		
		Throwable cause = new NullPointerException();
		Throwable throwable = new Throwable(cause);
		
		Event event = new Event(null, null, "myEvent1",new ArrayList<Payload>());
		
		
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, event, moduleName, flowName, flowElementName, expiry, actionTaken);
	
		Assert.assertEquals("moduleName should be set by constructor",moduleName, errorOccurrence.getModuleName());
		Assert.assertEquals("flowName should be set by constructor",flowName, errorOccurrence.getFlowName());
		Assert.assertEquals("flowElementName should be set by constructor",flowElementName, errorOccurrence.getFlowElementName());	
		Assert.assertEquals("expiry should be set by constructor",expiry, errorOccurrence.getExpiry());	
		Assert.assertEquals("actionTaken should be set by constructor",actionTaken, errorOccurrence.getActionTaken());	
	
		Assert.assertEquals("eventId should be set by constructor",event.getId(), errorOccurrence.getEventId());	
		Assert.assertNull("initiatorName should be null when flowName/event are used",errorOccurrence.getInitiatorName());	
	}

	
	/**
	 * Test method for {@link org.ikasan.framework.error.model.ErrorOccurrence#ErrorOccurrence(java.lang.Throwable, org.ikasan.framework.component.Event, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testErrorOccurrence_constructorWithoutEvent() {
		String moduleName = "moduleName";
		String initiatorName = "initiatorName";
		String actionTaken = "actionTaken";
		Date expiry = new Date();
		
		Throwable cause = new NullPointerException();
		Throwable throwable = new Throwable(cause);
	
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, moduleName, initiatorName,expiry, actionTaken);
	
		Assert.assertEquals("moduleName should be set by constructor",moduleName, errorOccurrence.getModuleName());
		Assert.assertEquals("initiatorName should be set by constructor",initiatorName, errorOccurrence.getInitiatorName());
		Assert.assertEquals("expiry should be set by constructor",expiry, errorOccurrence.getExpiry());	
		Assert.assertEquals("actionTaken should be set by constructor",actionTaken, errorOccurrence.getActionTaken());	

	}
	
}
