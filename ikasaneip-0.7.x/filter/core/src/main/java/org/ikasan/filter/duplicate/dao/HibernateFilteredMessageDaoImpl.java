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

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of {@link FilteredMessageDao}
 * 
 * @author Summer
 *
 */
public class HibernateFilteredMessageDaoImpl extends HibernateDaoSupport implements FilteredMessageDao
{
    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#findMessageById(org.ikasan.filter.duplicate.model.FilterEntry)
     */
    @SuppressWarnings("unchecked")
    public FilterEntry findMessage(FilterEntry message)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(FilterEntry.class);
        criteria.add(Restrictions.eq(FilterEntry.CRITERIA_PROP_KEY, message.getCriteria()));
        criteria.add(Restrictions.eq(FilterEntry.CLIENT_ID_PROP_KEY, message.getClientId()));
        List<FilterEntry> foundMessages = this.getHibernateTemplate().findByCriteria(criteria);
        if (foundMessages == null || foundMessages.isEmpty())
        {
            return null;
        }
        else
        {
            return foundMessages.get(0);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#save(org.ikasan.filter.duplicate.model.FilterEntry)
     */
    public void save(FilterEntry message)
    {
        this.getHibernateTemplate().save(message);
    }

    public void deleteAll(List<FilterEntry> messages)
    {
        this.getHibernateTemplate().deleteAll(messages);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#findExpiredMessages()
     */
    @SuppressWarnings("unchecked")
    public List<FilterEntry> findExpiredMessages()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(FilterEntry.class);
        criteria.add(Restrictions.lt("expiry", new Date()));
        List<FilterEntry> foundMessages = this.getHibernateTemplate().findByCriteria(criteria);
        if (foundMessages == null || foundMessages.isEmpty())
        {
            return null;
        }
        else
        {
            return foundMessages;
        }
    }

}
