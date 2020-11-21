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
import java.io.IOException;

/**
 * Abstract action command supporting standard start|stop process.
 *
 * @author Ikasan Developmnent Team
 */
public abstract class ActionCommands extends AbstractCommand
{
    /**
     * Start process.
     *
     * @param processType
     * @param name
     * @param command
     * @return
     */
    String start(ProcessType processType, String name, String command)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            if (operation.isRunning(processType, name))
            {
                sb.append(processType.toString() + " " + name + " already running\n");
            }
            else
            {
                Process process = operation.start(processType, ProcessUtils.getCommands(command, name), name);
                sb.append( ProcessUtils.getProcessInfo(processType.toString(), process, name) );
            }
        }
        catch (IOException e)
        {
            sb.append(processType + " process failed to start for [" + name + "] " + e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Stop process.
     *
     * @param processType
     * @param name
     * @return
     */
    String stop(ProcessType processType, String name)
    {
        try
        {
            if (!operation.isRunning(processType, name))
            {
                return processType.toString() + " process for [" + name + "] is already stopped!";
            }

            operation.stop(processType, name);
            return processType.toString() + " process stopped";
        }
        catch (IOException e)
        {
            return "Problem checking process [" + name + "] " + e.getMessage();
        }
    }
}