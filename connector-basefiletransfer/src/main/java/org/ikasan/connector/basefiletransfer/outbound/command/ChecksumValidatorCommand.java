/*
 * $Id: ChecksumValidatorCommand.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/outbound/command/ChecksumValidatorCommand.java $
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;

import org.ikasan.common.Payload;
import org.ikasan.common.util.ChecksumUtils;
import org.ikasan.common.util.checksum.ChecksumSupplier;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.ChecksumFailedException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;

/**
 * Command for comparing the calculated checksum of a retrieved file with that
 * provided by the remote system, if any
 * 
 * @author Ikasan Development Team
 */
public class ChecksumValidatorCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(FileDiscoveryCommand.class);

    /** Parser used to consume checksum foreign checksum file */
    private ChecksumSupplier checksumSupplier;
    
    /** Flag to skip execution if set */
    private boolean skip = false;

    /** The path of the checksum file we are validating against */
    protected String checksumFilePath;
    
    /** Flag for deletion of the checksum file after successful pickup */
    private boolean destructive = false;
    
    /** Default constructor for Hibernate */
    public ChecksumValidatorCommand()
    {
        // Do Nothing, we don't need to get checksumSupplier from the bean factory as 
        // the constructor below is always called before performExecute.
    }
    
    /**
     * Constructor
     * 
     * @param checksumFileParser The checksum file parser
     * @param destructive Whether it's a destructive read
     * @param checksumFilePath THe path of where the checksum file is
     */
    public ChecksumValidatorCommand(ChecksumSupplier checksumFileParser, boolean destructive, String checksumFilePath)
    {
        super();
        this.checksumSupplier = checksumFileParser;
        this.destructive = destructive;
        this.checksumFilePath = checksumFilePath + this.checksumSupplier.getFileExtension();
    }

    @Override
    protected void doCommit() throws ResourceException
    {
        logger.info("commit called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (destructive)
        {
            logger.debug("Deleting checksum file [" + checksumFilePath + "]");
            deleteFile(checksumFilePath);
        }
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        ExecutionOutput result = new ExecutionOutput();
        if (skip)
        {
            return result;
        }
   
        Payload payload = (Payload) executionContext
            .getRequired(ExecutionContext.PAYLOAD);

        try
        {
            String generatedChecksum = payload.getChecksum(); // which we know
                                                                // to be MD5
            String generatedChecksumAlgorithm = payload.getChecksumAlg();
            if (!generatedChecksumAlgorithm.equals(checksumSupplier
                .getAlgorithmName()))
            {
                throw new UnsupportedEncodingException(
                    "File was previously checksummed with an unsupported algorithm: [" + generatedChecksumAlgorithm + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Get the checksum file
            URI checksumURI = new URI(checksumFilePath);
            logger
                .debug("Checksum File URI is: [" + checksumURI.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            ClientListEntry checksumEntry = new ClientListEntry();
            checksumEntry.setUri(checksumURI);
            // Get the checksum file
            InputStream checksumFile = getContentAsStream(checksumEntry);

            String checksumFromFile = checksumSupplier
                .extractChecksumFromChecksumFile(checksumFile);

            // Close the InputStream now that we're done with it
            checksumFile.close();
            
            logger.debug("generatedChecksum [" + generatedChecksum + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            logger.debug("checksumFromFile [" + checksumFromFile + "]"); //$NON-NLS-1$ //$NON-NLS-2$

            if (!ChecksumUtils.checksumMatch(checksumFromFile,
                generatedChecksum))
            {
                throw new ChecksumFailedException("Checksums didn't match!"); //$NON-NLS-1$
            }
        }
        catch (UnsupportedEncodingException e)
        {
            logger.warn("An Unsupported Encoding related exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (IOException e)
        {
            logger.warn("An IO related exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (URISyntaxException e)
        {
            logger.warn("An URI Syntax related exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        catch (ChecksumFailedException e)
        {
            logger.warn("A checksum failed related exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        logger.info("checksum on file matched that from foreign system"); //$NON-NLS-1$
        return result;
    }

    @Override
    protected void doRollback()
    {
        logger.info("rollback called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Setter method to allow the skip flag to be set later than construction
     * @param skip skip flag to set
     */
    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    /**
     * Do we delete the file on successful delivery?
     * @return true if we delete, else false
     */
    public boolean isDestructive()
    {
        return destructive;
    }
    
    /**
     * Setter method to allow the destructive flag to be set later than construction
     * @param destructive destructive flag to set
     */
    public void setDestructive(boolean destructive)
    {
        this.destructive = destructive;
    }

    /**
     * Accessor method for checksum file path
     * @return checksumFilePath
     */
    public String getChecksumFilePath()
    {
        return checksumFilePath;
    }

    /**
     * Setter method for checksumFilePath, used by Hibernate
     * @param checksumFilePath The file path of hte checksum file
     */
    @SuppressWarnings("unused")
    private void setChecksumFilePath(String checksumFilePath)
    {
        this.checksumFilePath = checksumFilePath;
    }
    
}
