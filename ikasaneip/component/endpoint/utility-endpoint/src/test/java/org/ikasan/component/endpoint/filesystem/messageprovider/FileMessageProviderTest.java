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

import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final List<String> DYNAMIC_FILE_NAME_PATTERN = Arrays.asList(
        "./src/test/resources/data/unit/xxx/xxx_TradeLeg_20141212_99_20141212121212.txt");

    private static final String DYNAMIC_FILE_PATH_PATTERN = "./src/test/resources/data/unit/xxx";

    private static final List<String> DYNAMIC_DISTINCT_FILE_NAME_PATTERN = Arrays.asList(
        "xxx_TradeLeg_20141212_99_20141212121212.txt");

    private static String DIRECTORY_NAME = "file_delivery";

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
                exactly(2).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
                exactly(2).of(configuration).isDynamicFileName();
                will(returnValue(false));
                exactly(2).of(configuration).getFileNameSpelExpression();
                will(returnValue("spel expression"));
                exactly(2).of(configuration).getFilePathSpelExpression();
                will(returnValue("spel expression"));
                exactly(2).of(configuration).getFilePath();
                will(returnValue(null));
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
                exactly(2).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
                exactly(2).of(configuration).isDynamicFileName();
                will(returnValue(false));
                exactly(2).of(configuration).getFileNameSpelExpression();
                will(returnValue("spel expression"));
                exactly(2).of(configuration).getFilePathSpelExpression();
                will(returnValue("spel expression"));
                exactly(2).of(configuration).getFilePath();
                will(returnValue(null));
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
                exactly(3).of(configuration).getFilenames();
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

    @Test
    public void test_successful_list_of_files_dynamic_file_matcher()
    {
        setupDynamicFilenameExpectations(false);
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(false));
                exactly(1).of(configuration).getDirectoryDepth();
                will(returnValue(1));
                exactly(1).of(configuration).getFilePath();
                will(returnValue(null));
            }
        });

        FileMessageProvider messageProvider = new FileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);

        Assert.assertTrue("Should have returned 1 files, but returned " + files.size() + " files."
            , files.size() == 1);
        Assert.assertEquals(new File("./src/test/resources/data/unit/abc/abc_TradeLeg_20141212_99_20141212121212.txt")
            , files.get(0));

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_list_of_files_dynamic_file_matcher_distinct_file_path()
    {
        setupDynamicFilenameExpectations(true);
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(false));
                exactly(1).of(configuration).getDirectoryDepth();
                will(returnValue(1));
            }
        });

        FileMessageProvider messageProvider = new FileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);

        Assert.assertTrue("Should have returned 1 files, but returned " + files.size() + " files."
            , files.size() == 1);
        Assert.assertEquals(new File("./src/test/resources/data/unit/abc/abc_TradeLeg_20141212_99_20141212121212.txt")
            , files.get(0));

        mockery.assertIsSatisfied();
    }

    /**
     * Sets up expectations for dynamic filename based on the provided distinct flag.
     * If distinct is true, sets up expectations for distinct filename pattern and file path pattern.
     *
     * @param distinct flag indicating whether to set distinct filename expectations or not
     */
    private void setupDynamicFilenameExpectations(boolean distinct) {
        mockery.checking(new Expectations() {
            {
                if(distinct) {
                    exactly(2).of(configuration).getFilenames();
                    will(returnValue(DYNAMIC_DISTINCT_FILE_NAME_PATTERN));
                    exactly(1).of(configuration).getFilePath();
                    will(returnValue(DYNAMIC_FILE_PATH_PATTERN));
                }
                else {
                    exactly(2).of(configuration).getFilenames();
                    will(returnValue(DYNAMIC_FILE_NAME_PATTERN));
                }
                exactly(1).of(configuration).isDynamicFileName();
                will(returnValue(true));
                exactly(1).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
                exactly(1).of(configuration).getFileNameSpelExpression();
                will(returnValue("#fileNamePattern.replace('xxx', 'abc')"));
                exactly(1).of(configuration).getFilePathSpelExpression();
                will(returnValue("#filePathPattern.replace('xxx', 'abc')"));
            }
        });
    }

}