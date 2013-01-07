/*
 * $Id
 * $URL
 * 
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
		Date expiry = new Date();
		
		Throwable cause = new NullPointerException();
		Throwable throwable = new Throwable(cause);
		
		Event event = new Event(null, null, "myEvent1",new ArrayList<Payload>());
		
		
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, event, moduleName, flowName, flowElementName, expiry);
	
		Assert.assertEquals("moduleName should be set by constructor",moduleName, errorOccurrence.getModuleName());
		Assert.assertEquals("flowName should be set by constructor",flowName, errorOccurrence.getFlowName());
		Assert.assertEquals("flowElementName should be set by constructor",flowElementName, errorOccurrence.getFlowElementName());	
		Assert.assertEquals("expiry should be set by constructor",expiry, errorOccurrence.getExpiry());	
	
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
		Date expiry = new Date();
		
		Throwable cause = new NullPointerException();
		Throwable throwable = new Throwable(cause);
	
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, moduleName, initiatorName,expiry);
	
		Assert.assertEquals("moduleName should be set by constructor",moduleName, errorOccurrence.getModuleName());
		Assert.assertEquals("initiatorName should be set by constructor",initiatorName, errorOccurrence.getInitiatorName());
		Assert.assertEquals("expiry should be set by constructor",expiry, errorOccurrence.getExpiry());	
	}
	
}
