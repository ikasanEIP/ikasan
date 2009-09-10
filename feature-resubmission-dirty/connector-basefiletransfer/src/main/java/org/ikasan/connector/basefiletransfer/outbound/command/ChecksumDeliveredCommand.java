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

import java.io.UnsupportedEncodingException;

import javax.resource.ResourceException;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.common.factory.PayloadFactoryImpl;
import org.ikasan.common.util.ChecksumUtils;
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
    private static Logger logger = Logger.getLogger(ChecksumDeliveredCommand.class);
    
    private static final PayloadFactory payloadFactory = new PayloadFactoryImpl();

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
            String generatedChecksum = payload.getChecksum();
            
            // TODO This is a really poor way of doing checksums, delegating to the Payload class
            // Need to do this without using Payload
            
            Payload reloadedPayload = payloadFactory.newPayload("dummyId",
                file.getName(), payload.getSpec(),payload.getSrcSystem(),
                file.getContent());
            
            if (!payload.getChecksumAlg().equals(reloadedPayload
                .getChecksumAlg()))
            {
                throw new UnsupportedEncodingException(
                    "Payload was previously checksummed with a different checksum algorithm: [" + payload.getChecksumAlg() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            String checksumFromFile  = reloadedPayload.getChecksum();

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
}
