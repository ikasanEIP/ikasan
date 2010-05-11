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

package org.ikasan.filter.duplicate.dao;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.criterion.DetachedCriteria;
import org.ikasan.filter.duplicate.dao.HibernateMessagePersistenceDaoImpl;
import org.ikasan.filter.duplicate.dao.MessagePersistenceDao;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Test class for {@link HibernateMessagePersistenceDaoImpl}
 * 
 * @author Summer
 *
 */
public class HibernateMessagePersistenceDaoImplTest
{
    /** A {@link Mockery} for mocking classes and interfaces */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };



    /** A mocked {@link HibernateTemplate}*/
    private final HibernateTemplate template = this.mockery.mock(HibernateTemplate.class, "mockHibernateTemplate");

    /** A mocked {@link FilterEntry} */
    private final FilterEntry message = this.mockery.mock(FilterEntry.class, "mockMessage");

    /** The {@link MessagePersistenceDao} implementation to be tested.*/
    private HibernateMessagePersistenceDaoImpl dao;

    /**
     * Setup test object prior to each test case
     */
    @Before public void setup()
    {
        this.dao  = new HibernateMessagePersistenceDaoImpl();
        this.dao.setHibernateTemplate(this.template);
    }

    /**
     * Test case: query returns null result set since entry was not found in persistence. DAO
     * must return null.
     */
    @Test public void message_not_found_returns_null_case_1()
    {
        final Integer id = 1;
        final String clientId = "clientId";
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(null));
            }
        });
        //run test case
        FilterEntry result = this.dao.findMessageById(clientId, id);
        Assert.assertNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: query returns empty result set since entry was not found in persistence. DAO
     * must return null.
     */
    @Test public void message_not_found_returns_null_case_2()
    {
        final Integer id = 1;
        final String clientId = "clientId";
        final List<FilterEntry> resultList = new ArrayList<FilterEntry>();
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(resultList));
            }
        });
        //run test case
        FilterEntry result = this.dao.findMessageById(clientId, id);
        Assert.assertNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: entry was found in persistence. DAO returns found entry
     */
    @Test public void return_message_if_found()
    {
        final Integer id = 1;
        final String clientId = "clientId";
        final List<FilterEntry> resultList = new ArrayList<FilterEntry>();
        resultList.add(message);
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(resultList));
            }
        });
        //run test case
        FilterEntry result = this.dao.findMessageById(clientId, id);
        Assert.assertNotNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: save given filter entry
     */
    @Test public void save_message()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(template).save(message);
            }
        });
        this.dao.save(message);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: given a non-null/non-empty list of filter entries. DAO deletes
     * them from persistence
     */
    @Test public void you_get_a_list_of_messages_delete_all()
    {
        final List<FilterEntry> messages = new ArrayList<FilterEntry>();
        messages.add(message);
        this.mockery.checking(new Expectations()
        {
            {
                one(template).deleteAll(messages);
            }
        });
        this.dao.deleteAll(messages);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: there are expired filter entries in persistence. DAO returns
     * list of those entries
     */
    @Test public void found_expired_messages()
    {
        final List<FilterEntry> expiredMessages = new ArrayList<FilterEntry>();
        expiredMessages.add(message);
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(expiredMessages));
            }
        });
        Object result = this.dao.findExpiredMessages();
        Assert.assertNotNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: there are no expired entries in persistence. DOA returns
     * null
     */
    @Test public void no_expired_messages_found_case_1()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(null));
            }
        });
        Object result = this.dao.findExpiredMessages();
        Assert.assertNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: there are no expired entries in persistence. DOA returns
     * null
     */
    @Test public void no_expired_messages_found_case_2()
    {
        final List<FilterEntry> expiredMessages = new ArrayList<FilterEntry>();
        this.mockery.checking(new Expectations()
        {
            {
                one(template).findByCriteria(with(any(DetachedCriteria.class)));will(returnValue(expiredMessages));
            }
        });
        Object result = this.dao.findExpiredMessages();
        Assert.assertNull(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Cleanup after each test case
     */
    @After public void teardown()
    {
        this.dao = null;
    }
}
