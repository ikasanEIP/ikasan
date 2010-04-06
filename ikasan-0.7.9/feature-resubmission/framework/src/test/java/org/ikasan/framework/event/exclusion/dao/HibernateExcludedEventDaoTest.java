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
package org.ikasan.framework.event.exclusion.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
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
		

		DefaultPayload payload1 = new DefaultPayload("id1","payload1Content".getBytes());
		DefaultPayload payload2 = new DefaultPayload("id2","payload2Content".getBytes());
		
		
		List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		//add some custom attributes to each of the payloads
		String attribute1Name = "attribute1Name";
		String attribute1Value = "attribute1Value";
		String attribute2Name = "attribute2Name";
		String attribute2Value = "attribute2Value";
		
		payload1.setAttribute(attribute1Name, attribute1Value);
		payload2.setAttribute(attribute2Name, attribute2Value);
		
		Event event = new Event(null, null, "myEvent1",payloads);
		
		ExcludedEvent excludedEvent = new ExcludedEvent(event,moduleName, flowName, new Date());

		//set some resubmission details on the ExcludedEvent
		Date resubmissionTime = new Date(1000l);
		String resubmitter = "resubmitter";
		excludedEvent.setLastUpdatedTime(resubmissionTime);
		excludedEvent.setLastUpdatedBy(resubmitter);
		
		
		
		//save the excluded event
		excludedEventDao.save(excludedEvent);
		
		//check that following save, the various persistence ids have been set
		Assert.assertNotNull("id should not be null on persisted excludedEvent",excludedEvent.getId());
	
		//now try to reload the ExcludedEvent
		ExcludedEvent reloadedExcludedEvent = excludedEventDao.load(excludedEvent.getId());
		
		//check that the fields of ExcludedEvent were reloaded successfully
		Assert.assertTrue("reloaded moduleName should be same size as original moduleName", excludedEvent.getModuleName().equals(reloadedExcludedEvent.getModuleName()));
		Assert.assertTrue("reloaded flowName should be same size as original flowName", excludedEvent.getFlowName().equals(reloadedExcludedEvent.getFlowName()));
		Assert.assertTrue("reloaded exclusionTime should be same size as original exclusionTime", excludedEvent.getExclusionTime().equals(reloadedExcludedEvent.getExclusionTime()));
		
		Assert.assertTrue("resubmissionTime should be the same as that set on the original", resubmissionTime.equals(reloadedExcludedEvent.getLastUpdatedTime()));
		Assert.assertTrue("resubmitter should be the same as that set on the original", resubmitter.equals(reloadedExcludedEvent.getLastUpdatedBy()));
		
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
		
		//check that the custom attributes survived the trip
		List<String> attributeNames = payload.getAttributeNames();
		Assert.assertEquals("reloaded payload should have the same attribute names", attributeNames, reloadedPayload.getAttributeNames());
		
		for (String payloadAttribute : attributeNames){
			Assert.assertEquals("payload attruibute values should survive persistence", payload.getAttribute(payloadAttribute), reloadedPayload.getAttribute(payloadAttribute));
		}
	}
	
	@Test
	public void testFindExcludedEvents(){
		//first create some data
		ExcludedEvent excludedEvent1 = new ExcludedEvent(createEvent(),moduleName, flowName, new Date());
		excludedEventDao.save(excludedEvent1);
		
		ExcludedEvent excludedEvent2 = new ExcludedEvent(createEvent(),moduleName, flowName, new Date());
		excludedEventDao.save(excludedEvent2);	
		
		//now see if we can find it
		List<ExcludedEvent> found = excludedEventDao.findExcludedEvents(0, 25, null,true, moduleName, flowName);
		
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


		DefaultPayload payload1 = new DefaultPayload("id1", "payload1Content".getBytes());
		DefaultPayload payload2 = new DefaultPayload("id2", "payload2Content".getBytes());
		
		
		List<Payload> payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
		Event event = new Event(null, null, "myEvent1",payloads);
		return event;
	
	}
}
