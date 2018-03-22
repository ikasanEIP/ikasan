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
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.transaction.jta.JtaTransactionManager;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class SftpProducerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();

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
    public void sftpProducer_build_when_configuration_sftp_conf_provided() {

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

        assertTrue("instance should be a SftpProducer", sftpProducer instanceof SftpProducer);

        SftpProducerConfiguration configuration = ((ConfiguredResource<SftpProducerConfiguration>) sftpProducer).getConfiguration();
        assertTrue("checksum should be 'true'", configuration.getChecksumDelivered());
        assertEquals("clientID should be 'testClientId'","testClientId", configuration.getClientID());
        assertEquals("outputDirectory should be 'test/dir'","test/dir", configuration.getOutputDirectory());
        assertEquals("renameExtension should be 'TMP'","TMP", configuration.getRenameExtension());
        assertEquals("tempFileName should be 'testTmp'","testTmp", configuration.getTempFileName());
        assertTrue("cleanUpChunks should be 'true'", configuration.getCleanUpChunks());
        assertTrue("cleanupJournalOnComplete should be 'true'", configuration.getCleanupJournalOnComplete());
        assertTrue("createParentDirectory should be 'true'", configuration.getCreateParentDirectory());
        assertTrue("overwrite should be 'true'", configuration.getOverwrite());
        assertTrue("unzip should be 'true'", configuration.getUnzip());
        assertEquals("remoteHost should be 'testsftphost'","testsftphost", configuration.getRemoteHost());
        assertEquals("privateKeyFilename should be 'testprivatekey'","testprivatekey", configuration.getPrivateKeyFilename());
        assertEquals("maxRetryAttempts should be '3'",3, configuration.getMaxRetryAttempts().intValue());
        assertEquals("remotePort should be '22'",22, configuration.getRemotePort().intValue());
        assertEquals("knownHostsFilename should be 'testknownhost'","testknownhost", configuration.getKnownHostsFilename());
        assertEquals("username should be 'testUser'","testUser", configuration.getUsername());
        assertEquals("password should be 'testPassword'","testPassword", configuration.getPassword());
        assertEquals("connectionTimeout should be '300'",300, configuration.getConnectionTimeout().intValue());
        assertEquals("preferredKeyExchangeAlgorithm should be 'testalg'","testalg", configuration.getPreferredKeyExchangeAlgorithm());

        mockery.assertIsSatisfied();
    }


    @Test
    public void sftpProducer_build_when_configurationId_not_provided() {

        SftpProducerBuilder sftpProducerBuilder = new SftpProducerBuilderImpl(null, null, null, null);

        sftpProducerBuilder.setOutputDirectory("test").build();
    }

}
