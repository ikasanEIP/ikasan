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
 * Test class for {@link FtpProducerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
public class FtpProducerConfigurationTest
{
    /**
     * Test ftpConfiguration bean defaults.
     */
    @Test
    public void test_ftpConfiguration_defaults()
    {
        FtpProducerConfiguration ftpConfiguration = new FtpProducerConfiguration();
        
        Assert.assertNull(ftpConfiguration.getClientID());
        Assert.assertTrue(ftpConfiguration.getCleanupJournalOnComplete());
        Assert.assertEquals("localhost", ftpConfiguration.getRemoteHost());
        Assert.assertEquals(new Integer(3), ftpConfiguration.getMaxRetryAttempts());
        Assert.assertEquals(new Integer(21), ftpConfiguration.getRemotePort());
        Assert.assertNull(ftpConfiguration.getUsername());
        Assert.assertNull(ftpConfiguration.getPassword());
        Assert.assertFalse(ftpConfiguration.getActive());
        Assert.assertEquals(new Integer(60000), ftpConfiguration.getConnectionTimeout());
        Assert.assertNull(ftpConfiguration.getOutputDirectory());
        Assert.assertEquals(".tmp", ftpConfiguration.getRenameExtension());
        Assert.assertFalse(ftpConfiguration.getOverwrite());
        Assert.assertFalse(ftpConfiguration.getUnzip());
        Assert.assertFalse(ftpConfiguration.getChecksumDelivered());
        Assert.assertFalse(ftpConfiguration.getCreateParentDirectory());
        Assert.assertNull(ftpConfiguration.getTempFileName());
        Assert.assertEquals("", ftpConfiguration.getSystemKey());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    public void test_ftpConfiguration_mutators()
    {
        FtpProducerConfiguration ftpConfiguration = new FtpProducerConfiguration();
        
        ftpConfiguration.setClientID("clientID");
        Assert.assertTrue("clientID", ftpConfiguration.getClientID().equals("clientID"));

        ftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        Assert.assertFalse("cleanupJournalOnComplete", ftpConfiguration.getCleanupJournalOnComplete());

        ftpConfiguration.setRemoteHost("remoteHost");
        Assert.assertTrue("remoteHost", ftpConfiguration.getRemoteHost().equals("remoteHost"));

        ftpConfiguration.setMaxRetryAttempts(10);
        Assert.assertEquals("maxRetryAttempts", new Integer(10), ftpConfiguration.getMaxRetryAttempts());

        ftpConfiguration.setRemotePort(21);
        Assert.assertEquals("remotePort", new Integer(21), ftpConfiguration.getRemotePort());

        ftpConfiguration.setUsername("username");
        Assert.assertTrue("username", ftpConfiguration.getUsername().equals("username"));

        ftpConfiguration.setPassword("password");
        Assert.assertTrue("password", ftpConfiguration.getPassword().equals("password"));

        ftpConfiguration.setActive(true);
        Assert.assertTrue(ftpConfiguration.getActive());

        ftpConfiguration.setConnectionTimeout(1500);
        Assert.assertEquals("connectionTimeout", new Integer(1500), ftpConfiguration.getConnectionTimeout());

        ftpConfiguration.setOutputDirectory("outputDirectory");
        Assert.assertTrue("outputDirectory", ftpConfiguration.getOutputDirectory().equals("outputDirectory"));

        ftpConfiguration.setRenameExtension("renameExtension");
        Assert.assertTrue("renameExtension", ftpConfiguration.getRenameExtension().equals("renameExtension"));

        ftpConfiguration.setTempFileName("filename.tmp");
        Assert.assertEquals("filename.tmp", ftpConfiguration.getTempFileName());

        ftpConfiguration.setOverwrite(Boolean.TRUE);
        Assert.assertTrue("overwrite", ftpConfiguration.getOverwrite());

        ftpConfiguration.setUnzip(Boolean.TRUE);
        Assert.assertTrue("unzip", ftpConfiguration.getUnzip());

        ftpConfiguration.setChecksumDelivered(Boolean.TRUE);
        Assert.assertTrue("checksumDelivered", ftpConfiguration.getChecksumDelivered());

        ftpConfiguration.setCreateParentDirectory(Boolean.TRUE);
        Assert.assertTrue("createParentDirectory", ftpConfiguration.getCreateParentDirectory());

        ftpConfiguration.setSystemKey(null);
        ftpConfiguration.validate();
        Assert.assertEquals("", ftpConfiguration.getSystemKey());
    }
}
