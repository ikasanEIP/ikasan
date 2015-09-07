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
package org.ikasan.component.endpoint.rulecheck;

import org.ikasan.spec.configuration.Configured;

/**
 * Simple rule checks for an event within a relative time interval.
 *
 * @author Ikasan Development Team.
 */
public class RelativeTimeIntervalRule implements Rule, Configured<RelativeTimeIntervalRuleConfiguration>
{
    /** track the last event timestamp */
    long lastEventTimestamp;

    /** configuration for this rule */
    RelativeTimeIntervalRuleConfiguration configuration;

    @Override
    public void rebase()
    {
        if(lastEventTimestamp == 0 || configuration.isResetLastEventTimestampOnStartToNow())
        {
            lastEventTimestamp = System.currentTimeMillis();
        }

        if(configuration.getLastEventTimestampOverride() > 0)
        {
            lastEventTimestamp = configuration.getLastEventTimestampOverride();
        }
    }

    @Override
    public void update(Object o)
    {
        this.lastEventTimestamp = System.currentTimeMillis();
    }

    @Override
    public void check(Object o) throws RuleBreachException
    {
        long now = System.currentTimeMillis();
        if(now - configuration.getTimeInterval() > this.lastEventTimestamp)
        {
            throw new RuleBreachException("Event not received within timeInterval[" + configuration.getTimeInterval() + "] Last event received at [" + lastEventTimestamp + "]");
        }
    }

    @Override
    public RelativeTimeIntervalRuleConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(RelativeTimeIntervalRuleConfiguration configuration)
    {
        this.configuration = configuration;
    }
}
