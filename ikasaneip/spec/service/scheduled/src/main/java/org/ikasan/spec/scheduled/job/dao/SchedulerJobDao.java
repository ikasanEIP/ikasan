package org.ikasan.spec.scheduled.job.dao;

import org.ikasan.spec.scheduled.job.model.SchedulerJobRecord;
import org.ikasan.spec.scheduled.job.model.SchedulerJobSearchFilter;
import org.ikasan.spec.search.SearchResults;

public interface SchedulerJobDao<T extends SchedulerJobRecord> {

    SearchResults<? extends T> findAll(int limit, int offset);

    SearchResults<? extends T> findByContext(String contextId, int limit, int offset);

    SearchResults<? extends T> findByAgent(String agent, int limit, int offset);

    SearchResults<? extends T> findByFilter(SchedulerJobSearchFilter filter, int limit, int offset, String sortColumn, String sortDirection);

    T findById(String id);

    T findByContextIdAndJobName(String contextId, String jobName);

    void delete(T record);

    void deleteByContextName(String contextName);

    void deleteByAgentName(String agentName);

    void save(T record);
}
