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
package org.ikasan.ootb.data.sharing.module.component.endpoint.producer;

import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Scheduled process event rest publisher.
 *
 * @author Ikasan Development Team
 */
public class WiretapEventRestProducer implements Producer<List<WiretapEvent>>
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(WiretapEventRestProducer.class);

    private DashboardRestService wiretapEventDashboardRestService;

    /**
     * Constructor for the WiretapEventRestProducer class.
     * This initializes the producer with a specified {@link DashboardRestService}
     * implementation that manages wiretap event publishing.
     *
     * @param wiretapEventDashboardRestService the dashboard REST service used for publishing wiretap events.
     *                                         It cannot be null. An {@link IllegalArgumentException} will be
     *                                         thrown if this parameter is null.
     */
    public WiretapEventRestProducer(DashboardRestService wiretapEventDashboardRestService)
    {
        this.wiretapEventDashboardRestService = wiretapEventDashboardRestService;
        if(wiretapEventDashboardRestService == null) {
            throw new IllegalArgumentException("wiretapEventDashboardRestService cannot be 'null");
        }
    }

    @Override
    public void invoke(List<WiretapEvent> wiretapEvents) throws EndpointException
    {
        try {
            boolean success = this.wiretapEventDashboardRestService.publish(wiretapEvents);

            if(!success) {
                throw new EndpointException("Could not publish an event to the dashboard. " +
                    "Please confirm that dashboard extract is enabled!");
            }
        }
        catch (RuntimeException e) {
            logger.error("Could not publish wiretaps!", e);
            throw new EndpointException(e);
        }
    }
}
