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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>Configuration</code> class.
 *
 * @author Ikasan Development Team
 */
class ConfigurationTest
{
    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_consumerInvoker_instantiation()
    {
        assertTrue(Configuration.consumerInvoker() instanceof VanillaInvokerConfigurationBuilder, "ConsumerInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.consumerInvoker().build() instanceof InvokerConfiguration, "ConsumerInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_producerInvoker_instantiation()
    {
        assertTrue(Configuration.producerInvoker() instanceof VanillaInvokerConfigurationBuilder, "ProducerInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.producerInvoker().build() instanceof InvokerConfiguration, "ProducerInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_converterInvoker_instantiation()
    {
        assertTrue(Configuration.converterInvoker() instanceof VanillaInvokerConfigurationBuilder, "ConverterInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.converterInvoker().build() instanceof InvokerConfiguration, "ConverterInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_brokerInvoker_instantiation()
    {
        assertTrue(Configuration.brokerInvoker() instanceof VanillaInvokerConfigurationBuilder, "BrokerInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.brokerInvoker().build() instanceof InvokerConfiguration, "BrokerInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_sequencerInvoker_instantiation()
    {
        assertTrue(Configuration.sequencerInvoker() instanceof VanillaInvokerConfigurationBuilder, "SequencerInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.sequencerInvoker().build() instanceof InvokerConfiguration, "SequencerInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_translatorInvoker_instantiation()
    {
        assertTrue(Configuration.translatorInvoker() instanceof TranslatorInvokerConfigurationBuilder, "TranslatorInvoker should create a TranslatorInvokerConfigurationBuilder");
        assertTrue(Configuration.translatorInvoker().build() instanceof TranslatorInvokerConfiguration, "TranslatorInvokerBuilder should create an TranslatorInvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_singleRecipientRouterInvoker_instantiation()
    {
        assertTrue(Configuration.singleRecipientRouterInvoker() instanceof VanillaInvokerConfigurationBuilder, "SingleRecipientRouterInvoker should create a VanillaInvokerConfigurationBuilder");
        assertTrue(Configuration.singleRecipientRouterInvoker().build() instanceof InvokerConfiguration, "SingleRecipientRouterInvokerBuilder should create an InvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_multiRecipientRouterInvoker_instantiation()
    {
        assertTrue(Configuration.multiRecipientRouterInvoker() instanceof MultiRecipientRouterInvokerConfigurationBuilder, "MultiRecipientRouterInvoker should create a MultiRecipientRouterInvokerConfigurationBuilder");
        assertTrue(Configuration.multiRecipientRouterInvoker().build() instanceof MultiRecipientRouterInvokerConfiguration, "MultiRecipientRouterInvokerBuilder should create an MultiRecipientRouterInvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_filterInvoker_instantiation()
    {
        assertTrue(Configuration.filterInvoker() instanceof FilterInvokerConfigurationBuilder, "FilterInvoker should create a FilterInvokerConfigurationBuilder");
        assertTrue(Configuration.filterInvoker().build() instanceof FilterInvokerConfiguration, "FilterInvokerBuilder should create an FilterInvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_splitterInvoker_instantiation()
    {
        assertTrue(Configuration.splitterInvoker() instanceof SplitterInvokerConfigurationBuilder, "SplitterInvoker should create a SplitterInvokerConfigurationBuilder");
        assertTrue(Configuration.splitterInvoker().build() instanceof SplitterInvokerConfiguration, "SplitterInvokerBuilder should create an SplitterInvokerConfiguration");
    }

    /**
     * Test successful builder.
     */
    @Test
    void Configuration_test_concurrentSplitterInvoker_instantiation()
    {
        assertTrue(Configuration.concurrentSplitterInvoker() instanceof ConcurrentSplitterInvokerConfigurationBuilder, "ConcurrentSplitterInvoker should create a ConcurrentSplitterInvokerConfigurationBuilder");
        assertTrue(Configuration.concurrentSplitterInvoker().build() instanceof ConcurrentSplitterInvokerConfiguration, "ConcurrentSplitterInvokerBuilder should create an ConcurrentSplitterInvokerConfiguration");
    }
}
