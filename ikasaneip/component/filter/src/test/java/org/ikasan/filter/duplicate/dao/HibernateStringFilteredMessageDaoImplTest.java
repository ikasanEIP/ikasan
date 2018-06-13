/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.filter.duplicate.dao;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.DefaultStringFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Test class for {@link HibernateFilteredMessageDaoImpl} using an in memory
 * database rather than mocking the hibernate template.
 * 
 * @author Ikasan Development Team
 *
 */
/*
 * Application context will be loaded from location:
 * classpath:/org/ikasan/filter/duplicate/dao/MessagePersistenceDaoInMemDBTest-context.xml
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/FilteredMessageDaoInMemDBTest-context.xml",
        "/filter-service-conf.xml"
})
public class HibernateStringFilteredMessageDaoImplTest
{
    @Autowired
    private FilteredMessageDao duplicateStringFilterDao;

    /**
     * Test case: DAO must return null since filter entry was not found in database
     */
    @Test
    @DirtiesContext
    public void filter_entry_not_found_returns_null()
    {
        FilterEntry aMessage = new DefaultStringFilterEntry( "aMessage", "find_test", 1);
        this.duplicateStringFilterDao.save(aMessage);
        FilterEntry messageToBeFound = new DefaultFilterEntry( "test".hashCode(), "find_test", 1);
        FilterEntry result = this.duplicateStringFilterDao.findMessage(messageToBeFound);
        Assert.assertNull(result);
    }

    /**
     * Test case: save given filter entry. 
     * Test case: look for newly saved message; it must be found and must be the same!
     */
    @Test
    @DirtiesContext
    public void save_new_entry_find_returns_same_entry()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultStringFilterEntry("save_test", "test", timeToLive);
        this.duplicateStringFilterDao.save(newEntry);

        //Now lets find it..
        FilterEntry newEntryReloaded = this.duplicateStringFilterDao.findMessage(newEntry);

        Assert.assertNotNull(newEntryReloaded);
        Assert.assertEquals("test", newEntryReloaded.getClientId());
        Assert.assertEquals("save_test", ((DefaultStringFilterEntry)newEntryReloaded).getValue());

        // created should be before expiry
        Assert.assertTrue(newEntryReloaded.getCreatedDateTime() < newEntryReloaded.getExpiry());

        // created + TTL should equal expiry
        Assert.assertTrue((newEntryReloaded.getCreatedDateTime() + (timeToLive * 24 * 3600 * 1000)) == newEntryReloaded.getExpiry());
    }

    /**
     * Test case: bulk delete only expired messages found in persistence. Searching for
     * housekept entries will return null.
     * @throws InterruptedException
     */
    @Test
    @DirtiesContext
    public void bulk_delete_expired_entries() throws InterruptedException
    {
        FilterEntry one = new DefaultStringFilterEntry("one", "bulk_delete_test", 0);
        this.duplicateStringFilterDao.save(one);
        this.duplicateStringFilterDao.setHousekeepQuery("delete top _bs_ from MessageFilter where Expiry <= _ex_");   //sybase

        FilterEntry two = new DefaultStringFilterEntry("two", "bulk_delete_test", 0);
        this.duplicateStringFilterDao.save(two);

        FilterEntry three = new DefaultStringFilterEntry("three", "bulk_delete_test", 1);
        this.duplicateStringFilterDao.save(three);
        Thread.sleep(10l); // let time move on
        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);
        this.duplicateStringFilterDao.deleteAllExpired();

        FilterEntry found = this.duplicateStringFilterDao.findMessage(one);
        Assert.assertNull(found);

        found = this.duplicateStringFilterDao.findMessage(two);
        Assert.assertNull(found);

        found = this.duplicateStringFilterDao.findMessage(three);
        Assert.assertNotNull(found);
    }

    /**
     * Test case: bulk delete only expired messages found in persistence. Searching for
     * housekept entries will return null.
     * @throws InterruptedException
     */
    @Test
    @DirtiesContext
    public void bulk_batch_delete_expired_entries() throws InterruptedException
    {
        for(int i=0; i<19768; i++)
        {
            FilterEntry one = new DefaultStringFilterEntry(new Date()+"", "bulk_delete_test" + i	, 0);
            this.duplicateStringFilterDao.save(one);
        }

        Thread.sleep(10l); // let time move on
        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);
        this.duplicateStringFilterDao.setTransactionBatchSize(20000);
        this.duplicateStringFilterDao.deleteAllExpired();

        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);

        Assert.assertNull(this.duplicateStringFilterDao.findExpiredMessages());

        for(int i=0; i<77; i++)
        {
            FilterEntry one = new DefaultStringFilterEntry(new Date()+"", "bulk_delete_test" + i	, 0);
            this.duplicateStringFilterDao.save(one);
        }

        Thread.sleep(10l); // let time move on
        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);

        this.duplicateStringFilterDao.deleteAllExpired();

        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);

        Assert.assertNull(this.duplicateStringFilterDao.findExpiredMessages());
    }

    /**
     * Test case: delete expired entries in batches of pre-set size. Searching for
     * housekept entries will return null.
     * @throws InterruptedException
     */
    @Test
    @DirtiesContext
    public void batch_delete_expired_entries() throws InterruptedException
    {
        this.duplicateStringFilterDao.setBatchHousekeepDelete(false);
        this.duplicateStringFilterDao.setHousekeepingBatchSize(1);
        FilterEntry one = new DefaultStringFilterEntry("one", "batch_delete_test", 0);
        this.duplicateStringFilterDao.save(one);

        FilterEntry two = new DefaultStringFilterEntry("two", "batch_delete_test", 0);
        this.duplicateStringFilterDao.save(two);

        FilterEntry three = new DefaultStringFilterEntry("three", "batch_delete_test", 1);
        this.duplicateStringFilterDao.save(three);
        Thread.sleep(10l); // let time move on
        this.duplicateStringFilterDao.deleteAllExpired();

        FilterEntry found = this.duplicateStringFilterDao.findMessage(one);
        Assert.assertNull(found);

        found = this.duplicateStringFilterDao.findMessage(two);
        Assert.assertNull(found);

        found = this.duplicateStringFilterDao.findMessage(three);
        Assert.assertNotNull(found);
    }
    /**
     * Test case: try to save an already existing filter entry
     */
    @Test(expected=DataIntegrityViolationException.class)
    @DirtiesContext
    public void save_duplicate_must_fail()
    {
        //Save the entry
        int timeToLive = 1;
        FilterEntry newEntry = new DefaultStringFilterEntry("save_duplicate_test", "test", timeToLive);
        this.duplicateStringFilterDao.save(newEntry);

        //Now try to save it again
        this.duplicateStringFilterDao.save(newEntry);
    }
}
