package org.ikasan.history.service;

import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.model.HistoryEventFactory;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.management.HousekeeperService;
import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.Set;

/**
 * Implementation of the MessageHistoryService with Housekeeping
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
        MessageHistoryEvent<String> messageHistoryEvent = historyEventFactory.newEvent(moduleName, flowName, flowInvocationContext);
        messageHistoryDao.save(messageHistoryEvent);
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> findMessageHistoryEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                         Set<String> moduleNames, String flowName, String componentName,
                                                         String lifeId, String relatedLifeId, Date fromDate, Date toDate)
    {
        return messageHistoryDao.findMessageHistoryEvents(pageNo, pageSize, orderBy, orderAscending,
                                                          moduleNames, flowName, componentName,
                                                          lifeId, relatedLifeId, fromDate, toDate);
    }

    @Override
    public PagedSearchResult<MessageHistoryEvent> getMessageHistoryEvent(int pageNo, int pageSize, String orderBy, boolean orderAscending,
                                                       String lifeId, boolean lookupRelatedLifeId)
    {
        return messageHistoryDao.getMessageHistoryEvent(pageNo, pageSize, orderBy, orderAscending, lifeId, lookupRelatedLifeId ? lifeId : null);
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


}
