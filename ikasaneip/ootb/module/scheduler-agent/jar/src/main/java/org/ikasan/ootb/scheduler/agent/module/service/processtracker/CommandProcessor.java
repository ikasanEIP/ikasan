package org.ikasan.ootb.scheduler.agent.module.service.processtracker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public enum CommandProcessor {
    WINDOWS_CMD("cmd.exe", "/c", ".cmd"),
    WINDOWS_POWSHELL("powershell.exe", "-Command", ".ps1"),
    UNIX_BASH("/bin/bash", "-c", ".sh"),
    UNKNOWN("", "", "") ;
    private final String name;
    private final String cliFlags;
    private final String scriptFilePostfix;

    CommandProcessor(String name, String cliFlags, String scriptFilePostfix) {
        this.name = name;
        this.cliFlags = cliFlags;
        this.scriptFilePostfix = scriptFilePostfix;
    }

    public String[] getCommandArgs() {
        return new String[] {name, cliFlags};
    }
    public String getScriptFilePostfix() { return this.scriptFilePostfix; }

    public String getName() { return name; }

    public static CommandProcessor getCommandProcessor(String[] executionEnvironmentProperties) {
        CommandProcessor commandProcessor = CommandProcessor.UNIX_BASH;
        String executionEnvironmentPropertiesStr = executionEnvironmentProperties == null ? "" : String.join("", executionEnvironmentProperties);
        if (StringUtils.isNotBlank(executionEnvironmentPropertiesStr)) {
            if (executionEnvironmentPropertiesStr.contains(WINDOWS_POWSHELL.name)) {
                commandProcessor =  CommandProcessor.WINDOWS_POWSHELL;
            } else if (executionEnvironmentPropertiesStr.contains(WINDOWS_CMD.name)) {
                commandProcessor =  CommandProcessor.WINDOWS_CMD;
            }
        } else {
            // Guess based on current OS
            if (SystemUtils.OS_NAME.contains("Windows")) {
                commandProcessor =  CommandProcessor.WINDOWS_CMD;
            }
        }
        return commandProcessor;
    }
}
