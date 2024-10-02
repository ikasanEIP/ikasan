package org.ikasan.ootb.scheduled.processtracker.dao;

import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerKryoProcessPersistenceImplTest {
    private SchedulerProcessPersistenceDao schedulerKryoProcessPersistence;
    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        schedulerKryoProcessPersistence = new SchedulerKryoProcessPersistenceImpl(tempDir.toString());
    }

    @Test
    void successful_save_find_delete()
    {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess("type", "name", 12345
            , "user", "outdir", "errdir", 1L);
        schedulerKryoProcessPersistence.save(schedulerIkasanProcess);

        SchedulerIkasanProcess schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find("type", "name");
        assertThat(schedulerIkasanProcess1).isEqualTo(schedulerIkasanProcess);

        schedulerKryoProcessPersistence.delete("type", "name");
        schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find("type", "name");
        assertThat(schedulerIkasanProcess1).isNull();
    }

    @Test
    void successful_save_find_by_pid_delete()
    {
        SchedulerIkasanProcess schedulerIkasanProcess = new SchedulerIkasanProcess("type", "name", 12345
            , "user", "outdir", "errdir", 1L);
        schedulerKryoProcessPersistence.save(schedulerIkasanProcess);

        SchedulerIkasanProcess schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find(12345);
        assertThat(schedulerIkasanProcess1).isEqualTo(schedulerIkasanProcess);

        schedulerKryoProcessPersistence.delete("type", "name");
        schedulerIkasanProcess1 = schedulerKryoProcessPersistence.find(12345);
        assertThat(schedulerIkasanProcess1).isNull();
    }
}