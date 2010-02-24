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
package org.ikasan.connector.basefiletransfer.outbound;

import java.util.Map;
import java.io.InputStream;

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
            boolean checksumDelivered, boolean unzip) throws ResourceException;

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
