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
import org.ikasan.cli.shell.reporting.ProcessInfos;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

/**
 * Check running processes command.
 *
 * @author Ikasan Development Team
 */
@Command
public class PsCommand extends AbstractCommand
{
    @Value("${module.name:null}")
    String moduleName;

    @Command(description = "Check running process. Syntax: ps [process name] | [-name <process name>] [-user <user name>]", group = "Ikasan Commands", command = "ps")
    public String ps(@Option(longNames = "name", defaultValue = "")  String optionalModuleName,
                     @Option(longNames = "user", defaultValue = "")  String optionalUsername)
    {
       return _ps(optionalModuleName, optionalUsername).toString();
    }

    JSONObject _ps(String optionalModuleName, String optionalUsername)
    {
        String _moduleName = moduleName;
        String _username = username;

        // allow override of default module but with exact match
        if(optionalModuleName != null && !optionalModuleName.isEmpty())
        {
            _moduleName = optionalModuleName;
        }

        if(optionalUsername != null && !optionalUsername.isEmpty())
        {
            _username = optionalUsername;
        }

        ProcessInfos processInfos = getProcessInfos( operation.getProcessHandles(ProcessType.H2, _moduleName, _username), ProcessType.H2 );
        processInfos.add( getProcessInfos( operation.getProcessHandles(ProcessType.MODULE, _moduleName, _username), ProcessType.MODULE ) );
        return processInfos.toJSON();
    }

    /**
     * Create responses based on running processes
     * @param processHandles
     * @param processType
     * @return
     */
    protected ProcessInfos getProcessInfos(List<ProcessHandle> processHandles, ProcessType processType)
    {
        ProcessInfos processInfos = new ProcessInfos();

        for(ProcessHandle processHandle:processHandles)
        {
            ProcessInfo processInfo = ProcessUtils.createProcessInfo()
                .setPsOperation()
                .setUsername(this.username)
                .setName(this.moduleName)
                .setPid(processHandle.pid())
                .setProcessType(processType)
                .setRunning( processHandle.isAlive() );

            processInfos.add(processInfo);
        }

        return processInfos;
    }
}