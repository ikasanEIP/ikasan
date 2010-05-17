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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of {@link FilteredMessageDao}
 * 
 * @author Summer
 *
 */
public class HibernateFilteredMessageDaoImpl extends HibernateDaoSupport implements FilteredMessageDao
{
    /** Query used for housekeeping expired filtered messages */
    private static final String HOUSEKEEP_QUERY = "delete DefaultFilterEntry m where m.expiry <= ?";

    /** Flag for batch housekeeping option. Defaults to false */
    private boolean batchedHousekeep = false;

    /** The batch size used when {@link #batchedHousekeep} option is set. Default to 100*/
    //TODO investigate an optimum value for batch size
    private int batchSize = 100;

    /**
     * Setter for {@link #batchedHousekeep} flag for overriding default
     * value
     * @param batchedHousekeep
     */
    public void setBatchedHousekeep(boolean batchedHousekeep)
    {
        this.batchedHousekeep = batchedHousekeep;
    }

    /**
     * Setter for {@link #batchSize} for overriding default value
     * @param batchSize
     */
    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

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

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.FilteredMessageDao#deleteAllExpired()
     */
    public void deleteAllExpired()
    {
        if (!this.batchedHousekeep)
        {
            this.getHibernateTemplate().bulkUpdate(HOUSEKEEP_QUERY, new Date());
        }
        else
        {
            this.batchDeleteAllExpired();
        }
    }

    /**
     * Delete expired messages 100 at a time until non is left
     */
    private void batchDeleteAllExpired()
    {
        List<FilterEntry> expired = this.findExpiredMessages();
        while(expired != null)
        {
            this.getHibernateTemplate().deleteAll(expired);
            expired = this.findExpiredMessages();
        }
    }

    /**
     * Find expired entries
     * @return List of max 100 expired filter entries 
     */
    @SuppressWarnings("unchecked")
    private List<FilterEntry> findExpiredMessages()
    {
        List<FilterEntry> foundMessages = (List<FilterEntry>) this.getHibernateTemplate().execute( new HibernateCallback()
        {
            
            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                Criteria criteria = session.createCriteria(FilterEntry.class);
                criteria.add(Restrictions.lt(FilterEntry.EXPRIY_PROP_KEY, new Date()));
                criteria.setMaxResults(batchSize);
                return criteria.list();
            }
        });

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
