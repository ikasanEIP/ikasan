/*
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
package org.ikasan.framework.event.exclusion.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;


import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author The Ikasan Development Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration
public class HibernateExcludedEventDaoTest {

	private String moduleName = "moduleName";
	
	private String flowName = "flowName";
	
	
	@Autowired
	private HibernateExcludedEventDao excludedEventDao;
	
	@Test
	public void testSaveAndLoad(){
		
		//set up an event to exclude
		Event event = new Event("componentGroupName", null);

		DefaultPayload payload1 = new DefaultPayload(null, Spec.TEXT_CSV.name(), "thisSourceSystem", "payload1Content".getBytes());
		DefaultPayload payload2 = new DefaultPayload(null, Spec.BYTE_ZIP.name(), "thatSourceSystem", "payload2Content".getBytes());
		
		
		List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		event.setPayloads(payloads);
		
		
		ExcludedEvent excludedEvent = new ExcludedEvent(event,moduleName, flowName, new Date());

		
		//save the excluded event
		excludedEventDao.save(excludedEvent);
		
		//check that following save, the various persistence ids have been set
		Assert.assertNotNull("id should not be null on persisted excludedEvent",excludedEvent.getId());
		Assert.assertNotNull("persistenceId should not be null on persisted payload",payload1.getPersistenceId());
		Assert.assertNotNull("persistenceId should not be null on persisted payload",payload2.getPersistenceId());
	
		//now try to reload the ExcludedEvent
		ExcludedEvent reloadedExcludedEvent = excludedEventDao.load(excludedEvent.getId());
		
		//check that the fields of ExcludedEvent were reloaded successfully
		Assert.assertTrue("reloaded moduleName should be same size as original moduleName", excludedEvent.getModuleName().equals(reloadedExcludedEvent.getModuleName()));
		Assert.assertTrue("reloaded flowName should be same size as original flowName", excludedEvent.getFlowName().equals(reloadedExcludedEvent.getFlowName()));
		Assert.assertTrue("reloaded exclusionTime should be same size as original exclusionTime", excludedEvent.getExclusionTime().equals(reloadedExcludedEvent.getExclusionTime()));
		

		Event reloadedEvent = reloadedExcludedEvent.getEvent();
		List<Payload> reloadedPayloads = reloadedEvent.getPayloads();
		
		//check that all the important fields from all the payloads survived
		assertPayloadsEquivalent(payloads, reloadedPayloads);
	}

	/**
	 * @param payloads
	 * @param reloadedPayloads
	 */
	private void assertPayloadsEquivalent(List<Payload> payloads,
			List<Payload> reloadedPayloads) {
		Assert.assertEquals("reloaded Payloads should be same size as original payloads", payloads.size(), reloadedPayloads.size());
		for (int i=0;i<payloads.size();i++){
			assertPayloadEquivalent(payloads.get(i), reloadedPayloads.get(i));
		}
	}

	/**
	 * @param payload
	 * @param reloadedPayload
	 */
	private void assertPayloadEquivalent(Payload payload, Payload reloadedPayload) {
		Assert.assertEquals("reloaded Payload id should be same size as original payload id", payload.getId(), reloadedPayload.getId());
		Assert.assertTrue("reloaded Payload content should be same size as original payload content", Arrays.equals(payload.getContent(), reloadedPayload.getContent()));
		Assert.assertTrue("reloaded Payload spec should be same size as original payload spec", payload.getSpec().equals(reloadedPayload.getSpec()));
		Assert.assertTrue("reloaded Payload srcSystem should be same size as original payload srcSystem", payload.getSrcSystem().equals(reloadedPayload.getSrcSystem()));
	}
	
	@Test
	public void testFindExcludedEvents(){
		//first create some data
		ExcludedEvent excludedEvent1 = new ExcludedEvent(createEvent(),moduleName, flowName, new Date());
		excludedEventDao.save(excludedEvent1);
		
		ExcludedEvent excludedEvent2 = new ExcludedEvent(createEvent(),moduleName, flowName, new Date());
		excludedEventDao.save(excludedEvent2);	
		
		//now see if we can find it
		List<ExcludedEvent> found = excludedEventDao.findExcludedEvents(0, 25);
		
		Assert.assertTrue("result list should contain excludedEvent1",listingContainsEntry(excludedEvent1, found));
		Assert.assertTrue("result list should contain excludedEvent2",listingContainsEntry(excludedEvent2, found));
		
	}

	private boolean listingContainsEntry(ExcludedEvent entry,
			List<ExcludedEvent> found) {
		boolean result = false;
		for (ExcludedEvent excludedEvent : found){
			if (excludedEvent.getId().equals(entry.getId())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	@Test
	public void testGetExcludedEvent(){
		Assert.assertNull("getExcludedEvent with non existant id should return null", excludedEventDao.getExcludedEvent(-1));
	
		//setup an excluded event in the db
		ExcludedEvent excludedEvent = new ExcludedEvent(createEvent(),moduleName,flowName, new Date());
		excludedEventDao.save(excludedEvent);
		
		//try to reload it
		ExcludedEvent reloadedEvent = excludedEventDao.getExcludedEvent(excludedEvent.getId());
		Assert.assertNotNull("getExcludedEvent should return event for valid id", reloadedEvent);
	
	}
	
	public Event createEvent(){
		Event event = new Event("componentGroupName", null);

		DefaultPayload payload1 = new DefaultPayload(null, Spec.TEXT_CSV.name(), "thisSourceSystem", "payload1Content".getBytes());
		DefaultPayload payload2 = new DefaultPayload(null, Spec.BYTE_ZIP.name(), "thatSourceSystem", "payload2Content".getBytes());
		
		
		List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		event.setPayloads(payloads);
		return event;
	
	}
}
