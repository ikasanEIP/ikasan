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
