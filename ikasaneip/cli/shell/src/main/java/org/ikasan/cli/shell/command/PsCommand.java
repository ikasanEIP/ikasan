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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Check running processes command.
 *
 * @author Ikasan Development Team
 */
@ShellComponent
public class PsCommand extends AbstractCommand
{
    @Value("${module.name:null}")
    String moduleName;

    @ShellMethod(value = "Check running process. Syntax: ps <process name> | -name <process name>", group = "Ikasan Commands", key = "ps")
    public String ps(@ShellOption(value = "-name", defaultValue = "")  String optionalModuleName)
    {
        StringBuilder sb = new StringBuilder();
        if(optionalModuleName != null && !optionalModuleName.isEmpty())
        {
            this.moduleName = optionalModuleName;
        }

        if (operation.isRunning(ProcessType.H2, moduleName))
        {
            sb.append(ProcessType.H2.name() + " [" + moduleName + "] running\n");
        } else
        {
            sb.append(ProcessType.H2.name() + " [" + moduleName + "] not running\n");
        }

        if(operation.isRunning(ProcessType.MODULE, moduleName))
        {
            sb.append( ProcessType.MODULE.name() + " [" + moduleName + "] running\n" );
        }
        else
        {
            sb.append( ProcessType.MODULE.name() + " [" + moduleName + "] not running\n" );
        }

        return sb.toString();
    }

}