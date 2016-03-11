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

import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.model.HistoryEventFactory;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.management.HousekeeperService;
import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the MessageHistoryService with Housekeeping
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryServiceImpl implements MessageHistoryService<FlowInvocationContext, PagedSearchResult<MessageHistoryEvent>>, HousekeeperService
{
    protected MessageHistoryDao messageHistoryDao;

    protected HistoryEventFactory historyEventFactory = new HistoryEventFactory();

    public MessageHistoryServiceImpl(MessageHistoryDao messageHistoryDao)
    {
        if (messageHistoryDao == null)
        {
            throw new IllegalArgumentException("messageHistoryDao cannot be null");
        }
        this.messageHistoryDao = messageHistoryDao;
    }

    @Override
    public void save(FlowInvocationContext flowInvocationContext, String moduleName, String flowName)
    {
        List<MessageHistoryEvent<String>> messageHistoryEvents = historyEventFactory.newEvent(moduleName, flowName, flowInvocationContext);
        for (MessageHistoryEvent<String> messageHistoryEvent : messageHistoryEvents)
        {
            messageHistoryDao.save(messageHistoryEvent);
        }
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> findMessageHistoryEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                         Set<String> moduleNames, String flowName, String componentName,
                                                         String eventId, String relatedEventId, Date fromDate, Date toDate)
    {
        return messageHistoryDao.findMessageHistoryEvents(pageNo, pageSize, orderBy, orderAscending,
                                                          moduleNames, flowName, componentName,
                                                          eventId, relatedEventId, fromDate, toDate);
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> getMessageHistoryEvent(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                       String eventId, boolean lookupRelatedEventId)
    {
        return messageHistoryDao.getMessageHistoryEvent(pageNo, pageSize, orderBy, orderAscending, eventId, lookupRelatedEventId ? eventId : null);
    }

    @Override
    public void housekeep()
    {
        messageHistoryDao.deleteAllExpired();
    }

    @Override
    public boolean housekeepablesExist()
    {
        return messageHistoryDao.housekeepablesExist();
    }

    /** used to mock the factory in testing */
    protected void setHistoryEventFactory(HistoryEventFactory historyEventFactory)
    {
        this.historyEventFactory = historyEventFactory;
    }
}
