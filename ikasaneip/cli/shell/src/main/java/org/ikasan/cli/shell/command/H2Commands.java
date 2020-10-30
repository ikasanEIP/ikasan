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

import org.ikasan.cli.shell.operation.Operation;
import org.ikasan.cli.shell.operation.model.ProcessType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class H2Commands
{
    @Value("${ikasan.version}")
    String processName;

    //String processName = "replaceModuleName";

    Operation operation = Operation.getInstance();

    @ShellMethod(value = "start h2", group = "Ikasan Commands", key = "start-h2")
    public String starth2(@ShellOption(value = "-name", defaultValue = "")  String optionalProcessName)
    {
        StringBuilder sb = new StringBuilder();
        if(optionalProcessName != null && !optionalProcessName.isEmpty())
        {
           this.processName = optionalProcessName;
        }

        try
        {
            if (operation.isRunning(ProcessType.H2, processName))
            {
                sb.append(ProcessType.H2.name() + " already running for [" + processName + "]\n");
            }
            else
            {
                Process proc = operation.start(ProcessType.H2, processName);
                sb.append(ProcessType.H2.name() + " process started [" + proc.info().command().get() + "]\n");
            }
        }
        catch (IOException e)
        {
            sb.append(ProcessType.H2.name() + " process failed to start for [" + processName + "]\n");
            return sb.toString();
        }
        return sb.toString();
    }

    @ShellMethod(value = "Stop H2 persistence.", group = "Ikasan Commands", key = "stop-h2")
    public String stoph2(@ShellOption(arity=1, defaultValue="") String optionalProcessName)
    {
        StringBuilder sb = new StringBuilder();
        if(optionalProcessName != null && !optionalProcessName.isEmpty())
        {
            this.processName = optionalProcessName;
        }

        try
        {
            if (!operation.isRunning(ProcessType.H2, optionalProcessName))
            {
                return "H2 process for [" + optionalProcessName + "] is already stopped!";
            }

            operation.stop(ProcessType.H2, optionalProcessName);
            return "H2 process stopped";
        }
        catch (IOException e)
        {
            return "Problem checking if H2 process is not running";
        }
    }
}