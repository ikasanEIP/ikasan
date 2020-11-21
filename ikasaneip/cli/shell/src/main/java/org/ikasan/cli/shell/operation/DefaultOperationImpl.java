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
package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.operation.model.ProcessType;
import org.ikasan.cli.shell.operation.service.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default implementation of the Operation contract.
 *
 * @author Ikasan Development Team
 */
public class DefaultOperationImpl implements Operation
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(DefaultOperationImpl.class);

    PersistenceService persistenceService;

    /**
     * Constructor
     * @param persistenceService
     */
    public DefaultOperationImpl(PersistenceService persistenceService)
    {
        this.persistenceService = persistenceService;
        if(persistenceService == null)
        {
            throw new IllegalArgumentException("persistenceService cannot be 'null'");
        }
    }

    public Process start(ProcessType processType, List<String> commands, String name) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // persist the process
        try
        {
            if(processType.isPersist())
            {
                persistenceService.persist(processType.toString(), name, process);
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }

        return process;
    }

    protected ProcessHandle getProcess(String type, String name)
    {
        // first check if we have the process persisted
        ProcessHandle processHandle = persistenceService.find(type, name);
        if (processHandle != null)
        {
            return processHandle;
        }

        // not persisted, try checking if we can see a process running for that module of that type for that user
        Stream<ProcessHandle> processStream = ProcessHandle.allProcesses();
        long val = processStream.count();
//        for(ProcessHandle ph: processStream.sequential())
//        {
//
//        }
//
//        List myList = new ArrayList();
//        String s;
//        processStream.forEach(processInstance -> ( (s = printInfo(processInstance)) != null) ? myList.add(s) );

        // not running as that user, try checking if we can see a process running for that module

        // does look like its running
        return null;
    }

    public boolean isRunning(ProcessType processType, String name)
    {
        ProcessHandle processHandle = getProcessHandle(processType, name);
        if(processHandle != null && processHandle.isAlive())
        {
            return true;
        }

        return false;
    }

    public ProcessHandle getProcessHandle(ProcessType processType, String name)
    {
        return getProcess(processType.name(), name);
    }

    @Override
    public void stop(ProcessType processType, String name) throws IOException
    {
        ProcessHandle processHandle = getProcess(processType.name(), name);
        if(processHandle != null && processHandle.isAlive())
        {
            processHandle.destroy();
        }

        // remove persistence
        persistenceService.remove(processType.name(), name);
    }

    @Override
    public void kill(ProcessType processType, String name) throws IOException
    {
        ProcessHandle processHandle = getProcess(processType.name(), name);
        if(processHandle.isAlive())
        {
            processHandle.destroyForcibly();
        }
    }

    public String printInfo(ProcessHandle processHandle)
    {
        Optional<String> user = processHandle.info().user();
        if(!user.isEmpty())
        {
            user.get();
        }
        Optional<String[]> args = processHandle.info().arguments();
        if(!args.isEmpty())
        {
            args.get();
        }

        return "User " + user + " Args " + args;
    }

}