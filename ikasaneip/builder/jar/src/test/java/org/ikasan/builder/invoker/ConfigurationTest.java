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

import org.ikasan.flow.visitorPattern.invoker.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class supports the <code>Configuration</code> class.
 *
 * @author Ikasan Development Team
 */
public class ConfigurationTest
{
    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_consumerInvoker_instantiation()
    {
        Assert.assertTrue("ConsumerInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.consumerInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("ConsumerInvokerBuilder should create an InvokerConfiguration", Configuration.consumerInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_producerInvoker_instantiation()
    {
        Assert.assertTrue("ProducerInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.producerInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("ProducerInvokerBuilder should create an InvokerConfiguration", Configuration.producerInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_converterInvoker_instantiation()
    {
        Assert.assertTrue("ConverterInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.converterInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("ConverterInvokerBuilder should create an InvokerConfiguration", Configuration.converterInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_brokerInvoker_instantiation()
    {
        Assert.assertTrue("BrokerInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.brokerInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("BrokerInvokerBuilder should create an InvokerConfiguration", Configuration.brokerInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_sequencerInvoker_instantiation()
    {
        Assert.assertTrue("SequencerInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.sequencerInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("SequencerInvokerBuilder should create an InvokerConfiguration", Configuration.sequencerInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_translatorInvoker_instantiation()
    {
        Assert.assertTrue("TranslatorInvoker should create a TranslatorInvokerConfigurationBuilder", Configuration.translatorInvoker() instanceof TranslatorInvokerConfigurationBuilder);
        Assert.assertTrue("TranslatorInvokerBuilder should create an TranslatorInvokerConfiguration", Configuration.translatorInvoker().build() instanceof TranslatorInvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_singleRecipientRouterInvoker_instantiation()
    {
        Assert.assertTrue("SingleRecipientRouterInvoker should create a VanillaInvokerConfigurationBuilder", Configuration.singleRecipientRouterInvoker() instanceof VanillaInvokerConfigurationBuilder);
        Assert.assertTrue("SingleRecipientRouterInvokerBuilder should create an InvokerConfiguration", Configuration.singleRecipientRouterInvoker().build() instanceof InvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_multiRecipientRouterInvoker_instantiation()
    {
        Assert.assertTrue("MultiRecipientRouterInvoker should create a MultiRecipientRouterInvokerConfigurationBuilder", Configuration.multiRecipientRouterInvoker() instanceof MultiRecipientRouterInvokerConfigurationBuilder);
        Assert.assertTrue("MultiRecipientRouterInvokerBuilder should create an MultiRecipientRouterInvokerConfiguration", Configuration.multiRecipientRouterInvoker().build() instanceof MultiRecipientRouterInvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_filterInvoker_instantiation()
    {
        Assert.assertTrue("FilterInvoker should create a FilterInvokerConfigurationBuilder", Configuration.filterInvoker() instanceof FilterInvokerConfigurationBuilder);
        Assert.assertTrue("FilterInvokerBuilder should create an FilterInvokerConfiguration", Configuration.filterInvoker().build() instanceof FilterInvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_splitterInvoker_instantiation()
    {
        Assert.assertTrue("SplitterInvoker should create a SplitterInvokerConfigurationBuilder", Configuration.splitterInvoker() instanceof SplitterInvokerConfigurationBuilder);
        Assert.assertTrue("SplitterInvokerBuilder should create an SplitterInvokerConfiguration", Configuration.splitterInvoker().build() instanceof SplitterInvokerConfiguration);
    }

    /**
     * Test successful builder.
     */
    @Test
    public void Configuration_test_concurrentSplitterInvoker_instantiation()
    {
        Assert.assertTrue("ConcurrentSplitterInvoker should create a ConcurrentSplitterInvokerConfigurationBuilder", Configuration.concurrentSplitterInvoker() instanceof ConcurrentSplitterInvokerConfigurationBuilder);
        Assert.assertTrue("ConcurrentSplitterInvokerBuilder should create an ConcurrentSplitterInvokerConfiguration", Configuration.concurrentSplitterInvoker().build() instanceof ConcurrentSplitterInvokerConfiguration);
    }
}
