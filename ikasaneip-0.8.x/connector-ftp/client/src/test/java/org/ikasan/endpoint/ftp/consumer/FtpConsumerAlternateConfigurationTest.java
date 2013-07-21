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

import javax.resource.spi.InvalidPropertyException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for {@link FtpConsumerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
public class FtpConsumerAlternateConfigurationTest
{
    /**
     * Test ftpConfiguration bean defaults.
     */
    @Test
    public void test_ftpConfiguration_defaults()
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();

        Assert.assertNull(ftpConfiguration.getSourceDirectory());
        Assert.assertNull(ftpConfiguration.getFilenamePattern());
        Assert.assertNull(ftpConfiguration.getSourceDirectoryURLFactory());
        Assert.assertTrue(ftpConfiguration.getFilterDuplicates().booleanValue());
        Assert.assertTrue(ftpConfiguration.getFilterOnFilename().booleanValue());
        Assert.assertTrue(ftpConfiguration.getFilterOnLastModifiedDate().booleanValue());
        Assert.assertFalse(ftpConfiguration.getRenameOnSuccess().booleanValue());
        Assert.assertNull(ftpConfiguration.getRenameOnSuccessExtension());
        Assert.assertFalse(ftpConfiguration.getMoveOnSuccess().booleanValue());
        Assert.assertNull(ftpConfiguration.getMoveOnSuccessNewPath());
        Assert.assertTrue(ftpConfiguration.getChronological().booleanValue());
        Assert.assertFalse(ftpConfiguration.getChunking().booleanValue());
        Assert.assertEquals(Integer.valueOf(1048576), ftpConfiguration.getChunkSize());
        Assert.assertFalse(ftpConfiguration.getChecksum().booleanValue());
        Assert.assertEquals(Long.valueOf(120), ftpConfiguration.getMinAge());
        Assert.assertFalse(ftpConfiguration.getDestructive().booleanValue());
        Assert.assertEquals(Integer.valueOf(-1), ftpConfiguration.getMaxRows());
        Assert.assertEquals(Integer.valueOf(-1), ftpConfiguration.getAgeOfFiles());
        Assert.assertNull(ftpConfiguration.getClientID());
        Assert.assertTrue(ftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        Assert.assertEquals(String.valueOf("localhost"), ftpConfiguration.getRemoteHost());
        Assert.assertEquals(String.valueOf("localhost"), ftpConfiguration.getAlternateRemoteHost());
        Assert.assertEquals(Integer.valueOf(3), ftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(3), ftpConfiguration.getAlternateMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(21), ftpConfiguration.getRemotePort());
        Assert.assertEquals(Integer.valueOf(21), ftpConfiguration.getAlternateRemotePort());
        Assert.assertNull(ftpConfiguration.getUsername());
        Assert.assertNull(ftpConfiguration.getAlternateUsername());
        Assert.assertNull(ftpConfiguration.getPassword());
        Assert.assertNull(ftpConfiguration.getAlternatePassword());
        Assert.assertFalse(ftpConfiguration.getActive().booleanValue());
        Assert.assertFalse(ftpConfiguration.getAlternateActive().booleanValue());
        Assert.assertEquals(Integer.valueOf(60000), ftpConfiguration.getConnectionTimeout());
        Assert.assertEquals(Integer.valueOf(60000), ftpConfiguration.getAlternateConnectionTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getDataTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateDataTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getSocketTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateSocketTimeout());
        Assert.assertEquals("", ftpConfiguration.getSystemKey());
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_ftpConfiguration_mutators()
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();

        ftpConfiguration.setSourceDirectory("sourceDirectory");
        Assert.assertEquals("sourceDirectory", ftpConfiguration.getSourceDirectory());

        ftpConfiguration.setFilenamePattern("filenamePattern");
        Assert.assertEquals("filenamePattern", ftpConfiguration.getFilenamePattern());

        ftpConfiguration.setSourceDirectoryURLFactory(null);
        Assert.assertNull("sourceDirectoryURLFactory", ftpConfiguration.getSourceDirectoryURLFactory());

        ftpConfiguration.setFilterDuplicates(Boolean.FALSE);
        Assert.assertFalse("filterDuplicates", ftpConfiguration.getFilterDuplicates().booleanValue());

        ftpConfiguration.setFilterOnFilename(Boolean.FALSE);
        Assert.assertFalse("filterOnFilename", ftpConfiguration.getFilterOnFilename().booleanValue());

        ftpConfiguration.setFilterOnLastModifiedDate(Boolean.FALSE);
        Assert.assertFalse("filterOnLastModifiedDate", ftpConfiguration.getFilterOnLastModifiedDate().booleanValue());

        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        Assert.assertTrue("renameOnSuccess", ftpConfiguration.getRenameOnSuccess().booleanValue());

        ftpConfiguration.setRenameOnSuccessExtension("renameOnSuccessExtension");
        Assert.assertEquals("renameOnSuccessExtension", ftpConfiguration.getRenameOnSuccessExtension());

        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        Assert.assertTrue("moveOnSuccess", ftpConfiguration.getMoveOnSuccess().booleanValue());

        ftpConfiguration.setMoveOnSuccessNewPath("moveOnSuccessNewPath");
        Assert.assertEquals("moveOnSuccessNewPath", ftpConfiguration.getMoveOnSuccessNewPath());

        ftpConfiguration.setChronological(Boolean.TRUE);
        Assert.assertTrue("chronological", ftpConfiguration.getChronological().booleanValue());

        ftpConfiguration.setChunking(Boolean.TRUE);
        Assert.assertTrue("chunking", ftpConfiguration.getChunking().booleanValue());

        ftpConfiguration.setChunkSize(Integer.valueOf(10));
        Assert.assertEquals("chunkSize", Integer.valueOf(10), ftpConfiguration.getChunkSize());

        ftpConfiguration.setChecksum(Boolean.TRUE);
        Assert.assertTrue("checksum", ftpConfiguration.getChecksum().booleanValue());

        ftpConfiguration.setMinAge(Long.valueOf(10));
        Assert.assertEquals("minAge", Long.valueOf(10), ftpConfiguration.getMinAge());

        ftpConfiguration.setDestructive(Boolean.TRUE);
        Assert.assertTrue("destructive", ftpConfiguration.getDestructive().booleanValue());

        ftpConfiguration.setMaxRows(Integer.valueOf(10));
        Assert.assertEquals("maxRows", Integer.valueOf(10), ftpConfiguration.getMaxRows());

        ftpConfiguration.setAgeOfFiles(Integer.valueOf(10));
        Assert.assertEquals("ageOfFiles", Integer.valueOf(10), ftpConfiguration.getAgeOfFiles());

        ftpConfiguration.setClientID("clientID");
        Assert.assertEquals("clientID", ftpConfiguration.getClientID());

        ftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", ftpConfiguration.getCleanupJournalOnComplete().booleanValue());

        ftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertEquals("remoteHost", ftpConfiguration.getRemoteHost());
        ftpConfiguration.setAlternateRemoteHost("alternateRemoteHost");
        Assert.assertTrue("alternateRemoteHost", ftpConfiguration.getAlternateRemoteHost().equals("alternateRemoteHost"));

        ftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        Assert.assertEquals("maxRetryAttempts", Integer.valueOf(10), ftpConfiguration.getMaxRetryAttempts());
        ftpConfiguration.setAlternateMaxRetryAttempts(Integer.valueOf(5));
        Assert.assertEquals("alternateMaxRetryAttempts", Integer.valueOf(5), ftpConfiguration.getAlternateMaxRetryAttempts());

        ftpConfiguration.setRemotePort(Integer.valueOf(21));
        Assert.assertEquals("remotePort", Integer.valueOf(21), ftpConfiguration.getRemotePort());
        ftpConfiguration.setAlternateRemotePort(Integer.valueOf(20));
        Assert.assertEquals("alternateRemotePort", Integer.valueOf(20), ftpConfiguration.getAlternateRemotePort());

        ftpConfiguration.setUsername("username");
        Assert.assertEquals("username", ftpConfiguration.getUsername());
        ftpConfiguration.setAlternateUsername("alternateUsername");
        Assert.assertEquals("alternateUsername", ftpConfiguration.getAlternateUsername());

        ftpConfiguration.setPassword("password");
        Assert.assertEquals("password", ftpConfiguration.getPassword());
        ftpConfiguration.setAlternatePassword("alternatePassword");
        Assert.assertEquals("alternatePassword", ftpConfiguration.getAlternatePassword());

        ftpConfiguration.setActive(Boolean.TRUE);
        Assert.assertTrue("active", ftpConfiguration.getActive().booleanValue());
        ftpConfiguration.setAlternateActive(Boolean.FALSE);
        Assert.assertFalse("alternateActive", ftpConfiguration.getAlternateActive().booleanValue());

        ftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        Assert.assertEquals("connectionTimeout", Integer.valueOf(1500), ftpConfiguration.getConnectionTimeout());
        ftpConfiguration.setAlternateConnectionTimeout(Integer.valueOf(1501));
        Assert.assertEquals("alternateConnectionTimeout", Integer.valueOf(1501), ftpConfiguration.getAlternateConnectionTimeout());

        ftpConfiguration.setDataTimeout(Integer.valueOf(1500));
        Assert.assertEquals("dataTimeout", Integer.valueOf(1500), ftpConfiguration.getDataTimeout());
        ftpConfiguration.setAlternateDataTimeout(Integer.valueOf(1600));
        Assert.assertEquals("alternateDataTimeout", Integer.valueOf(1600), ftpConfiguration.getAlternateDataTimeout());

        ftpConfiguration.setSocketTimeout(Integer.valueOf(1500));
        Assert.assertEquals("socketTimeout", Integer.valueOf(1500), ftpConfiguration.getSocketTimeout());
        ftpConfiguration.setAlternateSocketTimeout(Integer.valueOf(2500));
        Assert.assertEquals("alternateSocketTimeout", Integer.valueOf(2500), ftpConfiguration.getAlternateSocketTimeout());

        ftpConfiguration.setSystemKey("systemKey");
        Assert.assertEquals("systemKey", "systemKey", ftpConfiguration.getSystemKey());
        ftpConfiguration.setAlternateSystemKey("alternateSystemKey");
        Assert.assertEquals("alternateSystemKey", "alternateSystemKey", ftpConfiguration.getAlternateSystemKey());
    }
    
    /**
     * Test property successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_ftpConfiguration_validate_success() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of destructive and renameOnSuccess being true.
     * 
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_ftpConfiguration_validate_failed_renameOnSuccess_and_destructive_both_true() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        ftpConfiguration.setDestructive(Boolean.TRUE);
        ftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess and renameOnSuccess being true.
     * 
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_ftpConfiguration_validate_failed_moveOnSuccess_and_renameOnSuccess_both_true() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        ftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based a missing property when
     * of renameOnSuccess is true and renameOnSuccessExtension is missing.
     * 
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_ftpConfiguration_validate_failed_renameOnSuccess_and_renameOnSuccessExtension_null() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        ftpConfiguration.validate();
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess is true and destructive is true.
     * 
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_ftpConfiguration_validate_failed_moveOnSuccess_and_destructive_both_true() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        ftpConfiguration.setDestructive(Boolean.TRUE);
        ftpConfiguration.validate();
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of moveOnSuccess is true and moveOnSuccessNewPath is missing.
     * 
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_ftpConfiguration_validate_failed_moveOnSuccess_and_moveOnSuccessNewPath_null() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        ftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_ftpConfiguration_validate_success_renameOnSuccess_true() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        ftpConfiguration.setRenameOnSuccessExtension(".done");
        ftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_ftpConfiguration_validate_success_moveOnSuccess_true() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();
        ftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        ftpConfiguration.setMoveOnSuccessNewPath("/done");
        ftpConfiguration.validate();
    }

    /**
     * If the systemKey value injected was invalid, validate will reset to default value <i>empty {@link String}</i>.
     * Invalid systemKey values are: <code>null</code> or <i>single space {@link String}</i> 
     * 
     * @throws InvalidPropertyException if error validating configuration
     */
    @Test public void validate_will_reset_systemKey_to_default_if_invalid() throws InvalidPropertyException
    {
        FtpConsumerAlternateConfiguration ftpConfiguration = new FtpConsumerAlternateConfiguration();

        ftpConfiguration.setSystemKey(null);
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setSystemKey(" ");
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setAlternateSystemKey(null);
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());

        ftpConfiguration.setAlternateSystemKey(" ");
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());
    }
}

