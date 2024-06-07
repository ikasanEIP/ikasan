package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.command.ProcessUtils;
import org.ikasan.cli.shell.operation.model.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultForkedExecutableOperationImpl implements ExecutableOperation {
    private ProcessType processType;
    private List<String> commands;
    private String name;

    /**
     * Default implementation of the ExecutableOperation interface.
     */
    public DefaultForkedExecutableOperationImpl(ProcessType processType, List<String> commands, String name) {
        this.processType = processType;
        this.commands = commands;
        this.name = name;
    }

    @Override
    public String execute() throws RuntimeException {
        try {
            Process process = Operation.getInstance().start(processType, ProcessUtils.getCommands(commands.stream()
                .collect(Collectors.joining("\r\n")), name), name);
            int exitCode = process.waitFor();
            if(exitCode != 0) {
                String error = new String(process.getErrorStream().readAllBytes());
                throw new RuntimeException(String.format("The forked executable process has ended and returned" +
                    " a non zero exit code[%s], error message[%s]", exitCode, error));
            }
            return String.format("Successfully executed command [%s]", commands.stream().collect(Collectors.joining(" ")));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(String.format("An error has occurred processing forked executable operation"), e);
        }
    }

    @Override
    public String getCommand() {
        return commands.stream()
            .collect(Collectors.joining("\r\n"));
    }
}
