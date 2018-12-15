package org.ikasan.connector.sftp.net;

import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.sftp.ssh.SftpServerWithPasswordAuthenticator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by amajewski on 24/06/15.
 */
public class SFTPClientTest
{


    private static final int SFTP_PORT_PASSWORD = 3001;
    private SftpServerWithPasswordAuthenticator server;
    private SFTPClient uut;
    private Path tempDir;


    @Before
    public void before() throws ClientConnectionException, IOException
    {
        tempDir = Files.createTempDirectory("tempfiles");
        server = new SftpServerWithPasswordAuthenticator(SFTP_PORT_PASSWORD, tempDir);
        server.start();
        uut = new SFTPClient(null, null, "testUser", "testPassword", "127.0.0.1", SFTP_PORT_PASSWORD, "127.0.0.1", 3, "", 20000, null);

        uut.connect();

    }

    @After
    public void teardown()
    {

        server.stop();

    }

    @Test
    public void doList_when_dir_is_empty() throws URISyntaxException, ClientCommandLsException
    {
        List<ClientListEntry> result = uut.ls(".");
        assertThat(result).extracting(
            ClientListEntry::getName,
            ClientListEntry::isDirectory
        ).containsExactlyInAnyOrder(
            tuple(".", true)
        );
    }

    @Test
    public void doList_when_dir_has_file() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempFile = Files.createTempFile(tempDir, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertThat(result).extracting(
            ClientListEntry::getName,
            ClientListEntry::isDirectory
        ).containsExactlyInAnyOrder(
            tuple(".", true),
            tuple(tempFile.getFileName().toString(), false)
        );
    }

    @Test
    public void doList_when_dir_has_two_files() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempFile = Files.createTempFile(tempDir, "tempfile1", ".tmp");
        Path tempFile2 = Files.createTempFile(tempDir, "tempfile2", ".tmp");

        List<ClientListEntry> result = uut.ls(".");

        assertThat(result).extracting(
            ClientListEntry::getName,
            ClientListEntry::isDirectory
        ).containsExactlyInAnyOrder(
            tuple(".", true),
            tuple(tempFile.getFileName().toString(), false),
            tuple(tempFile2.getFileName().toString(), false)
        );
    }

    @Test
    public void doList_when_has_file_in_subdir() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempDir2 = Files.createTempDirectory(tempDir, "subdir");
        Files.createTempFile(tempDir2, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");

        assertThat(result).extracting(
            ClientListEntry::getName,
            ClientListEntry::isDirectory
        ).containsExactlyInAnyOrder(
            tuple(".", true),
            tuple(tempDir2.getFileName().toString(), true)
        );
    }

    @Test
    public void doList_when_in_subdir() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempDir2 = Files.createTempDirectory(tempDir, "subdir");
        Path tempFile1 = Files.createTempFile(tempDir2, "tempfile1", ".tmp");
        Path tempFile2 = Files.createTempFile(tempDir2, "tempfile2", ".tmp");

        List<ClientListEntry> result = uut.ls(tempDir2.getFileName().toString());

        assertThat(result).extracting(
            ClientListEntry::getName,
            ClientListEntry::isDirectory
        ).containsExactlyInAnyOrder(
            tuple(".", true),
            tuple("..", true),
            tuple(tempFile1.getFileName().toString(), false),
            tuple(tempFile2.getFileName().toString(), false)
        );
    }
}
