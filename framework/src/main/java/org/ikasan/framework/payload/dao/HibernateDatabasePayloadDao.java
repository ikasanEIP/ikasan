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
    public void delete(DatabasePayload databaseFlowEvent)
    {
        getHibernateTemplate().delete(databaseFlowEvent);
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
    public void save(DatabasePayload databaseFlowEvent)
    {
        if (databaseFlowEvent.getId() == null)
        {
            getHibernateTemplate().save(databaseFlowEvent);
        }
        else
        {
            getHibernateTemplate().update(databaseFlowEvent);
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
