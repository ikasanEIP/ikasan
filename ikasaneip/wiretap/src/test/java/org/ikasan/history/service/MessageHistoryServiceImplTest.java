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
package org.ikasan.history.service;

import java.util.Collections;
import java.util.Date;

import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.model.HistoryEventFactory;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.wiretap.model.WiretapEventFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for MessageHistoryService
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryServiceImplTest
{
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    MessageHistoryDao messageHistoryDao = mockery.mock(MessageHistoryDao.class);
    WiretapEventFactory wiretapEventFactory = mockery.mock(WiretapEventFactory.class);
    FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class);
    HistoryEventFactory historyEventFactory = mockery.mock(HistoryEventFactory.class);
    MessageHistoryEvent messageHistoryEvent = mockery.mock(MessageHistoryEvent.class);

    MessageHistoryServiceImpl messageHistoryService = new MessageHistoryServiceImpl(messageHistoryDao, wiretapEventFactory);

    @Before
    public void setup()
    {
        messageHistoryService.setHistoryEventFactory(historyEventFactory);
    }

    @Test
    public void test_save()
    {
        mockery.checking(new Expectations(){{
            oneOf(historyEventFactory).newEvent("moduleName", "flowName", flowInvocationContext);
            will(returnValue(Collections.singletonList(messageHistoryEvent)));
            oneOf(messageHistoryDao).save(messageHistoryEvent);
        }});
        messageHistoryService.save(flowInvocationContext, "moduleName", "flowName");
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_housekeep()
    {
        mockery.checking(new Expectations(){{
            oneOf(messageHistoryDao).deleteAllExpired();
        }});
        messageHistoryService.housekeep();
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_housekeepablesExist()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(messageHistoryDao).housekeepablesExist();
                will(returnValue(true));
            }});
        Assert.assertTrue(messageHistoryService.housekeepablesExist());
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_findMessageHistoryEvents()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(messageHistoryDao).findMessageHistoryEvents(0, 0, "orderBy", true, Collections.<String>emptySet(),
                        "flowName", "componentName", "lifeId", "relatedLifeId", new Date(0L), new Date(0L));
                will(returnValue(null));
            }});
        messageHistoryService.findMessageHistoryEvents(0, 0, "orderBy", true, Collections.<String>emptySet(),
                "flowName", "componentName", "lifeId", "relatedLifeId", new Date(0L), new Date(0L));
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_getMessageHistoryEvents_with_relatedId()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(messageHistoryDao).getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", "lifeId");
                will(returnValue(null));
            }});
        messageHistoryService.getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", true);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_getMessageHistoryEvents_without_relatedId()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(messageHistoryDao).getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", null);
                will(returnValue(null));
            }});
        messageHistoryService.getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", false);
        mockery.assertIsSatisfied();
    }

}
