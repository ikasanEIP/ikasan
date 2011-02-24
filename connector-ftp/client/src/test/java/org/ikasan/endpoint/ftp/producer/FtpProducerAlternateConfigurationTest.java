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
package org.ikasan.endpoint.ftp.producer;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for {@link FtpProducerAlternateConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
public class FtpProducerAlternateConfigurationTest
{
    /**
     * Test ftpConfiguration bean defaults.
     */
    @Test
    public void test_ftpConfiguration_defaults()
    {
        FtpProducerAlternateConfiguration ftpConfiguration = new FtpProducerAlternateConfiguration();

        // These parameters must be the same regardless of different connections
        Assert.assertNull(ftpConfiguration.getClientID());
        Assert.assertTrue(ftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        Assert.assertFalse(ftpConfiguration.getOverwrite().booleanValue());
        Assert.assertFalse(ftpConfiguration.getUnzip().booleanValue());
        Assert.assertFalse(ftpConfiguration.getChecksumDelivered().booleanValue());
        Assert.assertFalse(ftpConfiguration.getCreateParentDirectory().booleanValue());

        // These parameters can have alternates and have the same default values
        Assert.assertEquals("localhost", ftpConfiguration.getRemoteHost());
        Assert.assertEquals("localhost", ftpConfiguration.getAlternateRemoteHost());

        Assert.assertNull(ftpConfiguration.getUsername());
        Assert.assertNull(ftpConfiguration.getAlternateUsername());

        Assert.assertEquals(Integer.valueOf(21), ftpConfiguration.getRemotePort());
        Assert.assertEquals(Integer.valueOf(21), ftpConfiguration.getAlternateRemotePort());

        Assert.assertNull(ftpConfiguration.getPassword());
        Assert.assertNull(ftpConfiguration.getAlternatePassword());

        Assert.assertFalse(ftpConfiguration.getActive().booleanValue());
        Assert.assertFalse(ftpConfiguration.getAlternateActive().booleanValue());

        Assert.assertEquals(Integer.valueOf(3), ftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(3), ftpConfiguration.getAlternateMaxRetryAttempts());

        Assert.assertEquals(Integer.valueOf(60000), ftpConfiguration.getConnectionTimeout());
        Assert.assertEquals(Integer.valueOf(60000), ftpConfiguration.getAlternateConnectionTimeout());

        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getDataTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateDataTimeout());

        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getSocketTimeout());
        Assert.assertEquals(Integer.valueOf(300000), ftpConfiguration.getAlternateSocketTimeout());

        // Should they be allowed to have different values in alternate?
        Assert.assertNull(ftpConfiguration.getOutputDirectory());

        Assert.assertEquals(".tmp", ftpConfiguration.getRenameExtension());

        Assert.assertNull(ftpConfiguration.getTempFileName());

        Assert.assertEquals("", ftpConfiguration.getSystemKey());
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_ftpConfiguration_mutators()
    {
        FtpProducerAlternateConfiguration ftpConfiguration = new FtpProducerAlternateConfiguration();

        ftpConfiguration.setClientID("clientID");
        Assert.assertEquals("clientID", ftpConfiguration.getClientID());

        ftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", ftpConfiguration.getCleanupJournalOnComplete().booleanValue());

        ftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertEquals("remoteHost", ftpConfiguration.getRemoteHost());
        ftpConfiguration.setAlternateRemoteHost("alternateRemoteHost");
        Assert.assertEquals("alternateRemoteHost", ftpConfiguration.getAlternateRemoteHost());

        ftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        Assert.assertEquals("maxRetryAttempts", Integer.valueOf(10), ftpConfiguration.getMaxRetryAttempts());
        ftpConfiguration.setAlternateMaxRetryAttempts(Integer.valueOf(6));
        Assert.assertEquals("alternateMaxRetryAttempts", Integer.valueOf(6), ftpConfiguration.getAlternateMaxRetryAttempts());

        ftpConfiguration.setRemotePort(Integer.valueOf(21));
        Assert.assertEquals("remotePort", Integer.valueOf(21), ftpConfiguration.getRemotePort());
        ftpConfiguration.setAlternateRemotePort(Integer.valueOf(1234));
        Assert.assertEquals("alternateRemotePort", Integer.valueOf(1234), ftpConfiguration.getAlternateRemotePort());

        ftpConfiguration.setUsername("username");
        Assert.assertEquals("username", ftpConfiguration.getUsername());
        ftpConfiguration.setAlternateUsername("alternateUsername");
        Assert.assertEquals("alternateUsername", ftpConfiguration.getAlternateUsername());

        ftpConfiguration.setPassword("password");
        Assert.assertEquals("password", ftpConfiguration.getPassword());
        ftpConfiguration.setAlternatePassword("alternatePassword");
        Assert.assertEquals("alternatePassword", ftpConfiguration.getAlternatePassword());

        ftpConfiguration.setActive(Boolean.TRUE);
        Assert.assertTrue(ftpConfiguration.getActive().booleanValue());
        ftpConfiguration.setAlternateActive(Boolean.FALSE);
        Assert.assertFalse(ftpConfiguration.getAlternateActive().booleanValue());

        ftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        Assert.assertEquals("connectionTimeout", Integer.valueOf(1500), ftpConfiguration.getConnectionTimeout());
        ftpConfiguration.setAlternateConnectionTimeout(Integer.valueOf(1000));
        Assert.assertEquals("alternateConnectionTimeout", Integer.valueOf(1000), ftpConfiguration.getAlternateConnectionTimeout());

        ftpConfiguration.setDataTimeout(Integer.valueOf(5000));
        Assert.assertEquals("dataTimeout", Integer.valueOf(5000), ftpConfiguration.getDataTimeout());
        ftpConfiguration.setAlternateDataTimeout(Integer.valueOf(10000));
        Assert.assertEquals("alternateDataTimeout", Integer.valueOf(10000), ftpConfiguration.getAlternateDataTimeout());

        ftpConfiguration.setSocketTimeout(Integer.valueOf(60000));
        Assert.assertEquals("socketTimeout", Integer.valueOf(60000), ftpConfiguration.getSocketTimeout());
        ftpConfiguration.setAlternateSocketTimeout(Integer.valueOf(2000));
        Assert.assertEquals("alternateSocketimeout", Integer.valueOf(2000), ftpConfiguration.getAlternateSocketTimeout());

        ftpConfiguration.setOutputDirectory("outputDirectory");
        Assert.assertEquals("outputDirectory", ftpConfiguration.getOutputDirectory());

        ftpConfiguration.setRenameExtension("renameExtension");
        Assert.assertEquals("renameExtension", ftpConfiguration.getRenameExtension());

        ftpConfiguration.setTempFileName("filename.tmp");
        Assert.assertEquals("filename.tmp", ftpConfiguration.getTempFileName());

        ftpConfiguration.setOverwrite(Boolean.TRUE);
        Assert.assertTrue("overwrite", ftpConfiguration.getOverwrite().booleanValue());

        ftpConfiguration.setUnzip(Boolean.TRUE);
        Assert.assertTrue("unzip", ftpConfiguration.getUnzip().booleanValue());

        ftpConfiguration.setChecksumDelivered(Boolean.TRUE);
        Assert.assertTrue("checksumDelivered", ftpConfiguration.getChecksumDelivered().booleanValue());

        ftpConfiguration.setCreateParentDirectory(Boolean.TRUE);
        Assert.assertTrue("createParentDirectory", ftpConfiguration.getCreateParentDirectory().booleanValue());

        ftpConfiguration.setSystemKey(null);
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setSystemKey(" ");
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getSystemKey());

        ftpConfiguration.setSystemKey("systemKey");
        ftpConfiguration.validate();
        Assert.assertEquals("systemKey", ftpConfiguration.getSystemKey());

        ftpConfiguration.setAlternateSystemKey(null);
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());

        ftpConfiguration.setAlternateSystemKey(" ");
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getAlternateSystemKey());

        ftpConfiguration.setAlternateSystemKey("alternateSystemKey");
        ftpConfiguration.validate();
        Assert.assertEquals("alternateSystemKey", ftpConfiguration.getAlternateSystemKey());
    }
}
