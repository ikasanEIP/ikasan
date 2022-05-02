package org.ikasan.ootb.scheduler.agent.module.component.cli;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLinesArgConverter {

    private final static Logger LOG = LoggerFactory.getLogger(CommandLinesArgConverter.class);

    private final List<String> commandLineArgs;

    public CommandLinesArgConverter(List<String> commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
        LOG.info("Setting module.broker.cli.args to: " + this.commandLineArgs);
    }

    public String[] getCommandLineArgs(String commandLine) {
        validateCommandLine(commandLine);

        if (commandLineArgs == null) {
            String os = System.getProperty("os.name");
            if (os != null && os.contains("Windows")) {
                return new String[]{"cmd.exe", "/c", commandLine};
            } else {
                // default will assume linux style
                return new String[]{"/bin/bash", "-c", commandLine};
            }
        }

        return ArrayUtils.add(commandLineArgs.toArray(new String[0]), commandLine);
    }

    private void validateCommandLine(String commandLine) {
        if (commandLine == null || commandLine.length() == 0) {
            throw new EndpointException("Invalid commandLine [" + commandLine + "]");
        }
    }
}
