/* 
 * $Id$
 * $URL$
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
     * @throws ResourceException - Exception if JCA connector fails
     */
    public void deliverInputStream(final InputStream inputStream, final String fileName, final String outputDir, final boolean overwrite,
            final String renameExtension, final boolean checksumDelivered, final boolean unzip) throws ResourceException
    {
        execute(new ConnectionCallback()
        {
            public Object doInConnection(Connection connection) throws ResourceException
            {
                ((BaseFileTransferConnection) connection).deliverInputStream(inputStream, fileName, outputDir,  overwrite, renameExtension, checksumDelivered,unzip);
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
