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

import org.ikasan.builder.component.Builder;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

/**
 * Contract for a default sftpProducerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface SftpProducerBuilder extends Builder<Producer>
{
    SftpProducerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    SftpProducerBuilder setConfiguredResourceId(String configuredResourceId);

    SftpProducerBuilder setConfiguration(SftpProducerConfiguration sftpProducerConfiguration);

    SftpProducerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager);

    SftpProducerBuilder setOutputDirectory(String outputDirectory);

    SftpProducerBuilder setRenameExtension(String renameExtension);

    SftpProducerBuilder setTempFileName(String tempFileName);

    SftpProducerBuilder setChecksumDelivered(Boolean checksumDelivered);

    SftpProducerBuilder setCleanUpChunks(Boolean cleanUpChunks);

    SftpProducerBuilder setClientID(String clientID);

    SftpProducerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete);

    SftpProducerBuilder setCreateParentDirectory(Boolean createParentDirectory);

    SftpProducerBuilder setOverwrite(Boolean overwrite);

    SftpProducerBuilder setUnzip(Boolean unzip);

    SftpProducerBuilder setRemoteHost(String remoteHost);

    SftpProducerBuilder setPrivateKeyFilename(String privateKeyFilename);

    SftpProducerBuilder setMaxRetryAttempts(Integer maxRetryAttempts);

    SftpProducerBuilder setRemotePort(Integer remotePort);

    SftpProducerBuilder setKnownHostsFilename(String knownHostsFilename);

    SftpProducerBuilder setUsername(String username);

    SftpProducerBuilder setPassword(String password);

    SftpProducerBuilder setConnectionTimeout(Integer connectionTimeout);

    SftpProducerBuilder setPreferredKeyExchangeAlgorithm(String preferredKeyExchangeAlgorithm);
    
}

