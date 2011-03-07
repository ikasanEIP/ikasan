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
package org.ikasan.endpoint.sftp.producer;

import junit.framework.Assert;

import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.junit.Test;

/**
 * Test class for {@link SftpProducerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
public class SftpProducerAlternateConfigurationTest
{
    /**
     * Test sftpConfiguration bean defaults.
     */
    @Test
    public void test_sftpConfiguration_defaults()
    {
        SftpProducerAlternateConfiguration sftpConfiguration = new SftpProducerAlternateConfiguration();
        
        Assert.assertNull(sftpConfiguration.getClientID());
        Assert.assertTrue(sftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        Assert.assertEquals("localhost", sftpConfiguration.getRemoteHost());
        Assert.assertEquals("localhost", sftpConfiguration.getAlternateRemoteHost());
        Assert.assertNull(sftpConfiguration.getPrivateKeyFilename());
        Assert.assertNull(sftpConfiguration.getAlternatePrivateKeyFilename());
        Assert.assertEquals(Integer.valueOf(3), sftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(3), sftpConfiguration.getAlternateMaxRetryAttempts());
        Assert.assertEquals(Integer.valueOf(22), sftpConfiguration.getRemotePort());
        Assert.assertEquals(Integer.valueOf(22), sftpConfiguration.getAlternateRemotePort());
        Assert.assertNull(sftpConfiguration.getKnownHostsFilename());
        Assert.assertNull(sftpConfiguration.getAlternateKnownHostsFilename());
        Assert.assertNull(sftpConfiguration.getUsername());
        Assert.assertNull(sftpConfiguration.getAlternateUsername());
        Assert.assertNull(sftpConfiguration.getPassword());
        Assert.assertNull(sftpConfiguration.getAlternatePassword());
        Assert.assertEquals(Integer.valueOf(60000), sftpConfiguration.getConnectionTimeout());
        Assert.assertEquals(Integer.valueOf(60000), sftpConfiguration.getAlternateConnectionTimeout());
        Assert.assertNull(sftpConfiguration.getOutputDirectory());
        Assert.assertEquals(".tmp", sftpConfiguration.getRenameExtension());
        Assert.assertFalse(sftpConfiguration.getOverwrite().booleanValue());
        Assert.assertFalse(sftpConfiguration.getUnzip().booleanValue());
        Assert.assertFalse(sftpConfiguration.getChecksumDelivered().booleanValue());
        Assert.assertFalse(sftpConfiguration.getCreateParentDirectory().booleanValue());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_sftpConfiguration_mutators()
    {
        SftpProducerAlternateConfiguration sftpConfiguration = new SftpProducerAlternateConfiguration();
        
        sftpConfiguration.setClientID("clientID");
        Assert.assertEquals("clientID", sftpConfiguration.getClientID());

        sftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", sftpConfiguration.getCleanupJournalOnComplete().booleanValue());

        sftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertEquals("remoteHost", sftpConfiguration.getRemoteHost());
        sftpConfiguration.setAlternateRemoteHost("alternateRemoteHost");
        Assert.assertEquals("alternateRemoteHost", sftpConfiguration.getAlternateRemoteHost());

        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        Assert.assertEquals("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename());
        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        Assert.assertEquals("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename());

        sftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        Assert.assertEquals("maxRetryAttempts", Integer.valueOf(10), sftpConfiguration.getMaxRetryAttempts());
        sftpConfiguration.setAlternateMaxRetryAttempts(Integer.valueOf(5));
        Assert.assertEquals("alternateMaxRetryAttempts", Integer.valueOf(5), sftpConfiguration.getAlternateMaxRetryAttempts());

        sftpConfiguration.setRemotePort(Integer.valueOf(21));
        Assert.assertEquals("remotePort", Integer.valueOf(21), sftpConfiguration.getRemotePort());
        sftpConfiguration.setAlternateRemotePort(Integer.valueOf(20));
        Assert.assertEquals("alternateRemotePort", Integer.valueOf(20), sftpConfiguration.getAlternateRemotePort());

        sftpConfiguration.setKnownHostsFilename("knownHostsFilename");
        Assert.assertEquals("knownHostsFilename", sftpConfiguration.getKnownHostsFilename());
        sftpConfiguration.setAlternateKnownHostsFilename("alternateKnownHostsFilename");
        Assert.assertEquals("alternateKnownHostsFilename", sftpConfiguration.getAlternateKnownHostsFilename());

        sftpConfiguration.setUsername("username");
        Assert.assertEquals("username", sftpConfiguration.getUsername());
        sftpConfiguration.setAlternateUsername("alternatUsername");
        Assert.assertEquals("alternatUsername", sftpConfiguration.getAlternateUsername());

        sftpConfiguration.setPassword("password");
        Assert.assertEquals("password", sftpConfiguration.getPassword());
        sftpConfiguration.setAlternatePassword("alternatePassword");
        Assert.assertEquals("alternatePassword", sftpConfiguration.getAlternatePassword());

        sftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        Assert.assertEquals("connectionTimeout", Integer.valueOf(1500), sftpConfiguration.getConnectionTimeout());
        sftpConfiguration.setAlternateConnectionTimeout(Integer.valueOf(1501));
        Assert.assertEquals("alernateConnectionTimeout", Integer.valueOf(1501), sftpConfiguration.getAlternateConnectionTimeout());

        sftpConfiguration.setOutputDirectory("outputDirectory");
        Assert.assertEquals("outputDirectory", sftpConfiguration.getOutputDirectory());

        sftpConfiguration.setRenameExtension("renameExtension");
        Assert.assertEquals("renameExtension", sftpConfiguration.getRenameExtension());

        sftpConfiguration.setOverwrite(Boolean.TRUE);
        Assert.assertTrue("overwrite", sftpConfiguration.getOverwrite().booleanValue());

        sftpConfiguration.setUnzip(Boolean.TRUE);
        Assert.assertTrue("unzip", sftpConfiguration.getUnzip().booleanValue());

        sftpConfiguration.setChecksumDelivered(Boolean.TRUE);
        Assert.assertTrue("checksumDelivered", sftpConfiguration.getChecksumDelivered().booleanValue());

        sftpConfiguration.setCreateParentDirectory(Boolean.TRUE);
        Assert.assertTrue("createParentDirectory", sftpConfiguration.getCreateParentDirectory().booleanValue());
    }
}
