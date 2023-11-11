package org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao;

import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.SchedulerKryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.SchedulerProcessPersistenceDao;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.model.SchedulerIkasanProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerKryoProcessPersistenceImplTest {
    private SchedulerProcessPersistenceDao schedulerKryoProcessPersistence;
    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        schedulerKryoProcessPersistence = new SchedulerKryoProcessPersistenceImpl(tempDir.toString());
    }

    @Test
    void successful_save_find_delete() throws IOException
    {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess("type", "name", 12345, "user", "outdir", "errdir");
        schedulerKryoProcessPersistence.save(schedulerIkasanProcess);

        SchedulerIkasanProcess schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find("type", "name");
        assertThat(schedulerIkasanProcess1).isEqualTo(schedulerIkasanProcess);

        schedulerKryoProcessPersistence.delete("type", "name");
        schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find("type", "name");
        assertThat(schedulerIkasanProcess1).isNull();
    }
}