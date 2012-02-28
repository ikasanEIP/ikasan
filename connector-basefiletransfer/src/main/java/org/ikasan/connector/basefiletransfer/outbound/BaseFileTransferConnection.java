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
package org.ikasan.connector.basefiletransfer.outbound;

import java.io.InputStream;
import java.util.Map;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.outbound.EISConnection;

/**
 * An interface for File Transfer based connections
 * 
 * @author Ikasan Development Team
 */
public interface BaseFileTransferConnection extends EISConnection
{

    /**
     * Set the managed connection
     * 
     * @param managedConnection -
     */
    public void setManagedConnection(TransactionalCommandConnection managedConnection);

    /**
     * Get the managed connection
     * 
     * @return Base File Transfer Managed Connection
     */
    public TransactionalCommandConnection getManagedConnection();
    
    /**
     * Delivers the content of this <code>Payload</code> using a File Transfer
     * client
     * 
     * @param payload <code>Payload</code> either containing, or refering to
     *            the file content
     * @param outputDir dir path on remote system to deliver the file or files
     * @param outputTargets map of output subdirectories for file delivery keyed by regular expression matches on the delivered file name
     * @param overwrite overwrite any existing files of the same name(s)
     * @param renameExtension temporary extension to use whilst delivering
     *            single file
     * @param checksumDelivered if true, attempt to reload the delivered file to
     *            compare the checksum value
     * @param unzip if true, attempt to unzip the payload
     * @param cleanup if true, cleans up any chunked data
     * 
     * @throws ResourceException -
     */
    public void deliverPayload(Payload payload, String outputDir, Map<String, String> outputTargets,
            boolean overwrite, String renameExtension,
            boolean checksumDelivered, boolean unzip, boolean cleanup) throws ResourceException;
    
    /**
     * Delivers the content of this <code>InputStream</code> using a File Transfer
     * client
     * 
     * @param inputStream <code>InputStream</code>  containing the file content
     * @param fileName name of destination file
     * @param outputDir dir path on remote system to deliver the file or files
     * @param overwrite overwrite any existing files of the same name(s)
     * @param renameExtension temporary extension to use whilst delivering
     *            single file
     * @param checksumDelivered if true, attempt to reload the delivered file to
     *            compare the checksum value
     * @param unzip if true, attempt to unzip the payload
     * @throws ResourceException -
     */
    public void deliverInputStream(InputStream inputStream, String fileName, String outputDir, boolean overwrite, String renameExtension,
            boolean checksumDelivered, boolean unzip, boolean createParentDirectory, final String tempFileName) throws ResourceException;

    /**
     * Discovers any new file on a remote system using a File Transfer
     * client and returns the first one that it finds
     * 
     * @param sourceDir directory to get files from 
     * @param filenamePattern files to look for
     * @param renameOnSuccess renaming flag
     * @param renameOnSuccessExtension extension used if renaming
     * @param moveOnSuccess moving flag
     * @param moveOnSuccessNewPath new path used if moving
     * @param chunking flag
     * @param chunkSize size of file chunks
     * @param checksum - checksum retrieval
     * @param minAge min age of file to match in seconds
     * @param destructive - Whether or not we destroy the file after successfully picking it up
     * @param filterDuplicates - Whether we filter out duplicates or not  
     * @param filterOnFilename filter out files based on their name
     * @param filterOnLastModifedDate filter out files based on last modified
     * @param chronological retrieve files on a chronological basis
     * 
     * @return Payload containing the file as content
     * @throws ResourceException -
     */
    public Payload getDiscoveredFile(String sourceDir, String filenamePattern,
            boolean renameOnSuccess, String renameOnSuccessExtension,
            boolean moveOnSuccess, String moveOnSuccessNewPath,
            boolean chunking, int chunkSize, boolean checksum,
            long minAge, boolean destructive, boolean filterDuplicates, 
            boolean filterOnFilename, boolean filterOnLastModifedDate,
            boolean chronological) throws ResourceException;
    
    /**
     * Housekeeping the file transfer based connectors
     * 
     * @param maxRows Max number of rows the housekeeper can work with at a time
     * @param ageOfFiles How old the entries should be (in days)
     * 
     * @throws ResourceException Exception thrown by the Connector
     */
    public void housekeep(int maxRows, int ageOfFiles) throws ResourceException;
    
}
