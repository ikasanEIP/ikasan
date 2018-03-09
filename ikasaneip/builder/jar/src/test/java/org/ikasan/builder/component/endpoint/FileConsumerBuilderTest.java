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
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.MessageProviderPostProcessor;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * This test class supports the <code>FileConsumerBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class FileConsumerBuilderTest {
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
    final MessageProviderPostProcessor messgeProviderPostProcessor = mockery.mock(MessageProviderPostProcessor.class, "mockMessageProviderPostProcessor");

    /**
     * Test successful builder creation.
     */
    @Test
    public void scheduledConsumer_build_when_configuration_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider, fileMessageProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("scheduledJobName"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("scheduledJobName"),
                        with("scheduledGroupJobName"));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("configuredResourceId")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setScheduledJobGroupName("scheduledGroupJobName")
                .setScheduledJobName("scheduledJobName")
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertTrue("configuredResourceId should be 'configuredResourceId'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void scheduledConsumer_build_when_no_aop_proxy() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, null, fileMessageProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("scheduledJobName"),
                        with("scheduledGroupJobName"));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("configuredResourceId")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setScheduledJobGroupName("scheduledGroupJobName")
                .setScheduledJobName("scheduledJobName")
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertTrue("configuredResourceId should be 'configuredResourceId'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test //(expected = IllegalArgumentException.class)
    public void scheduledConsumer_build_when_jobName_and_jobGroup_set() {
        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider, fileMessageProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with( any(String.class) ),
                        with( any(String.class) ));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("configuredResourceId")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertTrue("configuredResourceId should be 'configuredResourceId'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();
    }

    @Test //(expected = IllegalArgumentException.class)
    public void scheduledConsumer_build_when_jobName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider, fileMessageProvider);

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
                        with("scheduledJobGroupName"));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("configuredResourceId")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setScheduledJobGroupName("scheduledJobGroupName")
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertTrue("configuredResourceId should be 'configuredResourceId'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();    }

    @Test //(expected = IllegalArgumentException.class)
    public void scheduledConsumer_build_when_jobGroupName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider, fileMessageProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("scheduledJobName"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("scheduledJobName"),
                        with( any(String.class)));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("configuredResourceId")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setScheduledJobName("scheduledJobName")
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertTrue("configuredResourceId should be 'configuredResourceId'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();    }

    @Test
    public void scheduledConsumer_build_when_configurationId_not_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        final FileMessageProvider fileMessageProvider = new FileMessageProvider();
        FileConsumerBuilder fileConsumerBuilder = new FileConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider, fileMessageProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("scheduledJobName"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("scheduledJobName"),
                        with("scheduledGroupJobName"));
                will(returnValue(jobDetail));
            }
        });

        List filenames = new ArrayList();
        filenames.add("filename");
        Consumer scheduledFileConsumer = fileConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setFilenames(filenames)
                .setDirectoryDepth(2)
                .setIgnoreFileRenameWhilstScanning(false)
                .setCriticalOnStartup(true)
                .setMessageProviderPostProcessor(messgeProviderPostProcessor)
                .setEncoding("UTF-16")
                .setIncludeHeader(true)
                .setIncludeTrailer(true)
                .setLogMatchedFilenames(true)
                .setScheduledJobGroupName("scheduledGroupJobName")
                .setScheduledJobName("scheduledJobName")
                .setSortAscending(true)
                .setSortByModifiedDateTime(true)
                .build();

        assertTrue("instance should be a ScheduledConsumer", scheduledFileConsumer instanceof ScheduledConsumer);

        FileConsumerConfiguration fileConsumerConfiguration = ((ConfiguredResource<FileConsumerConfiguration>) scheduledFileConsumer).getConfiguration();

        assertEquals("cronExpression should be '121212'","121212", fileConsumerConfiguration.getCronExpression());
        assertTrue("eager should be 'true'", fileConsumerConfiguration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", fileConsumerConfiguration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", fileConsumerConfiguration.getTimezone() == "UTC");
        assertNull("configuredResourceId should be 'null'", ((ScheduledConsumer) scheduledFileConsumer).getConfiguredResourceId());
        assertTrue("Filenames should be 'filename'", fileConsumerConfiguration.getFilenames().get(0).equals("filename"));
        assertTrue("DirectoryDepth should be '2'", fileConsumerConfiguration.getDirectoryDepth() == 2);
        assertFalse("ignoreFileNameRenameWhilstScanning should be 'false'", fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning());
        assertTrue("criticalOnStartup should be 'true'", ((ScheduledConsumer) scheduledFileConsumer).isCriticalOnStartup());
        assertTrue("Encoding should be 'UTF-16'", fileConsumerConfiguration.getEncoding().equals("UTF-16"));
        assertTrue("isIncludeHeader should be 'true'", fileConsumerConfiguration.isIncludeHeader());
        assertTrue("isIncludeTrailer should be 'true'", fileConsumerConfiguration.isIncludeTrailer());
        assertTrue("logMatchedFilenames should be 'true'", fileConsumerConfiguration.isLogMatchedFilenames());

        FileMessageProvider fileMsgProvider = (FileMessageProvider)((ScheduledConsumer)scheduledFileConsumer).getMessageProvider();
        assertNotNull("messageProviderPostProcessor should not be 'null'", fileMsgProvider.getMessageProviderPostProcessor());

        mockery.assertIsSatisfied();    }

}
