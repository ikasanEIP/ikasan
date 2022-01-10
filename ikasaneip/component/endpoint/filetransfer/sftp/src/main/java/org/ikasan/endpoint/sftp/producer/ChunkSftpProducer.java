package org.ikasan.endpoint.sftp.producer;

import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.sftp.SftpResourceNotStartedException;
import org.ikasan.filetransfer.Payload;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.resource.ResourceException;
import java.util.HashMap;
import java.util.Map;

public class ChunkSftpProducer extends SftpProducer
{
    public ChunkSftpProducer(JtaTransactionManager transactionManager, BaseFileTransferDao baseFileTransferDao,
                             FileChunkDao fileChunkDao, TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        super(transactionManager, baseFileTransferDao, fileChunkDao, transactionalResourceCommandDAO);
    }

    public void invoke(Payload payload) throws EndpointException
    {
        try
        {
            if ( activeFileTransferConnectionTemplate != null )
            {
                Map<String,String> outputs = new HashMap<>();
                activeFileTransferConnectionTemplate
                    .deliverPayload(payload,
                        configuration.getOutputDirectory(),
                        outputs,
                        configuration.getOverwrite(),
                        configuration.getRenameExtension(),
                        configuration.getChecksumDelivered(),
                        configuration.getUnzip(),
                        configuration.getCleanUpChunks());

            }
            else
            {
                throw new SftpResourceNotStartedException(
                    "ChunkSftpProducer was not started correctly. activeFileTransferConnectionTemplate is null.");
            }
        }
        catch (ResourceException e)
        {
            this.switchActiveConnection();
            throw new EndpointException(e);
        }
    }

}
