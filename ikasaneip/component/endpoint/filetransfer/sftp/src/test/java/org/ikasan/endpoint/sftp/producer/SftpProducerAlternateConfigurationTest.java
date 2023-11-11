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

import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SftpProducerConfiguration}
 * 
 * @author Ikasan Development Team
 *
 */
class SftpProducerAlternateConfigurationTest
{
    /**
     * Test sftpConfiguration bean defaults.
     */
    @Test
    void test_sftpConfiguration_defaults()
    {
        SftpProducerAlternateConfiguration sftpConfiguration = new SftpProducerAlternateConfiguration();
        
        assertNull(sftpConfiguration.getClientID());
        assertTrue(sftpConfiguration.getCleanupJournalOnComplete().booleanValue());
        assertEquals("127.0.0.1", sftpConfiguration.getRemoteHost());
        assertEquals("127.0.0.1", sftpConfiguration.getAlternateRemoteHost());
        assertNull(sftpConfiguration.getPrivateKeyFilename());
        assertNull(sftpConfiguration.getAlternatePrivateKeyFilename());
        assertEquals(Integer.valueOf(3), sftpConfiguration.getMaxRetryAttempts());
        assertEquals(Integer.valueOf(3), sftpConfiguration.getAlternateMaxRetryAttempts());
        assertEquals(Integer.valueOf(22), sftpConfiguration.getRemotePort());
        assertEquals(Integer.valueOf(22), sftpConfiguration.getAlternateRemotePort());
        assertNull(sftpConfiguration.getKnownHostsFilename());
        assertNull(sftpConfiguration.getAlternateKnownHostsFilename());
        assertNull(sftpConfiguration.getUsername());
        assertNull(sftpConfiguration.getAlternateUsername());
        assertNull(sftpConfiguration.getPassword());
        assertNull(sftpConfiguration.getAlternatePassword());
        assertEquals(Integer.valueOf(60000), sftpConfiguration.getConnectionTimeout());
        assertEquals(Integer.valueOf(60000), sftpConfiguration.getAlternateConnectionTimeout());
        assertNull(sftpConfiguration.getOutputDirectory());
        assertEquals(".tmp", sftpConfiguration.getRenameExtension());
        assertFalse(sftpConfiguration.getOverwrite().booleanValue());
        assertFalse(sftpConfiguration.getUnzip().booleanValue());
        assertFalse(sftpConfiguration.getChecksumDelivered().booleanValue());
        assertFalse(sftpConfiguration.getCreateParentDirectory().booleanValue());
    }

    /**
     * Test public getters and setters.
     */
    @Test
    void test_sftpConfiguration_mutators()
    {
        SftpProducerAlternateConfiguration sftpConfiguration = new SftpProducerAlternateConfiguration();
        
        sftpConfiguration.setClientID("clientID");
        assertEquals("clientID", sftpConfiguration.getClientID());

        sftpConfiguration.setCleanupJournalOnComplete(Boolean.FALSE);
        assertFalse(sftpConfiguration.getCleanupJournalOnComplete().booleanValue(), "cleanupJournalOnComplete");

        sftpConfiguration.setRemoteHost("remoteHost");
        assertEquals("remoteHost", sftpConfiguration.getRemoteHost());
        sftpConfiguration.setAlternateRemoteHost("alternateRemoteHost");
        assertEquals("alternateRemoteHost", sftpConfiguration.getAlternateRemoteHost());

        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        assertEquals("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename());
        sftpConfiguration.setPrivateKeyFilename("privateKeyFilename");
        assertEquals("privateKeyFilename", sftpConfiguration.getPrivateKeyFilename());

        sftpConfiguration.setMaxRetryAttempts(Integer.valueOf(10));
        assertEquals(Integer.valueOf(10), sftpConfiguration.getMaxRetryAttempts(), "maxRetryAttempts");
        sftpConfiguration.setAlternateMaxRetryAttempts(Integer.valueOf(5));
        assertEquals(Integer.valueOf(5), sftpConfiguration.getAlternateMaxRetryAttempts(), "alternateMaxRetryAttempts");

        sftpConfiguration.setRemotePort(Integer.valueOf(21));
        assertEquals(Integer.valueOf(21), sftpConfiguration.getRemotePort(), "remotePort");
        sftpConfiguration.setAlternateRemotePort(Integer.valueOf(20));
        assertEquals(Integer.valueOf(20), sftpConfiguration.getAlternateRemotePort(), "alternateRemotePort");

        sftpConfiguration.setKnownHostsFilename("knownHostsFilename");
        assertEquals("knownHostsFilename", sftpConfiguration.getKnownHostsFilename());
        sftpConfiguration.setAlternateKnownHostsFilename("alternateKnownHostsFilename");
        assertEquals("alternateKnownHostsFilename", sftpConfiguration.getAlternateKnownHostsFilename());

        sftpConfiguration.setUsername("username");
        assertEquals("username", sftpConfiguration.getUsername());
        sftpConfiguration.setAlternateUsername("alternatUsername");
        assertEquals("alternatUsername", sftpConfiguration.getAlternateUsername());

        sftpConfiguration.setPassword("password");
        assertEquals("password", sftpConfiguration.getPassword());
        sftpConfiguration.setAlternatePassword("alternatePassword");
        assertEquals("alternatePassword", sftpConfiguration.getAlternatePassword());

        sftpConfiguration.setConnectionTimeout(Integer.valueOf(1500));
        assertEquals(Integer.valueOf(1500), sftpConfiguration.getConnectionTimeout(), "connectionTimeout");
        sftpConfiguration.setAlternateConnectionTimeout(Integer.valueOf(1501));
        assertEquals(Integer.valueOf(1501), sftpConfiguration.getAlternateConnectionTimeout(), "alernateConnectionTimeout");

        sftpConfiguration.setOutputDirectory("outputDirectory");
        assertEquals("outputDirectory", sftpConfiguration.getOutputDirectory());

        sftpConfiguration.setRenameExtension("renameExtension");
        assertEquals("renameExtension", sftpConfiguration.getRenameExtension());

        sftpConfiguration.setOverwrite(Boolean.TRUE);
        assertTrue(sftpConfiguration.getOverwrite().booleanValue(), "overwrite");

        sftpConfiguration.setUnzip(Boolean.TRUE);
        assertTrue(sftpConfiguration.getUnzip().booleanValue(), "unzip");

        sftpConfiguration.setChecksumDelivered(Boolean.TRUE);
        assertTrue(sftpConfiguration.getChecksumDelivered().booleanValue(), "checksumDelivered");

        sftpConfiguration.setCreateParentDirectory(Boolean.TRUE);
        assertTrue(sftpConfiguration.getCreateParentDirectory().booleanValue(), "createParentDirectory");
    }
}
