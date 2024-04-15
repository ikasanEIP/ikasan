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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

/**
 * Process commands for start, query, stop of the H2 process.
 *
 * @author Ikasan Developmnent Team
 */
@Command
public class H2Command extends ActionCommand
{
    @Value("${module.name:null}")
    String moduleName;

    @Value("${h2.java.command:null}")
    String h2JavaCommand;

    @Value("${h2.logging.file:logs/h2.log}")
    String h2Log;

    /**
     * Start H2 process.
     * @param altModuleName
     * @param altCommand
     * @return
     */
    @Command(description = "Start H2 persistence JVM", group = "Ikasan Commands", command = "start-h2")
    public String starth2(@Option(longNames = "name", defaultValue = "")  String altModuleName,
                          @Option(longNames = "command", defaultValue = "")  String altCommand)
    {
        return this._starth2(altModuleName, altCommand).toString();
    }

    JSONObject _starth2(String altModuleName, String altCommand)
    {
        String name = moduleName;
        if(altModuleName != null && !altModuleName.isEmpty())
        {
            name = altModuleName;
        }

        String command = h2JavaCommand;
        if(altCommand != null && !altCommand.isEmpty())
        {
            command = altCommand;
        }

        if(this.h2Log != null)
        {
            this.processType.setOutputLog(this.h2Log);
            this.processType.setErrorLog(this.h2Log);
        }

        return this.start(processType, name, command, -1);
    }

    public ProcessType getProcessType()
    {
        return ProcessType.getH2Instance();
    }

    /**
     * Stop H2 process.
     *
     * @param altModuleName
     * @return
     */
    @Command(description = "Stop H2 persistence JVM", group = "Ikasan Commands", command = "stop-h2")
    public String stoph2(@Option(longNames = "name", defaultValue="") String altModuleName)
    {
        return _stoph2(altModuleName).toString();
    }

    JSONObject _stoph2(String altModuleName)
    {
        String name = moduleName;
        if(altModuleName != null && !altModuleName.isEmpty())
        {
            name = altModuleName;
        }

        return this.stop(this.processType, name, username);
    }
}