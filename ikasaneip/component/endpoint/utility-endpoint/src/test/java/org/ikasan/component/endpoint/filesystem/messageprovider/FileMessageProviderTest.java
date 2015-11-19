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
package org.ikasan.component.endpoint.filesystem.messageprovider;

import junit.framework.Assert;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Functional unit test cases for <code>FileMessageProvider</code>.
 * 
 * @author Ikasan Development Team
 */
public class FileMessageProviderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private JobExecutionContext context = mockery.mock(JobExecutionContext.class);

    private FileConsumerConfiguration configuration = mockery.mock(FileConsumerConfiguration.class);

    private ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    /**
     * Test successful return of a list of files.
     */
    @Test
    public void test_successful_list_of_files()
    {
        final List<String> filenames = new ArrayList<>();
        filenames.add("src/test/resources/data/unit/Trade_\\d{8}_\\d+_\\d{14}.txt");
        filenames.add("src/test/resources/data/unit/TradeLeg_\\d{8}_\\d+_\\d{14}.txt");

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilenames();
                will(returnValue(filenames));
                exactly(2).of(configuration).getDirectoryDepth();
                // ensure we don't walk the subdirectory
                will(returnValue(1));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
            }
        });

        FileMessageProvider messageProvider = new FileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);
        Assert.assertTrue("Should have returned 2 files, but returned " + files.size() + " files.", files.size() == 2);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful return of a list of files.
     */
    @Test
    public void test_successful_list_of_files_with_subdir()
    {
        final List<String> filenames = new ArrayList<>();
        filenames.add("src/test/resources/data/unit/Trade_\\d{8}_\\d+_\\d{14}.txt");
        filenames.add("src/test/resources/data/unit/TradeLeg_\\d{8}_\\d+_\\d{14}.txt");

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilenames();
                will(returnValue(filenames));
                exactly(2).of(configuration).getDirectoryDepth();
                will(returnValue(2));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
            }
        });

        FileMessageProvider messageProvider = new FileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);
        Assert.assertTrue("Should have returned 3 files, but returned " + files.size() + " files.", files.size() == 3);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful return of an empty list of files.
     */
    @Test
    public void test_successful_empty_list_of_files()
    {
        final List<String> filenames = new ArrayList<>();

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(2).of(configuration).getFilenames();
                will(returnValue(filenames));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
            }
        });

        FileMessageProvider messageProvider = new FileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);
        Assert.assertNull("Should have returned null", files);

        mockery.assertIsSatisfied();
    }

}