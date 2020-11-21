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
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.shell.*;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Provide a simple shell command to allow execution of native commands from the Ikasan Shell.
 *
 * @Ikasan Development Team
 */
@ShellComponent
public class ShellCommand extends AbstractCommand
{
    /**
     * Execute native comamnd line command.
     * @param commands
     * @return
     */
    @ShellMethod(value = "Execute any command line command. Syntax: ! ls -la", group = "Ikasan Commands", key = "!")
    public String exec(@ShellOption(optOut = true) List<String> commands)
    {
        StringBuilder sb = new StringBuilder();
        String processName = commands.get(0);

        try
        {
            Process process = operation.start(ProcessType.OTHER, commands, processName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append(ProcessUtils.LINE_SEPARATOR);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ( (line = errorReader.readLine()) != null)
            {
                sb.append(line);
                sb.append(ProcessUtils.LINE_SEPARATOR);
            }

            sb.append( ProcessUtils.getProcessInfo(process, processName));
        }
        catch (IOException e)
        {
            sb.append(e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Provides infinite arity on the args passed.
     *
     * FIXME - pipe on ps -ef | grep ...   seems to have issues
     * @return
     */
    @Bean
    public ParameterResolver commandParameterResolver()
    {
        return new ParameterResolver()
        {
            @Override
            public boolean supports(MethodParameter parameter)
            {
                return parameter.getParameterType().isAssignableFrom(List.class);
            }

            @Override
            public ValueResult resolve(MethodParameter methodParameter, List<String> words)
            {
                return new ValueResult(methodParameter, words);
            }

            @Override
            public Stream<ParameterDescription> describe(MethodParameter parameter)
            {
                return Stream.of(ParameterDescription.outOf(parameter));
            }

            @Override
            public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext context)
            {
                return Collections.emptyList();
            }
        };
    }
}