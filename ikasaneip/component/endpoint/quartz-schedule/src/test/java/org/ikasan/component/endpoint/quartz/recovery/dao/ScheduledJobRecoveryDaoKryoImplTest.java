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
package org.ikasan.component.endpoint.quartz.recovery.dao;

import org.ikasan.component.endpoint.quartz.recovery.model.ScheduledJobRecoveryModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>ScheduledJobRecoveryDaoKryoImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
class ScheduledJobRecoveryDaoKryoImplTest
{
    static String persistentPath = "./tmp-" +System.currentTimeMillis();
    static File tmpTestDir = new File(persistentPath);

    @BeforeAll
    static void setup()
    {
        tmpTestDir.mkdirs();
    }

    @AfterAll
    static void teardown()
    {
        for(File file:tmpTestDir.listFiles())
        {
            assertTrue(file.delete() , "Failed to clean up files after test");
        }

        assertTrue(tmpTestDir.delete() , "Failed to clean up tmp dir after test");
    }

    /**
     * Test.
     */
    @Test
    void test_successful_kyro_save_and_find()
    {
        Date fireTime = new Date();
        ScheduledJobRecoveryModel scheduledJobRecoveryModel = new ScheduledJobRecoveryModel();
        scheduledJobRecoveryModel.setGroup("myGroupName");
        scheduledJobRecoveryModel.setName("myName");
        scheduledJobRecoveryModel.setFireTime(fireTime);
        Date nextFireTime = new Date();
        scheduledJobRecoveryModel.setNextFireTime(nextFireTime);

        ScheduledJobRecoveryDao<ScheduledJobRecoveryModel> scheduledJobRecoveryDao = new ScheduledJobRecoveryDaoKryoImpl(persistentPath);

        scheduledJobRecoveryDao.save(scheduledJobRecoveryModel);

        assertNull(scheduledJobRecoveryDao.find("anyGroupName", "myName") ,
            "Should not find anything with invalid group name");

        assertNull(scheduledJobRecoveryDao.find("myGroupName", "myInvalidName") ,
            "Should not find anything with invalid name");

        ScheduledJobRecoveryModel foundModel = scheduledJobRecoveryDao.find("myName", "myGroupName");
        assertEquals("myGroupName", foundModel.getGroup(), "Should have found the matching model groupName");
        assertEquals("myName", foundModel.getName(), "Should have found the matching model name");
        assertEquals(foundModel.getFireTime(), fireTime, "Should have found the matching model fireTime");
        assertEquals(foundModel.getNextFireTime(), nextFireTime, "Should have found the matching model nextFireTime");
    }
}
