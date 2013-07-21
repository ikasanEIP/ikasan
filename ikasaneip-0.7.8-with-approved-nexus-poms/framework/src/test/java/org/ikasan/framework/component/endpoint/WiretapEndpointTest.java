/*
 * $Id$
 * $URL$
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
package org.ikasan.framework.component.endpoint;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * This test class supports the <code>WiretapEndpoint</code> class.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEndpointTest {
	/**
	 * Mockery for mocking concrete classes
	 */
	private Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/** service */
	final WiretapService wiretapService = this.mockery
			.mock(WiretapService.class);

	/** mock event */
	final Event event = this.mockery.mock(Event.class);
	/** mock payload */

	/** module name */
	String moduleName = "moduleName";
	/** flow name */
	String flowName = "flowName";
	/** component name */
	String componentName = "componentName";



	/**
	 * Tests that the wiretapService gets called with the Event
	 */
	@Test
	public void testOnEvent() {

		WiretapEndpoint wiretapEndpoint = new WiretapEndpoint(moduleName,
				flowName, componentName, wiretapService);
		wiretapEndpoint.setWiretapEventTimeToLive(1000l);
		mockery.checking(new Expectations() {
			{
				one(wiretapService).tapEvent(event, componentName, moduleName,
						flowName, 1000l);
			}
		});
		wiretapEndpoint.onEvent(event);
		mockery.assertIsSatisfied();

	}

	/**
	 * Test failed constructor based on a 'null' service.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test_constructorFailureNullWiretapDao() {
		// create the class to be tested
		new WiretapEndpoint(moduleName, flowName, componentName, null);

	}



	/**
	 * The suite is this class
	 * 
	 * @return JUnit Test class
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(WiretapEndpointTest.class);
	}
}
