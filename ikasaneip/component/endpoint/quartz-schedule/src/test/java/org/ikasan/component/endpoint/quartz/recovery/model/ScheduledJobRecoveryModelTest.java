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
package org.ikasan.component.endpoint.quartz.recovery.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * This test class supports the <code>ScheduledJobRecoveryModel</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledJobRecoveryModelTest
{
    /**
     * Test.
     */
    @Test
    public void test_successful_default_instantiation()
    {
        ScheduledJobRecoveryModel scheduledJobRecoveryModel = new ScheduledJobRecoveryModel();
        Assert.assertNull("group should be null", scheduledJobRecoveryModel.getGroup());
        Assert.assertNull("name should be null", scheduledJobRecoveryModel.getName());
        Assert.assertNull("fireTime should be null", scheduledJobRecoveryModel.getFireTime());
        Assert.assertNull("nextFireTime should be null", scheduledJobRecoveryModel.getNextFireTime());
    }

    /**
     * Test.
     */
    @Test
    public void test_successful_mutators()
    {
        Date fireTime = new Date();
        ScheduledJobRecoveryModel scheduledJobRecoveryModel = new ScheduledJobRecoveryModel();
        scheduledJobRecoveryModel.setGroup("group");
        scheduledJobRecoveryModel.setName("name");
        scheduledJobRecoveryModel.setFireTime(fireTime);
        Date nextFireTime = new Date();
        scheduledJobRecoveryModel.setNextFireTime(nextFireTime);

        Assert.assertTrue("group should 'group''", scheduledJobRecoveryModel.getGroup().equals("group"));
        Assert.assertTrue("name should be 'name'", scheduledJobRecoveryModel.getName().equals("name"));
        Assert.assertTrue("fireTime should be " + fireTime, scheduledJobRecoveryModel.getFireTime().equals(fireTime));
        Assert.assertTrue("nextFireTime should be " + nextFireTime, scheduledJobRecoveryModel.getNextFireTime().equals(nextFireTime));
    }
}
