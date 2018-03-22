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
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.consumer.FtpMessageProvider;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.transaction.jta.JtaTransactionManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class FtpConsumerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    /**
     * Mocks
     */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");
    final MessageProvider messageProvider = mockery.mock(FtpMessageProvider.class, "mockMessageProvider");
    final JtaTransactionManager jtaTransactionManager = mockery.mock(JtaTransactionManager.class, "mockJtaTransactionManager");
    final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class, "mockBaseFileTransferDao");
    final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class, "mockFileChunkDao");
    final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class, "mockTransactionalResourceCommandDAO");


    /**
     * Test successful builder creation.
     */
    @Test
    public void ftpConsumer_build_when_configuration_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider,null, null, null, null);

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
            }
        });

        Consumer scheduledConsumer = ftpConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .setMessageProvider(messageProvider)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        FtpConsumerConfiguration configuration = ((ConfiguredResource<FtpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());
        assertTrue("eager should be 'true'", configuration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", configuration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", configuration.getTimezone() == "UTC");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation with sftp Options.
     */
    @Test
    public void ftpConsumer_build_when_configuration_ftp_conf_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, null, null, null, null, null);

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
            }
        });

        Consumer scheduledConsumer = ftpConsumerBuilder
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
                .setMaxRetryAttempts(3)
                .setRemotePort(22)
                .setUsername("testUser")
                .setPassword("testPassword")
                .setConnectionTimeout(300)
                .setIsRecursive(true)
                .setFtpsKeyStoreFilePassword("ftpsKetStoreFilePass")
                .setFtpsKeyStoreFilePath("ftpsKetStoreFilePath")
                .setFtpsIsImplicit(true)
                .setFtpsProtocol("protocol")
                .setFtpsPort(24)
                .setIsFTPS(true)
                .setPasswordFilePath("testPasswordFilePath")
                .setSystemKey("testKey")
                .setSocketTimeout(6000)
                .setDataTimeout(60001)
                .setActive(true)
                .setMessageProvider(messageProvider)
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        FtpConsumerConfiguration configuration = ((ConfiguredResource<FtpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());
        assertEquals("sourceDirectory should be 'test/dir'","test/dir", configuration.getSourceDirectory());
        assertEquals("filenamePattern should be '*.txt'","*.txt", configuration.getFilenamePattern());
        assertTrue("filterDuplicates should be 'true'", configuration.getFilterDuplicates());
        assertTrue("filterOnFilename should be 'true'", configuration.getFilterOnFilename());
        assertTrue("filterOnFilename should be 'true'", configuration.getFilterOnFilename());
        assertTrue("filterOnLastModifiedDate should be 'true'", configuration.getFilterOnLastModifiedDate());
        assertTrue("renameOnSuccess should be 'true'", configuration.getRenameOnSuccess());
        assertTrue("moveOnSuccess should be 'true'", configuration.getMoveOnSuccess());
        assertEquals("renameOnSuccessExtension should be '.done'",".done", configuration.getRenameOnSuccessExtension());
        assertEquals("moveOnSuccessNewPath should be 'done'","done", configuration.getMoveOnSuccessNewPath());
        assertTrue("chronological should be 'true'", configuration.getChronological());
        assertEquals("chunkSize should be '200'",200, configuration.getChunkSize().intValue());
        assertTrue("checksum should be 'true'", configuration.getChecksum());
        assertEquals("minAge should be '120'",120L, configuration.getMinAge().longValue());
        assertTrue("destructive should be 'true'", configuration.getDestructive());
        assertEquals("maxRows should be '20'",20, configuration.getMaxRows().intValue());
        assertEquals("ageOfFiles should be '30'",30, configuration.getAgeOfFiles().intValue());
        assertEquals("clientID should be 'testClientId'","testClientId", configuration.getClientID());
        assertTrue("cleanupJournalOnComplete should be 'true'", configuration.getCleanupJournalOnComplete());
        assertEquals("remoteHost should be 'testsftphost'","testsftphost", configuration.getRemoteHost());
        assertEquals("maxRetryAttempts should be '3'",3, configuration.getMaxRetryAttempts().intValue());
        assertEquals("remotePort should be '22'",22, configuration.getRemotePort().intValue());
        assertEquals("username should be 'testUser'","testUser", configuration.getUsername());
        assertEquals("password should be 'testPassword'","testPassword", configuration.getPassword());
        assertEquals("connectionTimeout should be '300'",300, configuration.getConnectionTimeout().intValue());
        assertTrue("isRecursive should be 'true'", configuration.getIsRecursive());
        assertEquals("ftpsKeyStoreFilePassword should be 'ftpsKetStoreFilePass'","ftpsKetStoreFilePass", configuration.getFtpsKeyStoreFilePassword());
        assertEquals("ftpsKeyStoreFilePath should be 'ftpsKetStoreFilePath'","ftpsKetStoreFilePath", configuration.getFtpsKeyStoreFilePath());
        assertTrue("ftpsIsImplicit should be 'true'", configuration.getFtpsIsImplicit());
        assertEquals("ftpsProtocol should be 'protocol'","protocol", configuration.getFtpsProtocol());
        assertEquals("ftpsPort should be 'protocol'",24, configuration.getFtpsPort().intValue());
        assertTrue("isFTPS should be 'true'", configuration.getIsFTPS());
        assertEquals("passwordFilePath should be 'testPasswordFilePath'", "testPasswordFilePath",configuration.getPasswordFilePath());
        assertEquals("systemKey should be 'testKey'", "testKey",configuration.getSystemKey());
        assertEquals("socketTimeout should be '6000'",6000, configuration.getSocketTimeout().intValue());
        assertEquals("dataTimeout should be '60001'",60001, configuration.getDataTimeout().intValue());
        assertTrue("active should be 'true'", configuration.getActive());

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation with sftp Options.
     */
    @Test
    public void ftpConsumer_build_when_configuration_sftp_conf_provided_and_default_sftpMessageProvider_notsupplied() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, null, jtaTransactionManager, baseFileTransferDao, fileChunkDao, transactionalResourceCommandDAO);

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
            }
        });

        Consumer scheduledConsumer = ftpConsumerBuilder
                .setCronExpression("121212")
                .setSourceDirectory("test/dir")
                .setRemoteHost("testsftphost")
                .setRemotePort(22)
                .setUsername("testUser")
                .setPassword("testPassword")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        FtpConsumerConfiguration configuration = ((ConfiguredResource<FtpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());
        assertEquals("sourceDirectory should be 'test/dir'","test/dir", configuration.getSourceDirectory());
        assertEquals("remoteHost should be 'testsftphost'","testsftphost", configuration.getRemoteHost());
        assertEquals("remotePort should be '22'",22, configuration.getRemotePort().intValue());
        assertEquals("username should be 'testUser'","testUser", configuration.getUsername());
        assertEquals("password should be 'testPassword'","testPassword", configuration.getPassword());


        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void ftpConsumer_build_when_no_aop_proxy() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, null ,null, null, null, null);

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
            }
        });

        Consumer scheduledConsumer = ftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setMessageProvider(messageProvider)
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        FtpConsumerConfiguration configuration = ((ConfiguredResource<FtpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void ftpConsumer_build_when_jobName_and_jobGroup_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider,null, null, null, null);

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
            }
        });

        Consumer scheduledConsumer = ftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .setMessageProvider(messageProvider)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        FtpConsumerConfiguration configuration = ((ConfiguredResource<FtpConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());

        mockery.assertIsSatisfied();

    }

    @Test
    public void ftpConsumer_build_when_jobName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider,null, null, null, null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(startsWith("scheduledJobName is a required property for the scheduledConsumer and cannot be 'null'"));

        ftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName(null)
                .build();

    }

    @Test
    public void ftpConsumer_build_when_jobGroupName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        FtpConsumerBuilder ftpConsumerBuilder = new FtpConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider,null, null, null, null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(startsWith("scheduledJobGroupName is a required property for the scheduledConsumer and cannot be 'null'"));

        ftpConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName(null)
                .setScheduledJobName("testJob")
                .build();

    }

}
