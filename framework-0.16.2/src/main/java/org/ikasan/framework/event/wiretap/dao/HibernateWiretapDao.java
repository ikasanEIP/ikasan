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
package org.ikasan.framework.event.wiretap.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.event.wiretap.model.WiretapEventHeader;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>WiretapDao</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class HibernateWiretapDao extends HibernateDaoSupport implements WiretapDao
{
    /** Query used for housekeeping expired wiretap events */
    private static final String HOUSEKEEP_QUERY = "delete WiretapEvent w where w.expiry <= ?";

    /** Query for finding all wiretap events with the same payloadId */
    private static final String WIRETAP_EVENT_IDS_FOR_PAYLOAD_ID = "select w.id from WiretapEvent w where w.payloadId = ?";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.component.endpoint.wiretap.dao.WiretapDao#save(org
     * .ikasan.framework.component.endpoint.wiretap.model.WiretapEvent)
     */
    public void save(WiretapEvent wiretapEvent)
    {
        getHibernateTemplate().save(wiretapEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.event.wiretap.dao.WiretapDao#findById(java.lang.
     * Long)
     */
    @SuppressWarnings("unchecked")
    public WiretapEvent findById(Long id)
    {
        // get the WiretapEvent
        WiretapEvent wiretapEvent = (WiretapEvent) getHibernateTemplate().get(WiretapEvent.class, id);
        // find any next or previous by payloadId
        List<Long> relatedIds = getHibernateTemplate().find(WIRETAP_EVENT_IDS_FOR_PAYLOAD_ID, wiretapEvent.getPayloadId());
        Collections.sort(relatedIds);
        int thisWiretapsIndex = relatedIds.indexOf(wiretapEvent.getId());
        Long nextEvent = null;
        Long previousEvent = null;
        if (thisWiretapsIndex > 0)
        {
            previousEvent = relatedIds.get(thisWiretapsIndex - 1);
        }
        if (thisWiretapsIndex < relatedIds.size() - 1)
        {
            nextEvent = relatedIds.get(thisWiretapsIndex + 1);
        }
        wiretapEvent.setNextByPayload(nextEvent);
        wiretapEvent.setPreviousByPayload(previousEvent);
        return wiretapEvent;
    }

    @SuppressWarnings("unchecked")
    public PagedWiretapSearchResult findPaging(final Set<String> moduleNames, final String componentName, final String eventId, final String payloadId,
            final Date fromDate, final Date untilDate, final String payloadContent, final int maxResults, final int firstResult) throws DataAccessException
    {
        return (PagedWiretapSearchResult) getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(WiretapEvent.class, "event");
                // if (restrictionExists(moduleName))
                // {
                criteria.add(Restrictions.in("moduleName", moduleNames));
                // }
                // if (restrictionExists(flowName))
                // {
                // criteria.add(Restrictions.eq("flowName", flowName));
                // }
                if (restrictionExists(componentName))
                {
                    criteria.add(Restrictions.eq("componentName", componentName));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.eq("eventId", eventId));
                }
                if (restrictionExists(payloadId))
                {
                    criteria.add(Restrictions.eq("payloadId", payloadId));
                }
                if (restrictionExists(payloadContent))
                {
                    criteria.add(Restrictions.like("payloadContent", payloadContent, MatchMode.ANYWHERE));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.gt("created", fromDate));
                }
                if (restrictionExists(untilDate))
                {
                    criteria.add(Restrictions.lt("created", untilDate));
                }
                criteria.setMaxResults(maxResults);
                criteria.setFirstResult(firstResult);
                criteria.addOrder(Order.desc("id"));
                List<WiretapEventHeader> wiretapResults = criteria.list();
                criteria.setProjection(Projections.rowCount());
                Integer rowCount = 0;
                List<Integer> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                return new PagedWiretapSearchResult(wiretapResults, rowCount, firstResult);
            }
        });
    }

    /**
     * Check to see if the restriction exists
     * 
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static final boolean restrictionExists(Object restrictionValue)
    {
        return (restrictionValue != null && !"".equals(restrictionValue));
    }

    public void deleteAllExpired()
    {
        getHibernateTemplate().bulkUpdate(HOUSEKEEP_QUERY, new Date());
    }
}
