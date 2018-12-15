/*
 * $Id:$
 * $URL:$
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

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.ChecksumFailedException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.filetransfer.Payload;
import org.ikasan.filetransfer.util.ChecksumUtils;
import org.ikasan.filetransfer.util.checksum.ChecksumSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.resource.ResourceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

/**
 * Command for comparing the calculated checksum of a delivered file with that
 * provided by the remote system, if any
 *
 * @author Ikasan Development Team
 */
public class ChecksumDeliveredCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{

    /** The logger instance. */
    private static Logger logger = LoggerFactory.getLogger(FileDiscoveryCommand.class);

    private ChecksumSupplier checksumSupplier;

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

    /**
     * Constructor
     *
     * @param checksumSupplier The checksum supplier
     */
    public ChecksumDeliveredCommand(ChecksumSupplier checksumSupplier)
    {
        super();
        this.checksumSupplier = checksumSupplier;
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
        Payload payload = (Payload) executionContext.getRequired(ExecutionContext.PAYLOAD);

        try
        {
            //reload the delivered file from the remote file system, and checksum it.
            URI deliveredUri;
            try {
                deliveredUri = new URI(deliveredPath);
            } catch (URISyntaxException e) {
                logger.warn("Could not retrieve file to run checksum on. Invalid delivered file uri: '" + deliveredPath + "'", e); //$NON-NLS-1$
                throw new ResourceException(e);
            }
            ClientListEntry deliveredFileEntry = new ClientListEntry();
            deliveredFileEntry.setUri(deliveredUri);
            String deliveredFileChecksum;
            try (InputStream deliveredFileStream = getContentAsStream(deliveredFileEntry)) {
                if (deliveredFileStream == null)
                {
                    throw new ChecksumFailedException("Could not retrieve delivered file!"); //$NON-NLS-1$
                }
                deliveredFileChecksum = checksumSupplier.calculateChecksumString(deliveredFileStream);
            } catch (IOException e) {
                throw new ResourceException("Failed to run checksum on delivered file.", e);
            }

            String localPayloadChecksum;
            try (InputStream localPayloadStream = payload.getInputStream()){
                localPayloadChecksum = checksumSupplier.calculateChecksumString(localPayloadStream);
            } catch (IOException e) {
                throw new ResourceException("Failed to run checksum on local payload.", e);
            }

            if (ChecksumUtils.checksumMatch(deliveredFileChecksum, localPayloadChecksum))
            {
                throw new ChecksumFailedException(
                    format("Checksums do not match. Delivered file: '%s', checksum: '%s', local file checksum: '%s'",
                        deliveredFileChecksum, deliveredPath, localPayloadChecksum)); //$NON-NLS-1$
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
}
