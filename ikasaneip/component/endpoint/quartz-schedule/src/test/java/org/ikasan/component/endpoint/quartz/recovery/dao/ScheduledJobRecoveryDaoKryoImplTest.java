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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Date;

/**
 * This test class supports the <code>ScheduledJobRecoveryDaoKryoImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledJobRecoveryDaoKryoImplTest
{
    static String persistentPath = "./tmp-" +System.currentTimeMillis();
    static File tmpTestDir = new File(persistentPath);

    @BeforeClass
    public static void setup()
    {
        tmpTestDir.mkdirs();
    }

    @AfterClass
    public static void teardown()
    {
        for(File file:tmpTestDir.listFiles())
        {
            Assert.assertTrue("Failed to clean up files after test", file.delete() );
        }

        Assert.assertTrue("Failed to clean up tmp dir after test", tmpTestDir.delete() );
    }

    /**
     * Test.
     */
    @Test
    public void test_successful_kyro_save_and_find()
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

        Assert.assertNull("Should not find anything with invalid group name",
            scheduledJobRecoveryDao.find("anyGroupName", "myName") );

        Assert.assertNull("Should not find anything with invalid name",
            scheduledJobRecoveryDao.find("myGroupName", "myInvalidName") );

        ScheduledJobRecoveryModel foundModel = scheduledJobRecoveryDao.find("myName", "myGroupName");
        Assert.assertTrue("Should have found the matching model groupName", foundModel.getGroup().equals("myGroupName"));
        Assert.assertTrue("Should have found the matching model name", foundModel.getName().equals("myName"));
        Assert.assertTrue("Should have found the matching model fireTime", foundModel.getFireTime().equals(fireTime));
        Assert.assertTrue("Should have found the matching model nextFireTime", foundModel.getNextFireTime().equals(nextFireTime));
    }
}
