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

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.framework.payload.dao.DatabasePayloadDao;
import org.ikasan.framework.payload.model.DatabasePayload;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * JUnit test class for DatabasePayloadPublisher
 * @author Ikasan Development Team
 */
public class DatabasePayloadPublisherTest extends TestCase
{
    /**
     * Test publishing
     */
    public void testPublish()
    {
        Mockery mockery = new Mockery();
        final DatabasePayloadDao databaseEventDao = mockery.mock(DatabasePayloadDao.class);
        DatabasePayloadPublisher databasePayloadPublisher = new DatabasePayloadPublisher(databaseEventDao);
        final byte[] payloadContent = "payloadContent".getBytes();
        final Payload payload = mockery.mock(Payload.class);
        mockery.checking(new Expectations()
        {
            {
                one(payload).getId();
                will(returnValue(null));
                one(payload).getContent();
                will(returnValue(payloadContent));
                one(databaseEventDao).save((DatabasePayload) with(a(DatabasePayload.class)));
            }
        });
        databasePayloadPublisher.publish(payload);
    }
}
