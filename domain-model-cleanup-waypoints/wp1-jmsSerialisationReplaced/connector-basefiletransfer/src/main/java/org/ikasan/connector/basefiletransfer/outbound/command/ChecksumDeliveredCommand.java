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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.BaseFileTransferMappedRecord;
import org.ikasan.connector.basefiletransfer.net.ChecksumFailedException;

/**
 * Command for comparing the calculated checksum of a delivered file with that
 * provided by the remote system, if any
 * 
 * @author Ikasan Development Team 
 */
public class ChecksumDeliveredCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(FileDiscoveryCommand.class);

    /** 
     * Constructor 
     * 
     * TODO Check that we should actually be calling super as that reloads state
     * to be initial and we might not want that on reload of command from DB
     */
    public ChecksumDeliveredCommand()
    {
        super();
    }

    @Override
    protected void doCommit()
    {
        logger.info("commit called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        String deliveredPath = executionContext
            .getRequiredString(ExecutionContext.DELIVERED_FILE_PATH_PARAM);
        
        logger.debug("checksum delivered got deliveredPath: [" + deliveredPath + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        Payload payload = (Payload) executionContext
            .getRequired(ExecutionContext.PAYLOAD);

        try
        {
            //reload the delivered file from the remote file system, and checksum it.
            BaseFileTransferMappedRecord file = getFile(deliveredPath);
            
            if (file == null)
            {
                throw new ChecksumFailedException("Could not retrieve delivered file!"); //$NON-NLS-1$
            }
            
            // which we know to be MD5
            String generatedChecksum = getChecksum(payload.getContent());
            String reloadedChecksum = getChecksum(file.getContent());
  
               

            if (!generatedChecksum.equals(reloadedChecksum))
            {
                throw new ChecksumFailedException("Checksums didn't match!"); //$NON-NLS-1$
            }
        }

        catch (ChecksumFailedException e)
        {
            logger.warn("A checksum failed related exception occurred!", e); //$NON-NLS-1$
            throw new ResourceException(e);
        }
        logger.info("checksum on file matched that from foreign system"); //$NON-NLS-1$
        return new ExecutionOutput();
    }

    @Override
    protected void doRollback()
    {
        logger.info("rollback called on this command: [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Get the MD5 checksum given an ImputStream
     * 
     * @param byte array for which to calculate the checksum
     * @return <code>String</code> representing the checksum
     */
    public static String getChecksum(byte[] input)
    {
        MessageDigest checksum;
		try {
			checksum = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		checksum.update(input);
        
       
        byte[] byteDigest = checksum.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteDigest.length; i++)
        {
            String hex = Integer.toHexString(0xff & byteDigest[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return new String(sb.toString());
    }
}
