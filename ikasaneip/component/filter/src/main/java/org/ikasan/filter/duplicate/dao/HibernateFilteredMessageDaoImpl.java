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

import org.hibernate.query.Query;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Hibernate implementation of {@link FilteredMessageDao}
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateFilteredMessageDaoImpl extends HibernateDaoSupport implements FilteredMessageDao
{
    public static final String EXPIRY = "expiry";
    public static final String EVENT_IDS = "eventIds";
    public static final String NOW = "now";

    /** Query used for housekeeping expired filtered messages */
    private static final String HOUSEKEEP_QUERY = "delete DefaultFilterEntry m where m.expiry <= :" + EXPIRY;

    public static final String MESSAGE_FILTER_ENTRIES_TO_DELETE_QUERY = "select id from DefaultFilterEntry se " +
            " where se.expiry < :" + NOW;

    public static final String MESSAGE_FILTER_ENTRIES_TO_SELECT_ONE_QUERY = "select se from DefaultFilterEntry se " +
        " where se.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY + " and se.criteria = :" +FilterEntry.CRITERIA_PROP_KEY;

    public static final String MESSAGE_FILTER_ENTRIES_TO_SELECT_BY_CLIENTID_QUERY = "select se from DefaultFilterEntry se " +
        " where se.clientId = :" + FilterEntry.CLIENT_ID_PROP_KEY ;

    public static final String MESSAGE_FILTER_ENTRIES_DELETE_QUERY = "delete DefaultFilterEntry se " +
            " where se.id in(:" + EVENT_IDS + ")";

    /** Flag for batch housekeeping option. Defaults to true */
    private boolean batchHousekeepDelete = true;

    /** The batch size used when {@link #batchHousekeepDelete} option is set. Default to 100*/
    private int housekeepingBatchSize = 100;

    /** Batch size used when in a single transaction */
    private Integer transactionBatchSize = 1000;

    private String housekeepQuery;


    @Override
    public List<FilterEntry> findMessages(String clientId)
    {
        return getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_SELECT_BY_CLIENTID_QUERY);
            query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  clientId);

            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result;
            }else{
                return null;
            }

        });

    }

    /*
         * (non-Javadoc)
         * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#findMessageById(org.ikasan.filter.duplicate.window.FilterEntry)
         */
    @SuppressWarnings("unchecked")
    public FilterEntry findMessage(FilterEntry message)
    {
        return getHibernateTemplate().execute((session) -> {

            Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_SELECT_ONE_QUERY);
            query.setParameter(FilterEntry.CRITERIA_PROP_KEY,  message.getCriteria());
            query.setParameter(FilterEntry.CLIENT_ID_PROP_KEY,  message.getClientId());

            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result.get(0);
            }else{
                return null;
            }

        });

    }

    @Override
    public void saveOrUpdate(FilterEntry message)
    {
        this.getHibernateTemplate().saveOrUpdate(message);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.dao.MessagePersistanceDao#save(org.ikasan.filter.duplicate.window.FilterEntry)
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
            getHibernateTemplate().execute(session -> {
                Query query = session.createQuery(HOUSEKEEP_QUERY);
                query.setParameter(EXPIRY, System.currentTimeMillis());
                query.executeUpdate();
                return null;
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

        int numberDeleted = 0;

        while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
        {
            numberDeleted += this.housekeepingBatchSize;
            getHibernateTemplate().execute(session -> {
                Query query = session.createQuery(MESSAGE_FILTER_ENTRIES_TO_DELETE_QUERY);
                query.setParameter(NOW, System.currentTimeMillis());
                query.setMaxResults(housekeepingBatchSize);

                List<Long> filterIds = (List<Long>) query.list();
                if (filterIds.size() > 0)
                {
                    query = session.createQuery(MESSAGE_FILTER_ENTRIES_DELETE_QUERY);
                    query.setParameterList(EVENT_IDS, filterIds);
                    query.executeUpdate();
                }

                return null;
            });
        }
    }

    /**
     * Find expired entries
     * @return List of max 100 expired filter entries
     */
    @SuppressWarnings("unchecked")
    public List<FilterEntry> findExpiredMessages()
    {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<FilterEntry> criteriaQuery = builder.createQuery(FilterEntry.class);
            Root<DefaultFilterEntry> root = criteriaQuery.from(DefaultFilterEntry.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.lessThan(root.get(FilterEntry.EXPRIY_PROP_KEY), System.currentTimeMillis()));
            criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
            Query<FilterEntry> query = session.createQuery(criteriaQuery);
            query.setMaxResults(housekeepingBatchSize);
            List<FilterEntry> result = query.getResultList();
            if (!result.isEmpty())
            {
                return result;
            }else{
                return null;
            }

        });
    }

    /**
     * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
     *
     * @return true if there is at least 1 expired WiretapFlowEvent
     */
    public boolean housekeepablesExist()
    {
        return getHibernateTemplate().execute((session) -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<DefaultFilterEntry> root = criteriaQuery.from(DefaultFilterEntry.class);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.lessThan(root.get(FilterEntry.EXPRIY_PROP_KEY), System.currentTimeMillis()));
            criteriaQuery.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));
            org.hibernate.query.Query<Long> query = session.createQuery(criteriaQuery);
            List<Long> rowCountList = query.getResultList();
            Long rowCount = new Long(0);
            if (!rowCountList.isEmpty())
            {
                rowCount = rowCountList.get(0);
            }
            logger.info(rowCount+", MessageFilter housekeepables exist");
            return new Boolean(rowCount > 0);
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