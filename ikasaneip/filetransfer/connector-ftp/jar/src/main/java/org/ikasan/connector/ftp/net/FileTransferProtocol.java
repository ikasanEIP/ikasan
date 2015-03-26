package org.ikasan.connector.ftp.net;

import org.apache.log4j.Level;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.connector.basefiletransfer.net.ClientInitialisationException;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;

public interface FileTransferProtocol extends FileTransferClient {

    public void echoConfig(Level logLevel);
    public boolean isConnected();
    public void validateConstructorArgs() throws ClientInitialisationException;
    public void connect() throws ClientConnectionException;
    public void login() throws ClientConnectionException;

}
