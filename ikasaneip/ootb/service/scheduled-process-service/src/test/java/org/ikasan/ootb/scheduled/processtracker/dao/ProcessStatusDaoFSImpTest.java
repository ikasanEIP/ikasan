package org.ikasan.ootb.scheduled.processtracker.dao;

import org.ikasan.ootb.scheduled.processtracker.CommandProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessStatusDaoFSImpTest {
    private ProcessStatusDao processStatusDao;

    @TempDir
    File tempDir;

    private static final String COMMANDS = "myCommand1";
    private static final String IDENTITY = "IdentityXYZ";
    private static final Integer RETURNVALUE = 1;
    private static final String  RETURNVALUESTR = ""+RETURNVALUE;

    @BeforeEach
    void setUp() {
        processStatusDao = new ProcessStatusDaoFSImp(tempDir.toString());
    }

    @Test
    void creation_of_command_script() throws IOException {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);
        String pathToCommands = processStatusDao.createCommandScript(IDENTITY, cp.getScriptFilePostfix(), COMMANDS);

        assertThat(pathToCommands).contains(IDENTITY + ProcessStatusDaoFSImp.SCRIPT_FILE_POSTFIX + cp.getScriptFilePostfix());

        Path filePath = Path.of(pathToCommands);
        String fileContent = Files.readString(filePath).trim();

        assertThat(fileContent).contains(COMMANDS);
    }

    @Test
    void when_the_returned_value_is_numeric_it_is_returned_as_execpected() throws IOException {
        String resultsFile = processStatusDao.getResultAbsoluteFilePath(IDENTITY);

        assertThat(resultsFile).contains(IDENTITY + ProcessStatusDaoFSImp.RESULTS_FILE_POSTFIX);

        writeContentToFile(resultsFile, "" + RETURNVALUE);

        String acutalReturnValue = processStatusDao.getPersistedReturnCode(IDENTITY);

        assertThat(acutalReturnValue).isEqualTo(RETURNVALUESTR);

        Integer numericReturnValue = Integer.valueOf(RETURNVALUESTR);
        assertThat(numericReturnValue).isEqualTo(RETURNVALUE);
    }

    @Test
    void when_windows_inserts_bom_it_is_removed_from_results() throws IOException {
        String resultsFile = processStatusDao.getResultAbsoluteFilePath(IDENTITY);
        writeContentToFile(resultsFile, "\uFEFF" + "" + RETURNVALUE);

        String acutalReturnValue = processStatusDao.getPersistedReturnCode(IDENTITY);
        assertThat(acutalReturnValue).isEqualTo(RETURNVALUESTR);
    }

    @Test
    void when_the_result_is_non_numeric_error_string_is_returned() throws IOException {
        String resultsFile = processStatusDao.getResultAbsoluteFilePath(IDENTITY);
        writeContentToFile(resultsFile, "X");

        String acutalReturnValue = processStatusDao.getPersistedReturnCode(IDENTITY);
        
        assertThat(acutalReturnValue).contains("failed, content was [X]");
        assertThat(acutalReturnValue).contains("NumberFormatException");
    }

    @Test
    void ensure_the_tidy_up_removes_all_files() throws IOException {
        CommandProcessor cp = CommandProcessor.getCommandProcessor(null);
        String resultsFilePath = processStatusDao.getResultAbsoluteFilePath(IDENTITY);
        String scriptFilePath = processStatusDao.getScriptFilePath(IDENTITY, cp.getScriptFilePostfix());

        writeContentToFile(resultsFilePath, "X");
        writeContentToFile(scriptFilePath, "X");

        File resultsFile = new File(resultsFilePath);
        File scriptFile = new File(scriptFilePath);

        assertThat(resultsFile.exists()).isTrue();
        assertThat(scriptFile.exists()).isTrue();

        processStatusDao.removeScriptAndResult(IDENTITY, cp.getScriptFilePostfix());

        assertThat(resultsFile.exists()).isFalse();
        assertThat(scriptFile.exists()).isFalse();
    }

    private void writeContentToFile(String resultsFile, String value) throws FileNotFoundException {
        File file = new File(resultsFile);
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(value);
        }
    }

}