/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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
package org.ikasan.filter.duplicate.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link DefaultDuplicateFilterService}
 * 
 * @author Summer
 *
 */
@SuppressWarnings("unchecked")
public class DefaultDuplicateFilterServiceTest
{
    /** {@link Mockery} for mocking interfaces */
    private Mockery mockery = new Mockery();

    /** Mocked {@link FilteredMessageDao} */
    private final FilteredMessageDao dao = this.mockery.mock(FilteredMessageDao.class, "mockDao");

    /** Mocked  {@link FilterEntryConverter} for messages of type {@link String}*/
    private final FilterEntryConverter<String> converter = (FilterEntryConverter<String>)this.mockery.mock(FilterEntryConverter.class, "mockConverter");

    /** Mocked {@link FilterEntry} returned by {@link #converter} */
    private final FilterEntry entry = this.mockery.mock(FilterEntry.class, "filterEntry");

    /** Dummy message content*/
    private final String message ="somemessage";

    /** Implementation of {@link DuplicateFilterService} to be tested*/
    private DuplicateFilterService serviceToTest = new DefaultDuplicateFilterService(this.dao, this.converter);

    /**
     * Test case: persist message
     */
    @Test public void new_messages_are_persisted()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(returnValue(entry));
                one(dao).save(entry);
            }
        });
        this.serviceToTest.persistMessage(this.message);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: if message not found, service must return false
     */
    @Test public void return_false_when_message_not_found()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(returnValue(entry));
                one(dao).findMessage(entry);will(returnValue(null));
            }
        });
        boolean result = this.serviceToTest.isDuplicate(this.message);
        Assert.assertFalse(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: if message is found, service must resturn true
     */
    @Test public void return_true_when_message_not_found()
    {
        final FilterEntry entry = this.mockery.mock(FilterEntry.class, "mockMessage");
        this.mockery.checking(new Expectations()
        {
            {
                one(converter).convert(message);will(returnValue(entry));
                one(dao).findMessage(entry);will(returnValue(entry));
            }
        });
        boolean result = this.serviceToTest.isDuplicate(this.message);
        Assert.assertTrue(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: housekeep persisted messages
     */
    @Test public void delete_expired_messages()
    {
        final FilterEntry entry = this.mockery.mock(FilterEntry.class, "mockMessage");
        final List<FilterEntry> expiredMessages = new ArrayList<FilterEntry>();
        expiredMessages.add(entry);
        this.mockery.checking(new Expectations()
        {
            {
                one(dao).findExpiredMessages();will(returnValue(expiredMessages));
                one(dao).deleteAll(expiredMessages);
            }
        });
        this.serviceToTest.housekeepExpiredMessages();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: there are no messages to housekeep
     */
    @Test public void no_messages_to_housekeep_case_1()
    {
        final List<FilterEntry> expiredMessages = new ArrayList<FilterEntry>();
        this.mockery.checking(new Expectations()
        {
            {
                one(dao).findExpiredMessages();will(returnValue(expiredMessages));
            }
        });
        this.serviceToTest.housekeepExpiredMessages();
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: there are no messages to housekeep
     */
    @Test public void no_messages_to_housekeep_case_2()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(dao).findExpiredMessages();will(returnValue(null));
            }
        });
        this.serviceToTest.housekeepExpiredMessages();
        this.mockery.assertIsSatisfied();
    }
}
