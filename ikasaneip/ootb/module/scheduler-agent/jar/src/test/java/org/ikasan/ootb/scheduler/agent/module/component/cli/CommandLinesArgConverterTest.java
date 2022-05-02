package org.ikasan.ootb.scheduler.agent.module.component.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommandLinesArgConverterTest {

    private String currentOS;

    @Before
    public void setUp() {
        currentOS = System.getProperty("os.name");
    }

    @After
    public void reset() {
        System.setProperty("os.name", currentOS);
    }

    @Test
    public void should_use_defaults_shell_if_cli_args_null_unix_if_os_not_windows() {
        System.setProperty("os.name", "Red Hat Linux");
        CommandLinesArgConverter converter = new CommandLinesArgConverter(null);
        String commandLine = "source $HOME/.some_profile";
        String[] commandLineArgs = converter.getCommandLineArgs(commandLine);
        assertEquals(3, commandLineArgs.length);
        assertEquals("/bin/bash", commandLineArgs[0]);
        assertEquals("-c", commandLineArgs[1]);
        assertEquals(commandLine, commandLineArgs[2]);
    }

    @Test
    public void should_use_defaults_shell_if_cli_args_null_if_os_like_windows() {
        System.setProperty("os.name", "Windows 10");
        CommandLinesArgConverter converter = new CommandLinesArgConverter(null);
        String commandLine = "vbaScript -someParam";
        String[] commandLineArgs = converter.getCommandLineArgs(commandLine);
        assertEquals(3, commandLineArgs.length);
        assertEquals("cmd.exe", commandLineArgs[0]);
        assertEquals("/c", commandLineArgs[1]);
        assertEquals(commandLine, commandLineArgs[2]);
    }

    @Test
    public void should_use_empty_list_args_shell_if_nothing_set() {
        System.setProperty("os.name", "Dont care about os");
        CommandLinesArgConverter converter = new CommandLinesArgConverter(Collections.emptyList());
        String commandLine = "source $HOME/.some_profile";
        String[] commandLineArgs = converter.getCommandLineArgs(commandLine);
        assertEquals(1, commandLineArgs.length);
        assertEquals(commandLine, commandLineArgs[0]);
    }

    @Test
    public void should_use_cli_args_shell_if_set() {
        System.setProperty("os.name", "Mac OS X");
        CommandLinesArgConverter converter = new CommandLinesArgConverter(List.of("zsh", "-x", "-y"));
        String commandLine = "source $HOME/.some_profile";
        String[] commandLineArgs = converter.getCommandLineArgs(commandLine);
        assertEquals(4, commandLineArgs.length);
        assertEquals("zsh", commandLineArgs[0]);
        assertEquals("-x", commandLineArgs[1]);
        assertEquals("-y", commandLineArgs[2]);
        assertEquals(commandLine, commandLineArgs[3]);
    }

    @Test(expected = EndpointException.class)
    public void should_throw_invalid_exception_if_command_line_null() {
        CommandLinesArgConverter converter = new CommandLinesArgConverter(null);
        converter.getCommandLineArgs(null);
    }

    @Test(expected = EndpointException.class)
    public void should_throw_invalid_exception_if_command_line_empty() {
        CommandLinesArgConverter converter = new CommandLinesArgConverter(null);
        converter.getCommandLineArgs("");
    }
}