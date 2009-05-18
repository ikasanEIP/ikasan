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
package org.ikasan.framework.payload.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.payload.dao.DatabasePayloadDao;
import org.ikasan.framework.payload.model.DatabasePayload;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * JUnit test class for Database payload provider
 * @author Ikasan Development Team
 */
public class DatabasePayloadProviderTest extends TestCase
{
    /**
     * Test valid constructor
     */
    public void testConstructor_acceptsValidArguments()
    {
        Mockery mockery = new Mockery();
        DatabaseHousekeeper databaseEventHouseKeepingMatcher = mockery
            .mock(DatabaseHousekeeper.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        boolean destructiveRead = false;
        final Spec payloadSpec = Spec.TEXT_PLAIN;
        final String payloadSrcSystem = "testSrcSystem";
        
        // housekeeping but not destructive read
        new DatabasePayloadProvider(null, payloadFactory,
            databaseEventHouseKeepingMatcher, destructiveRead,
            payloadSpec, payloadSrcSystem);
        // not housekeeping but destructive read
        destructiveRead = true;
        new DatabasePayloadProvider(null, payloadFactory, null, destructiveRead,
                payloadSpec, payloadSrcSystem);
    }

    /**
     * Test that constructor enforces some business rules
     */
    public void testConstructor_enforcesHousekeepingAndDestructiveReadMutuallyExclusive()
    {
        Mockery mockery = new Mockery();
        DatabaseHousekeeper databaseEventHouseKeepingMatcher = mockery
            .mock(DatabaseHousekeeper.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final Spec payloadSpec = Spec.TEXT_PLAIN;
        final String payloadSrcSystem = "testSrcSystem";
        try
        {
            boolean destructiveRead = true;
            new DatabasePayloadProvider(null, payloadFactory,
                databaseEventHouseKeepingMatcher, destructiveRead,
                payloadSpec, payloadSrcSystem);
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
        Mockery mockery = new Mockery();
        final DatabaseHousekeeper housekeeper = mockery
            .mock(DatabaseHousekeeper.class);
        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);

        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final Spec payloadSpec = Spec.TEXT_PLAIN;
        final String payloadSrcSystem = "testSrcSystem";
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
            destructiveRead, payloadSpec, payloadSrcSystem);

        databaseEventProvider.getNextRelatedPayloads();
    }

    /**
     * Test non destructive read
     */
    public void testGetNextRelatedPayloads_discoversDatabaseEventNonDestructiveRead()
    {
        Mockery mockery = new Mockery();
        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final String payloadContent = "blah";
        final DatabasePayload unconsumedEvent = new DatabasePayload(
            payloadContent, new Date());
        final Spec payloadSpec = Spec.TEXT_PLAIN;
        final String payloadSrcSystem = "testSrcSystem";
        final List<DatabasePayload> unconsumedEvents = new ArrayList<DatabasePayload>();
        unconsumedEvents.add(unconsumedEvent);
        mockery.checking(new Expectations()
        {
            {
                // a new event to consume
                one(databaseEventDao).findUnconsumed();
                will(returnValue(unconsumedEvents));
                one(payloadFactory).newPayload(payloadSpec,
                        payloadSrcSystem, payloadContent.getBytes());
                one(databaseEventDao).save(unconsumedEvent);
            }
        });
        boolean destructiveRead = false;
        // housekeeping but not destructive read
        DatabasePayloadProvider databaseEventProvider = new DatabasePayloadProvider(
            databaseEventDao, payloadFactory, null,
            destructiveRead, payloadSpec, payloadSrcSystem);
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
        Mockery mockery = new Mockery();
        final DatabasePayloadDao databaseEventDao = mockery
            .mock(DatabasePayloadDao.class);
        final PayloadFactory payloadFactory = mockery
            .mock(PayloadFactory.class);
        final String payloadContent = "blah";
        final DatabasePayload unconsumedEvent = new DatabasePayload(
            payloadContent, new Date());
        final Spec payloadSpec = Spec.TEXT_PLAIN;
        final String payloadSrcSystem = "testSrcSystem";
        final List<DatabasePayload> unconsumedEvents = new ArrayList<DatabasePayload>();
        unconsumedEvents.add(unconsumedEvent);
        mockery.checking(new Expectations()
        {
            {
                // a new event to consume
                one(databaseEventDao).findUnconsumed();
                will(returnValue(unconsumedEvents));
                one(payloadFactory).newPayload(payloadSpec,
                        payloadSrcSystem, payloadContent.getBytes());
                one(databaseEventDao).delete(unconsumedEvent);
            }
        });
        boolean destructiveRead = true;
        // housekeeping but not destructive read
        DatabasePayloadProvider databaseEventProvider = new DatabasePayloadProvider(
            databaseEventDao, payloadFactory, null, destructiveRead,
            payloadSpec, payloadSrcSystem);
        List<Payload> nextRelatedPayloads = databaseEventProvider
            .getNextRelatedPayloads();
        assertEquals("should have returned a list of 1 related Payload", 1,
            nextRelatedPayloads.size());
    }
}
