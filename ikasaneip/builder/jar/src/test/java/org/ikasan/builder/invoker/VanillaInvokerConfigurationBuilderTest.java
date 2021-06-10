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

import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class supports the <code>VanillaInvokerConfigurationBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class VanillaInvokerConfigurationBuilderTest
{
    /**
     * Test successful builder.
     */
    @Test
    public void vanillaInvokerConfigurationBuilder_test_properties_setters()
    {
        VanillaInvokerConfigurationBuilder ticb = new VanillaInvokerConfigurationBuilder(new InvokerConfiguration());

        Assert.assertTrue("InvokerConfiguration should have 1 property", TestUtils.getFields(InvokerConfiguration.class).size() == 1);
        Assert.assertFalse("InvokerConfiguration should be false", ticb.withDynamicConfiguration(false).build().isDynamicConfiguration());
        Assert.assertTrue("InvokerConfiguration should be true", ticb.withDynamicConfiguration(true).build().isDynamicConfiguration());

        Assert.assertTrue("InvokerConfiguration should be true", ticb.withCaptureMetrics(true).build().getCaptureMetrics() == true);
        Assert.assertTrue("InvokerConfiguration should be false", ticb.withCaptureMetrics(false).build().getCaptureMetrics() == false);
        Assert.assertTrue("InvokerConfiguration should be true", ticb.withSnapMetricsEvent(true).build().getSnapEvent() == true);
        Assert.assertTrue("InvokerConfiguration should be false", ticb.withSnapMetricsEvent(false).build().getSnapEvent() == false);

    }

}
