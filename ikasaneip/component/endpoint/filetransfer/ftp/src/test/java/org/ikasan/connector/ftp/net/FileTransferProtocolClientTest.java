package org.ikasan.connector.ftp.net;

import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;


import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by majean on 12/06/2015.
 */
class FileTransferProtocolClientTest
{

    private static final String HOME_DIR = "/";
    private static final String FTP_DIR = "/ftpdir";
    private static final String FILE_1 = "sample1.txt";
    private static final String CONTENTS_1 = "abcdef 1234567890";
    private static final String FILE_2 = "sample2.txt";
    private static final String CONTENTS_2 = "abcdef121212 1234567890";
    private static final String HOST = "localhost";
    private static final String USERNAME = "ftpAccount";
    private static final String PASSWORD = "ftpPassword";

    private int port;
    private FakeFtpServer fakeFtpServer;

    private  FileTransferProtocolClient uut;


    @BeforeEach
    void setup() throws ClientConnectionException
    {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port

        FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry  directoryEntry1 = new DirectoryEntry(HOME_DIR);
        DirectoryEntry  directoryEntry2 = new DirectoryEntry(FTP_DIR);

        fileSystem.add(directoryEntry1);
        fileSystem.add(directoryEntry2);
        fileSystem.add(new FileEntry(FTP_DIR+"/"+FILE_1, CONTENTS_1));
        fileSystem.add(new FileEntry(FTP_DIR+"/"+FILE_2, CONTENTS_2));


        fakeFtpServer.setFileSystem(fileSystem);

        UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, FTP_DIR);
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();
        port = fakeFtpServer.getServerControlPort();

        uut = new FileTransferProtocolClient(true,HOST,HOST,3,PASSWORD,port,USERNAME,null,null,null,null);

        uut.connect();
        uut.login();

    }

    @AfterEach
    void tearDown() throws Exception {
        fakeFtpServer.stop();
    }

    @Test
    void ls_when_input_single_file() throws URISyntaxException, ClientCommandLsException
    {

        String input = FTP_DIR+"/"+FILE_1;
        List<ClientListEntry> result =  uut.ls(input);

        assertEquals(1, result.size());
        assertEquals(FILE_1 ,result.get(0).getName());
    }

    @Test
    void ls_when_input_single_file_2() throws URISyntaxException, ClientCommandLsException
    {

        String input = FTP_DIR+"/"+FILE_2;
        List<ClientListEntry> result =  uut.ls(input);

        assertEquals(1, result.size());
        assertEquals(FILE_2 ,result.get(0).getName());
    }


    @Test
    void ls_when_input_is_directory() throws URISyntaxException, ClientCommandLsException
    {

        List<ClientListEntry> result =  uut.ls(FTP_DIR);

        assertEquals(2, result.size());
    }

    @Test
    void ls_when_input_directory_does_not_exist() throws URISyntaxException, ClientCommandLsException
    {
        assertThrows(ClientCommandLsException.class, () -> {

            List<ClientListEntry> result = uut.ls("/does_not_exsist");
        });
    }


    @Test
    void cd_when_correctDir() throws URISyntaxException, ClientCommandCdException
    {

        String input = FTP_DIR+"/";
        uut.cd(input);
     }

    @Test
    void cd_when_dir_does_not_exist() throws URISyntaxException, ClientCommandCdException
    {
        assertThrows(ClientCommandCdException.class, () -> {

            String input = "/does_not_exist";
            uut.cd(input);
        });
    }


    @Test
    void login_when_credentials_are_wrong()
        throws URISyntaxException, ClientCommandLsException, ClientConnectionException
    {
        assertThrows(ClientConnectionException.class, () -> {

            FakeFtpServer   fakeFtpServer = new FakeFtpServer();
            fakeFtpServer.setServerControlPort(0);  // use any free port

            FileSystem fileSystem = new UnixFakeFileSystem();
            DirectoryEntry  directoryEntry1 = new DirectoryEntry(HOME_DIR);
            DirectoryEntry  directoryEntry2 = new DirectoryEntry(FTP_DIR);

            fileSystem.add(directoryEntry1);
            fileSystem.add(directoryEntry2);
            fileSystem.add(new FileEntry(FTP_DIR + "/" + FILE_1, CONTENTS_1));
            fileSystem.add(new FileEntry(FTP_DIR + "/" + FILE_2, CONTENTS_2));


            fakeFtpServer.setFileSystem(fileSystem);

            UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, FTP_DIR);
            fakeFtpServer.addUserAccount(userAccount);

            fakeFtpServer.start();
            int port = fakeFtpServer.getServerControlPort();

            uut = new FileTransferProtocolClient(true, HOST, HOST, 3, "test", port, USERNAME, null, null, null, null);

            uut.connect();
            uut.login();

        });

    }


    @Test
    void login_when_user_name_is_not_valid()
        throws URISyntaxException, ClientCommandLsException, ClientConnectionException
    {
        assertThrows(ClientConnectionException.class, () -> {

            FakeFtpServer   fakeFtpServer = new FakeFtpServer();
            fakeFtpServer.setServerControlPort(0);  // use any free port

            FileSystem fileSystem = new UnixFakeFileSystem();
            DirectoryEntry  directoryEntry1 = new DirectoryEntry(HOME_DIR);
            DirectoryEntry  directoryEntry2 = new DirectoryEntry(FTP_DIR);

            fileSystem.add(directoryEntry1);
            fileSystem.add(directoryEntry2);
            fileSystem.add(new FileEntry(FTP_DIR + "/" + FILE_1, CONTENTS_1));
            fileSystem.add(new FileEntry(FTP_DIR + "/" + FILE_2, CONTENTS_2));


            fakeFtpServer.setFileSystem(fileSystem);

            UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, FTP_DIR);
            fakeFtpServer.addUserAccount(userAccount);

            fakeFtpServer.start();
            int port = fakeFtpServer.getServerControlPort();

            uut = new FileTransferProtocolClient(true, HOST, HOST, 3, PASSWORD, port, "wrongUser", null, null, null, null);

            uut.connect();
            uut.login();

        });

    }

}
