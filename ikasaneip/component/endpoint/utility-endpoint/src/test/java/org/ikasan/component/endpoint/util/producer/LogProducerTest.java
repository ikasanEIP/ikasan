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
package org.ikasan.component.endpoint.util.producer;

import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Test;

/**
 * Test class for LogProducer.
 *
 * @author Ikasan Development Team
 */
public class LogProducerTest
{
    /**
     * Test logProducer
     */
    @Test
    public void test_logProducer_say_hello()
    {
        Producer producer = new LogProducer<String>();
        LogProducerConfiguration configuration = new LogProducerConfiguration();
        ((ConfiguredResource)producer).setConfiguration(configuration);
        producer.invoke("payload1, payload2, payload34");
    }

    /**
     * Test logProducer
     */
    @Test
    public void test_logProducer_say_hello_every_2nd_invoke()
    {
        Producer producer = new LogProducer<String>();
        LogProducerConfiguration configuration = new LogProducerConfiguration();
        configuration.setLogEveryNth(2);
        ((ConfiguredResource)producer).setConfiguration(configuration);
        producer.invoke("hi");
        producer.invoke("hello");
    }

    /**
     * Test logProducer
     */
    @Test
    public void test_logProducer_say_hello_with_pattern_no_textReplace()
    {
        Producer producer = new LogProducer<String>();
        LogProducerConfiguration configuration = new LogProducerConfiguration();
        configuration.setRegExpPattern("payload");
        ((ConfiguredResource)producer).setConfiguration(configuration);
        producer.invoke("payload1, payload2, payload34");
    }

    /**
     * Test logProducer
     */
    @Test
    public void test_logProducer_say_hello_with_no_pattern_but_textReplace()
    {
        Producer producer = new LogProducer<String>();
        LogProducerConfiguration configuration = new LogProducerConfiguration();
        configuration.setReplacementText("X");
        ((ConfiguredResource)producer).setConfiguration(configuration);
        producer.invoke("payload1, payload2, payload34");
    }

    /**
     * Test logProducer
     */
    @Test
    public void test_logProducer_say_hello_with_pattern_and_text_replacement()
    {
        Producer producer = new LogProducer<String>();
        LogProducerConfiguration configuration = new LogProducerConfiguration();
        configuration.setRegExpPattern("payload");
        configuration.setReplacementText("X");
        ((ConfiguredResource)producer).setConfiguration(configuration);
        producer.invoke("payload1, payload2, payload34");
    }
}
