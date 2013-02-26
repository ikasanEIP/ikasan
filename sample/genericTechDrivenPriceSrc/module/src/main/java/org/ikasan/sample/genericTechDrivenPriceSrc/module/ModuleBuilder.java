/* 
 * $Id: PriceFlowFactory.java 3740 2011-05-06 13:16:01Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/sample/genericTechDrivenPriceSrc/module/src/main/java/org/ikasan/sample/genericTechDrivenPriceSrc/flow/PriceFlowFactory.java $
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
package org.ikasan.sample.genericTechDrivenPriceSrc.module;

import static org.ikasan.builder.FlowBuilder.newFlow;

import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.DefaultFlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowConfiguration;
import org.ikasan.flow.visitorPattern.FlowElementImpl;
import org.ikasan.flow.visitorPattern.VisitingFlowElementInvoker;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.RecoveryManagerFactory;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.converter.PriceConverter;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceConsumer;
import org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint.PriceProducer;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.recovery.RecoveryManager;

import static org.ikasan.builder.ModuleBuilder.newModule;

/**
 * Pure Java based sample of Ikasan EIP for sourcing prices from a tech endpoint.
 * 
 * @author Ikasan Development Team
 */
public class ModuleBuilder
{
    public Module create()
    {
    	Module module = newModule("genericTechDrivenModule").withDescription("Sample generic tech module")
    	.addFlow(newFlow("flowName1", "moduleName")
    			.consumer("priceConsumer",  new PriceConsumer( new PriceTechImpl() ))
    			.converter("priceConverter", new PriceConverter())
    			.publisher("priceProducer", new PriceProducer())
    			.build())
    	.build();
    }
}
