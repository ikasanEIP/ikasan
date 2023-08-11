package org.ikasan.ootb.scheduler.agent.module.service.processtracker.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class ProcessStatusDaoFSImp implements ProcessStatusDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStatusDaoFSImp.class);
    protected static final String RESULTS_FILE_POSTFIX = "_results";
    protected static final String SCRIPT_FILE_POSTFIX = "_script";
    /** persistence directory */
    String persistenceDir;

    File persistenceDirFile;

    /**
     * Constructor
     * @param persistenceDir to be created
     */
    public ProcessStatusDaoFSImp(String persistenceDir)
    {
        this.persistenceDir = persistenceDir;
        if(persistenceDir == null)
        {
            throw new IllegalArgumentException("persistence directory cannot be 'null");
        }

        this.persistenceDirFile = new File(persistenceDir);
        if(!persistenceDirFile.exists())
        {
            if (!persistenceDirFile.mkdirs())
                LOGGER.warn("Attempt to create persistence directory " + persistenceDir + " failed when we would not expect it to, this may case further issues");
        }
    }

    /**
     * In order to later retrieve the process stats, we first persist all the commands so that they can be executed as a script.
     * @param processIdentity uniquly identifies the process
     * @param scriptPostfix is appended to the end of the generated filename to identify it as a runnable script
     * @param commandsToBeExecuted that are saved within the generated file
     * @return the full path of the generated script.
     * @throws IOException if the file could not be created
     */
    public String createCommandScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException {
        String location = getScriptFilePath(processIdentity, scriptPostfix);
        File file = new File(location);
        String fullPath = file.getCanonicalPath();
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(commandsToBeExecuted);
        }
        return fullPath;
    }

    /**
     * Attempt to get the return code that has been persisted.
     * If the return code is not set, return a string to indicate why.
     * @param processIdentity to obtain the return code for
     * @return a string containing either "UNSET" or the numeric value of hte return code.
     */
    public String getPersistedReturnCode(String processIdentity) {
        String returnCodeString ;
        Integer numericValue;
        String returnResultsPath = getResultAbsoluteFilePath(processIdentity);
        String fileContent = null;
        Path filePath = Path.of(returnResultsPath);
        try {
            fileContent = Files.readString(filePath).trim();
            // On Windows we force utf-8 output, MS inserts a Byte Order Mark for UTF8 files :(
            fileContent = fileContent.replace("\uFEFF", "");
            numericValue = Integer.parseInt(fileContent);
            returnCodeString = numericValue.toString();
        } catch (IOException | NumberFormatException e) {
            String message = "Attempt to read the status from file [" + returnResultsPath +
                "] failed, content was [" + fileContent +
                "] ,issue [" + e.getMessage() + "]";
            LOGGER.warn(message, e);
            returnCodeString = message + " see agent logs for " + e.getClass().getSimpleName();
        }
        return returnCodeString;
    }

    /*
     * The script file was generated to hold the commands to execute, the result file holds the returned result of the commands
     * Once they have served their purpose, they need to be housekept.
     * @param processIdentity which will be used to generate the file names
     * @param scriptPostfix that was used to create the commandds script
     * @throws IOException if there were issues removing the file
     */
    public void removeScriptAndResult(String processIdentity, String scriptPostfix) throws IOException {
        Path fileToDeletePath = Paths.get(getScriptFilePath(processIdentity, scriptPostfix));
        Files.delete(fileToDeletePath);
        fileToDeletePath = Paths.get(getResultFilePath(processIdentity));
        Files.delete(fileToDeletePath);
    }


    private String getResultFilePath(String processIdentity) {
        return persistenceDir + FileSystems.getDefault().getSeparator() + processIdentity + RESULTS_FILE_POSTFIX;
    }

    public String getResultAbsoluteFilePath(String processIdentity) {
        String path = getResultFilePath(processIdentity);
        File file = new File(path);
        return file.getAbsolutePath();
    }

    public String getScriptFilePath(String processIdentity, String scriptPostfix)  {
        return persistenceDir + FileSystems.getDefault().getSeparator() + processIdentity + SCRIPT_FILE_POSTFIX + scriptPostfix;
    }

    public String getScriptAbsoluteFilePath(String processIdentity, String scriptPostfix) {
        String path = getScriptFilePath(processIdentity, scriptPostfix);
        File file = new File(path);
        return file.getAbsolutePath();
    }

}
