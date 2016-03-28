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
package org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ConsumerEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ConverterEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.integrationTest.comparator.ProducerEventComparator;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechMessage;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ikasan.platform.IkasanEIPTest;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/component-conf.xml",
        "/flow-conf.xml",
        "/substitute-components.xml",
        "/ikasan-transaction-conf.xml",
        "/module-conf.xml",
        "/replay-service-conf.xml",
        "/exception-conf.xml",
        "/hsqldb-conf.xml"
        })

public class PriceFlowSampleTest extends IkasanEIPTest
{
    /** Consumer specific event comparator */
    ConsumerEventComparator consumerEventComparator;

    /** Converter specific event comparator */
    ConverterEventComparator converterEventComparator;

    /** Producer specific event comparator */
    ProducerEventComparator producerEventComparator;

    @Resource
    Module<Flow> module;

    @Before
    public void setup()
    {
        // create the test comparators
        this.consumerEventComparator = new ConsumerEventComparator();
        this.converterEventComparator = new ConverterEventComparator();
        this.producerEventComparator = new ProducerEventComparator();
    }

    @Test
    public void test_flow_consumer_translator_producer()
    {
        final PriceTechMessage priceTechConsumerMessage = new PriceTechMessage("abc", 10, 10);
        final StringBuilder priceTechConverterMessage = new StringBuilder("identifier = abc bid = 10 spread = 10 at ");
        final StringBuilder priceTechProducerMessage = new StringBuilder("identifier = abc bid = 10 spread = 10 at ");

        List<PriceTechMessage> priceTechMessages = new ArrayList<PriceTechMessage>();
        priceTechMessages.add(priceTechConsumerMessage);

        for(Flow flow:module.getFlows())
        {
            flow.start();
        }

        try
        {
            Thread.sleep(100);
        }
        catch(InterruptedException e)
        {
            // dont care
        }

        for(Flow flow:module.getFlows())
        {
            flow.stop();
        }

    }

}
