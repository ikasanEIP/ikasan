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

import org.ikasan.cli.shell.operation.DefaultOperationImpl;
import org.ikasan.cli.shell.operation.model.ProcessType;
import org.ikasan.cli.shell.reporting.ProcessInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Process commands for start, query, stop of the H2 process.
 *
 * @author Ikasan Developmnent Team
 */
@Command
public class SolrCommand extends ActionCommand
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(SolrCommand.class);

    @Value("${module.name:null}")
    String moduleName;

    @Value("${solr.java.start.command:null}")
    String solrStartJavaCommand;

    @Value("${solr.java.stop.command:null}")
    String solrStopJavaCommand;

    /**
     * Start H2 process.
     * @param altModuleName
     * @param altCommand
     * @return
     */
    @Command(description = "Start Solr", group = "Ikasan Commands", command = "start-solr")
    public String startSolr(@Option(longNames = "name", defaultValue = "")  String altModuleName,
                          @Option(longNames = "command", defaultValue = "")  String altCommand)
    {
        return this._startSolr(altModuleName, altCommand).toString();
    }

    JSONObject _startSolr(String altModuleName, String altCommand)
    {
        String name = moduleName;
        if(altModuleName != null && !altModuleName.isEmpty())
        {
            name = altModuleName;
        }

        String command = solrStartJavaCommand;
        if(altCommand != null && !altCommand.isEmpty())
        {
            command = altCommand;
        }

        return this.start(processType, name, command);
    }

    /**
     * Stop H2 process.
     *
     * @param altModuleName
     * @return
     */
    @Command(description = "Stop Solr", group = "Ikasan Commands", command = "stop-solr")
    public String stopSolr(@Option(longNames = "name", defaultValue="") String altModuleName)
    {
        return _stopSolr(altModuleName, null).toString();
    }

    JSONObject _stopSolr(String altModuleName, String altCommand)
    {
        String name = moduleName;
        if(altModuleName != null && !altModuleName.isEmpty())
        {
            name = altModuleName;
        }

        String command = solrStopJavaCommand;
        if(altCommand != null && !altCommand.isEmpty())
        {
            command = altCommand;
        }

        return this.stop(this.processType, name, username, command);
    }

    JSONObject stop(ProcessType processType, String name, String username, String command) {
        ProcessInfo processInfo = ProcessUtils.createProcessInfo()
            .setStopOperation()
            .setProcessType(processType)
            .setName(name)
            .setCommandLine(command)
            .setUsername(username);

        try
        {
            Process process = operation.start(processType, ProcessUtils.getCommands(command, name), name);
            processInfo.setProcess(process);

            CompletableFuture<Process> completableFuture = process.onExit();

            try {
                // Will wait for the timeout or throw a TimeoutException
                // if the timeout is exceeded.
                completableFuture.orTimeout(super.commandStopProcessWaitTimeoutSeconds, TimeUnit.SECONDS);
                logger.info("Solr process shutdown complete: " + completableFuture.get());
                processInfo.setRunning(false);
            }
            catch (ExecutionException | InterruptedException e) {
                logger.error("Error occurred while waiting for process shutdown", e);
                throw new RuntimeException(String.format("An error has occurred waiting for the Solr process to shutdown. " +
                    " This is likely to be a timout waiting for the process to end. The timeout is currently configured to " +
                    "[%s] seconds. This can be adjusted by setting command.stop.process.wait.timeout.seconds in the application" +
                    " properties.", super.commandStopProcessWaitTimeoutSeconds)
                    , e);
            }
        }
        catch (IOException e)
        {
            processInfo.setException(e);
        }

        return processInfo.toJSON();
    }

    public ProcessType getProcessType()
    {
        return ProcessType.getSolrInstance();
    }
}