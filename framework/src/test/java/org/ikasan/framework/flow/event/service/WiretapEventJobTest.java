/*
 * $Id: WiretapEventJobTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/flow/event/service/WiretapEventJobTest.java $
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
package org.ikasan.framework.flow.event.service;

import java.util.HashMap;

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.service.WiretapService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapEventJobTest {
	
	/**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mocked WiretapService
     */
    WiretapService wiretapService = mockery.mock(WiretapService.class);
    
    /**
     * System under test
     */
    private WiretapEventJob wiretapEventJob = new WiretapEventJob(wiretapService);

	@Test
	public void testExecute() {
		final String location = "location";
		final String moduleName = "moduleName";
		final String flowName = "flowName";
		final Event event = mockery.mock(Event.class); 
		final HashMap<String, String> params = new HashMap<String, String>();
		String timeToLiveString = "30000";
		final long timeToLive = Long.parseLong(timeToLiveString);
		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, timeToLiveString);
        mockery.checking(new Expectations()
        {
            {
            	one(wiretapService).tapEvent(event, location, moduleName, flowName, timeToLive);
            }
        });
		
		wiretapEventJob.execute(location, moduleName, flowName, event, params);
		mockery.assertIsSatisfied();
	}

	@Test
	public void testGetParameters() {
		Assert.assertTrue("getParameters should return the timeToLive param", wiretapEventJob.getParameters().contains(WiretapEventJob.TIME_TO_LIVE_PARAM) );
	}

	@Test
	public void testValidateParameters_withValidTimeToLivePassesValidation() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, "30000");
		Assert.assertTrue("should be no validation errors when valid timeToLive exists", wiretapEventJob.validateParameters(params).isEmpty() );
	}

	@Test
	public void testValidateParameters_withNoTimeToLiveFailsValidation() {
		HashMap<String, String> params = new HashMap<String, String>();
		Assert.assertFalse("validation should fail if there is no timeToLive", wiretapEventJob.validateParameters(params).isEmpty() );
	}
	
	@Test
	public void testValidateParameters_withEmptyTimeToLiveFailsValidation() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, null);
		Assert.assertFalse("validation should fail if there is no timeToLive", wiretapEventJob.validateParameters(params).isEmpty() );

		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, "");
		Assert.assertFalse("validation should fail if there is no timeToLive", wiretapEventJob.validateParameters(params).isEmpty() );
	}
	
	@Test
	public void testValidateParameters_withNonNumericTimeToLiveFailsValidation() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, "abc");
		Assert.assertFalse("validation should fail if timeToLive is non numeric", wiretapEventJob.validateParameters(params).isEmpty() );
	}
	
	@Test
	public void testValidateParameters_withNegativeTimeToLiveFailsValidation() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(WiretapEventJob.TIME_TO_LIVE_PARAM, "-1000");
		Assert.assertFalse("validation should fail if timeToLive is negative", wiretapEventJob.validateParameters(params).isEmpty() );
	}
}
