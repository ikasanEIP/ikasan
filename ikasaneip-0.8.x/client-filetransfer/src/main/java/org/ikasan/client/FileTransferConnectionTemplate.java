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
package org.ikasan.client;

import java.io.InputStream;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.common.Payload;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;

/**
 * Test the FileTRansferConnectionTempalte class
 * 
 * @author Ikasan Development Team
 */
public class FileTransferConnectionTemplate extends ConnectionTemplate
{
    /**
     * Constructor
     * 
     * @param connectionFactory - The connection factory
     * @param connectionSpec - THe connection spec
     */
    public FileTransferConnectionTemplate(ConnectionFactory connectionFactory, ConnectionSpec connectionSpec)
    {
        super(connectionFactory, connectionSpec);
    }

    /**
     * Test Delivering a payload
     * 
     * @param payload - The payload to deliver
     * @param outputDir - The directory to place the file in
     * @param outputTargets - The Map of targets to deliver the file to
     * @param overwrite - Overwrite existing files flag
     * @param renameExtension - The extension for the temp file rename
     * @param checksumDelivered - Flag for whether we perform checksumming
     * @param unzip - Flag for whether we unzip the delivered file
     * @param cleanup - Cleanup txn journal flag
     * @throws ResourceException - Exception if JCA connector fails
     * @deprecated - use deliverInputStream
     */
    public void deliverPayload(final Payload payload, final String outputDir, final Map<String, String> outputTargets, final boolean overwrite,
            final String renameExtension, final boolean checksumDelivered, final boolean unzip, final boolean cleanup) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                ((BaseFileTransferConnection) connection).deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered,
                    unzip, cleanup);
                return null;
            }
        });
    }
    
    /**
     * Delivering an InputStream
     * 
     * @param inputStream - The 'file'
     * @param fileName - The name of the file
     * @param outputDir - The directory to place the file in
     * @param overwrite - Overwrite existing files flag
     * @param renameExtension - The extension for the temp file rename
     * @param checksumDelivered - Flag for whether we perform checksumming
     * @param unzip - Flag for whether we unzip the delivered file
     * @param createParentDirectory -  
     * @param tempFileName -
     * @throws ResourceException - Exception if JCA connector fails
     */
    public void deliverInputStream(final InputStream inputStream, final String fileName, final String outputDir, final boolean overwrite,
            final String renameExtension, final boolean checksumDelivered, final boolean unzip, final boolean createParentDirectory,
            final String tempFileName) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                ((BaseFileTransferConnection) connection).deliverInputStream(inputStream, fileName, outputDir,  overwrite, renameExtension, checksumDelivered,unzip, createParentDirectory, tempFileName);
                return null;
            }
        });
    }

    /**
     * Test the getDiscoveredFile
     * 
     * @param sourceDir - The directory to get the file from
     * @param filenamePattern - The pattern to search on
     * @param renameOnSuccess - Whether we rename a file on successful delivery
     * @param renameOnSuccessExtension - The extension to rename to
     * @param moveOnSuccess - Whether we move the file on successful delivery
     * @param moveOnSuccessNewPath - Where we move the file to
     * @param chunking - Whether we are chunking enabled
     * @param chunkSize - The size of the chunks
     * @param checksum - Whether we checksum the pickup 
     * @param minAge - The minimum age the file has to be in order to be picked up 
     * @param destructive - Whether we pick up destructively
     * @param filterDuplicates - Whether we filter duplicates
     * @param filterOnFilename - Whether we filter duplicates based on file name
     * @param filterOnLastModifedDate - Whether we filter duplicates based on file name
     * @param chronological - Whether we pickup files in age order
     * 
     * @return The discovered file as a Payload
     * @throws ResourceException - Exception if the JCA connector fails
     */
    public Payload getDiscoveredFile(final String sourceDir, final String filenamePattern, final boolean renameOnSuccess,
            final String renameOnSuccessExtension, final boolean moveOnSuccess, final String moveOnSuccessNewPath, final boolean chunking, final int chunkSize,
            final boolean checksum, final long minAge, final boolean destructive,
            final boolean filterDuplicates, final boolean filterOnFilename, final boolean filterOnLastModifedDate, final boolean chronological) throws ResourceException
    {
        return (Payload) execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                Payload discoveredFile = ((BaseFileTransferConnection) connection).getDiscoveredFile(sourceDir, filenamePattern, renameOnSuccess, renameOnSuccessExtension, moveOnSuccess, moveOnSuccessNewPath, chunking, chunkSize, checksum, minAge, destructive, filterDuplicates, filterOnFilename, filterOnLastModifedDate, chronological);
                return discoveredFile;
            }
        });
    }
    
    /**
     * Housekeep the FileFilter table
     * 
     * @param maxRows Max rows the housekeeper will deal with
     * @param ageOfFiles How old the files have to be in days to 
     * be considered for housekeeping
     * @throws ResourceException - Exception if JCA connector fails
     */
    public void housekeep(final int maxRows, final int ageOfFiles) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                BaseFileTransferConnection baseFileTransferConnection = (BaseFileTransferConnection)connection;
                baseFileTransferConnection.housekeep(maxRows, ageOfFiles);
                return null;
            }
        });
    }
    
}
