package org.ikasan.connector.ftp.net;

import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by majean on 12/06/2015.
 */
public class FileTransferProtocolClientTest
{

    private static final String HOME_DIR = "/";
    private static final String FILE = "/dir/sample.txt";
    private static final String CONTENTS = "abcdef 1234567890";
    private static final String HOST = "localhost";
    private static final String USERNAME = "ftpAccount";
    private static final String PASSWORD = "ftpPassword";

    private int port;
    private FakeFtpServer fakeFtpServer;

    private  FileTransferProtocolClient uut;


    @Before
    public void setup() throws ClientConnectionException
    {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port

        FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry  directoryEntry1 = new DirectoryEntry("/");
        directoryEntry1.setPermissions(new Permissions("rwxrwxrwx"));
        DirectoryEntry  directoryEntry2 = new DirectoryEntry("/dir");
        directoryEntry2.setPermissions(new Permissions("rwxrwxrwx"));

        fileSystem.add(directoryEntry1);

        fileSystem.add(directoryEntry2);

        fileSystem.add(new FileEntry(FILE, CONTENTS));
        fakeFtpServer.setFileSystem(fileSystem);

        UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, HOME_DIR);
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();
        port = fakeFtpServer.getServerControlPort();

        uut = new FileTransferProtocolClient(true,HOST,HOST,3,PASSWORD,port,USERNAME,null,null,null,null);
        uut.connect();

    }

    @After
    public void tearDown() throws Exception {
        fakeFtpServer.stop();
    }

    @Test
    public void test_file() throws URISyntaxException, ClientCommandLsException
    {

        List<ClientListEntry> result =  uut.ls(CONTENTS);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void test_file_parent_dir() throws URISyntaxException, ClientCommandLsException
    {

        List<ClientListEntry> result =  uut.ls("/");

        Assert.assertEquals(1, result.size());
    }

    @Test(expected = ClientCommandLsException.class)
    public void test_dir() throws URISyntaxException, ClientCommandLsException
    {

        List<ClientListEntry> result =  uut.ls("/does_not_esist");
    }
}
