package org.ikasan.cli.shell;

import org.ikasan.cli.shell.noninteractive.Command;
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
    public static void main(String[] args)
    {
        // deal with any non-interactive commands passed as args
        List<String> scriptArgs = new ArrayList<>();
        if(args.length > 0)
        {
            scriptArgs.add( Command.getNonInterative(args) );
        }

        SpringApplication.run( IkasanShell.class, scriptArgs.toArray(String[]::new) );

        // clean up any non-interactive files
        for(String scriptArg:scriptArgs)
        {
            Command.remove(scriptArg);
        }
    }
}