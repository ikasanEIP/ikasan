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
package org.ikasan.replay.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.replay.model.*;
import org.ikasan.spec.replay.*;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/replay-service-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class ReplayServiceTest
{
	/**
     * Mockery for mocking concrete classes
     */
	@Resource Mockery mockery;

	@Resource ReplayDao replayDao;


	@Resource(name = "replayDao") ReplayAuditDao<HibernateReplayAudit, HibernateReplayAuditEvent> replayAuditDao;

	@Resource ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse> replayService;
	
	@Resource SerialiserFactory ikasanSerialiserFactory;
	
	@Resource Serialiser<byte[], byte[]> serialiser;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    private String baseUri;

	@Before
	public void addReplayEvents()
	{

        baseUri = "http://localhost:"+wireMockRule.port()+"/";

        for(int i=0; i<100; i++)
		{
			HibernateReplayEvent replayEvent = new HibernateReplayEvent("errorUri-" + i, "this is a test event".getBytes(), "this is a test event", "moduleName", "flowName", 0);
			
	        
			this.replayDao.saveOrUpdate(replayEvent);
		}

        stubFor(put(urlEqualTo("/moduleName/rest/replay/eventReplay/moduleName/flowName"))
                .withHeader(HttpHeaders.USER_AGENT, equalTo("moduleName"))

				.willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("Event resubmitted!")));
		wireMockRule.addMockServiceRequestListener(new RequestListener() {
			@Override
			public void requestReceived(Request request, Response response) {
			//	System.out.print(request);
			//	System.out.print(response);
			}
		});
	}


	@Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void test_replay_success() throws MalformedURLException 
    {    
    	// expectations
    	mockery.checking(new Expectations()
    	{
    		{
    			for(int i=0; i<100; i++)
    			{
	    			// get each flow name
	    			one(ikasanSerialiserFactory).getDefaultSerialiser();
	    			will(returnValue(serialiser));
	    			one(serialiser).deserialise("event".getBytes());
	    			will(returnValue("event".getBytes()));
    			}
    			
    		}
    	});
    	
    	ReplayListenerImpl listener = new ReplayListenerImpl();
    	this.replayService.addReplayListener(listener);
    	
    	ArrayList<String> moduleNames = new ArrayList<String>();
    	moduleNames.add("moduleName");
    	
    	ArrayList<String> flowNames = new ArrayList<String>();
    	flowNames.add("flowName");
    	
    	List<ReplayEvent> replayEvents = this.replayDao.getReplayEvents
    			(moduleNames, flowNames, "", "", new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	this.replayService.replay(baseUri, replayEvents, "user", "password", "user", "this is a test!");
    	
    	
    	List<HibernateReplayAudit> replayAudits = this.replayAuditDao.getReplayAudits(null, null, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	HibernateReplayAudit replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, null, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, flowNames, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(null, flowNames, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, flowNames, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, flowNames, null, "user", new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, flowNames, "errorUri-10", "user", new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    	
    	replayAudits = this.replayAuditDao.getReplayAudits(moduleNames, flowNames, "errorUri-10", "user", new Date(0), new Date(System.currentTimeMillis() + 1000000));
    	
    	Assert.assertTrue(replayAudits.size() == 1);
    	
    	replayAudit = replayAudits.get(0);
    	replayAudit = this.replayAuditDao.getReplayAuditById(replayAudit.getId());
    	
    	Assert.assertTrue(this.replayAuditDao.getReplayAuditEventsByAuditId(replayAudit.getId()).size() == 100);
    	
    	Assert.assertTrue(listener.count == 100);
    }

	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void test_delete_success() throws MalformedURLException
	{
		// expectations
		mockery.checking(new Expectations()
		{
			{
				for(int i=0; i<100; i++)
				{
					// get each flow name
					one(ikasanSerialiserFactory).getDefaultSerialiser();
					will(returnValue(serialiser));
					one(serialiser).deserialise("event".getBytes());
					will(returnValue("event".getBytes()));
				}

			}
		});

		ReplayListenerImpl listener = new ReplayListenerImpl();
		this.replayService.addReplayListener(listener);

		ArrayList<String> moduleNames = new ArrayList<String>();
		moduleNames.add("moduleName");

		ArrayList<String> flowNames = new ArrayList<String>();
		flowNames.add("flowName");

		List<ReplayEvent> replayEvents = this.replayDao.getReplayEvents
				(moduleNames, flowNames, "", "", new Date(0), new Date(System.currentTimeMillis() + 1000000));

		this.replayService.replay(baseUri, replayEvents, "user", "password", "user", "this is a test!");
		this.replayService.replay(baseUri, replayEvents, "user", "password", "user", "this is a test!");
		this.replayService.replay(baseUri, replayEvents, "user", "password", "user", "this is a test!");
		this.replayService.replay(baseUri, replayEvents, "user", "password", "user", "this is a test!");

		List<HibernateReplayAudit> replayAudits = this.replayAuditDao.getReplayAudits(null, null, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));

		Long auditId1 = replayAudits.get(0).getId();
		Long auditId2 = replayAudits.get(1).getId();
		Long auditId3 = replayAudits.get(2).getId();
		Long auditId4 = replayAudits.get(3).getId();

		List<HibernateReplayAuditEvent> auditEvents = this.replayAuditDao.getReplayAuditEventsByAuditId(auditId1);
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId2));
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId3));
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId4));


		System.out.println("Number of replay events: " + replayEvents.size());
		System.out.println("Number of replay audits: " + replayAudits.size());
		System.out.println("Number of replay audit events: " + auditEvents.size());

		this.replayDao.housekeep(1000);

		replayAudits = this.replayAuditDao.getReplayAudits(null, null, null, null, new Date(0), new Date(System.currentTimeMillis() + 1000000));
		replayEvents = this.replayDao.getReplayEvents
				(moduleNames, flowNames, "", "", new Date(0), new Date(System.currentTimeMillis() + 1000000));
		auditEvents = this.replayAuditDao.getReplayAuditEventsByAuditId(auditId1);
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId2));
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId3));
		auditEvents.addAll(this.replayAuditDao.getReplayAuditEventsByAuditId(auditId4));

		Assert.assertTrue("Replay audits must be empty!", replayAudits.size() == 0);
		Assert.assertTrue("Replay events must be empty!", replayEvents.size() == 0);
		Assert.assertTrue("Replay audit events must be empty!", auditEvents.size() == 0);
	}
    
    
    class ReplayListenerImpl implements ReplayListener<HibernateReplayAuditEvent>
    {
    	public int count = 0;
		/* (non-Javadoc)
		 * @see org.ikasan.spec.replay.ReplayListener#onReplay(java.lang.Object)
		 */
		@Override
		public void onReplay(HibernateReplayAuditEvent event)
		{
			++count;
		}
    	
    }
}
