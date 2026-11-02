package org.ikasan.cli.shell;

import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.ikasan.cli.shell.noninteractive.Command;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Ikasan Shell
 *
 * @author Ikasan Development Team
 */
@SpringBootApplication
@CommandScan
public class IkasanShell
{
    static SpringApplication createApplication() {
        SpringApplication application = new SpringApplication(IkasanShell.class);
        // Resolve local ENC(...) properties before command beans receive their values,
        // using the same environment as the scheduler-agent application.
        application.setEnvironment(new StandardEncryptableEnvironment());
        return application;
    }

    public static void main(String[] args)
    {
        // deal with any non-interactive commands passed as args
        List<String> scriptArgs = new ArrayList<>();
        SpringApplication app = createApplication();
        if(args.length > 0)
        {
            scriptArgs.add( Command.getNonInteractive(args) );
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