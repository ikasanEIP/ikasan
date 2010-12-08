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
