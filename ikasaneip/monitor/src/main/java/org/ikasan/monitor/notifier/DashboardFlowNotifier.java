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
package org.ikasan.monitor.notifier;

import org.ikasan.monitor.notifier.model.FlowStateImpl;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.monitor.FlowNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ikasan default dashboard notifier implementation.
 *
 * @author Ikasan Development Team
 */
public class DashboardFlowNotifier implements FlowNotifier<String>
{
    /**
     * logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(DashboardFlowNotifier.class);

    /**
     * only interested in state changes
     */
    boolean notifyStateChangesOnly = true;

    /**
     * the base url of the dashboard
     */
    private DashboardRestService dashboardRestService;


    public DashboardFlowNotifier(DashboardRestService dashboardRestService)
    {
        this.dashboardRestService = dashboardRestService;
        if(this.dashboardRestService == null)
        {
            throw new IllegalArgumentException("dashboardRestService cannot be null!");
        }
    }

    @Override public void invoke(String environment, String moduleName, String context, String state)
    {
        notify(environment, moduleName, context, state);
    }

    @Override public void setNotifyStateChangesOnly(boolean notifyStateChangesOnly)
    {
        this.notifyStateChangesOnly = notifyStateChangesOnly;
    }

    @Override public boolean isNotifyStateChangesOnly()
    {
        return this.notifyStateChangesOnly;
    }


    /**
     * Internal notify method
     *
     * @param environment
     * @param moduleName
     * @param flowName
     * @param state
     */
    protected void notify(String environment, String moduleName, String flowName, String state)
    {
        FlowStateImpl flowState = new FlowStateImpl();
        flowState.setModuleName(moduleName);
        flowState.setFlowName(flowName);
        flowState.setState(state);

        boolean success = this.dashboardRestService.publish(flowState);

        if(success)
        {
            logger.info("Notify Ikasan Dashboard SUCCESS. Flow Name[%s], State[%s]".formatted(flowName, state));
        }
        else
        {
            logger.info("Notify Ikasan Dashboard FAILED. Flow Name[%s], State[%s]".formatted(flowName, state));
        }
    }
}
