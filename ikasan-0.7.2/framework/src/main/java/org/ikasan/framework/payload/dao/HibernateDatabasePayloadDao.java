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
package org.ikasan.framework.payload.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.ikasan.framework.payload.model.DatabasePayload;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>DatabasePayloadDao</code>
 * 
 * @author Ikasan Development Team
 */
public class HibernateDatabasePayloadDao extends HibernateDaoSupport implements DatabasePayloadDao
{
    /** Id query for Unconsumed Database Payload */
    protected static final String DATABASE_PAYLOAD_ID_QUERY = "select d.id from DatabasePayload d where d.consumed = false";

    /** The maximum number of result objects to retrieve from database. Default value is 1. Values <=0 means no limit.*/
    private int maxResults = 1;

    /** Hibernate criteria for matching unconsumed Payloads */
    private DetachedCriteria unconsumedCriteria;

    /** Constructor */
    public HibernateDatabasePayloadDao()
    {
        super();
        unconsumedCriteria = DetachedCriteria.forClass(DatabasePayload.class);
        unconsumedCriteria.add(Restrictions.eq("consumed", false));
        unconsumedCriteria.addOrder(Property.forName("id").asc());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.payload.dao.DatabasePayloadDao#delete(org.ikasan.framework.payload.model.DatabasePayload)
     */
    public void delete(DatabasePayload databaseEvent)
    {
        getHibernateTemplate().delete(databaseEvent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.dao.DatabasePayloadDao#findUnconsumed()
     */
    @SuppressWarnings("unchecked")
    public List<DatabasePayload> findUnconsumed()
    {
        return getHibernateTemplate().findByCriteria(unconsumedCriteria, 0, this.maxResults);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.dao.DatabasePayloadDao#save(org.ikasan.framework.payload.model.DatabasePayload)
     */
    public void save(DatabasePayload databaseEvent)
    {
        if (databaseEvent.getId() == null)
        {
            getHibernateTemplate().save(databaseEvent);
        }
        else
        {
            getHibernateTemplate().update(databaseEvent);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.dao.DatabasePayloadDao#findUnconsumedIds()
     */
    @SuppressWarnings("unchecked")
    public List<Long> findUnconsumedIds()
    {
        List<Long> contentIds = getHibernateTemplate().find(DATABASE_PAYLOAD_ID_QUERY);
        return contentIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.payload.dao.DatabasePayloadDao#getDatabasePayload(java.lang.Long)
     */
    public DatabasePayload getDatabasePayload(Long id)
    {
        return (DatabasePayload) getHibernateTemplate().get(DatabasePayload.class, id);
    }

    /**
     * Set the maxim of result objects to retrieve from db
     * @param maxResults integer value of max results
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }
}
