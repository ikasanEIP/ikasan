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
package org.ikasan.hospital.dao;

import javax.annotation.Resource;

import org.ikasan.hospital.HospitalAutoConfiguration;
import org.ikasan.hospital.HospitalTestAutoConfiguration;
import org.ikasan.hospital.model.ExclusionEventActionImpl;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HospitalAutoConfiguration.class, HospitalTestAutoConfiguration.class})
public class HibernateHospitalDaoTest
{

	@Resource HospitalDao hospitalDao;
	
	@Test
    @DirtiesContext
	public void test_SaveExclusionEvent_success()
	{
		ExclusionEventAction action = new ExclusionEventActionImpl("errorUri", "actionedBy", "state", "event".getBytes(), "moduleName", "flowName");
		
		this.hospitalDao.saveOrUpdate(action);
	}

    @Test
    @DirtiesContext
    public void test_getExclusionEventAction_success()
    {
        // setup
        ExclusionEventAction action = new ExclusionEventActionImpl("errorUri", "actionedBy", "state", "event".getBytes(), "moduleName", "flowName");

        this.hospitalDao.saveOrUpdate(action);

        // test
        ExclusionEventAction result = this.hospitalDao.getExclusionEventActionByErrorUri("errorUri");

        Assert.assertEquals(action,result);
    }
}
