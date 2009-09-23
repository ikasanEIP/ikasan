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
