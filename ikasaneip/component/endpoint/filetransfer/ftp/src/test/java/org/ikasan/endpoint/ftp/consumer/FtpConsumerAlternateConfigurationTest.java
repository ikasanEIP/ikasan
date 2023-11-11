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
package org.ikasan.endpoint.ftp.consumer;

import org.ikasan.spec.configuration.InvalidConfigurationException;
import org.ikasan.spec.configuration.IsValidationAware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FtpConsumerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
class FtpConsumerAlternateConfigurationTest
{
    /**
     * Test ftpConfiguration bean defaults.
     */
    @Test
    void test_ftpConfiguration_defaults()
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();

        assertNull(ftpConfiguration.getSourceDirectory());
        assertNull(ftpConfiguration.getFilenamePattern());
        assertNull(ftpConfiguration.getSourceDirectoryURLFactory());
        assertTrue(ftpConfiguration.getFilterDuplicates().booleanValue());
        assertTrue(ftpConfiguration.getFilterOnFilename().booleanValue());
        assertTrue(ftpConfiguration.getFilterOnLastModifiedDate().booleanValue());
        assertFalse(ftpConfiguration.getRenameOnSuccess().booleanValue());
        assertNull(ftpConfiguration.getRenameOnSuccessExtension());
        assertFalse(ftpConfiguration.getMoveOnSuccess().booleanValue());
        assertNull(ftpConfiguration.getMoveOnSuccessNewPath());
        assertTrue(ftpConfiguration.getChronological().booleanValue());
        assertFalse(ftpConfiguration.getChunking().booleanValue());
        assertEquals(Integer.valueOf(1048576), ftpConfiguration.getChunkSize());
        assertFalse(ftpConfiguration.getChecksum().booleanValue());
        assertEquals(Long.valueOf(120), ftpConfiguration.getMinAge());
        assertFalse(ftpConfiguration.getDestructive().booleanValue());
        assertEquals(Integer.valueOf(-1), ftpConfiguration.getMaxRows());
        assertEquals(Integer.valueOf(-1), ftpConfiguration.getAgeOfFiles());
        assertNull(ftpConfiguration.getClientID());
        assertTrue(ftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        assertEquals(String.valueOf("127.0.0.1"), ftpConfiguration.getRemoteHost());
        assertEquals(String.valueOf("localhost"), ftpConfiguration.getAlternateRemoteHost());
        assertEquals(Integer.valueOf(3), ftpConfiguration.getMaxRetryAttempts());
        assertEquals(Integer.valueOf(3), ftpConfiguration.getAlternateMaxRetryAttempts());
        assertEquals(Integer.valueOf(21), ftpConfiguration.getRemotePort());
        assertEquals(Integer.valueOf(21), ftpConfiguration.getAlternateRemotePort());
        assertNull(ftpConfiguration.getUsername());
        assertNull(ftpConfiguration.getAlternateUsername());
        assertNull(ftpConfiguration.getPassword());
        assertNull(ftpConfiguration.getAlternatePassword());
        assertFalse(ftpConfiguration.getActive().booleanValue());
        assertFalse(ftpConfiguration.getAlternateActive().booleanValue());
        assertEquals(Integer.valueOf(60000), ftpConfiguration.getConnectionTimeout());
        assertEquals(Integer.valueOf(60000), ftpConfiguration.getAlternateConnectionTimeout());
        assertEquals(Integer.valueOf(300000), ftpConfiguration.getDataTimeout());
        assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateDataTimeout());
        assertEquals(Integer.valueOf(300000), ftpConfiguration.getSocketTimeout());
        assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateSocketTimeout());
        assertEquals("", ftpConfiguration.getSystemKey());
        assertEquals("", ftpConfiguration.getAlternateSystemKey());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    void test_ftpConfiguration_mutators()
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();

        ftpConfiguration.setSourceDirectory("sourceDirectory");
        assertEquals("sourceDirectory", ftpConfiguration.getSourceDirectory());

        ftpConfiguration.setFilenamePattern("filenamePattern");
        assertEquals("filenamePattern", ftpConfiguration.getFilenamePattern());

        ftpConfiguration.setSourceDirectoryURLFactory(null);
        assertNull(ftpConfiguration.getSourceDirectoryURLFactory(), "sourceDirectoryURLFactory");

        ftpConfiguration.setFilterDuplicates(Boolean.FALSE);
        assertFalse(ftpConfiguration.getFilterDuplicates().booleanValue(), "filterDuplicates");

        ftpConfiguration.setFilterOnFilename(Boolean.FALSE);
        assertFalse(ftpConfiguration.getFilterOnFilename().booleanValue(), "filterOnFilename");

        ftpConfiguration.setFilterOnLastModifiedDate(Boolean.FALSE);
        assertFalse(ftpConfiguration.getFilterOnLastModifiedDate().booleanValue(), "filterOnLastModifiedDate");

        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        assertTrue(ftpConfiguration.getRenameOnSuccess().booleanValue(), "renameOnSuccess");

        ftpConfiguration.setRenameOnSuccessExtension("renameOnSuccessExtension");
        assertEquals("renameOnSuccessExtension", ftpConfiguration.getRenameOnSuccessExtension());

        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        assertTrue(ftpConfiguration.getMoveOnSuccess().booleanValue(), "moveOnSuccess");

        ftpConfiguration.setMoveOnSuccessNewPath("moveOnSuccessNewPath");
        assertEquals("moveOnSuccessNewPath", ftpConfiguration.getMoveOnSuccessNewPath());

        ftpConfiguration.setChronological(Boolean.TRUE);
        assertTrue(ftpConfiguration.getChronological().booleanValue(), "chronological");

        ftpConfiguration.setChunking(Boolean.TRUE);
        assertTrue(ftpConfiguration.getChunking().booleanValue(), "chunking");

        ftpConfiguration.setChunkSize(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), ftpConfiguration.getChunkSize(), "chunkSize");

        ftpConfiguration.setChecksum(Boolean.TRUE);
        assertTrue(ftpConfiguration.getChecksum().booleanValue(), "checksum");

        ftpConfiguration.setMinAge(Long.valueOf(10));
        assertEquals(Long.valueOf(10), ftpConfiguration.getMinAge(), "minAge");

        ftpConfiguration.setDestructive(Boolean.TRUE);
        assertTrue(ftpConfiguration.getDestructive().booleanValue(), "destructive");

        ftpConfiguration.setMaxRows(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), ftpConfiguration.getMaxRows(), "maxRows");

        ftpConfiguration.setAgeOfFiles(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), ftpConfiguration.getAgeOfFiles(), "ageOfFiles");

        ftpConfiguration.setClientID("clientID");
        assertEquals("clientID", ftpConfiguration.getClientID());

        ftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        assertFalse(ftpConfiguration.getCleanupJournalOnComplete().booleanValue(), "cleanupJournalOnComplete");

        ftpConfiguration.setRemoteHost("remoteHost");
        assertEquals("remoteHost", ftpConfiguration.getRemoteHost());
        ftpConfiguration.setAlternateRemoteHost("alternateRemoteHost");
        assertEquals("alternateRemoteHost", ftpConfiguration.getAlternateRemoteHost(), "alternateRemoteHost");

        ftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), ftpConfiguration.getMaxRetryAttempts(), "maxRetryAttempts");
        ftpConfiguration.setAlternateMaxRetryAttempts(Integer.valueOf(5));
        assertEquals(Integer.valueOf(5), ftpConfiguration.getAlternateMaxRetryAttempts(), "alternateMaxRetryAttempts");

        ftpConfiguration.setRemotePort(Integer.valueOf(21));
        assertEquals(Integer.valueOf(21), ftpConfiguration.getRemotePort(), "remotePort");
        ftpConfiguration.setAlternateRemotePort(Integer.valueOf(20));
        assertEquals(Integer.valueOf(20), ftpConfiguration.getAlternateRemotePort(), "alternateRemotePort");

        ftpConfiguration.setUsername("username");
        assertEquals("username", ftpConfiguration.getUsername());
        ftpConfiguration.setAlternateUsername("alternateUsername");
        assertEquals("alternateUsername", ftpConfiguration.getAlternateUsername());

        ftpConfiguration.setPassword("password");
        assertEquals("password", ftpConfiguration.getPassword());
        ftpConfiguration.setAlternatePassword("alternatePassword");
        assertEquals("alternatePassword", ftpConfiguration.getAlternatePassword());

        ftpConfiguration.setActive(Boolean.TRUE);
        assertTrue(ftpConfiguration.getActive().booleanValue(), "active");
        ftpConfiguration.setAlternateActive(Boolean.FALSE);
        assertFalse(ftpConfiguration.getAlternateActive().booleanValue(), "alternateActive");

        ftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        assertEquals(Integer.valueOf(1500), ftpConfiguration.getConnectionTimeout(), "connectionTimeout");
        ftpConfiguration.setAlternateConnectionTimeout(Integer.valueOf(1501));
        assertEquals(Integer.valueOf(1501), ftpConfiguration.getAlternateConnectionTimeout(), "alternateConnectionTimeout");

        ftpConfiguration.setDataTimeout(Integer.valueOf(1500));
        assertEquals(Integer.valueOf(1500), ftpConfiguration.getDataTimeout(), "dataTimeout");
        ftpConfiguration.setAlternateDataTimeout(Integer.valueOf(1600));
        assertEquals(Integer.valueOf(1600), ftpConfiguration.getAlternateDataTimeout(), "alternateDataTimeout");

        ftpConfiguration.setSocketTimeout(Integer.valueOf(1500));
        assertEquals(Integer.valueOf(1500), ftpConfiguration.getSocketTimeout(), "socketTimeout");
        ftpConfiguration.setAlternateSocketTimeout(Integer.valueOf(2500));
        assertEquals(Integer.valueOf(2500), ftpConfiguration.getAlternateSocketTimeout(), "alternateSocketTimeout");

        ftpConfiguration.setSystemKey("systemKey");
        assertEquals("systemKey", ftpConfiguration.getSystemKey(), "systemKey");
        ftpConfiguration.setAlternateSystemKey("alternateSystemKey");
        assertEquals("alternateSystemKey", ftpConfiguration.getAlternateSystemKey(), "alternateSystemKey");
    }

    /**
     * Test property successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_success() throws InvalidConfigurationException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setCronExpression("0/5 * * * * ?");
        ftpConfiguration.validate();
    }

    /**
     * Test to ensure the configuration is validation aware.
     *
     **/
    @Test
    void test_ftpConfiguration_isValidationAware() throws InvalidConfigurationException
    {
        assertTrue(new FtpConsumerAlternateConfiguration() instanceof IsValidationAware, "Configuration doesnt implement IsValidationAware");
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of destructive and renameOnSuccess being true.
     * 
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_failed_renameOnSuccess_and_destructive_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
            ftpConfiguration.setCronExpression("0/5 * * * * ?");
            ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            ftpConfiguration.setDestructive(Boolean.TRUE);
            ftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess and renameOnSuccess being true.
     * 
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_failed_moveOnSuccess_and_renameOnSuccess_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
            ftpConfiguration.setCronExpression("0/5 * * * * ?");
            ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            ftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of renameOnSuccess is true and renameOnSuccessExtension is missing.
     * 
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_failed_renameOnSuccess_and_renameOnSuccessExtension_null() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
            ftpConfiguration.setCronExpression("0/5 * * * * ?");
            ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
            ftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess is true and destructive is true.
     * 
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_failed_moveOnSuccess_and_destructive_both_true() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
            ftpConfiguration.setCronExpression("0/5 * * * * ?");
            ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            ftpConfiguration.setDestructive(Boolean.TRUE);
            ftpConfiguration.validate();
        });
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of moveOnSuccess is true and moveOnSuccessNewPath is missing.
     * 
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_failed_moveOnSuccess_and_moveOnSuccessNewPath_null() throws InvalidConfigurationException
    {
        assertThrows(InvalidConfigurationException.class, () -> {
            FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
            ftpConfiguration.setCronExpression("0/5 * * * * ?");
            ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
            ftpConfiguration.validate();
        });
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_success_renameOnSuccess_true() throws InvalidConfigurationException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setCronExpression("0/5 * * * * ?");
        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        ftpConfiguration.setRenameOnSuccessExtension(".done");
        ftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidConfigurationException if configuration instance is invalid
     */
    @Test
    void test_ftpConfiguration_validate_success_moveOnSuccess_true() throws InvalidConfigurationException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setCronExpression("0/5 * * * * ?");
        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        ftpConfiguration.setMoveOnSuccessNewPath("/done");
        ftpConfiguration.validate();
    }

    /**
     * If the systemKey value injected was invalid, validate will reset to default value <i>empty {@link String}</i>.
     * Invalid systemKey values are: <code>null</code> or <i>single space {@link String}</i> 
     * 
     * @throws InvalidConfigurationException if error validating configuration
     */
    @Test
    void validate_will_reset_systemKey_to_default_if_invalid() throws InvalidConfigurationException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setCronExpression("0/5 * * * * ?");

        ftpConfiguration.setSystemKey(null);
        ftpConfiguration.validate();
        assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setSystemKey(" ");
        ftpConfiguration.validate();
        assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setAlternateSystemKey(null);
        ftpConfiguration.validate();
        assertEquals("", ftpConfiguration.getAlternateSystemKey());

        ftpConfiguration.setAlternateSystemKey(" ");
        ftpConfiguration.validate();
        assertEquals("", ftpConfiguration.getAlternateSystemKey());
    }
}

