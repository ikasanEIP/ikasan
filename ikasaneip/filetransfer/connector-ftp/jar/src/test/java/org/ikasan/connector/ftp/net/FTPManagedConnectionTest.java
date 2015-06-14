package org.ikasan.connector.ftp.net;

import org.ikasan.connector.ftp.outbound.FTPConnectionRequestInfo;
import org.ikasan.connector.ftp.outbound.FTPManagedConnection;
import org.ikasan.connector.ftp.outbound.FTPManagedConnectionFactory;
import org.junit.Ignore;
import org.junit.Test;

public class FTPManagedConnectionTest {

    @Test
    @Ignore
    public void test() throws Exception {

        System.out.println("################ Starting Test ################################");

        FTPConnectionRequestInfo info = new FTPConnectionRequestInfo();
        info.setRemoteHostname("141.231.2.10");
        info.setRemotePort(21);
        info.setUsername("IBJIFTP");
        info.setPassword("Thursday1");
        info.setClientID("FTPManagedConnectionTest");
        info.setActive(false);
        info.setMaxRetryAttempts(3);
        info.setConnectionTimeout(10000);

        info.setIsFTPS(true);
        info.setFtpsProtocol("SSL");
        info.setFtpsPort(21);
        info.setFtpsIsImplicit(false);
        info.setFtpsKeyStoreFilePath("/home2/gulese/ftps/sftu03ftp.jks");
        info.setFtpsKeyStoreFilePassword("MHI555");

        FTPManagedConnectionFactory factory = new FTPManagedConnectionFactory();
        factory.setLocalHostname("adl-swiftgw01");

        FTPManagedConnection connection = new FTPManagedConnection(factory, info);

        System.out.println("################ Opening Session ################################");
        connection.openSession();

        System.out.println("################ Completed Test ################################");

    }



}
