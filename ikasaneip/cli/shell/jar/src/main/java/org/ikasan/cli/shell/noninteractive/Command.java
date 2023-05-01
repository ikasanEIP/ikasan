package org.ikasan.cli.shell.noninteractive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

/**
 * Non-interactive command class.
 *
 * @author Ikasan Developmnent Team
 */
public class Command
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(Command.class);

    static String tmpDir = System.getProperty("java.io.tmpdir") + FileSystems.getDefault().getSeparator();

    /** supported non-interactive commands and script contents */
    static Map<String,String> noninteractiveCommands = Map.of(
        "start-h2","start-h2",
        "start-module","start-module",
        "start","start-h2\nstart-module",
        "stop-h2","stop-h2",
        "stop-module","stop-module",
        "stop","stop-module\nstop-h2",
        "env","env",
        "ps", "ps");


    public static String NON_INTERACTIVE_PREFIX = "@";

    /**
     * Return the non-interactive version of the command if this is a non-interactive version.
     *
     * @param command
     * @return
     */
    public static String getNonInterative(String command)
    {
        String nonInteractiveCommandPath = tmpDir + command;
        Path commandFilePath = Paths.get(nonInteractiveCommandPath);
        String noninteractiveCommandContent = noninteractiveCommands.get(command);

        try
        {
            if(noninteractiveCommandContent != null)
            {
                if(Files.notExists(commandFilePath, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}) )
                {
                    Files.createFile(commandFilePath);
                    Files.write(commandFilePath, noninteractiveCommandContent.getBytes());
                }

                return NON_INTERACTIVE_PREFIX + nonInteractiveCommandPath;
            }
        }
        catch(IOException e)
        {
            logger.warn("Problem creating the non-interactive file script for " + command, e);
        }

        return command;
    }

}