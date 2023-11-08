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
package org.ikasan.cli.shell.reporting;

import org.ikasan.cli.shell.operation.model.ProcessType;
import org.json.JSONObject;

import java.util.Optional;

/**
 * ProcessInfo model.
 *
 * @author Ikasan Development Team
 */
public class ProcessInfo
{
    private static String START = "start";
    private static String STOP = "stop";
    private static String PS = "ps";

    Long pid;
    String operation;
    ProcessType processType;
    String name;
    String username;
    boolean running;
    String command;
    String commandLine;
    Process process;
    Exception exception;

    public String getOperation()
    {
        return operation;
    }

    public Long getPid()
    {
        return pid;
    }

    public ProcessInfo setPid(Long pid)
    {
        this.pid = pid;
        return this;
    }

    public ProcessInfo setStartOperation()
    {
        this.operation = START;
        return this;
    }

    public ProcessInfo setStopOperation()
    {
        this.operation = STOP;
        return this;
    }

    public ProcessInfo setPsOperation()
    {
        this.operation = PS;
        return this;
    }

    public ProcessType getProcessType()
    {
        return processType;
    }

    public ProcessInfo setProcessType(ProcessType processType)
    {
        this.processType = processType;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ProcessInfo setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getUsername()
    {
        return username;
    }

    public ProcessInfo setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public boolean isRunning()
    {
        return running;
    }

    public ProcessInfo setRunning(boolean running)
    {
        this.running = running;
        return this;
    }


    public String getCommandLine()
    {
        return commandLine;
    }

    public ProcessInfo setCommandLine(String commandLine)
    {
        this.commandLine = commandLine;
        return this;
    }

    public ProcessInfo setProcess(Process process)
    {
        this.process = process;
        if(process != null && process.info() != null)
        {
            this.running = process.isAlive();
            this.pid = process.pid();

            if (process.info().command().isPresent())
            {
                this.command = process.info().command().get();
            }

            if (process.info().user().isPresent())
            {
                this.username = process.info().user().get();
            }

            Optional<String> commandLine = process.info().commandLine();
            if (commandLine.isPresent())
            {
                this.commandLine = process.info().commandLine().get();
            }
        }

        return this;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public Exception getException()
    {
        return exception;
    }

    public ProcessInfo setException(Exception exception)
    {
        this.exception = exception;
        return this;
    }

    public JSONObject toJSON()
    {
        return new JSONObject()
            .put("operation", operation)
            .put("type", (processType != null) ? processType.getName() : null )
            .put("name", name)
            .put("username", username)
            .put("running", running)
            .put("command", command)
            .put("commandLine", commandLine)
            .put("pid", pid)
            .put("exception", exception);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessInfo that = (ProcessInfo) o;

        if (running != that.running) return false;
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        if (processType != null ? !processType.equals(that.processType) : that.processType != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (commandLine != null ? !commandLine.equals(that.commandLine) : that.commandLine != null) return false;
        if (process != null ? !process.equals(that.process) : that.process != null) return false;
        return exception != null ? exception.equals(that.exception) : that.exception == null;
    }

    @Override
    public int hashCode()
    {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (processType != null ? processType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (running ? 1 : 0);
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (commandLine != null ? commandLine.hashCode() : 0);
        result = 31 * result + (process != null ? process.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ProcessInfo{" +
            "pid=" + pid +
            ", operation='" + operation + '\'' +
            ", processType=" + processType +
            ", name='" + name + '\'' +
            ", username='" + username + '\'' +
            ", running=" + running +
            ", command='" + command + '\'' +
            ", commandLine='" + commandLine + '\'' +
            ", process=" + process +
            ", exception=" + exception +
            '}';
    }
}