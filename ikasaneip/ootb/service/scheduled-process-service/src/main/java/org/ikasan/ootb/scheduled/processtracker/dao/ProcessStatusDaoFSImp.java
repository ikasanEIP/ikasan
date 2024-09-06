package org.ikasan.ootb.scheduled.processtracker.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.retry.support.RetryTemplate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;

public class ProcessStatusDaoFSImp implements ProcessStatusDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStatusDaoFSImp.class);
    protected static final String RESULTS_FILE_POSTFIX = "_results";
    protected static final String SCRIPT_FILE_POSTFIX = "_script";
    protected static final String WRAPPER_FILE_POSTFIX = "_wrapper";
    private String persistenceDir;
    private int maxGetStatusRetries;
    private long retryInterval;
    private File persistenceDirFile;
    private RetryTemplate retryTemplate;

    /**
     * Constructor
     * @param persistenceDir to be created
     */
    public ProcessStatusDaoFSImp(String persistenceDir,
                                 int maxGetStatusRetries,
                                 long retryInterval)
    {
        this.persistenceDir = persistenceDir;
        this.maxGetStatusRetries = maxGetStatusRetries;
        this.retryInterval = retryInterval;
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
        retryTemplate = RetryTemplate.builder()
            .maxAttempts(maxGetStatusRetries)
            .fixedBackoff(retryInterval)
            .retryOn(NoSuchFileException.class)
            .build();
    }

    /**
     * In order to later retrieve the process stats, we first persist all the commands so that they can be executed as a script.
     * @param processIdentity uniquely identifies the process
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
     * The command wrapper script invokes the createCommandScript and gathers the return code information from it
     * Though similar in function to createCommandScript, createCommandWrapperScript aims to encapsulate how wrapped
     * commands are handled.
     * @param processIdentity uniquely identifies the process.
     * @param scriptPostfix is appended to the end of the generated filename to identify it as a runnable script.
     * @param commandsToBeExecuted that are saved within the generated file, these will be the wrapping commands.
     * @return the full path of the generated script.
     * @throws IOException if the file could not be created.
     */
    public String createCommandWrapperScript(String processIdentity, String scriptPostfix, String commandsToBeExecuted) throws IOException {
        return createCommandScript(processIdentity, WRAPPER_FILE_POSTFIX + scriptPostfix, commandsToBeExecuted);
    }

    /**
     * Attempt to get the return code that has been persisted.
     * If the return code is not set, return a string to indicate why.
     * @param processIdentity to obtain the return code for
     * @return a string containing either "UNSET" or the numeric value of hte return code.
     */
    public String getPersistedReturnCode(String processIdentity) {
        String returnCodeString ;
        String returnResultsPath = getResultAbsoluteFilePath(processIdentity);
        String fileContent = null;
        try {
            fileContent = retryTemplate.execute(ctx -> getPersistedReturnCodeData(Path.of(returnResultsPath)));
            // On Windows we have to force utf-8 output, MS inserts a Byte Order Mark for UTF8 files
            fileContent = fileContent.replace("\uFEFF", "");
            int numericValue = Integer.parseInt(fileContent);
            returnCodeString = Integer.toString(numericValue);
        } catch (IOException | NumberFormatException e) {
            returnCodeString = "Attempts to read the status from file [" + returnResultsPath +
                "] failed";
            if (e instanceof NoSuchFileException) {
                returnCodeString += " after " + maxGetStatusRetries + " retries and an interval of " + retryInterval +
                    " milliseconds, this is a known operating system issue, consider increasing these values";
            } else {
                Object[] trace = Arrays.stream(e.getStackTrace()).toArray();
                returnCodeString += ", content was [" + fileContent + "] ,issue [" + e.getMessage() + ", " + e.getClass().getSimpleName()+ ", " + (trace.length > 0 ? trace[0] : "") + "]";
            }
            LOGGER.info(returnCodeString);
        }
        return returnCodeString;
    }

    /**
     * Attempt to get the content of file containing the return results using a spring retry approach
     * It has been proven on Windows, for parallel optations, that the OS does not make the
     * file available until several seconds after the process that created it has finished.
     * @param fileResultsPath the expected location of the results
     * @return the contents of the results file.
     */
    private String getPersistedReturnCodeData(Path fileResultsPath) throws IOException {
        RetryContext retryContext = RetrySynchronizationManager.getContext();
        if (retryContext.getRetryCount() > 1) {
            LOGGER.warn("Attempting to get persisted return code from path " +
                fileResultsPath + "Retry Number: " + retryContext.getRetryCount() + ", interval " + retryInterval);
        }
        return Files.readString(fileResultsPath).trim();
    }

    /*
     * The script file was generated to hold the commands to execute, the result file holds the returned result of the commands
     * Once they have served their purpose, they need to be housekept.
     * @param processIdentity which will be used to generate the file names
     * @throws IOException if there were issues removing the file
     */
    public void removeScriptAndResult(String processIdentity) throws IOException {
        Files.list(Path.of(this.persistenceDir))
            .filter(p -> p.toString().contains(getScriptFile(processIdentity))
                ||  p.toString().contains(getWrapperScriptFile(processIdentity))
                ||  p.toString().contains(getResultFile(processIdentity)))
            .forEach((p) -> {
            try {
                Files.deleteIfExists(p);
                LOGGER.info(String.format("Successfully deleted process file[%s].", p));
            } catch (Exception e) {
                LOGGER.info(String.format("Failed to delete file[%s]. Error message[%s].", p, e.getMessage()));
            }
        });
    }


    private String getResultFile(String processIdentity) {
        return processIdentity + RESULTS_FILE_POSTFIX;
    }

    private String getResultFilePath(String processIdentity) {
        return persistenceDir + FileSystems.getDefault().getSeparator() + getResultFile(processIdentity);
    }

    public String getResultAbsoluteFilePath(String processIdentity) {
        String path = getResultFilePath(processIdentity);
        File file = new File(path);
        return file.getAbsolutePath();
    }

    public String getScriptFile(String processIdentity)  {
        return processIdentity + SCRIPT_FILE_POSTFIX;
    }

    @Override
    public String getScriptFilePath(String processIdentity, String fileExtension)  {
        return persistenceDir + FileSystems.getDefault().getSeparator() + processIdentity + SCRIPT_FILE_POSTFIX + fileExtension;
    }

    public String getWrapperScriptFile(String processIdentity)  {
        return processIdentity + SCRIPT_FILE_POSTFIX + WRAPPER_FILE_POSTFIX;
    }
}
