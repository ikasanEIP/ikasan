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
package org.ikasan.spec.flow;

import java.util.List;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedService;
import org.ikasan.spec.resubmission.ResubmissionService;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public interface FlowConfiguration
{
    public FlowElement<Consumer> getConsumerFlowElement();
    public List<FlowElement<?>> getFlowElements();

    /**
     * Getter for Managed Services within this flow.
     * @return
     *
     * A Managed Service is something used by the flow that is automatically instantiated
     * on deployment and requires destroying on undeployment.
     */
    public List<ManagedService> getManagedServices();
    public List<FlowElement<ManagedResource>> getManagedResourceFlowElements();
    public List<FlowElement<ConfiguredResource>> getConfiguredResourceFlowElements();
    public List<FlowElement<DynamicConfiguredResource>> getDynamicConfiguredResourceFlowElements();
    public List<FlowElement<IsErrorReportingServiceAware>> getErrorReportingServiceAwareFlowElements();
    public ResubmissionService getResubmissionService();

    /**
     * Provision for the configuration of anything passed as a configuredResource
     * @param configuredResource
     */
    public void configure(ConfiguredResource configuredResource);
    
    /**
     * Allow for the saving of a DynamicConfiguredResources changed configuration
     * 
     * @param dynamicConfiguredResource
     */
    public void update(DynamicConfiguredResource dynamicConfiguredResource);
}
