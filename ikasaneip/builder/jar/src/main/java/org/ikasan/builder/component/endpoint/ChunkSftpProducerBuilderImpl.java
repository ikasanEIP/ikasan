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
package org.ikasan.builder.component.endpoint;

import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.sftp.producer.ChunkSftpProducer;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided Sftp Producer Builder implementation.
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class ChunkSftpProducerBuilderImpl extends SftpProducerBuilderImpl implements ChunkSftpProducerBuilder
{
    /**
     * @param transactionManager
     * @param baseFileTransferDao
     * @param fileChunkDao
     * @param transactionalResourceCommandDAO
     */
    public ChunkSftpProducerBuilderImpl(JtaTransactionManager transactionManager,
                                        BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
                                        TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        super(transactionManager, baseFileTransferDao, fileChunkDao, transactionalResourceCommandDAO);
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public ChunkSftpProducer build()
    {
        ChunkSftpProducer sftpProducer = new ChunkSftpProducer(transactionManager, baseFileTransferDao, fileChunkDao,
                transactionalResourceCommandDAO);
        sftpProducer.setConfiguration(this.configuration);
        sftpProducer.setConfiguredResourceId(this.configuredResourceId);
        if(this.criticalOnStartup)
        {
            sftpProducer.setCriticalOnStartup(criticalOnStartup);
        }

        if(this.managedResourceRecoveryManager!=null)
        {
            sftpProducer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        }
        return sftpProducer;
    }

}

