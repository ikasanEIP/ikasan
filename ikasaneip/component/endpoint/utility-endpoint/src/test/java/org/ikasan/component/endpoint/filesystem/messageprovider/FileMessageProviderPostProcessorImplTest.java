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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Functional unit test cases for <code>FileMessageProviderPostProcessorImpl</code>.
 * 
 * @author Ikasan Development Team
 */
class FileMessageProviderPostProcessorImplTest
{
    File file1;
    File file2;
    File file3;

    List<File> files = new ArrayList<File>();

    @BeforeEach
    void setUp() throws IOException, InterruptedException
    {
        file1 = new File("file1");
        file2 = new File("file2");
        file3 = new File("file3");

        file1.createNewFile();
        Thread.sleep(1000);
        file2.createNewFile();
        Thread.sleep(1000);
        file3.createNewFile();

        files = new ArrayList<File>();
        files.add(file2);
        files.add(file1);
        files.add(file3);

        // check order before test execution
        assertEquals("file2", files.get(0).getName());
        assertEquals("file1", files.get(1).getName());
        assertEquals("file3", files.get(2).getName());
    }

    @AfterEach
    void teardown()
    {
        file1.delete();
        file2.delete();
        file3.delete();
    }

    /**
     * Test successful return of a list of files.
     */
    @Test
    void test_successful_sorted_files_by_lastModifiedDate_ascending() throws IOException
    {
        // configure
        FileConsumerConfiguration configuration = new FileConsumerConfiguration();
        configuration.setSortByModifiedDateTime(true);
        configuration.setSortAscending(true);

        // class on test
        FileMessageProviderPostProcessorImpl fileMessageProviderPostProcessor = new FileMessageProviderPostProcessorImpl();
        fileMessageProviderPostProcessor.setConfiguration(configuration);

        // invoke test
        fileMessageProviderPostProcessor.invoke(files);

        // test order after sorting
        assertEquals("file1", files.get(0).getName(), "expected file1 first, but found " + files.get(0).getName());
        assertEquals("file2", files.get(1).getName(), "expected file2 second, but found " + files.get(1).getName());
        assertEquals("file3", files.get(2).getName(), "expected file3 third, but found " + files.get(2).getName());
    }

    /**
     * Test successful return of a list of files.
     */
    @Test
    void test_successful_sorted_files_by_lastModifiedDate_descending() throws IOException
    {
        // configure
        FileConsumerConfiguration configuration = new FileConsumerConfiguration();
        configuration.setSortByModifiedDateTime(true);
        configuration.setSortAscending(false);

        // class on test
        FileMessageProviderPostProcessorImpl fileMessageProviderPostProcessor = new FileMessageProviderPostProcessorImpl();
        fileMessageProviderPostProcessor.setConfiguration(configuration);

        // invoke test
        fileMessageProviderPostProcessor.invoke(files);

        // test order after sorting
        assertEquals("file3", files.get(0).getName(), "expected file3 first, but found " + files.get(0).getName());
        assertEquals("file2", files.get(1).getName(), "expected file2 second, but found " + files.get(1).getName());
        assertEquals("file1", files.get(2).getName(), "expected file1 third, but found " + files.get(2).getName());
    }
}