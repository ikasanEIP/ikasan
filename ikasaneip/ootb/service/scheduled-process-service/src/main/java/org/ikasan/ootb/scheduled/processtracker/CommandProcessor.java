package org.ikasan.ootb.scheduled.processtracker;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public enum CommandProcessor {
    WINDOWS_CMD("CMD","cmd.exe", "/c", ".cmd"),
    WINDOWS_POWSHELL("POWERSHELL", "powershell.exe", "-Command", ".ps1"),
    UNIX_BASH("BASH","/bin/bash", "-c", ".sh"),
    UNIX_KSH("KSH","/bin/ksh", "-c", ".ksh"),
    UNKNOWN("", "", "", "") ;
    private final String title;
    private final String name;
    private final String cliFlags;
    private final String scriptFilePostfix;

    CommandProcessor(String title, String name, String cliFlags, String scriptFilePostfix) {
        this.title = title;
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
        CommandProcessor commandProcessor = null;
        String executionEnvironmentPropertiesStr = executionEnvironmentProperties == null ? "" : String.join("", executionEnvironmentProperties).toUpperCase();
        if (StringUtils.isNotBlank(executionEnvironmentPropertiesStr)) {
            for (CommandProcessor processor : CommandProcessor.values()) {
                if (executionEnvironmentPropertiesStr.contains(processor.title.toUpperCase())) {
                    commandProcessor = processor;
                    break;
                }
            }
        }

        if (commandProcessor == null || commandProcessor == CommandProcessor.UNKNOWN) {
            // Guess based on current OS
            if (SystemUtils.OS_NAME.contains("Windows")) {
                commandProcessor =  CommandProcessor.WINDOWS_CMD;
            } else {
                commandProcessor =  CommandProcessor.UNIX_BASH;
            }
        }
        return commandProcessor;
    }
}
