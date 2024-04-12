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

    /** supported non-interactive commands and script contents for backward compatibility */
    static Map<String,String> noninteractiveCommands = Map.of(
        "start","migrate-h2\nstart-h2\nstart-module",
        "stop","stop-module\nstop-h2",
        "start-dashboard","migrate-h2\nstart-h2\nstart-solr\nstart-module",
        "stop-dashboard","stop-module\nstop-h2\nstop-solr");

    public static String NON_INTERACTIVE_PREFIX = "@";

    /**
     * Return the non-interactive version of the command if this is a non-interactive version.
     *
     * @param commands
     * @return
     */
    public static String getNonInterative(String[] commands)
    {
        Path nonInteractiveCommandPath = Paths.get(tmpDir + System.currentTimeMillis());
        Path commandFilePath = Paths.get(nonInteractiveCommandPath + FileSystems.getDefault().getSeparator() + commands[0]);
        String noninteractiveCommandContent = noninteractiveCommands.get(commands[0]);
        if(noninteractiveCommandContent == null)
        {
            StringBuilder sb = new StringBuilder();
            for(String command:commands)
            {
                sb.append(command);
                sb.append(" ");
            }

            noninteractiveCommandContent = sb.toString();
        }

        try
        {
            if(noninteractiveCommandContent != null)
            {
                if(Files.notExists(commandFilePath, LinkOption.NOFOLLOW_LINKS) )
                {
                    if(Files.notExists(nonInteractiveCommandPath, LinkOption.NOFOLLOW_LINKS))
                    {
                        Files.createDirectories(nonInteractiveCommandPath);
                    }

                   Files.createFile(commandFilePath);
                }

                Files.write(commandFilePath, noninteractiveCommandContent.getBytes());
                return NON_INTERACTIVE_PREFIX + commandFilePath;
            }
        }
        catch(IOException e)
        {
            logger.warn("Problem creating the non-interactive file script for " + noninteractiveCommandContent, e);
        }

        return noninteractiveCommandContent;
    }

    /**
     * Remove used command files.
     * @param commandfile
     */
    public static void remove(String commandfile)
    {
        commandfile = commandfile.replace(NON_INTERACTIVE_PREFIX,"");
        try
        {
            Path commandFilePath = Paths.get(commandfile);
            Files.deleteIfExists(commandFilePath);
        }
        catch(IOException e)
        {
            logger.warn("Problem removing the non-interactive file script for " + commandfile, e);
        }
    }
}