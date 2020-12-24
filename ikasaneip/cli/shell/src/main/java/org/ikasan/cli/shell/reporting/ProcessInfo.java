package org.ikasan.cli.shell.reporting;

import org.ikasan.cli.shell.operation.model.ProcessType;
import org.json.JSONObject;

import java.util.Optional;

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
        JSONObject jsonObject = new JSONObject()
            .put("operation", operation)
            .put("type", processType.getName())
            .put("name", name)
            .put("user", username)
            .put("running", running)
            .put("command", command)
            .put("commandLine", commandLine)
            .put("pid", pid)
            ;

        if(process != null && process.info() != null)
        {
            jsonObject.put("running", process.isAlive());

            if (!process.info().command().isEmpty())
            {
                jsonObject.put("command", command);
            }

            if (!process.info().user().isEmpty())
            {
                jsonObject.put("user", process.info().user().get());
            }

            Optional<String> commandLine = process.info().commandLine();
            if (!commandLine.isEmpty())
            {
                jsonObject.put("commandLine", commandLine.get());
            }
        }

        if(exception != null)
        {
            jsonObject.put("exception", exception.getMessage());
        }

        return jsonObject;
    }
}