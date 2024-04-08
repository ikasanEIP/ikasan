package org.ikasan.cli.shell.operation.h2.migration;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for H2DatabaseMigrationSourcePostProcess
 */
public class H2DatabaseMigrationSourcePostProcessTest {

    /**
     * Test method for {@link H2DatabaseMigrationSourcePostProcessOperation#execute()}
     * Tests the scenario where the post processing is successful.
     */
    @Test
    public void testExecuteSuccess() throws IOException {
        String inputFilePath = "classpath:migration/script.sql";
        String outputFilePath = "./target/testOutputFile.sql";

        H2DatabaseMigrationSourcePostProcessOperation h2DatabaseMigrationSourcePostProcess =
                new H2DatabaseMigrationSourcePostProcessOperation(inputFilePath, outputFilePath);

        String result = h2DatabaseMigrationSourcePostProcess.execute();

        String expected = String.format("Successfully perform migration process post process. Pre process file [%s] -" +
            " Post process file [%s]",
            inputFilePath,
            outputFilePath);

        assertEquals(expected, result);

        Reader reader1 = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:migration/expected.sql")));
        Reader reader2 = new BufferedReader(new FileReader(ResourceUtils.getFile("./target/testOutputFile.sql")));

        assertTrue("The post processed and expected files differ!", IOUtils.contentEqualsIgnoreEOL(reader1, reader2));
    }


    /**
     * Test method for {@link H2DatabaseMigrationSourcePostProcessOperation#execute()}
     * Tests the scenario where the post processing fails due to an exception.
     */
    @Test
    public void testExecuteFailure() {
        String inputFilePath = "nonExistingInputFile.sql";
        String outputFilePath = "nonExistingOutputFile.sql";

        H2DatabaseMigrationSourcePostProcessOperation h2DatabaseMigrationSourcePostProcess =
                new H2DatabaseMigrationSourcePostProcessOperation(inputFilePath, outputFilePath);

        assertThrows(RuntimeException.class, h2DatabaseMigrationSourcePostProcess::execute);
    }
}