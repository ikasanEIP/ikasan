package org.ikasan.ootb.scheduled.processtracker.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import org.ikasan.cli.shell.operation.dao.KryoProcessPersistenceImpl;
import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

        File file = new File(path);
        if(!file.exists()) {
            LOGGER.info("File [" + path + "] not found. Returning null.");
            return null;
        }
        else if(file.isDirectory()) {
            LOGGER.info("File [" + path + "] is a directory and will be ignored. Returning null.");
            return null;
        }

        try (Input input = new Input(new FileInputStream(file)))
        {
            return (SchedulerIkasanProcess)kryo.readClassAndObject(input);
        }
        catch(KryoException e)
        {
            LOGGER.info("A Kryo error has occurred reading file [" + path + "]" +
                ". It is likely that this process was not a SchedulerIkasanProcess. Moving onto next file.");
        }
        catch(FileNotFoundException e)
        {
            LOGGER.info("File [" + path + "] not found. Will move on and try the next file.");
        }

        return null;
    }

    @Override
    public SchedulerIkasanProcess find(long pid) {
        Kryo kryo = kryoThreadLocal.get();

        File pidDir = new File(getPidBaseDir());

        for(File pidFile: pidDir.listFiles()) {

            if(!pidFile.exists()) {
                LOGGER.info("File [" + pidFile.getAbsolutePath() + "] not found. Will move on and try the next file.");
                continue;
            }
            else if(pidFile.isDirectory()) {
                LOGGER.info("File [" + pidFile.getAbsolutePath() + "] is a directory and will be ignored. Will move on and try the next file.");
                continue;
            }

            try (Input input = new Input(new FileInputStream(pidFile)))
            {
                 SchedulerIkasanProcess process = (SchedulerIkasanProcess)kryo.readClassAndObject(input);
                 if(process.getPid() == pid) return process;
            }
            catch(KryoException e)
            {
                LOGGER.info("A Kryo error has occurred reading file [" + pidFile.getAbsolutePath() + "]" +
                    ". It is likely that this process was not a SchedulerIkasanProcess. Moving onto next file.");
            }
            catch(FileNotFoundException e)
            {
                LOGGER.info("File [" + pidFile.getAbsolutePath() + "] not found. Will move on and try the next file.");
            }
        }

        return null;
    }
}
