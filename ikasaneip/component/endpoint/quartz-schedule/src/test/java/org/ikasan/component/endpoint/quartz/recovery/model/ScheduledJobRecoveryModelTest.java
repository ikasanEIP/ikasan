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

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This test class supports the <code>ScheduledJobRecoveryModel</code> class.
 * 
 * @author Ikasan Development Team
 */
class ScheduledJobRecoveryModelTest
{
    /**
     * Test.
     */
    @Test
    void test_successful_default_instantiation()
    {
        ScheduledJobRecoveryModel scheduledJobRecoveryModel = new ScheduledJobRecoveryModel();
        assertNull(scheduledJobRecoveryModel.getGroup(), "group should be null");
        assertNull(scheduledJobRecoveryModel.getName(), "name should be null");
        assertNull(scheduledJobRecoveryModel.getFireTime(), "fireTime should be null");
        assertNull(scheduledJobRecoveryModel.getNextFireTime(), "nextFireTime should be null");
    }

    /**
     * Test.
     */
    @Test
    void test_successful_mutators()
    {
        Date fireTime = new Date();
        ScheduledJobRecoveryModel scheduledJobRecoveryModel = new ScheduledJobRecoveryModel();
        scheduledJobRecoveryModel.setGroup("group");
        scheduledJobRecoveryModel.setName("name");
        scheduledJobRecoveryModel.setFireTime(fireTime);
        Date nextFireTime = new Date();
        scheduledJobRecoveryModel.setNextFireTime(nextFireTime);

        assertEquals("group", scheduledJobRecoveryModel.getGroup(), "group should 'group''");
        assertEquals("name", scheduledJobRecoveryModel.getName(), "name should be 'name'");
        assertEquals(scheduledJobRecoveryModel.getFireTime(), fireTime, "fireTime should be " + fireTime);
        assertEquals(scheduledJobRecoveryModel.getNextFireTime(), nextFireTime, "nextFireTime should be " + nextFireTime);
    }
}
