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

import org.hamcrest.CoreMatchers;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.consumer.SftpMessageProvider;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.transaction.jta.JtaTransactionManager;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class SftpConsumerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocks
     */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");
    final SftpMessageProvider messageProvider = mockery.mock(SftpMessageProvider.class, "mockMessageProvider");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");
    final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class, "mockBaseFileTransferDao");
    final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class, "mockFileChunkDao");
    final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class, "mockTransactionalResourceCommandDAO");

    /**
     * Test successful builder creation.
     */
    @Test
    void sftpConsumer_build_when_configuration_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testjob"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testjob"),
                        with("testGroup"));
                will(returnValue(jobDetail));
                exactly(1).of(((Configured)messageProvider)).setConfiguration(with(any(SftpConsumerConfiguration.class)));
            }
        });

        Consumer scheduledConsumer = sftpConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("testConfigId")
                .setMessageProvider(messageProvider)
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        SftpConsumerConfiguration configuration = ((ConfiguredResource<SftpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");
        assertTrue(configuration.isEager(), "eager should be 'true'");
        assertTrue(configuration.isIgnoreMisfire(), "ignoreMisfire should be 'true'");
        assertTrue(configuration.getTimezone() == "UTC", "Timezone should be 'true'");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation with sftp Options.
     */
    @Test
    void sftpConsumer_build_when_configuration_sftp_conf_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                // set event factory
                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testjob"),
                        with("testGroup"));
                will(returnValue(jobDetail));
                exactly(1).of(((Configured)messageProvider)).setConfiguration(with(any(SftpConsumerConfiguration.class)));
            }
        });

        Consumer scheduledConsumer = sftpConsumerBuilder
                .setCronExpression("121212")
                .setSourceDirectory("test/dir")
                .setFilenamePattern("*.txt")
                .setFilterDuplicates(true)
                .setFilterOnFilename(true)
                .setFilterOnLastModifiedDate(true)
                .setRenameOnSuccess(true)
                .setRenameOnSuccess(true)
                .setMoveOnSuccess(true)
                .setRenameOnSuccessExtension(".done")
                .setMoveOnSuccessNewPath("done")
                .setChronological(true)
                .setChunking(true)
                .setChunkSize(200)
                .setChecksum(true)
                .setMinAge(120l)
                .setDestructive(true)
                .setMaxRows(20)
                .setAgeOfFiles(30)
                .setClientID("testClientId")
                .setCleanupJournalOnComplete(true)
                .setRemoteHost("testsftphost")
                .setPrivateKeyFilename("testprivatekey")
                .setMaxRetryAttempts(3)
                .setRemotePort(22)
                .setKnownHostsFilename("testknownhost")
                .setUsername("testUser")
                .setPassword("testPassword")
                .setConnectionTimeout(300)
                .setIsRecursive(true)
                .setPreferredKeyExchangeAlgorithm("testalg")
                .setMessageProvider(messageProvider)
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        SftpConsumerConfiguration configuration = ((ConfiguredResource<SftpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");
        assertEquals("test/dir", configuration.getSourceDirectory(), "sourceDirectory should be 'test/dir'");
        assertEquals("*.txt", configuration.getFilenamePattern(), "filenamePattern should be '*.txt'");
        assertTrue(configuration.getFilterDuplicates(), "filterDuplicates should be 'true'");
        assertTrue(configuration.getFilterOnFilename(), "filterOnFilename should be 'true'");
        assertTrue(configuration.getFilterOnFilename(), "filterOnFilename should be 'true'");
        assertTrue(configuration.getFilterOnLastModifiedDate(), "filterOnLastModifiedDate should be 'true'");
        assertTrue(configuration.getRenameOnSuccess(), "renameOnSuccess should be 'true'");
        assertTrue(configuration.getMoveOnSuccess(), "moveOnSuccess should be 'true'");
        assertEquals(".done", configuration.getRenameOnSuccessExtension(), "renameOnSuccessExtension should be '.done'");
        assertEquals("done", configuration.getMoveOnSuccessNewPath(), "moveOnSuccessNewPath should be 'done'");
        assertTrue(configuration.getChronological(), "chronological should be 'true'");
        assertEquals(200, configuration.getChunkSize().intValue(), "chunkSize should be '200'");
        assertTrue(configuration.getChecksum(), "checksum should be 'true'");
        assertEquals(120L, configuration.getMinAge().longValue(), "minAge should be '120'");
        assertTrue(configuration.getDestructive(), "destructive should be 'true'");
        assertEquals(20, configuration.getMaxRows().intValue(), "maxRows should be '20'");
        assertEquals(30, configuration.getAgeOfFiles().intValue(), "ageOfFiles should be '30'");
        assertEquals("testClientId", configuration.getClientID(), "clientID should be 'testClientId'");
        assertTrue(configuration.getCleanupJournalOnComplete(), "cleanupJournalOnComplete should be 'true'");
        assertEquals("testsftphost", configuration.getRemoteHost(), "remoteHost should be 'testsftphost'");
        assertEquals("testprivatekey", configuration.getPrivateKeyFilename(), "privateKeyFilename should be 'testprivatekey'");
        assertEquals(3, configuration.getMaxRetryAttempts().intValue(), "maxRetryAttempts should be '3'");
        assertEquals(22, configuration.getRemotePort().intValue(), "remotePort should be '22'");
        assertEquals("testknownhost", configuration.getKnownHostsFilename(), "knownHostsFilename should be 'testknownhost'");
        assertEquals("testUser", configuration.getUsername(), "username should be 'testUser'");
        assertEquals("testPassword", configuration.getPassword(), "password should be 'testPassword'");
        assertEquals(300, configuration.getConnectionTimeout().intValue(), "connectionTimeout should be '300'");
        assertTrue(configuration.getIsRecursive(), "isRecursive should be 'true'");
        assertEquals("testalg", configuration.getPreferredKeyExchangeAlgorithm(), "preferredKeyExchangeAlgorithm should be 'testalg'");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation with sftp Options.
     */
    @Test
    void sftpConsumer_build_when_configuration_sftp_conf_provided_and_default_sftpMessageProvider_notsupplied() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                // set event factory
                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testjob"),
                        with("testGroup"));
                will(returnValue(jobDetail));
            }
        });

        Consumer scheduledConsumer = sftpConsumerBuilder
                .setCronExpression("121212")
                .setSourceDirectory("test/dir")
                .setRemoteHost("testsftphost")
                .setRemotePort(22)
                .setKnownHostsFilename("testknownhost")
                .setUsername("testUser")
                .setPassword("testPassword")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        SftpConsumerConfiguration configuration = ((ConfiguredResource<SftpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");
        assertEquals("test/dir", configuration.getSourceDirectory(), "sourceDirectory should be 'test/dir'");
        assertEquals("testsftphost", configuration.getRemoteHost(), "remoteHost should be 'testsftphost'");
        assertEquals(22, configuration.getRemotePort().intValue(), "remotePort should be '22'");
        assertEquals("testknownhost", configuration.getKnownHostsFilename(), "knownHostsFilename should be 'testknownhost'");
        assertEquals("testUser", configuration.getUsername(), "username should be 'testUser'");
        assertEquals("testPassword", configuration.getPassword(), "password should be 'testPassword'");


        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void sftpConsumer_build_when_no_aop_proxy() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, null);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testjob"),
                        with("testGroup"));
                will(returnValue(jobDetail));
                exactly(1).of(((Configured)messageProvider)).setConfiguration(with(any(SftpConsumerConfiguration.class)));
            }
        });

        Consumer scheduledConsumer = sftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setMessageProvider(messageProvider)
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        SftpConsumerConfiguration configuration = ((ConfiguredResource<SftpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void sftpConsumer_build_when_jobName_and_jobGroup_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(jobDetail));
                exactly(1).of(((Configured)messageProvider)).setConfiguration(with(any(SftpConsumerConfiguration.class)));
            }
        });

        Consumer scheduledConsumer = sftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .setMessageProvider(messageProvider)
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        SftpConsumerConfiguration configuration = ((ConfiguredResource<SftpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");

        mockery.assertIsSatisfied();

    }

    @Test
    void sftpConsumer_build_when_jobName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(jobDetail));
            }
        });

        sftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName(null)
                .build();

    }

    @Test
    void sftpConsumer_build_when_jobGroupName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        SftpConsumerBuilder sftpConsumerBuilder = new ExtendedSftpConsumerBuilderImpl(emptyScheduleConsumer, scheduler,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with(any(String.class)),
                        with(any(String.class)));
                will(returnValue(jobDetail));
            }
        });
        sftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName(null)
                .setScheduledJobName("testJob")
                .build();
    }

    /**
     * Test class
     */
    class ExtendedSftpConsumerBuilderImpl extends SftpConsumerBuilderImpl
    {
        ScheduledConsumer scheduledConsumer;

        /**
         * Constructor
         * @param scheduledConsumer
         * @param scheduler
         * @param scheduledJobFactory
         * @param aopProxyProvider
         */
        public ExtendedSftpConsumerBuilderImpl(ScheduledConsumer scheduledConsumer,
                                               Scheduler scheduler,
                                               ScheduledJobFactory scheduledJobFactory,
                                               AopProxyProvider aopProxyProvider)
        {
            super(scheduler, scheduledJobFactory, aopProxyProvider, null, null, null, null);
            this.scheduledConsumer = scheduledConsumer;
        }

        /**
         * Factory method to return a vanilla scheduled consumer to aid testing
         * @return
         */
        protected ScheduledConsumer getScheduledConsumer()
        {
            return scheduledConsumer;
        }

        /**
         * Factory method to return a callback scheduled consumer to aid testing
         * @return
         */
        protected ScheduledConsumer getCallbackScheduledConsumer()
        {
            return scheduledConsumer;
        }

    }
}
