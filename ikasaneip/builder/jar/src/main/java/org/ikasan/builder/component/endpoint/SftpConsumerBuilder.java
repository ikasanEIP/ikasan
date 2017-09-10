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

import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

/**
 * Contract for a default sftpConsumerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface SftpConsumerBuilder extends ScheduledConsumerBuilder
{
    SftpConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    SftpConsumerBuilder setConfiguredResourceId(String configuredResourceId);

    SftpConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration);

    SftpConsumerBuilder setMessageProvider(MessageProvider messageProvider);

    SftpConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService);

    SftpConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager);

    SftpConsumerBuilder setEventFactory(EventFactory eventFactory);

    SftpConsumerBuilder setCronExpression(String cronExpression);

    SftpConsumerBuilder setEager(boolean eager);

    SftpConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire);

    SftpConsumerBuilder setTimezone(String timezone);

    SftpConsumerBuilder setSourceDirectory(String sourceDirectory);

    SftpConsumerBuilder setFilenamePattern(String filenamePattern);

    SftpConsumerBuilder setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory);

    SftpConsumerBuilder setFilterDuplicates(Boolean filterDuplicates);

    SftpConsumerBuilder setFilterOnFilename(Boolean filterOnFilename);

    SftpConsumerBuilder setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate);

    SftpConsumerBuilder setRenameOnSuccess(Boolean renameOnSuccess);

    SftpConsumerBuilder setRenameOnSuccessExtension(String renameOnSuccessExtension);

    SftpConsumerBuilder setMoveOnSuccess(Boolean moveOnSuccess);

    SftpConsumerBuilder setMoveOnSuccessNewPath(String moveOnSuccessNewPath);

    SftpConsumerBuilder setChronological(Boolean chronological);

    SftpConsumerBuilder setChunking(Boolean chunking);

    SftpConsumerBuilder setChunkSize(Integer chunkSize);

    SftpConsumerBuilder setChecksum(Boolean checksum);

    SftpConsumerBuilder setMinAge(Long minAge);

    SftpConsumerBuilder setDestructive(Boolean destructive);

    SftpConsumerBuilder setMaxRows(Integer maxRows);

    SftpConsumerBuilder setAgeOfFiles(Integer ageOfFiles);

    SftpConsumerBuilder setClientID(String clientID);

    SftpConsumerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete);

    SftpConsumerBuilder setRemoteHost(String remoteHost);

    SftpConsumerBuilder setPrivateKeyFilename(String privateKeyFilename);

    SftpConsumerBuilder setMaxRetryAttempts(Integer maxRetryAttempts);

    SftpConsumerBuilder setRemotePort(Integer remotePort);

    SftpConsumerBuilder setKnownHostsFilename(String knownHostsFilename);

    SftpConsumerBuilder setUsername(String username);

    SftpConsumerBuilder setPassword(String password);

    SftpConsumerBuilder setConnectionTimeout(Integer connectionTimeout);

    SftpConsumerBuilder setIsRecursive(Boolean isRecursive);

    SftpConsumerBuilder setPreferredKeyExchangeAlgorithm(String preferredKeyExchangeAlgorithm);

    SftpConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName);

    SftpConsumerBuilder setScheduledJobName(String scheduledJobName);

    
}

