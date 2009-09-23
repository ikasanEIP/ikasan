/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
