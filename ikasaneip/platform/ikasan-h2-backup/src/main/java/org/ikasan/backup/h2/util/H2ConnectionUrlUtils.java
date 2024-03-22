package org.ikasan.backup.h2.util;

import org.ikasan.backup.h2.exception.InvalidH2ConnectionUrlException;

import java.net.URI;
import java.net.URISyntaxException;

public class H2ConnectionUrlUtils {



    /**
     * Creates a test URL for testing a backed up database.
     *
     * @param connectionUrl the original connection URL
     * @param port the port to be used for the test
     * @param replacementFilePath the file path for the test database
     *
     * @return the test URL for the backed up database
     *
     * @throws URISyntaxException if a URI syntax exception occurs
     */
    public static String createTestUrl(String connectionUrl, String port, String replacementFilePath) throws InvalidH2ConnectionUrlException {
        try {
            String start = connectionUrl.substring(0, connectionUrl.indexOf(":", connectionUrl.indexOf(":") + 1));
            URI uri = new URI(connectionUrl.substring(start.length() + 1));

            String dbName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1, uri.getPath().indexOf(";"));
            String params = uri.getPath().substring(uri.getPath().indexOf(";"));

            return start + ":" + uri.getScheme() + ":" + "//" + uri.getHost()
                + ":" + port + "/" + replacementFilePath + "/" + dbName + params;
        }
        catch (Exception e) {
            throw new InvalidH2ConnectionUrlException(String.format("Connection url [%s] appears to be invalid!", connectionUrl), e);
        }
    }


    /**
     * Returns the name of the database from the given connection URL.
     *
     * @param connectionUrl the URL of the database connection
     *
     * @return the name of the database
     *
     * @throws URISyntaxException if a URI syntax exception occurs
     */
    public static String getDatabaseName(String connectionUrl) throws InvalidH2ConnectionUrlException {
        try {
            String start = connectionUrl.substring(0, connectionUrl.indexOf(":", connectionUrl.indexOf(":")+1));
            URI uri = new URI(connectionUrl.substring(start.length()+1));

            String dbName;

            if(uri.getScheme().equals("mem")) {
                dbName = uri.getSchemeSpecificPart().substring(0, uri.getSchemeSpecificPart().indexOf(";"));
            }
            else {
                dbName = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1, uri.getPath().indexOf(";"));
            }


            return dbName;
        }
        catch (Exception e) {
            throw new InvalidH2ConnectionUrlException(String.format("Connection url [%s] appears to be invalid!", connectionUrl), e);
        }
    }
}
