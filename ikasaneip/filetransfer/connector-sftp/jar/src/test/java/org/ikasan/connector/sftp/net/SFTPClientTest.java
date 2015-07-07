package org.ikasan.connector.sftp.net;

import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.sftp.ssh.SftpServerWithPasswordAuthenticator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;

/**
 * Created by amajewski on 24/06/15.
 */
public class SFTPClientTest
{


    private static final int SFTP_PORT_PASSWORD=3001;
    private SftpServerWithPasswordAuthenticator server;
    private SFTPClient uut;
    private Path tempDir;


    @Before
    public void before() throws ClientConnectionException, IOException
    {
        tempDir = Files.createTempDirectory("tempfiles");
        server = new SftpServerWithPasswordAuthenticator(SFTP_PORT_PASSWORD, tempDir);
        server.start();
        uut = new SFTPClient(null,null,"testUser","testPassword","localhost",SFTP_PORT_PASSWORD,"localhost",3,"",20000);

        uut.connect();

    }

    @After
    public void teardown()
    {

        server.stop();

    }

    @Test
    public void doList_when_dir_is_empty() throws URISyntaxException, ClientCommandLsException, IOException
    {
        List<ClientListEntry> result = uut.ls(".");
        assertEquals(true,result.isEmpty());

    }

    @Test
    public void doList_when_dir_has_file() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempFile = Files.createTempFile(tempDir, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertEquals(false,result.isEmpty());
        assertEquals(1,result.size());
        assertThat(result.get(0).getName(), containsString("tempfile1"));

    }

    @Test
    public void doList_when_dir_has_two_files() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempFile = Files.createTempFile(tempDir, "tempfile1", ".tmp");
        Path tempFile2 = Files.createTempFile(tempDir, "tempfile2", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertEquals(false,result.isEmpty());
        assertEquals(2,result.size());
        assertThat(result.get(0).getName(), containsString("tempfile1"));
        assertThat(result.get(1).getName(), containsString("tempfile2"));

    }

    @Test
    public void doList_when_is_recursive_false_and_dir_has_file_in_subdir() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempDir2 = Files.createTempDirectory(tempDir,"subdir");
        Path tempFile = Files.createTempFile(tempDir2, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertEquals(false,result.isEmpty());
        assertEquals(1,result.size());

        assertThat(result.get(0).getName(), containsString("subdir"));
        assertTrue(result.get(0).isDirectory());

    }

    @Test
    public void doList_when_is_recursive_true_and_dir_has_file_in_subdir() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path tempDir2 = Files.createTempDirectory(tempDir,"subdir");
        Path tempFile = Files.createTempFile(tempDir2, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertEquals(false,result.isEmpty());
        assertEquals(1,result.size());

        assertThat(result.get(0).getName(), containsString("subdir"));
        assertTrue(result.get(0).isDirectory());

        //assertThat(result.get(1).getName(), containsString("tempfile1"));

    }

    @Test
    public void doList_when_is_recursive_true_and_file_is_five_level_deep() throws URISyntaxException, ClientCommandLsException, IOException
    {
        Path level1 = Files.createTempDirectory(tempDir,"level1");
        Path level2 = Files.createTempDirectory(level1,"level2");
        Path level3 = Files.createTempDirectory(level2,"level3");
        Path level4 = Files.createTempDirectory(level3,"level4");
        Path level5 = Files.createTempDirectory(level4,"level5");
        Path tempFile = Files.createTempFile(level5, "tempfile1", ".tmp");

        List<ClientListEntry> result = uut.ls(".");
        assertEquals(false,result.isEmpty());
        assertEquals(1,result.size());

        assertThat(result.get(0).getName(), containsString("level1"));
        assertTrue(result.get(0).isDirectory());

        /**
        assertThat(result.get(1).getName(), containsString("level2"));
        assertTrue(result.get(1).isDirectory());

        assertThat(result.get(2).getName(), containsString("level3"));
        assertTrue(result.get(2).isDirectory());

        assertThat(result.get(3).getName(), containsString("level4"));
        assertTrue(result.get(3).isDirectory());

        assertThat(result.get(4).getName(), containsString("level5"));
        assertTrue(result.get(4).isDirectory());

        assertThat(result.get(5).getName(), containsString("tempfile1"));
         */

    }
}
