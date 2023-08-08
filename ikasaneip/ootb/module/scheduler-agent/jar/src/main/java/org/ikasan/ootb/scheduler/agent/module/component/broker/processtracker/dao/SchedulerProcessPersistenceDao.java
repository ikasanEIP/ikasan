package org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.dao;

import org.ikasan.cli.shell.operation.dao.ProcessPersistenceDao;
import org.ikasan.ootb.scheduler.agent.module.component.broker.processtracker.model.SchedulerIkasanProcess;

public interface SchedulerProcessPersistenceDao extends ProcessPersistenceDao {
    SchedulerIkasanProcess find(String type, String name);
}
