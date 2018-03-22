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

import javax.resource.spi.InvalidPropertyException;

import org.junit.Assert;

import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.junit.Test;

/**
 * Test class for {@link SftpConsumerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
public class SftpConsumerConfigurationTest
{
    /**
     * Test sftpConfiguration bean defaults.
     */
    @Test
    public void test_sftpConfiguration_defaults()
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        
        Assert.assertNull(sftpConfiguration.getSourceDirectory());
        Assert.assertNull(sftpConfiguration.getFilenamePattern());
        Assert.assertNull(sftpConfiguration.getSourceDirectoryURLFactory());
        Assert.assertTrue(sftpConfiguration.getFilterDuplicates().booleanValue());
        Assert.assertTrue(sftpConfiguration.getFilterOnFilename().booleanValue());
        Assert.assertTrue(sftpConfiguration.getFilterOnLastModifiedDate().booleanValue());
        Assert.assertFalse(sftpConfiguration.getRenameOnSuccess().booleanValue());
        Assert.assertNull(sftpConfiguration.getRenameOnSuccessExtension());
        Assert.assertFalse(sftpConfiguration.getMoveOnSuccess().booleanValue());
        Assert.assertNull(sftpConfiguration.getMoveOnSuccessNewPath());
        Assert.assertFalse(sftpConfiguration.getChronological().booleanValue());
        Assert.assertFalse(sftpConfiguration.getChunking().booleanValue());
        Assert.assertEquals(Integer.valueOf(1048576), sftpConfiguration.getChunkSize());
        Assert.assertFalse(sftpConfiguration.getChecksum().booleanValue());
        Assert.assertEquals(Long.valueOf(120), sftpConfiguration.getMinAge());
        Assert.assertFalse(sftpConfiguration.getDestructive().booleanValue());
        Assert.assertEquals(Integer.valueOf(-1), sftpConfiguration.getMaxRows());
        Assert.assertEquals(Integer.valueOf(-1), sftpConfiguration.getAgeOfFiles());
        Assert.assertNull(sftpConfiguration.getClientID());
        Assert.assertTrue(sftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        Assert.assertEquals(String.valueOf("localhost"), sftpConfiguration.getRemoteHost());
        Assert.assertNull(sftpConfiguration.getPrivateKeyFilename());
        Assert.assertEquals(Integer.valueOf(3), sftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(22), sftpConfiguration.getRemotePort());
        Assert.assertNull(sftpConfiguration.getKnownHostsFilename());
        Assert.assertNull(sftpConfiguration.getUsername());
        Assert.assertNull(sftpConfiguration.getPassword());
        Assert.assertEquals(Integer.valueOf(60000), sftpConfiguration.getConnectionTimeout());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_sftpConfiguration_mutators()
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        
        sftpConfiguration.setSourceDirectory("sourceDirectory");
        Assert.assertEquals("sourceDirectory", sftpConfiguration.getSourceDirectory());

        sftpConfiguration.setFilenamePattern("filenamePattern");
        Assert.assertEquals("filenamePattern", sftpConfiguration.getFilenamePattern());

        // TODO - find a way to test this mutator
        sftpConfiguration.setSourceDirectoryURLFactory(null);
        Assert.assertNull("sourceDirectoryURLFactory", sftpConfiguration.getSourceDirectoryURLFactory());

        sftpConfiguration.setFilterDuplicates(Boolean.FALSE);
        Assert.assertFalse("filterDuplicates", sftpConfiguration.getFilterDuplicates().booleanValue());

        sftpConfiguration.setFilterOnFilename(Boolean.FALSE);
        Assert.assertFalse("filterOnFilename", sftpConfiguration.getFilterOnFilename().booleanValue());

        sftpConfiguration.setFilterOnLastModifiedDate(Boolean.FALSE);
        Assert.assertFalse("filterOnLastModifiedDate", sftpConfiguration.getFilterOnLastModifiedDate().booleanValue());

        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        Assert.assertTrue("renameOnSuccess", sftpConfiguration.getRenameOnSuccess().booleanValue());

        sftpConfiguration.setRenameOnSuccessExtension("renameOnSuccessExtension");
        Assert.assertEquals("renameOnSuccessExtension", sftpConfiguration.getRenameOnSuccessExtension());

        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        Assert.assertTrue("moveOnSuccess", sftpConfiguration.getMoveOnSuccess().booleanValue());

        sftpConfiguration.setMoveOnSuccessNewPath("moveOnSuccessNewPath");
        Assert.assertEquals("moveOnSuccessNewPath", sftpConfiguration.getMoveOnSuccessNewPath());

        sftpConfiguration.setChronological(Boolean.TRUE);
        Assert.assertTrue("chronological", sftpConfiguration.getChronological().booleanValue());

        sftpConfiguration.setChunking(Boolean.TRUE);
        Assert.assertTrue("chunking", sftpConfiguration.getChunking().booleanValue());

        sftpConfiguration.setChunkSize(Integer.valueOf(10));
        Assert.assertEquals("chunkSize", Integer.valueOf(10), sftpConfiguration.getChunkSize());

        sftpConfiguration.setChecksum(Boolean.TRUE);
        Assert.assertTrue("checksum", sftpConfiguration.getChecksum().booleanValue());

        sftpConfiguration.setMinAge(Long.valueOf(10));
        Assert.assertEquals("minAge", Long.valueOf(10), sftpConfiguration.getMinAge());

        sftpConfiguration.setDestructive(Boolean.TRUE);
        Assert.assertTrue("destructive", sftpConfiguration.getDestructive().booleanValue());

        sftpConfiguration.setMaxRows(Integer.valueOf(10));
        Assert.assertEquals("maxRows", Integer.valueOf(10), sftpConfiguration.getMaxRows());

        sftpConfiguration.setAgeOfFiles(Integer.valueOf(10));
        Assert.assertEquals("ageOfFiles", Integer.valueOf(10), sftpConfiguration.getAgeOfFiles());

        sftpConfiguration.setClientID("clientID");
        Assert.assertTrue("clientID", sftpConfiguration.getClientID().equals("clientID"));

        sftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", sftpConfiguration.getCleanupJournalOnComplete().booleanValue());

        sftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertTrue("remoteHost", sftpConfiguration.getRemoteHost().equals("remoteHost"));

        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        Assert.assertTrue("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename().equals("privateKeyFilename"));

        sftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        Assert.assertEquals("maxRetryAttempts", Integer.valueOf(10), sftpConfiguration.getMaxRetryAttempts());

        sftpConfiguration.setRemotePort(Integer.valueOf(21));
        Assert.assertEquals("remotePort", Integer.valueOf(21), sftpConfiguration.getRemotePort());

        sftpConfiguration.setKnownHostsFilename("knownHostsFilename");
        Assert.assertTrue("knownHostsFilename", sftpConfiguration.getKnownHostsFilename().equals("knownHostsFilename"));

        sftpConfiguration.setUsername("username");
        Assert.assertTrue("username", sftpConfiguration.getUsername().equals("username"));

        sftpConfiguration.setPassword("password");
        Assert.assertTrue("password", sftpConfiguration.getPassword().equals("password"));

        sftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        Assert.assertEquals("connectionTimeout", Integer.valueOf(1500), sftpConfiguration.getConnectionTimeout());
    }
    
    /**
     * Test property successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_sftpConfiguration_validate_success() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of destructive and renameOnSuccess being true.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_sftpConfiguration_validate_failed_renameOnSuccess_and_destructive_both_true() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        sftpConfiguration.setDestructive(Boolean.TRUE);
        sftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess and renameOnSuccess being true.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_sftpConfiguration_validate_failed_moveOnSuccess_and_renameOnSuccess_both_true() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        sftpConfiguration.validate();
    }
    
    /**
     * Test property failed validate invocation based a missing property when
     * of renameOnSuccess is true and renameOnSuccessExtension is missing.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_sftpConfiguration_validate_failed_renameOnSuccess_and_renameOnSuccessExtension_null() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        sftpConfiguration.validate();
    }

    /**
     * Test property failed validate invocation based on mutually exclusive properties
     * of moveOnSuccess is true and destructive is true.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_sftpConfiguration_validate_failed_moveOnSuccess_and_destructive_both_true() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        sftpConfiguration.setDestructive(Boolean.TRUE);
        sftpConfiguration.validate();
    }

    /**
     * Test property failed validate invocation based a missing property when
     * of moveOnSuccess is true and moveOnSuccessNewPath is missing.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test(expected = InvalidPropertyException.class)
    public void test_sftpConfiguration_validate_failed_moveOnSuccess_and_moveOnSuccessNewPath_null() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        sftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_sftpConfiguration_validate_success_renameOnSuccess_true() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setRenameOnSuccess(Boolean.TRUE);
        sftpConfiguration.setRenameOnSuccessExtension(".done");
        sftpConfiguration.validate();
    }

    /**
     * Test default property values successful validate invocation.
     * @throws InvalidPropertyException if configuration instance is invalid
     */
    @Test
    public void test_sftpConfiguration_validate_success_moveOnSuccess_true() throws InvalidPropertyException
    {
        SftpConsumerConfiguration sftpConfiguration = new SftpConsumerConfiguration();
        sftpConfiguration.setMoveOnSuccess(Boolean.TRUE);
        sftpConfiguration.setMoveOnSuccessNewPath("/done");
        sftpConfiguration.validate();
    }
}
