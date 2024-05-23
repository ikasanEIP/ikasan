package org.ikasan.ootb.scheduler.agent.module.service.processtracker.service;

import org.ikasan.cli.shell.operation.service.DefaultPersistenceServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.ProcessStatusDao;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao.SchedulerProcessPersistenceDao;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.model.SchedulerIkasanProcess;

import java.io.IOException;
import java.util.Optional;

import static org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder.SCHEDULER_PROCESS_TYPE;

public class SchedulerDefaultPersistenceServiceImpl extends DefaultPersistenceServiceImpl implements SchedulerPersistenceService {
    SchedulerProcessPersistenceDao schedulerProcessPersistenceDao;
    ProcessStatusDao processStatusDao;

    public SchedulerDefaultPersistenceServiceImpl(SchedulerProcessPersistenceDao schedulerProcessPersistenceDao, ProcessStatusDao processStatusDao) {
        super(schedulerProcessPersistenceDao);
        this.schedulerProcessPersistenceDao = schedulerProcessPersistenceDao;
        this.processStatusDao = processStatusDao;
    }

    @Override
    public void persist(String type, String name, Process process, String resultOutput, String errorOutput, long fireTime)
    {
        Optional<String> user = process.info().user();
        processPersistenceDao.save(
            new SchedulerIkasanProcess(
                type,
                name,
                process.pid(),
                user.orElse(null),
                resultOutput,
                errorOutput,
                fireTime)
        );
    }

    @Override
    public void persist(String type, String name, ProcessHandle processHandle, String resultOutput, String errorOutput, long fireTime)
    {
        Optional<String> user = processHandle.info().user();
        processPersistenceDao.save(
            new SchedulerIkasanProcess(type, name, processHandle.pid(),
            user.orElse(null),
            resultOutput,
            errorOutput,
            fireTime)
        );
    }

    @Override
    public ProcessHandle find(String type, String name)
    {
        SchedulerIkasanProcess ikasanProcess = schedulerProcessPersistenceDao.find(type, name);
        if(ikasanProcess != null)
        {
            Optional<ProcessHandle> processHandle = ProcessHandle.of(ikasanProcess.getPid());
            return processHandle.orElse(null);
        }

        return null;
    }

    @Override
    public String getPersistedReturnCode(String processIdentity) {
        return processStatusDao.getPersistedReturnCode(processIdentity);
    }

    @Override
    public void removeAll(String processIdentity, String scriptPostfix) throws IOException {
        remove(SCHEDULER_PROCESS_TYPE, processIdentity);
        processStatusDao.removeScriptAndResult(processIdentity, scriptPostfix);
    }

    @Override
    public String getResultAbsoluteFilePath(String processIdentity) {
        return processStatusDao.getResultAbsoluteFilePath(processIdentity);
    }

    @Override
    public String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException {
        return processStatusDao.createCommandScript(processIdentity, scriptPostfix, commandsToBeExecuted);
    }

    @Override
    public String createCommandWrapperScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException {
        return processStatusDao.createCommandWrapperScript(processIdentity, scriptPostfix, commandsToBeExecuted);
    }

    public String getScriptFilePath(String processIdentity, String scriptPostfix) {
        return processStatusDao.getScriptFilePath(processIdentity,scriptPostfix);
    }
}
