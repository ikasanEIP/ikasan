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
 * Contract for a default ftpConsumerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface FtpConsumerBuilder extends ScheduledConsumerBuilder
{
    FtpConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    FtpConsumerBuilder setConfiguredResourceId(String configuredResourceId);

    FtpConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration);

    FtpConsumerBuilder setMessageProvider(MessageProvider messageProvider);

    FtpConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService);

    FtpConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager);

    FtpConsumerBuilder setEventFactory(EventFactory eventFactory);

    FtpConsumerBuilder setCronExpression(String cronExpression);

    FtpConsumerBuilder setEager(boolean eager);

    FtpConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire);

    FtpConsumerBuilder setTimezone(String timezone);

    FtpConsumerBuilder setSourceDirectory(String sourceDirectory);

    FtpConsumerBuilder setFilenamePattern(String filenamePattern);

    FtpConsumerBuilder setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory);

    FtpConsumerBuilder setFilterDuplicates(Boolean filterDuplicates);

    FtpConsumerBuilder setFilterOnFilename(Boolean filterOnFilename);

    FtpConsumerBuilder setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate);

    FtpConsumerBuilder setRenameOnSuccess(Boolean renameOnSuccess);

    FtpConsumerBuilder setRenameOnSuccessExtension(String renameOnSuccessExtension);

    FtpConsumerBuilder setMoveOnSuccess(Boolean moveOnSuccess);

    FtpConsumerBuilder setMoveOnSuccessNewPath(String moveOnSuccessNewPath);

    FtpConsumerBuilder setChronological(Boolean chronological);

    FtpConsumerBuilder setChunking(Boolean chunking);

    FtpConsumerBuilder setChunkSize(Integer chunkSize);

    FtpConsumerBuilder setChecksum(Boolean checksum);

    FtpConsumerBuilder setMinAge(Long minAge);

    FtpConsumerBuilder setDestructive(Boolean destructive);

    FtpConsumerBuilder setMaxRows(Integer maxRows);

    FtpConsumerBuilder setAgeOfFiles(Integer ageOfFiles);

    FtpConsumerBuilder setClientID(String clientID);

    FtpConsumerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete);

    FtpConsumerBuilder setRemoteHost(String remoteHost);

    FtpConsumerBuilder setMaxRetryAttempts(Integer maxRetryAttempts);

    FtpConsumerBuilder setRemotePort(Integer remotePort);

    FtpConsumerBuilder setUsername(String username);

    FtpConsumerBuilder setPassword(String password);

    FtpConsumerBuilder setConnectionTimeout(Integer connectionTimeout);

    FtpConsumerBuilder setIsRecursive(Boolean isRecursive);

    FtpConsumerBuilder setFtpsKeyStoreFilePassword(String ftpsKeyStoreFilePassword);

    FtpConsumerBuilder setFtpsKeyStoreFilePath(String ftpsKeyStoreFilePath);

    FtpConsumerBuilder setFtpsIsImplicit(Boolean ftpsIsImplicit);

    FtpConsumerBuilder setFtpsProtocol(String ftpsProtocol);

    FtpConsumerBuilder setFtpsPort(Integer ftpsPort);

    FtpConsumerBuilder setIsFTPS(Boolean isFTPS);

    FtpConsumerBuilder setPasswordFilePath(String passwordFilePath);

    FtpConsumerBuilder setSystemKey(String systemKey);

    FtpConsumerBuilder setSocketTimeout(Integer socketTimeout);

    FtpConsumerBuilder setDataTimeout(Integer dataTimeout);

    FtpConsumerBuilder setActive(Boolean active);

    FtpConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName);

    FtpConsumerBuilder setScheduledJobName(String scheduledJobName);

    
}

