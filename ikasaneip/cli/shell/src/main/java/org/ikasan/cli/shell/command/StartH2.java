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
import org.ikasan.cli.shell.operation.ProcessType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class StartH2
{
//    @Value("${ikasan.version}")
//    String ikasanVersion;
    Operation operation = Operation.getInstance();

    @ShellMethod(value = "Start H2 persistence.", group = "Ikasan Commands", key = "start-h2")
    public String startH2(@ShellOption(arity=1, defaultValue="") String optionalModuleName)
    {
        try
        {
            if(operation.isRunning(ProcessType.H2, optionalModuleName))
            {
                return "H2 process for [" + optionalModuleName + "] already running!";
            }

            Process process = operation.start(ProcessType.H2, optionalModuleName);
            return "H2 process started [" + process.info().command().get() + "]";
        }
        catch(IOException e)
        {
            return "H2 process failed to start!";
        }
    }

    @ShellMethod(value = "Check H2 persistence.", group = "Ikasan Commands", key = "check-h2")
    public String checkH2(@ShellOption(arity=1, defaultValue="") String optionalModuleName)
    {
        if(operation.isRunning(ProcessType.H2, optionalModuleName))
        {
            return "H2 process for [" + optionalModuleName + "] is running!";
        }

        return "H2 process is not running";
    }

    @ShellMethod(value = "Stop H2 persistence.", group = "Ikasan Commands", key = "stop-h2")
    public String stopH2(@ShellOption(arity=1, defaultValue="") String optionalModuleName)
    {
        try
        {
            if (!operation.isRunning(ProcessType.H2, optionalModuleName))
            {
                return "H2 process for [" + optionalModuleName + "] is already stopped!";
            }

            operation.stop(ProcessType.H2, optionalModuleName);
            return "H2 process is not running";
        }
        catch (IOException e)
        {
            return "Problem checking if H2 process is not running";
        }
    }
}