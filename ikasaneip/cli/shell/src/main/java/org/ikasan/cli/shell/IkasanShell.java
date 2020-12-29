package org.ikasan.cli.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Ikasan Shell
 *
 * @author Ikasan Development Team
 */
@SpringBootApplication
public class IkasanShell
{
    static List<String> supportedScriptCommands = new ArrayList<>(List.of(
        "start", "start-h2", "start-module",
        "stop", "stop-h2", "stop-module",
        "ps"
        ));

    public static void main(String[] args)
    {
        // append any args for scripting with an "@"
        List<String> scriptArgs = new ArrayList<>();
        for(String arg:args)
        {
            if(supportedScriptCommands.contains(arg.toLowerCase()))
            {
                scriptArgs.add("@" + arg);
            }
        }

        SpringApplication.run( IkasanShell.class, scriptArgs.toArray(String[]::new) );
    }
}