/*
 * $Id: FileTransferClient.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/net/FileTransferClient.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.basefiletransfer.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.ikasan.connector.base.command.TransactionalResource;

/**
 * An interface for various File Transfer Clients to implement
 * 
 * @author Ikasan Development Team 
 */
public interface FileTransferClient extends TransactionalResource
{
    /**
     * Method to change directory
     * 
     * @param targetPath
     * @throws ClientCommandCdException
     */
    public void cd(String targetPath) throws ClientCommandCdException;    
    
    /**
     * Method to delete a remote directory
     * 
     * @param directoryPath
     * @param recurse (whether to delete recursively or not)
     * 
     * @throws ClientException
     * @throws ClientCommandLsException 
     */
    public void deleteRemoteDirectory(String directoryPath, boolean recurse) throws ClientException, ClientCommandLsException;    
    
    /**
     * Method to delete a remote file
     * 
     * @param filename
     * @throws ClientException
     */
    public void deleteRemoteFile(String filename) throws ClientException;    
    
    /**
     * Gets a BaseFileTransferMappedRecord (a 'File') given a ClientListEntry
     *  
     * @param clientListEntry
     * @return BaseFileTransferMappedRecord (a 'File')
     * 
     * @throws ClientCommandGetException
     */
    public BaseFileTransferMappedRecord get(ClientListEntry clientListEntry) throws ClientCommandGetException;

    /**
     * Gets a BaseFileTransferMappedRecord (a 'File') given a filePath
     *  
     * @param filePath
     * @return BaseFileTransferMappedRecord (a 'File')
     * 
     * @throws ClientCommandGetException
     */
    public BaseFileTransferMappedRecord get(String filePath) throws ClientCommandGetException;

    /**
     * Get a file as a OutputStream
     * 
     * @param filePath
     * @param outputStream
     * @throws ClientCommandGetException
     */
    public void get(String filePath, OutputStream outputStream) throws ClientCommandGetException;
    
    /**
     * Get a file as an OutputStream given an offset and a resume flag
     * 
     * @param filePath
     * @param outputStream
     * @param resume
     * @param offset
     * @throws ClientCommandGetException 
     */
    public void get(String filePath, OutputStream outputStream, int resume, long offset) throws ClientCommandGetException;    
    
    /**
     * Utilises the underlying API to return an InputStream as the result of the
     * GET operation
     * 
     * @param filePath
     * @return InputStream
     * 
     * @throws ClientCommandGetException
     */
    public InputStream getAsInputStream(String filePath) throws ClientCommandGetException;
    
    /**
     * Get the content of a ClientListFileEntry (a 'File') as an InputStream
     * 
     * @param entry
     * @return InputStream
     * @throws ClientCommandGetException
     */
    public InputStream getContentAsStream(ClientListEntry entry) throws ClientCommandGetException;    

    /**
     * Method to return a list of the contents of a directory
     * 
     * @param path
     * @return A list of ClientListEntries
     * 
     * @throws ClientCommandLsException
     * @throws URISyntaxException
     */
    public List<ClientListEntry> ls(String path) throws ClientCommandLsException, URISyntaxException;

    /**
     * Method to put a content on to a File Tansfer system
     * 
     * @param name (of the 'File')
     * @param content (of the 'File')
     * @throws ClientCommandPutException
     */
    public void put(String name, byte[] content) throws ClientCommandPutException;

    /**
     * Method to put the file using an OutputStream
     * 
     * @param fileName
     * @param inputstream 
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException 
     * @throws ClientCommandMkdirException 
     */
    public void putWithOutputStream(String fileName, InputStream inputstream)
        throws ClientCommandPutException, ClientCommandLsException, ClientCommandMkdirException;
    
    /** 
     * Method to list what directory you are currently in
     * 
     * @return Fully qualified path to what directory you are in.
     * @throws ClientCommandPwdException 
     */
    public String pwd() throws ClientCommandPwdException;

    /**
     * Method to rename a path (a 'File') 
     * 
     * @param currentPath
     * @param newPath
     * @throws ClientCommandRenameException
     */
    public void rename(String currentPath, String newPath) throws ClientCommandRenameException;
    
    /**TODO add comment */
    public void disconnect();
}
