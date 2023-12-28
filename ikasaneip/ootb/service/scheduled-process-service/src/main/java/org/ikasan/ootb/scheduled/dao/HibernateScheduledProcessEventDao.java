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
package org.ikasan.ootb.scheduled.dao;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.ikasan.ootb.scheduled.model.ScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.event.dao.ScheduledProcessEventDao;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Scheduled Process Event DAO based on Hibernate persistence.
 *
 * @author Ikasan Development Team
 * 
 */
public class HibernateScheduledProcessEventDao implements ScheduledProcessEventDao
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HibernateScheduledProcessEventDao.class);

    public static final String EVENT_IDS = "eventIds";
    public static final String NOW = "now";

    public static final String UPDATE_HARVESTED_QUERY = "update ScheduledProcessEventImpl w set w.harvestedDateTime = :"
        + NOW + ", w.harvested = true" + " where w.id in(:" + EVENT_IDS + ")";

    @PersistenceContext(unitName = "scheduled-process")
    private EntityManager entityManager;

    /**
     * Constructor
     */
    public HibernateScheduledProcessEventDao() {
        super();
    }

    @Override
    public void save(ScheduledProcessEvent scheduledProcessEvent) {
        this.entityManager.persist(scheduledProcessEvent);
    }

    @Override
    public List<ScheduledProcessEvent> harvest(int housekeepingBatchSize) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<ScheduledProcessEventImpl> criteriaQuery = builder.createQuery(ScheduledProcessEventImpl.class);
        Root<ScheduledProcessEventImpl> root = criteriaQuery.from(ScheduledProcessEventImpl.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("harvestedDateTime"), 0));

        TypedQuery<ScheduledProcessEventImpl> query = this.entityManager.createQuery(criteriaQuery);
        query.setMaxResults(housekeepingBatchSize);
        return query.getResultList().stream()
            .map(scheduledProcessEvent -> (ScheduledProcessEvent)scheduledProcessEvent)
            .collect(Collectors.toList());
    }

    @Override
    public boolean harvestableRecordsExist() {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<ScheduledProcessEventImpl> root = criteriaQuery.from(ScheduledProcessEventImpl.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("harvestedDateTime"),0));

        criteriaQuery.select(builder.count(root))
            .where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Long> query = this.entityManager.createQuery(criteriaQuery);
        List<Long> rowCountList = query.getResultList();

        Long rowCount = 0L;
        if (!rowCountList.isEmpty())
        {
            rowCount = rowCountList.get(0);
        }

        logger.debug(rowCount + ", FlowInvocation harvestable records exist");
        return rowCount>0;
    }

    @Override
    public void saveHarvestedRecord(ScheduledProcessEvent harvestedRecord) {
        this.save(harvestedRecord);
    }

    @Override
    public void updateAsHarvested(List<ScheduledProcessEvent> events) {
        List<Long> scheduledProcessEventIds = new ArrayList();

        for(ScheduledProcessEvent event: events)
        {
            scheduledProcessEventIds.add(((ScheduledProcessEventImpl)event).getId());
        }

        List<List<Long>> partitionedIds = Lists.partition(scheduledProcessEventIds, 300);

        for(List<Long> eventIds: partitionedIds)
        {
            Query query = this.entityManager.createQuery(UPDATE_HARVESTED_QUERY);
            query.setParameter(NOW, System.currentTimeMillis());
            query.setParameter(EVENT_IDS, eventIds);
            query.executeUpdate();
        }
    }

    @Override
    public void housekeep() {
        Query query = this.entityManager.createQuery("delete ScheduledProcessEventImpl s where s.harvested = true");
        query.executeUpdate();
    }

    /**
     * Used in unit test.
     *
     * @return
     */
    protected List<ScheduledProcessEvent> findAll() {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<ScheduledProcessEventImpl> criteriaQuery = builder.createQuery(ScheduledProcessEventImpl.class);
        criteriaQuery.from(ScheduledProcessEventImpl.class);

        TypedQuery<ScheduledProcessEventImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(scheduledProcessEvent -> (ScheduledProcessEvent)scheduledProcessEvent)
            .collect(Collectors.toList());
    }
}