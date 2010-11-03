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
 * @author Ikasan Development Team
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
