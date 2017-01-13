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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.harvest.HarvestService;
import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.listener.MessageHistoryContextListener;
import org.ikasan.history.model.CustomMetric;
import org.ikasan.history.model.HistoryEventFactory;
import org.ikasan.history.model.MetricEvent;
import org.ikasan.housekeeping.HousekeepService;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.management.HousekeeperService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapEventFactory;
import org.ikasan.wiretap.model.WiretapFlowEvent;

/**
 * Implementation of the MessageHistoryService with Housekeeping
 *
 * @author Ikasan Development Team
 */
public class MessageHistoryServiceImpl implements MessageHistoryService<FlowInvocationContext, FlowEvent, PagedSearchResult<MessageHistoryEvent>, MessageHistoryEvent>
        , HousekeepService, HarvestService<MessageHistoryEvent>
{
    private static final Logger logger = Logger.getLogger(MessageHistoryServiceImpl.class);

    public static final String MESSAGE_HISTORY_DAYS_TO_LIVE = "messageHistoryDaysToLive";

    protected MessageHistoryDao messageHistoryDao;

    protected HistoryEventFactory historyEventFactory = new HistoryEventFactory();

    protected PlatformConfigurationService platformConfigurationService;

    protected Integer messageHistoryDaysToLive = 7;
    
    /** The wiretap event factory */
    private WiretapEventFactory wiretapEventFactory;


    public MessageHistoryServiceImpl(MessageHistoryDao messageHistoryDao, WiretapEventFactory wiretapEventFactory)
    {
        if (messageHistoryDao == null)
        {
            throw new IllegalArgumentException("messageHistoryDao cannot be null");
        }
        this.messageHistoryDao = messageHistoryDao;
        if (wiretapEventFactory == null)
        {
            throw new IllegalArgumentException("wiretapEventFactory cannot be null");
        }
        this.wiretapEventFactory = wiretapEventFactory;

    }

    @Override
    public void save(FlowInvocationContext flowInvocationContext, String moduleName, String flowName)
    {
        if(this.platformConfigurationService != null)
        {
            String messageHistoryDaysToLiveString = this.platformConfigurationService.getConfigurationValue(MESSAGE_HISTORY_DAYS_TO_LIVE);

            if (messageHistoryDaysToLiveString != null && !messageHistoryDaysToLiveString.isEmpty())
            {
                try
                {
                    this.messageHistoryDaysToLive = Integer.parseInt(messageHistoryDaysToLiveString);
                }
                catch (Exception e)
                {
                    logger.info("Could not convert message history days to live from platform configuration to Integer! Using default.", e);
                }
            }
        }

        List<MessageHistoryEvent<String, CustomMetric, MetricEvent>> messageHistoryEvents = historyEventFactory.newEvent(moduleName, flowName
                , flowInvocationContext, this.messageHistoryDaysToLive);
        for (MessageHistoryEvent<String, CustomMetric, MetricEvent > messageHistoryEvent : messageHistoryEvents)
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
    
    /* (non-Javadoc)
	 * @see org.ikasan.spec.history.MessageHistoryService#snapMetricEvent(java.lang.Object, java.lang.String, java.lang.String, java.lang.String, java.lang.Long)
	 */
    @Override
    public void snapMetricEvent(FlowEvent event, String componentName,
                                String moduleName, String flowName, Long timeToLive)
    {
        long expiry = System.currentTimeMillis() + (timeToLive * 60000);
        WiretapEvent wiretapEvent = wiretapEventFactory.newEvent(moduleName, flowName, componentName, event, expiry);
        this.messageHistoryDao.save(wiretapEvent);
    }

    @Override
    public List<MessageHistoryEvent> harvest(int transactionBatchSize)
    {
        List<MessageHistoryEvent> events = this.messageHistoryDao.getHarvestableRecords(transactionBatchSize);

        for(MessageHistoryEvent event: events)
        {
            event.setHarvested(true);

            this.messageHistoryDao.save(event);
        }

        return events;
    }

    @Override
    public boolean harvestableRecordsExist()
    {
        return this.messageHistoryDao.harvestableRecordsExist();
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

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        this.messageHistoryDao.setHousekeepingBatchSize(housekeepingBatchSize);
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {
        this.messageHistoryDao.setTransactionBatchSize(transactionBatchSize);
    }

    /** used to mock the factory in testing */
    protected void setHistoryEventFactory(HistoryEventFactory historyEventFactory)
    {
        this.historyEventFactory = historyEventFactory;
    }

    public PlatformConfigurationService getPlatformConfigurationService()
    {
        return platformConfigurationService;
    }

    public void setPlatformConfigurationService(PlatformConfigurationService platformConfigurationService)
    {
        this.platformConfigurationService = platformConfigurationService;
    }
}
