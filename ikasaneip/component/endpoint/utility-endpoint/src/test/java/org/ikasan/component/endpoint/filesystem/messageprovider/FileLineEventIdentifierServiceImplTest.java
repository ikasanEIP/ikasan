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

import org.junit.Assert;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Functional unit test cases for <code>FileLineEventIdentifierServiceImpl</code>.
 * 
 * @author Ikasan Development Team
 */
public class FileLineEventIdentifierServiceImplTest
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

    private File file = mockery.mock(File.class);

    /**
     * Test single file getIndentifier.
     */
    @Test
    public void test_get_identifier_single_file()
    {
        String expectedIdentifier = "my_prefix_filename";
        final List<File> filenames = new ArrayList<File>();
        filenames.add(file);

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(file).getName();
                will(returnValue("filename"));
            }
        });

        ManagedEventIdentifierService<String,List<File>> managedEventIdentifierService = new FileLineEventIdentifierServiceImpl("my_prefix");
        String identifier = managedEventIdentifierService.getEventIdentifier(filenames);

        Assert.assertTrue(identifier.equals(expectedIdentifier));
        mockery.assertIsSatisfied();
    }

    /**
     * Test single file getIndentifier.
     */
    @Test
    public void test_get_identifier_multiple_files()
    {
        String expectedIdentifier = "my_prefix_filename1_filename2_filename3";
        final List<File> filenames = new ArrayList<File>();
        filenames.add(file);
        filenames.add(file);
        filenames.add(file);

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(file).getName();
                will(returnValue("filename1"));
                exactly(1).of(file).getName();
                will(returnValue("filename2"));
                exactly(1).of(file).getName();
                will(returnValue("filename3"));
            }
        });

        ManagedEventIdentifierService<String,List<File>> managedEventIdentifierService = new FileLineEventIdentifierServiceImpl("my_prefix");
        String identifier = managedEventIdentifierService.getEventIdentifier(filenames);

        Assert.assertTrue(identifier.equals(expectedIdentifier));
        mockery.assertIsSatisfied();
    }

    /**
     * Test single file getIndentifier.
     */
    @Test
    public void test_get_identifier_no_files()
    {
        String expectedIdentifier = "my_prefix";
        final List<File> filenames = new ArrayList<File>();

        // set test expectations
        mockery.checking(new Expectations() {
            {
                // nothing
            }
        });

        ManagedEventIdentifierService<String,List<File>> managedEventIdentifierService = new FileLineEventIdentifierServiceImpl("my_prefix");
        String identifier = managedEventIdentifierService.getEventIdentifier(filenames);

        Assert.assertTrue(identifier.equals(expectedIdentifier));
        mockery.assertIsSatisfied();
    }

}