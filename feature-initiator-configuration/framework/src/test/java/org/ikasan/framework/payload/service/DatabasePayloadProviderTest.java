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
package org.ikasan.framework.payload.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.payload.dao.DatabasePayloadDao;
import org.ikasan.framework.payload.model.DatabasePayload;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * JUnit test class for Database payload provider
 * @author Ikasan Development Team
 */
public class DatabasePayloadProviderTest extends TestCase
{
	
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Test invalid constructor
     */
    public void testConstructor_withNullDao()
    {
        try
        {
            new DatabasePayloadProvider(null, null, null, false);
        }
        catch (Exception e)
        {
            assertTrue(
                "Exception thrown by constructor should be IllegalArgumentException",
                (e instanceof IllegalArgumentException));
        }
    }

    /**
     * Test valid constructor
     */
    public void testConstructor_acceptsValidArguments()
    {
        DatabaseHousekeeper databaseEventHouseKeepingMatcher = mockery
            .mock(DatabaseHousekeeper.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final DatabasePayloadDao databaseEventDao = mockery
        .mock(DatabasePayloadDao.class);

        boolean destructiveRead = false;
        
        // housekeeping but not destructive read
        new DatabasePayloadProvider(databaseEventDao, payloadFactory,
            databaseEventHouseKeepingMatcher, destructiveRead);
        // not housekeeping but destructive read
        destructiveRead = true;
        new DatabasePayloadProvider(databaseEventDao, payloadFactory, null, destructiveRead);
    }

    /**
     * Test that constructor enforces some business rules
     */
    public void testConstructor_enforcesHousekeepingAndDestructiveReadMutuallyExclusive()
    {
        DatabaseHousekeeper databaseEventHouseKeepingMatcher = mockery
            .mock(DatabaseHousekeeper.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final DatabasePayloadDao databaseEventDao = mockery
        .mock(DatabasePayloadDao.class);

        try
        {
            boolean destructiveRead = true;
            new DatabasePayloadProvider(databaseEventDao, payloadFactory,
                databaseEventHouseKeepingMatcher, destructiveRead);
            fail("Exception should have been thrown by constructor as housekeeping and destructive reading are mutuallly exclusive");
        }
        catch (Exception e)
        {
            assertTrue(
                "Exception thrown by constructor should be IllegalArgumentException",
                (e instanceof IllegalArgumentException));
        }
    }

    /**
     * Test some housekeeping
     */
    public void testGetNextRelatedPayloads_housekeepingConfiguredCallsHousekeeping()
    {
        final DatabaseHousekeeper housekeeper = mockery
            .mock(DatabaseHousekeeper.class);
        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);

        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);

        mockery.checking(new Expectations()
        {
            {
                // no new events to consume
                one(databaseEventDao).findUnconsumed();
                will(returnValue(new ArrayList<DatabasePayload>()));
                // calls housekeeping
                one(housekeeper).housekeep();
             
            }
        });
        boolean destructiveRead = false;
        // housekeeping but not destructive read
        DatabasePayloadProvider databaseEventProvider = new DatabasePayloadProvider(
            databaseEventDao, payloadFactory, housekeeper,
            destructiveRead);

        databaseEventProvider.getNextRelatedPayloads();
    }

    /**
     * Test non destructive read
     */
    public void testGetNextRelatedPayloads_discoversDatabaseEventNonDestructiveRead()
    {

        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final String payloadContent = "blah";
//        final DatabasePayload unconsumedEvent = new DatabasePayload(
//            payloadContent, new Date());
        final DatabasePayload unconsumedEvent = mockery.mock(DatabasePayload.class);

        final List<DatabasePayload> unconsumedEvents = new ArrayList<DatabasePayload>();
        final Long databasePayloadId = 1l;
        unconsumedEvents.add(unconsumedEvent);
        mockery.checking(new Expectations()
        {
            {
                // a new event to consume
                one(databaseEventDao).findUnconsumed();
                will(returnValue(unconsumedEvents));
                allowing(unconsumedEvent).getId();will(returnValue(databasePayloadId));
                one(unconsumedEvent).getEvent();will(returnValue(payloadContent));
                
                one(payloadFactory).newPayload("1", payloadContent.getBytes());
                
                //set it as consumed and save it
                one(unconsumedEvent).setConsumed(true);
                one(unconsumedEvent).setLastUpdated(with(any(Date.class)));
                one(databaseEventDao).save(unconsumedEvent);
            }
        });
        boolean destructiveRead = false;
        // housekeeping but not destructive read
        DatabasePayloadProvider databaseEventProvider = new DatabasePayloadProvider(
            databaseEventDao, payloadFactory, null,
            destructiveRead);
        List<Payload> nextRelatedPayloads = databaseEventProvider
            .getNextRelatedPayloads();
        assertEquals("should have returned a list of 1 related Payload", 1,
            nextRelatedPayloads.size());
    }

    /**
     * Test destructive read
     */
    public void testGetNextRelatedPayloads_discoversDatabaseEventDestructiveRead()
    {
        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final String payloadContent = "blah";
//        final DatabasePayload unconsumedEvent = new DatabasePayload(
//            payloadContent, new Date());
        final DatabasePayload unconsumedEvent = mockery.mock(DatabasePayload.class);

        final List<DatabasePayload> unconsumedEvents = new ArrayList<DatabasePayload>();
        final Long databasePayloadId = 1l;
        
        
        unconsumedEvents.add(unconsumedEvent);
        mockery.checking(new Expectations()
        {
            {
                // a new event to consume
                one(databaseEventDao).findUnconsumed();
                will(returnValue(unconsumedEvents));
                allowing(unconsumedEvent).getId();will(returnValue(databasePayloadId));
                one(unconsumedEvent).getEvent();will(returnValue(payloadContent));
                
                
                one(payloadFactory).newPayload("1", payloadContent.getBytes());
                
                one(unconsumedEvent).setConsumed(true);
                one(unconsumedEvent).setLastUpdated(with(any(Date.class)));
                one(databaseEventDao).delete(unconsumedEvent);
            }
        });
        boolean destructiveRead = true;
        // housekeeping but not destructive read
        DatabasePayloadProvider databaseEventProvider = new DatabasePayloadProvider(
            databaseEventDao, payloadFactory, null, destructiveRead);
        List<Payload> nextRelatedPayloads = databaseEventProvider
            .getNextRelatedPayloads();
        assertEquals("should have returned a list of 1 related Payload", 1,
            nextRelatedPayloads.size());
    }
}
