package org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.ikasan.cli.shell.operation.dao.KryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.model.SchedulerIkasanProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class SchedulerKryoProcessPersistenceImpl extends KryoProcessPersistenceImpl implements SchedulerProcessPersistenceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerKryoProcessPersistenceImpl.class);

    public SchedulerKryoProcessPersistenceImpl(String persistenceDir) {
        super(persistenceDir);
    }

    /**
     * Thread local instance of Kyro instance.
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial( () -> {
        Kryo kryo = new Kryo();
        kryo.register(SchedulerIkasanProcess.class);
        kryo.register(org.ikasan.cli.shell.operation.model.ProcessType.class);
        return kryo;
    });

    @Override
    public SchedulerIkasanProcess find(String type, String name)
    {
        Kryo kryo = kryoThreadLocal.get();
        String path = getPidFQN(type, name);

        try (Input input = new Input(new FileInputStream(path)))
        {
            return (SchedulerIkasanProcess)kryo.readClassAndObject(input);
        }
        catch(FileNotFoundException e)
        {
            LOGGER.debug("File [" + path + "] not found", e);
            return null;
        }
    }

    @Override
    public SchedulerIkasanProcess find(long pid) {
        Kryo kryo = kryoThreadLocal.get();

        File pidDir = new File(getPidBaseDir());

        for(File pidFile: pidDir.listFiles()) {
            try (Input input = new Input(new FileInputStream(pidFile)))
            {
                 SchedulerIkasanProcess process = (SchedulerIkasanProcess)kryo.readClassAndObject(input);
                 if(process.getPid() == pid) return process;
            }
            catch(Exception e)
            {
                LOGGER.info("File [" + pidFile.getAbsolutePath() + "] not found", e);
            }
        }

        return null;
    }
}
