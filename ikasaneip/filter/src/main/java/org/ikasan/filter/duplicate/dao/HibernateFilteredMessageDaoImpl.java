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

import java.util.List;

import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of {@link FilteredMessageDao}
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateFilteredMessageDaoImpl extends HibernateDaoSupport implements FilteredMessageDao
{
    private static final String EXPIRY = "expiry";

    /** Query used for housekeeping expired filtered messages */
    private static final String HOUSEKEEP_QUERY = "delete DefaultFilterEntry m where m.expiry <= :" + EXPIRY;

    /** Flag for batch housekeeping option. Defaults to true */
    private boolean batchHousekeepDelete = true;

    /** The batch size used when {@link #batchHousekeepDelete} option is set. Default to 100*/
    private int housekeepingBatchSize = 100;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 1000;

    private String housekeepQuery;


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
        List<FilterEntry> foundMessages = (List<FilterEntry>) this.getHibernateTemplate().findByCriteria(criteria);
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
        if (!this.batchHousekeepDelete)
        {
            getHibernateTemplate().execute(new HibernateCallback<Object>()
            {
                public Object doInHibernate(Session session) throws HibernateException
                {

                    Query query = session.createQuery(HOUSEKEEP_QUERY);
                    query.setParameter(EXPIRY, System.currentTimeMillis());
                    query.executeUpdate();
                    return null;
                }
            });
        }
        else
        {
            this.batchDeleteAllExpired();
        }
    }

    /**
     * Delete expired messages 100 at a time until non is left
     */
    protected void batchDeleteAllExpired()
    {
        logger.info("MessageFilter batch delete.");

        int numDeleted = 0;

        while (housekeepablesExist() && numDeleted < this.transactionBatchSize)
        {
            getHibernateTemplate().execute(new HibernateCallback<Object>()
            {
                public Object doInHibernate(Session session) throws HibernateException{


                    String formattedQuery = housekeepQuery.replace("_bs_", String.valueOf(housekeepingBatchSize))
                            .replace("_ex_", String.valueOf(System.currentTimeMillis()));

                    SQLQuery query = session.createSQLQuery(formattedQuery);
                    query.executeUpdate();

                    return null;
                }
            });

            numDeleted += housekeepingBatchSize;
        }
    }

    /**
     * Find expired entries
     * @return List of max 100 expired filter entries
     */
    @SuppressWarnings("unchecked")
    public List<FilterEntry> findExpiredMessages()
    {
        List<FilterEntry> foundMessages = (List<FilterEntry>) this.getHibernateTemplate().execute( new HibernateCallback<Object>()
        {

            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(FilterEntry.class);
                criteria.add(Restrictions.lt(FilterEntry.EXPRIY_PROP_KEY, System.currentTimeMillis()));
                criteria.setMaxResults(housekeepingBatchSize);
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

    /**
     * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
     *
     * @return true if there is at least 1 expired WiretapFlowEvent
     */
    public boolean housekeepablesExist()
    {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria criteria = session.createCriteria(FilterEntry.class);
                criteria.add(Restrictions.lt("expiry", System.currentTimeMillis()));
                criteria.setProjection(Projections.rowCount());
                Long rowCount = 0L;
                List<Long> rowCountList = criteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }
                logger.info(rowCount+", MessageFilter housekeepables exist");
                return new Boolean(rowCount>0);
            }
        });
    }

    /* (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.FilteredMessageDao#setTransactionBatchSize(int)
     */
    @Override
    public void setTransactionBatchSize(int transactionBatchSize)
    {
        this.transactionBatchSize = transactionBatchSize;
    }

    @Override
    public void setHousekeepQuery(String housekeepQuery)
    {
        this.housekeepQuery = housekeepQuery;
    }

    /**
     * Setter for {@link #batchHousekeepDelete} flag for overriding default
     * value
     * @param batchHousekeepDelete
     */
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    /**
     * Setter for {@link #housekeepingBatchSize} for overriding default value
     * @param housekeepingBatchSize
     */
    public void setHousekeepingBatchSize(int housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }

}