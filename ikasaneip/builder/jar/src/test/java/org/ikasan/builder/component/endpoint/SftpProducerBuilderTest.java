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
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.jta.JtaTransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class SftpProducerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocks
     */
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");
    final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class, "mockBaseFileTransferDao");
    final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class, "mockFileChunkDao");
    final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class, "mockTransactionalResourceCommandDAO");

    /**
     * Test successful builder creation with sftp producer Options.
     */
    @Test
    void sftpProducer_build_when_configuration_sftp_conf_provided() {

        SftpProducerBuilder sftpProducerBuilder = new SftpProducerBuilderImpl( null, null, null, null);


        Producer sftpProducer = sftpProducerBuilder
                .setChecksumDelivered(true)
                .setClientID("testClientId")
                .setOutputDirectory("test/dir")
                .setRenameExtension("TMP")
                .setTempFileName("testTmp")
                .setCleanUpChunks(true)
                .setCleanupJournalOnComplete(true)
                .setCreateParentDirectory(true)
                .setOverwrite(true)
                .setUnzip(true)
                .setRemoteHost("testsftphost")
                .setPrivateKeyFilename("testprivatekey")
                .setMaxRetryAttempts(3)
                .setRemotePort(22)
                .setKnownHostsFilename("testknownhost")
                .setUsername("testUser")
                .setPassword("testPassword")
                .setConnectionTimeout(300)
                .setPreferredKeyExchangeAlgorithm("testalg")
                .setConfiguredResourceId("testConfigId")
                .build();

        assertTrue(sftpProducer instanceof SftpProducer, "instance should be a SftpProducer");

        SftpProducerConfiguration configuration = ((ConfiguredResource<SftpProducerConfiguration>) sftpProducer).getConfiguration();
        assertTrue(configuration.getChecksumDelivered(), "checksum should be 'true'");
        assertEquals("testClientId", configuration.getClientID(), "clientID should be 'testClientId'");
        assertEquals("test/dir", configuration.getOutputDirectory(), "outputDirectory should be 'test/dir'");
        assertEquals("TMP", configuration.getRenameExtension(), "renameExtension should be 'TMP'");
        assertEquals("testTmp", configuration.getTempFileName(), "tempFileName should be 'testTmp'");
        assertTrue(configuration.getCleanUpChunks(), "cleanUpChunks should be 'true'");
        assertTrue(configuration.getCleanupJournalOnComplete(), "cleanupJournalOnComplete should be 'true'");
        assertTrue(configuration.getCreateParentDirectory(), "createParentDirectory should be 'true'");
        assertTrue(configuration.getOverwrite(), "overwrite should be 'true'");
        assertTrue(configuration.getUnzip(), "unzip should be 'true'");
        assertEquals("testsftphost", configuration.getRemoteHost(), "remoteHost should be 'testsftphost'");
        assertEquals("testprivatekey", configuration.getPrivateKeyFilename(), "privateKeyFilename should be 'testprivatekey'");
        assertEquals(3, configuration.getMaxRetryAttempts().intValue(), "maxRetryAttempts should be '3'");
        assertEquals(22, configuration.getRemotePort().intValue(), "remotePort should be '22'");
        assertEquals("testknownhost", configuration.getKnownHostsFilename(), "knownHostsFilename should be 'testknownhost'");
        assertEquals("testUser", configuration.getUsername(), "username should be 'testUser'");
        assertEquals("testPassword", configuration.getPassword(), "password should be 'testPassword'");
        assertEquals(300, configuration.getConnectionTimeout().intValue(), "connectionTimeout should be '300'");
        assertEquals("testalg", configuration.getPreferredKeyExchangeAlgorithm(), "preferredKeyExchangeAlgorithm should be 'testalg'");

        mockery.assertIsSatisfied();
    }


    @Test
    void sftpProducer_build_when_configurationId_not_provided() {

        SftpProducerBuilder sftpProducerBuilder = new SftpProducerBuilderImpl(null, null, null, null);

        sftpProducerBuilder.setOutputDirectory("test").build();
    }

}
