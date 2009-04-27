/*
 * $Id: WiretapServiceImplTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/event/wiretap/service/WiretapServiceImplTest.java $
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
package org.ikasan.framework.event.wiretap.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hamcrest.Description;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.dao.WiretapDao;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.module.service.ModuleService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.TypeSafeMatcher;

/**
 * 
 * @author Ikasan Development Team
 *
 */

public class WiretapServiceImplTest {

    private static final long ONE_WEEK_IN_MILLISECONDS = 1000 * 3600 * 24 * 7;

	/**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    ModuleService moduleService = mockery.mock(ModuleService.class);
    
    Event event = mockery.mock(Event.class);
    private Payload payload1 = mockery.mock(Payload.class, "payload1");
    private Payload payload2 = mockery.mock(Payload.class, "payload2");
    String moduleName = "moduleName";
    /** flow name */
    String flowName = "flowName";
    /** component name */
    String componentName = "componentName";
    
	private Date now = new Date();
	Date expiryInOneWeek = new Date(now.getTime()+ONE_WEEK_IN_MILLISECONDS);

    
    WiretapDao wiretapDao = mockery.mock(WiretapDao.class);

    
    
	/**
	 * Tests that the housekeep method simply attempts to delete all expired WiretapEvents
	 */
	@Test
	public void testHousekeep() {
		WiretapServiceImpl wiretapServiceImpl = new WiretapServiceImpl(wiretapDao,moduleService);

        mockery.checking(new Expectations()
        {
            {
            	one(wiretapDao).deleteAllExpired();
            }
        });
		

		wiretapServiceImpl.housekeep();
		mockery.assertIsSatisfied();
	}
	
	@Test (expected= IllegalArgumentException.class)
	public void testFindWiretapEvents_willThrowIllegalArgumentExceptionForNullModuleNames(){
	    WiretapServiceImpl wiretapServiceImpl = new WiretapServiceImpl(wiretapDao,moduleService);
	    wiretapServiceImpl.findWiretapEvents(null, null, null, null, null, null, null, 10, 1);
	}
    
    @Test (expected= IllegalArgumentException.class)
    public void testFindWiretapEvents_willThrowIllegalArgumentExceptionForEmptyModuleNames(){
        WiretapServiceImpl wiretapServiceImpl = new WiretapServiceImpl(wiretapDao,moduleService);
        wiretapServiceImpl.findWiretapEvents(new HashSet<String>(), null, null, null, null, null, null, 10, 1);
    } 
    
    /**
     * Tests the getWiretapEvent - should make calls to the dao for the event, and if not null,
     * call the moduleContainer for security purposes
     */
    @Test
    public void testGetWiretapEvent(){
        WiretapServiceImpl wiretapServiceImpl = new WiretapServiceImpl(wiretapDao,moduleService);
        
        final Long requestedWiretapEventId = 1000l;
        final WiretapEvent wiretapEvent = mockery.mock(WiretapEvent.class);
        final String moduleName = "moduleName";
        
        mockery.checking(new Expectations()
        {
            {
                one(wiretapDao).findById(requestedWiretapEventId);will(returnValue(wiretapEvent));
                one(wiretapEvent).getModuleName();will(returnValue(moduleName));
                one(moduleService).getModule(moduleName);will(returnValue(null));
                //note that we actually dont care here that the module doesnt exist in container
                //if acl security is enabled, then this will blow up before we get the result
            }
        });
        
        wiretapServiceImpl.getWiretapEvent(requestedWiretapEventId);
    }
    
    
	/**
	 * Tests that the save method gets called once with a WiretapEvent appropriate for the event and each payload in the event
	 */
	@Test
	public void testTapEvent() {
		WiretapServiceImpl wiretapServiceImpl = new WiretapServiceImpl(wiretapDao,moduleService);

		setWiretapEventExpectations(new Payload[]{payload1, payload2});
		setWiretapPayloadExpectations(payload1, "payload1", "payload1Content");
		setWiretapPayloadExpectations(payload2, "payload2", "payload2Content");
		setWiretapDaoExpectations(payload1);
		setWiretapDaoExpectations(payload2);
		

		wiretapServiceImpl.tapEvent(event, componentName, moduleName, flowName, ONE_WEEK_IN_MILLISECONDS);
		mockery.assertIsSatisfied();
	}
	

    private void setWiretapEventExpectations(Payload[] payloads)
    {
      
		final List<Payload> payloadList = new ArrayList<Payload>();
		for (Payload payload : payloads){
			payloadList.add(payload);
		}
		
        mockery.checking(new Expectations()
        {
            {
            	allowing(event).getId();will(returnValue("eventId"));
            	one(event).getPayloads();will(returnValue(payloadList));
            }
        });
    }

    private void setWiretapPayloadExpectations(final Payload payload, final String payloadId, final String payloadContent)
    {
        mockery.checking(new Expectations()
        {
            {
            	allowing(payload).getId();will(returnValue(payloadId));
            	one(payload).getContent();will(returnValue(payloadContent.getBytes()));
            }
        });
    }

    private void setWiretapDaoExpectations(final Payload payload)
    {
      
        mockery.checking(new Expectations()
        {
            {
            	one(wiretapDao).save(with(new WiretapEventMatcher(event,componentName, moduleName, flowName, payload, expiryInOneWeek)));
            }
        });
    }




    public class WiretapEventMatcher extends TypeSafeMatcher<WiretapEvent> {
        private String componentName, moduleName, flowName;
        private Event event;
        private Payload payload;
        private Date expiry;

        public WiretapEventMatcher(Event event, String componentName, String moduleName, String flowName, Payload payload, Date expiry) {
            this.componentName = componentName;
            this.moduleName = moduleName;
            this.flowName = flowName;
            this.event = event;
            this.payload = payload;
            this.expiry = expiry;
        }

        @Override
        public boolean matchesSafely(WiretapEvent wiretapEvent) {
        	if (!this.componentName.equals(wiretapEvent.getComponentName())){
        		return false;
        	}
        	if (!this.moduleName.equals(wiretapEvent.getModuleName())){
        		return false;
        	}
        	if (!this.flowName.equals(wiretapEvent.getFlowName())){
        		return false;
        	}
        	if (!this.event.getId().equals(wiretapEvent.getEventId())){
        		return false;
        	}
        	if (!this.payload.getId().equals(wiretapEvent.getPayloadId())){
        		return false;
        	}
//        	if (!this.expiry.equals(wiretapEvent.getExpiry())){
//        		return false;
//        	}
        	
        	
            return true;
        }

		public void describeTo(Description description) {
			 description.appendText("a WiretapEvent where ");
			 description.appendText("componentName is ").appendValue(componentName);
			 description.appendText("moduleName is ").appendValue(moduleName);
			 description.appendText("flowName is ").appendValue(flowName);
			 description.appendText("eventId is ").appendValue(event.getId());
			 description.appendText("payloadId is ").appendValue(payload.getId());
			 description.appendText("expiry is ").appendValue(expiry);
		}
    }

}
