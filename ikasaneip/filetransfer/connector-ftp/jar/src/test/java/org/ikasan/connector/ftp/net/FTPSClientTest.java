package org.ikasan.connector.ftp.net;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.util.TrustManagerUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class FTPSClientTest {

    @Test
    @Ignore
    public  void test() throws Exception {

        String server = "141.231.2.10";
        String username = "IBJIFTP";
        String password = "Thursday1";
        String protocol = "SSL"; // TLS / null (SSL)
        int port = 21;
        int timeoutInMillis = 10000;
        boolean isImpicit = false;

        File storeFile = new File("/home2/gulese/ftps/sftu03ftp.jks");

        KeyStore keyStore = loadStore("JKS", storeFile, "MHI555");
        X509TrustManager defaultTrustManager = TrustManagerUtils.getDefaultTrustManager(keyStore);

        FTPSClient client = new FTPSClient(protocol, isImpicit);

        client.setTrustManager(defaultTrustManager);
        client.setDataTimeout(timeoutInMillis);
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        System.out.println("################ Connecting to Server ################################");

        try
        {
            int reply;
            System.out.println("################ Connect Call ################################");
            client.connect(server, port);

            System.out.println("Connected to " + server + ".");
            reply = client.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                client.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            client.execPBSZ(0);  // Set protection buffer size
            client.execPROT("P"); // Set data channel protection to private
            client.execCCC();
            //client.feat();
            client.enterLocalPassiveMode();

            client.login(username, password);
            System.out.println("################ Login Success ################################");


            boolean result = client.changeWorkingDirectory("MHI");
            System.out.println("client.changeWorkingDirectory(\"MHI\") Result " + result);

            result = client.changeWorkingDirectory("MHIToSwift");
            System.out.println("client.changeWorkingDirectory(\"MHIToSwift\") Result " + result);

            result = client.changeWorkingDirectory("SAAReceived");
            System.out.println("client.changeWorkingDirectory(\"SAAReceived\") Result " + result);

            System.out.println("PWD: "+client.printWorkingDirectory());

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


    private static KeyStore loadStore(String storeType, File storePath, String storePass)
            throws KeyStoreException,  IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance(storeType);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(storePath);
            ks.load(stream, storePass.toCharArray());
        } finally {
            Util.closeQuietly(stream);
        }
        return ks;
    }

}



