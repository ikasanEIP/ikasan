package org.ikasan.cli.shell;

import org.ikasan.cli.shell.command.H2MigrationCommand;
import org.ikasan.cli.shell.noninteractive.Command;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Ikasan Shell
 *
 * @author Ikasan Development Team
 */
@SpringBootApplication
@CommandScan
//@EnableCommand(H2MigrationCommand.class)
public class IkasanShell
{
    public static void main(String[] args)
    {
        // deal with any non-interactive commands passed as args
        List<String> scriptArgs = new ArrayList<>();
        SpringApplication app = new SpringApplication(IkasanShell.class);
        if(args.length > 0)
        {
            scriptArgs.add( Command.getNonInterative(args) );
            app.setBannerMode(Banner.Mode.OFF);
        }

        app.run(scriptArgs.toArray(String[]::new));

        // clean up any non-interactive files
        for(String scriptArg:scriptArgs)
        {
            Command.remove(scriptArg);
        }
    }
}