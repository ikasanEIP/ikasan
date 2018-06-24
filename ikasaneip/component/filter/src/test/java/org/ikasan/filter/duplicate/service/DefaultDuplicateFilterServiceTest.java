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
package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for {@link DefaultDuplicateFilterService}
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultDuplicateFilterServiceTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** {@link Mockery} for mocking interfaces */
    private Mockery mockery = new Mockery();

    /** Mocked {@link FilteredMessageDao} */
    private final FilteredMessageDao dao = this.mockery.mock(FilteredMessageDao.class, "mockDao");

    /** Mocked {@link FilteredMessageDao} */
    private final FilteredMessageDao oldDao = this.mockery.mock(FilteredMessageDao.class, "mockOldDao");

    /** Mocked {@link FilterEntry} returned by {@link #converter} */
    private final FilterEntry entry = this.mockery.mock(FilterEntry.class, "filterEntry");

    /** Implementation of {@link DuplicateFilterService} to be tested*/
    private DefaultDuplicateFilterService serviceToTest = new DefaultDuplicateFilterService(this.dao,this.oldDao);

    /**
     * Test case: persist message
     */
    @Test public void new_messages_are_persisted()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(dao).save(entry);
            }
        });
        this.serviceToTest.persistMessage(this.entry);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: if message not found, service must return false
     */
    @Test
    public void isDuplicate_when_oldFilter_hasMessages()
    {

        expectedException.expect(RequiresFilterMessageMigrationException.class);

        this.mockery.checking(new Expectations()
        {
            {
                atLeast(2).of(entry).getClientId();will(returnValue("testClientId"));
                one(oldDao).hasMessages("testClientId");will(returnValue(true));
            }
        });
        boolean result = this.serviceToTest.isDuplicate(this.entry);
        Assert.assertFalse(result);
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
                one(entry).getClientId();will(returnValue("testClientId"));
                one(oldDao).hasMessages("testClientId");will(returnValue(false));
                one(dao).findMessage(entry);will(returnValue(null));
            }
        });
        boolean result = this.serviceToTest.isDuplicate(this.entry);
        Assert.assertFalse(result);
        this.mockery.assertIsSatisfied();
    }


    /**
     * Test case: if message is found, service must resturn true
     */
    @Test public void return_true_when_message_found_first_run()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(entry).getClientId();will(returnValue("testClientId"));
                one(oldDao).hasMessages("testClientId");will(returnValue(false));
                one(dao).findMessage(entry);will(returnValue(entry));
            }
        });
        boolean result = this.serviceToTest.isDuplicate(this.entry);
        Assert.assertTrue(result);
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test case: if message is found, service must resturn true
     */
    @Test public void return_true_when_message_found_subsequence_runs()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(entry).getClientId();will(returnValue("testClientId"));
                one(oldDao).hasMessages("testClientId");will(returnValue(false));

                atLeast(2).of(dao).findMessage(entry);will(returnValue(entry));
            }
        });
      this.serviceToTest.isDuplicate(this.entry);
        boolean result = this.serviceToTest.isDuplicate(this.entry);
        Assert.assertTrue(result);
        this.mockery.assertIsSatisfied();
    }


    /**
     * Test case: housekeep persisted messages
     */
    @Test public void delete_expired_messages()
    {
        this.mockery.checking(new Expectations()
        {
            {
                one(dao).deleteAllExpired();
                one(oldDao).deleteAllExpired();
            }
        });
        this.serviceToTest.housekeep();
        this.mockery.assertIsSatisfied();
    }
}
