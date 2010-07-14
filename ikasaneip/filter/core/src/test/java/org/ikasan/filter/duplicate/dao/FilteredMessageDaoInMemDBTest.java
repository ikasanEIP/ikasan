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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for {@link HibernateFilteredMessageDaoImpl} using an in memory
 * database rather than mocking the hibernate template.
 * 
 * @author Ikasan Development Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
/*
 * Application context will be loaded from location:
 * classpath:/org/ikasan/filter/duplicate/dao/MessagePersistenceDaoInMemDBTest-context.xml
 */

@ContextConfiguration
public class FilteredMessageDaoInMemDBTest
{
    @Autowired
    private HibernateFilteredMessageDaoImpl daoToTest;

    /**
     * Test case: DAO must return null since filter entry was not found in database
     */
    @Test public void filter_entry_not_found_returns_null()
    {
        FilterEntry aMessage = new DefaultFilterEntry( "aMessage".hashCode(), "find_test", 1);
        this.daoToTest.save(aMessage);
        FilterEntry messageToBeFound = new DefaultFilterEntry( "test".hashCode(), "find_test", 1);
        FilterEntry result = this.daoToTest.findMessage(messageToBeFound);
        Assert.assertNull(result);
    }

    /**
     * Test case: save given filter entry. 
     * Test case: look for newly saved message; it must be found and must be the same!
     */
    @Test public void save_new_entry_find_returns_same_entry()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultFilterEntry("save_test".hashCode(), "test", timeToLive);
        this.daoToTest.save(newEntry);

        //Now lets find it..
        FilterEntry newEntryReloaded = this.daoToTest.findMessage(newEntry);

        Assert.assertNotNull(newEntryReloaded);
        Assert.assertEquals("test", newEntryReloaded.getClientId());
        Assert.assertEquals("save_test".hashCode(), newEntryReloaded.getCriteria().intValue());

        //Because testing Date sucks. Will restrict the assertions to yyyyMMdd only!
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expectedCreatedDate = formatter.format(new Date(System.currentTimeMillis()));
        String expectedExpiryDate = formatter.format(new Date(System.currentTimeMillis() + (timeToLive * 24 * 3600 * 1000)));

        Assert.assertEquals(expectedCreatedDate, formatter.format(newEntryReloaded.getCreatedDateTime()));
        Assert.assertEquals(expectedExpiryDate, formatter.format(newEntryReloaded.getExpiry()));
    }

    /**
     * Test case: bulk delete only expired messages found in persistence. Searching for
     * housekept entries will return null.
     */
    @Test public void bulk_delete_expired_entries()
    {
        FilterEntry one = new DefaultFilterEntry("one".hashCode(), "bulk_delete_test", 0);
        this.daoToTest.save(one);

        FilterEntry two = new DefaultFilterEntry("two".hashCode(), "bulk_delete_test", 0);
        this.daoToTest.save(two);

        FilterEntry three = new DefaultFilterEntry("three".hashCode(), "bulk_delete_test", 1);
        this.daoToTest.save(three);

        this.daoToTest.deleteAllExpired();

        FilterEntry found = this.daoToTest.findMessage(one);
        Assert.assertNull(found);

        found = this.daoToTest.findMessage(two);
        Assert.assertNull(found);

        found = this.daoToTest.findMessage(three);
        Assert.assertNotNull(found);
    }

    /**
     * Test case: delete expired entries in batches of pre-set size. Searching for
     * housekept entries will return null.
     */
    @Test public void batch_delete_expired_entries()
    {
        this.daoToTest.setBatchedHousekeep(true);
        this.daoToTest.setBatchSize(1);
        FilterEntry one = new DefaultFilterEntry("one".hashCode(), "batch_delete_test", 0);
        this.daoToTest.save(one);

        FilterEntry two = new DefaultFilterEntry("two".hashCode(), "batch_delete_test", 0);
        this.daoToTest.save(two);

        FilterEntry three = new DefaultFilterEntry("three".hashCode(), "batch_delete_test", 1);
        this.daoToTest.save(three);

        this.daoToTest.deleteAllExpired();

        FilterEntry found = this.daoToTest.findMessage(one);
        Assert.assertNull(found);

        found = this.daoToTest.findMessage(two);
        Assert.assertNull(found);

        found = this.daoToTest.findMessage(three);
        Assert.assertNotNull(found);
    }
    /**
     * Test case: try to save an already existing filter entry
     */
    @Test(expected=UncategorizedSQLException.class) public void save_duplicate_must_fail()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultFilterEntry("save_duplicate_test".hashCode(), "test", timeToLive);
        this.daoToTest.save(newEntry);

        //Now try to save it again
        this.daoToTest.save(newEntry);
    }
}
