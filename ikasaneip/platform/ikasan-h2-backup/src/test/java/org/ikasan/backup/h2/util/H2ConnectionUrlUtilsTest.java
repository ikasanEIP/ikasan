package org.ikasan.backup.h2.util;

import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class H2ConnectionUrlUtilsTest {

    /**
     * This test case verifies the correct creation of a test connection URL
     * out of the given original connection URL, port and replacement file path.
     * We assume that the inputs are correctly formatted.
     */
    @Test
    public void test_create_test_url_success() throws InvalidH2ConnectionUrlException {
        // Init test data
        String originalConnectionUrl = "jdbc:h2:tcp://localhost:1337/~/testdb;DB_CLOSE_DELAY=-1";
        String port = "1444";
        String replacementFilePath = "~/testfolder";
        String expectedTestUrl = "jdbc:h2:tcp://localhost:1444/~/testfolder/testdb;DB_CLOSE_DELAY=-1";

        // Call method under test
        String testUrl = H2ConnectionUrlUtils.createTestUrl(originalConnectionUrl, port, replacementFilePath);

        // Assert the result
        assertEquals(expectedTestUrl, testUrl);
    }


    /**
     * This test case verifies the correct creation of a test connection URL
     * out of the given original windows connection URL, port and windows replacement file path.
     * We assume that the inputs are correctly formatted.
     */
    @Test
    public void test_create_test_url_success_window_path() throws InvalidH2ConnectionUrlException {
        // Init test data
        String originalConnectionUrl = "jdbc:h2:tcp://localhost:1337/C:/data/sample;DB_CLOSE_DELAY=-1";
        String port = "1444";
        String replacementFilePath = "C:/data/sample/testfolder";
        String expectedTestUrl = "jdbc:h2:tcp://localhost:1444/C:/data/sample/testfolder/sample;DB_CLOSE_DELAY=-1";

        // Call method under test
        String testUrl = H2ConnectionUrlUtils.createTestUrl(originalConnectionUrl, port, replacementFilePath);

        // Assert the result
        assertEquals(expectedTestUrl, testUrl);
    }


    /**
     * This test case verifies the correct exception handling when an URISyntaxException is expected to be thrown,
     * as the input original connection URL is incorrectly formatted.
     */
    @Test(expected = InvalidH2ConnectionUrlException.class)
    public void test_create_test_url_with_invalid_original_url_exception() throws InvalidH2ConnectionUrlException {
        String invalidOriginalConnectionUrl = "jdbc";
        String port = "1444";
        String replacementFilePath = "~/testfolder";

        try {
            H2ConnectionUrlUtils.createTestUrl(invalidOriginalConnectionUrl, port, replacementFilePath);
        } catch (InvalidH2ConnectionUrlException ex) {
            assertEquals("Connection url [jdbc] appears to be invalid!", ex.getMessage());
            assertEquals(ex.getCause().getClass(), StringIndexOutOfBoundsException.class);
            throw ex;
        }
    }

    /**
     * This test case verifies the correct exception handling when an URISyntaxException is expected to be thrown,
     * as the input original connection URL is incorrectly formatted.
     */
    @Test(expected = InvalidH2ConnectionUrlException.class)
    public void test_create_test_url_with_invalid_original_url_uri_component_exception() throws InvalidH2ConnectionUrlException {
        String invalidOriginalConnectionUrl = "jdbc:h2:this is a bad uri;DB_CLOSE_DELAY=-1";
        String port = "1444";
        String replacementFilePath = "~/testfolder";

        try {
            H2ConnectionUrlUtils.createTestUrl(invalidOriginalConnectionUrl, port, replacementFilePath);
        } catch (InvalidH2ConnectionUrlException ex) {
            assertEquals("Connection url [jdbc:h2:this is a bad uri;DB_CLOSE_DELAY=-1] appears to be invalid!", ex.getMessage());
            assertEquals(ex.getCause().getClass(), URISyntaxException.class);
            throw ex;
        }
    }

    /**
     * Unit test for getDatabaseName method in H2ConnectionUrlUtils class.
     */
    @Test
    public void test_getDatabaseName_returns_correct_db_name() {
        String connectionUrl = "jdbc:h2:file:/data/demo;AUTO_SERVER=TRUE";
        try {
            String dbName = H2ConnectionUrlUtils.getDatabaseName(connectionUrl);
            assertEquals("The extracted database name should be 'demo'", "demo", dbName);
        } catch (InvalidH2ConnectionUrlException e) {
            fail("No exception expected, but got: " + e.getMessage());
        }
    }

    @Test
    public void test_getDatabaseName_throws_exception_for_invalid_url() {
        String connectionUrl = "this_is_not_valid_url";
        assertThrows(InvalidH2ConnectionUrlException.class, () -> H2ConnectionUrlUtils.getDatabaseName(connectionUrl));
    }

}
