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
package org.ikasan.builder.invoker;

import org.ikasan.flow.visitorPattern.invoker.SplitterInvokerConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class supports the <code>SplitterInvokerConfigurationBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class SplitterInvokerConfigurationBuilderTest
{
    /**
     * Test successful builder.
     */
    @Test
    public void splitterInvokerConfigurationBuilder_test_properties_setters()
    {
        SplitterInvokerConfigurationBuilder sicb = new SplitterInvokerConfigurationBuilder(new SplitterInvokerConfiguration());

        Assert.assertTrue("SplitterInvokerConfiguration should have 4 properties", TestUtils.getFields(SplitterInvokerConfiguration.class).size() == 4);
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.withDynamicConfiguration(false).build().isDynamicConfiguration());
        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.withDynamicConfiguration(true).build().isDynamicConfiguration());

        // default behaviour
        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.build().isSplitEventToIndividualEvents());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToListOfPayloads());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToListOfEvents());

        // overrride with list of payloads
        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.withSplitAsEventWithListOfPayloads().build().isSplitEventToListOfPayloads());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToListOfEvents());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToIndividualEvents());

        // overrride with list of events
        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.withSplitAsEventWithListOfEvents().build().isSplitEventToListOfEvents());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToListOfPayloads());
        Assert.assertFalse("SplitterInvokerConfiguration should be false", sicb.build().isSplitEventToIndividualEvents());

        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.withCaptureMetrics(true).build().getCaptureMetrics() == true);
        Assert.assertTrue("SplitterInvokerConfiguration should be false", sicb.withCaptureMetrics(false).build().getCaptureMetrics() == false);
        Assert.assertTrue("SplitterInvokerConfiguration should be true", sicb.withSnapMetricsEvent(true).build().getSnapEvent() == true);
        Assert.assertTrue("SplitterInvokerConfiguration should be false", sicb.withSnapMetricsEvent(false).build().getSnapEvent() == false);

    }

}
