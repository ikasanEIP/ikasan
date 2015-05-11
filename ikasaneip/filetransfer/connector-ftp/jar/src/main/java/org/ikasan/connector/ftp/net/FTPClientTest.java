package org.ikasan.connector.ftp.net;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.InetAddress;

public class FTPClientTest {

    public static void main(String[] args) throws Exception {

        String server = "adl-cmi10";
        String username = "majean";
        String password = "xxxxxxxx";
        int port = 21;
        int timeoutInMillis = 10000;

        FTPClient client = new FTPClient();

        client.setDataTimeout(timeoutInMillis);
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        System.out.println("################ Connecting to Server ################################");

        try
        {
            int reply;
            System.out.println("################ Connect Call ################################");
           // client.connect(server, port);

            client.connect(InetAddress.getByName(server), port, InetAddress.getByName("10.110.125.25"), 0);

            client.login(username, password);

            System.out.println("################ Login Success ################################");

            System.out.println("Connected to " + server + ".");
            reply = client.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                client.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            //client.enterLocalPassiveMode();

            boolean result = client.changeWorkingDirectory("MHI");
            System.out.println("client.changeWorkingDirectory(\"MHI\") Result " + result);

            result = client.changeWorkingDirectory("MHIToSwift");
            System.out.println("client.changeWorkingDirectory(\"MHIToSwift\") Result " + result);

            result = client.changeWorkingDirectory("SAAReceived");
            System.out.println("client.changeWorkingDirectory(\"SAAReceived\") Result " + result);

            InputStream inputStream = new FileInputStream("/home2/gulese/ftps/testFile.txt");
            result = client.storeFile("testFileRemote.txt", inputStream);
            System.out.println("client.storeFile(\"testFileRemote.txt\", inputStream) Result " + result);

        }
        catch (Exception e)
        {
            if (client.isConnected())
            {
                try
                {
                    client.disconnect();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            System.err.println("Could not connect to server.");
            e.printStackTrace();
            return;
        }
        finally
        {
            //client.disconnect();
            client.logout();
            System.out.println("# client disconnected");
        }
    }
}
