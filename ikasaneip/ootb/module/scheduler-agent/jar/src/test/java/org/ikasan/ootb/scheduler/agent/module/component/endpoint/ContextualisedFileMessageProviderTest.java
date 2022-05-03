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
package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.ootb.scheduler.agent.rest.cache.ContextParametersCache;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration.ContextualisedFileConsumerConfiguration;
import org.ikasan.job.orchestration.model.context.ContextParameterInstanceImpl;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.*;

/**
 * Functional unit test cases for <code>ContextualisedFileMessageProvider</code>.
 * 
 * @author Ikasan Development Team
 */
public class ContextualisedFileMessageProviderTest
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

    private ContextualisedFileConsumerConfiguration configuration = mockery.mock(ContextualisedFileConsumerConfiguration.class);

    private ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    @Before
    public void setup() {
        ContextParameterInstance contextParameterInstance1 = new ContextParameterInstanceImpl();
        contextParameterInstance1.setName("PRODUCTTYPE");
        contextParameterInstance1.setValue("Equity");
        ContextParameterInstance contextParameterInstance2 = new ContextParameterInstanceImpl();
        contextParameterInstance2.setName("BUSINESSDATE");
        contextParameterInstance2.setValue("20220426");

        Map<String, List<ContextParameterInstance>> contextParameters = new HashMap<>();
        contextParameters.put("test-context", Arrays.asList(contextParameterInstance1, contextParameterInstance2));


        ContextParametersCache.instance().put(contextParameters);
    }

    /**
     * Test successful return of a list of files.
     */
    @Test
    public void test_successful_list_of_files()
    {
        final List<String> filenames = new ArrayList<>();
        filenames.add("src/test/resources/data/files/Trade_PRODUCTTYPE_\\d{8}_\\d+_\\d{14}.txt");
        filenames.add("src/test/resources/data/files/TradeLeg_PRODUCTTYPE_\\d{8}_\\d+_\\d{14}.txt");

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(3).of(configuration).getFilenames();
                will(returnValue(filenames));
                exactly(2).of(configuration).getContextId();
                will(returnValue("test-context"));
                exactly(4).of(configuration).getDirectoryDepth();
                // ensure we don't walk the subdirectory
                will(returnValue(1));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
                exactly(4).of(configuration).isIgnoreFileRenameWhilstScanning();
                will(returnValue(true));
            }
        });

        ContextualisedFileMessageProvider messageProvider = new ContextualisedFileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);
        Assert.assertTrue("Should have returned 2 files, but returned " + files.size() + " files.", files.size() == 2);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_empty_list_of_files()
    {
        final List<String> filenames = new ArrayList<>();

        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(4).of(configuration).getFilenames();
                will(returnValue(filenames));
                exactly(1).of(configuration).isLogMatchedFilenames();
                will(returnValue(true));
            }
        });

        ContextualisedFileMessageProvider messageProvider = new ContextualisedFileMessageProvider();
        messageProvider.setConfiguration(configuration);
        messageProvider.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        messageProvider.startManagedResource();
        List<File> files = messageProvider.invoke(context);
        Assert.assertNull("Should have returned null", files);

        mockery.assertIsSatisfied();
    }


}