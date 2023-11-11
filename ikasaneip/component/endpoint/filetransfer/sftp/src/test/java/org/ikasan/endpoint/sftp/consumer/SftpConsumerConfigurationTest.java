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
package org.ikasan.endpoint.sftp.consumer;

import org.ikasan.spec.configuration.InvalidConfigurationException;
import org.ikasan.spec.configuration.IsValidationAware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SftpConsumerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
class SftpConsumerConfigurationTest
{
    /**
     * Test sftpConfiguration bean defaults.
     */
    @Test
    void test_sftpConfiguration_defaults()
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        
        assertNull(sftpConfiguration.getSourceDirectory());
        assertNull(sftpConfiguration.getFilenamePattern());
        assertNull(sftpConfiguration.getSourceDirectoryURLFactory());
        assertTrue(sftpConfiguration.getFilterDuplicates().booleanValue());
        assertTrue(sftpConfiguration.getFilterOnFilename().booleanValue());
        assertTrue(sftpConfiguration.getFilterOnLastModifiedDate().booleanValue());
        assertFalse(sftpConfiguration.getRenameOnSuccess().booleanValue());
        assertNull(sftpConfiguration.getRenameOnSuccessExtension());
        assertFalse(sftpConfiguration.getMoveOnSuccess().booleanValue());
        assertNull(sftpConfiguration.getMoveOnSuccessNewPath());
        assertFalse(sftpConfiguration.getChronological().booleanValue());
        assertFalse(sftpConfiguration.getChunking().booleanValue());
        assertEquals(Integer.valueOf(1048576), sftpConfiguration.getChunkSize());
        assertFalse(sftpConfiguration.getChecksum().booleanValue());
        assertEquals(Long.valueOf(120), sftpConfiguration.getMinAge());
        assertFalse(sftpConfiguration.getDestructive().booleanValue());
        assertEquals(Integer.valueOf(-1), sftpConfiguration.getMaxRows());
        assertEquals(Integer.valueOf(-1), sftpConfiguration.getAgeOfFiles());
        assertNull(sftpConfiguration.getClientID());
        assertTrue(sftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        assertEquals(String.valueOf("127.0.0.1"), sftpConfiguration.getRemoteHost());
        assertNull(sftpConfiguration.getPrivateKeyFilename());
        assertEquals(Integer.valueOf(3), sftpConfiguration.getMaxRetryAttempts());
        assertEquals(Integer.valueOf(22), sftpConfiguration.getRemotePort());
        assertNull(sftpConfiguration.getKnownHostsFilename());
        assertNull(sftpConfiguration.getUsername());
        assertNull(sftpConfiguration.getPassword());
        assertEquals(Integer.valueOf(60000), sftpConfiguration.getConnectionTimeout());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    void test_sftpConfiguration_mutators()
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        
        sftpConfiguration.setSourceDirectory("sourceDirectory");
        assertEquals("sourceDirectory", sftpConfiguration.getSourceDirectory());

        sftpConfiguration.setFilenamePattern("filenamePattern");
        assertEquals("filenamePattern", sftpConfiguration.getFilenamePattern());

        // TODO - find a way to test this mutator
        sftpConfiguration.setSourceDirectoryURLFactory(null);
        assertNull(sftpConfiguration.getSourceDirectoryURLFactory(), "sourceDirectoryURLFactory");

        sftpConfiguration.setFilterDuplicates(Boolean.FALSE);
        assertFalse(sftpConfiguration.getFilterDuplicates().booleanValue(), "filterDuplicates");

        sftpConfiguration.setFilterOnFilename(Boolean.FALSE);
        assertFalse(sftpConfiguration.getFilterOnFilename().booleanValue(), "filterOnFilename");

        sftpConfiguration.setFilterOnLastModifiedDate(Boolean.FALSE);
        assertFalse(sftpConfiguration.getFilterOnLastModifiedDate().booleanValue(), "filterOnLastModifiedDate");

        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        assertTrue(sftpConfiguration.getRenameOnSuccess().booleanValue(), "renameOnSuccess");

        sftpConfiguration.setRenameOnSuccessExtension("renameOnSuccessExtension");
        assertEquals("renameOnSuccessExtension", sftpConfiguration.getRenameOnSuccessExtension());

        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        assertTrue(sftpConfiguration.getMoveOnSuccess().booleanValue(), "moveOnSuccess");

        sftpConfiguration.setMoveOnSuccessNewPath("moveOnSuccessNewPath");
        assertEquals("moveOnSuccessNewPath", sftpConfiguration.getMoveOnSuccessNewPath());

        sftpConfiguration.setChronological(Boolean.TRUE);
        assertTrue(sftpConfiguration.getChronological().booleanValue(), "chronological");

        sftpConfiguration.setChunking(Boolean.TRUE);
        assertTrue(sftpConfiguration.getChunking().booleanValue(), "chunking");

        sftpConfiguration.setChunkSize(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), sftpConfiguration.getChunkSize(), "chunkSize");

        sftpConfiguration.setChecksum(Boolean.TRUE);
        assertTrue(sftpConfiguration.getChecksum().booleanValue(), "checksum");

        sftpConfiguration.setMinAge(Long.valueOf(10));
        assertEquals(Long.valueOf(10), sftpConfiguration.getMinAge(), "minAge");

        sftpConfiguration.setDestructive(Boolean.TRUE);
        assertTrue(sftpConfiguration.getDestructive().booleanValue(), "destructive");

        sftpConfiguration.setMaxRows(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), sftpConfiguration.getMaxRows(), "maxRows");

        sftpConfiguration.setAgeOfFiles(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), sftpConfiguration.getAgeOfFiles(), "ageOfFiles");

        sftpConfiguration.setClientID("clientID");
        assertEquals("clientID", sftpConfiguration.getClientID(), "clientID");

        sftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        assertFalse(sftpConfiguration.getCleanupJournalOnComplete().booleanValue(), "cleanupJournalOnComplete");

        sftpConfiguration.setRemoteHost("remoteHost");
        assertEquals("remoteHost", sftpConfiguration.getRemoteHost(), "remoteHost");

        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        assertEquals("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename(), "privateKeyFilename");

        sftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), sftpConfiguration.getMaxRetryAttempts(), "maxRetryAttempts");

        sftpConfiguration.setRemotePort(Integer.valueOf(21));
        assertEquals(Integer.valueOf(21), sftpConfiguration.getRemotePort(), "remotePort");

        sftpConfiguration.setKnownHostsFilename("knownHostsFilename");
        assertEquals("knownHostsFilename", sftpConfiguration.getKnownHostsFilename(), "knownHostsFilename");

        sftpConfiguration.setUsername("username");
        assertEquals("username", sftpConfiguration.getUsername(), "username");

        sftpConfiguration.setPassword("password");
        assertEquals("password", sftpConfiguration.getPassword(), "password");

        sftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        assertEquals(Integer.valueOf(1500), sftpConfiguration.getConnectionTimeout(), "connectionTimeout");
    }

    /**
     * Test to ensure the configuration is validation aware.
     *
     **/
    @Test
    void test_ftpConfiguration_isValidationAware() throws InvalidConfigurationException
    {
        assertTrue(new SftpConsumerConfiguration() instanceof IsValidationAware, "Configuration doesnt implement IsValidationAware");
    }

    /**
     * Test property successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_success() throws InvalidConfigurationException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setCronExpression("0/5 * * * * ?");
        sftpConfiguration.validate();
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of destructive and renameOnSuccess being true.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_failed_renameOnSuccess_and_destructive_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
            sftpConfiguration.setCronExpression("0/5 * * * * ?");
            sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            sftpConfiguration.setDestructive(Boolean.TRUE);
            sftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess and renameOnSuccess being true.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_failed_moveOnSuccess_and_renameOnSuccess_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
            sftpConfiguration.setCronExpression("0/5 * * * * ?");
            sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            sftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of renameOnSuccess is true and renameOnSuccessExtension is missing.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_failed_renameOnSuccess_and_renameOnSuccessExtension_null() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
            sftpConfiguration.setCronExpression("0/5 * * * * ?");
            sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            sftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess is true and destructive is true.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_failed_moveOnSuccess_and_destructive_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
            sftpConfiguration.setCronExpression("0/5 * * * * ?");
            sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            sftpConfiguration.setDestructive(Boolean.TRUE);
            sftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of moveOnSuccess is true and moveOnSuccessNewPath is missing.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_failed_moveOnSuccess_and_moveOnSuccessNewPath_null() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
            sftpConfiguration.setCronExpression("0/5 * * * * ?");
            sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            sftpConfiguration.validate();
        });
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_success_renameOnSuccess_true() throws InvalidConfigurationException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setCronExpression("0/5 * * * * ?");
        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        sftpConfiguration.setRenameOnSuccessExtension(".done");
        sftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_sftpConfiguration_validate_success_moveOnSuccess_true() throws InvalidConfigurationException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setCronExpression("0/5 * * * * ?");
        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        sftpConfiguration.setMoveOnSuccessNewPath("/done");
        sftpConfiguration.validate();
    }
}
