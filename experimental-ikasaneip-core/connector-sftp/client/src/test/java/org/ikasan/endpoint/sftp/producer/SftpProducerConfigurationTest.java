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
public class SftpProducerConfigurationTest
{
    /**
     * Test sftpConfiguration bean defaults.
     */
    @Test
    public void test_sftpConfiguration_defaults()
    {
        SftpProducerConfiguration sftpConfiguration = new SftpProducerConfiguration();
        
        Assert.assertNull(sftpConfiguration.getClientID());
        Assert.assertTrue(sftpConfiguration.getCleanupJournalOnComplete());
        Assert.assertEquals("localhost", sftpConfiguration.getRemoteHost());
        Assert.assertNull(sftpConfiguration.getPrivateKeyFilename());
        Assert.assertEquals(new Integer(3), sftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(new Integer(22), sftpConfiguration.getRemotePort());
        Assert.assertNull(sftpConfiguration.getKnownHostsFilename());
        Assert.assertNull(sftpConfiguration.getUsername());
        Assert.assertNull(sftpConfiguration.getPassword());
        Assert.assertEquals(new Integer(60000), sftpConfiguration.getConnectionTimeout());
        Assert.assertNull(sftpConfiguration.getOutputDirectory());
        Assert.assertEquals(".tmp", sftpConfiguration.getRenameExtension());
        Assert.assertFalse(sftpConfiguration.getOverwrite());
        Assert.assertFalse(sftpConfiguration.getUnzip());
        Assert.assertFalse(sftpConfiguration.getChecksumDelivered());
        Assert.assertFalse(sftpConfiguration.getCreateParentDirectory());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_sftpConfiguration_mutators()
    {
        SftpProducerConfiguration sftpConfiguration = new SftpProducerConfiguration();
        
        sftpConfiguration.setClientID("clientID");
        Assert.assertTrue("clientID", sftpConfiguration.getClientID().equals("clientID"));

        sftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", sftpConfiguration.getCleanupJournalOnComplete());

        sftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertTrue("remoteHost", sftpConfiguration.getRemoteHost().equals("remoteHost"));

        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        Assert.assertTrue("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename().equals("privateKeyFilename"));

        sftpConfiguration.setMaxRetryAttempts(10);
        Assert.assertEquals("maxRetryAttempts", new Integer(10), sftpConfiguration.getMaxRetryAttempts());

        sftpConfiguration.setRemotePort(21);
        Assert.assertEquals("remotePort", new Integer(21), sftpConfiguration.getRemotePort());

        sftpConfiguration.setKnownHostsFilename("knownHostsFilename");
        Assert.assertTrue("knownHostsFilename", sftpConfiguration.getKnownHostsFilename().equals("knownHostsFilename"));

        sftpConfiguration.setUsername("username");
        Assert.assertTrue("username", sftpConfiguration.getUsername().equals("username"));

        sftpConfiguration.setPassword("password");
        Assert.assertTrue("password", sftpConfiguration.getPassword().equals("password"));

        sftpConfiguration.setConnectionTimeout(1500);
        Assert.assertEquals("connectionTimeout", new Integer(1500), sftpConfiguration.getConnectionTimeout());

        sftpConfiguration.setOutputDirectory("outputDirectory");
        Assert.assertTrue("outputDirectory", sftpConfiguration.getOutputDirectory().equals("outputDirectory"));

        sftpConfiguration.setRenameExtension("renameExtension");
        Assert.assertTrue("renameExtension", sftpConfiguration.getRenameExtension().equals("renameExtension"));

        sftpConfiguration.setOverwrite(Boolean.TRUE);
        Assert.assertTrue("overwrite", sftpConfiguration.getOverwrite());

        sftpConfiguration.setUnzip(Boolean.TRUE);
        Assert.assertTrue("unzip", sftpConfiguration.getUnzip());

        sftpConfiguration.setChecksumDelivered(Boolean.TRUE);
        Assert.assertTrue("checksumDelivered", sftpConfiguration.getChecksumDelivered());

        sftpConfiguration.setCreateParentDirectory(Boolean.TRUE);
        Assert.assertTrue("createParentDirectory", sftpConfiguration.getCreateParentDirectory());
    }
}
