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
package org.ikasan.cli.shell.command;

import org.ikasan.cli.shell.operation.model.ProcessType;
import org.ikasan.cli.shell.reporting.ProcessInfo;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Abstract action command supporting standard start|stop process.
 *
 * @author Ikasan Developmnent Team
 */
public abstract class ActionCommand extends AbstractCommand
{
    ProcessType processType = getProcessType();

    /**
     * Start process.
     *
     * @param processType
     * @param name
     * @param command
     * @return
     */
    JSONObject start(ProcessType processType, String name, String command)
    {
        ProcessInfo processInfo = ProcessUtils.createProcessInfo()
            .setStartOperation()
            .setProcessType(processType)
            .setName(name)
            .setCommandLine(command)
            .setUsername(username);

        try
        {
            List<ProcessHandle> processHandles = operation.getProcessHandles(processType, name, username);
            if(processHandles != null && processHandles.size() > 0)
            {
                processInfo.setRunning(true);
            }
            else
            {
                Process process = operation.start(processType, ProcessUtils.getCommands(command, name), name);
                processInfo.setProcess(process);
            }
        }
        catch (IOException e)
        {
            processInfo.setException(e);
        }

        return processInfo.toJSON();
    }

    /**
     * Stop process.
     *
     * @param processType
     * @param name
     * @param username
     * @return
     */
    JSONObject stop(ProcessType processType, String name, String username)
    {
        ProcessInfo processInfo = ProcessUtils.createProcessInfo()
            .setStopOperation()
            .setProcessType(processType)
            .setName(name)
            .setUsername(username);

        try
        {
            List<ProcessHandle> processHandles = operation.getProcessHandles(processType, name, username);
            if(processHandles == null || processHandles.size() == 0)
            {
                processInfo.setRunning(false);
            }

            operation.stop(processType, name, username);
            processInfo.setRunning(false);
        }
        catch (IOException e)
        {
            processInfo.setException(e);
        }

        return processInfo.toJSON();
    }

    public abstract ProcessType getProcessType();

}