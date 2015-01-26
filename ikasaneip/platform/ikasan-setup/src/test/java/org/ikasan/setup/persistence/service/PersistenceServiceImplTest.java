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
package org.ikasan.setup.persistence.service;

import junit.framework.Assert;

import org.ikasan.setup.persistence.dao.PersistenceDAOHibernateImpl;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * JUnit based test class for testing HibernatePointToPointFlowProfileDao
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/providers-properties.xml", "/hsqldb-datasource-conf.xml"})
public class PersistenceServiceImplTest
{
    /**
     * The context that the tests run in, allows for mocking actual concrete
     * classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
  
    @Resource 
    PersistenceDAOHibernateImpl persistenceDAOHibernateImpl;

    @Resource
    PersistenceService persistenceService;

    /**
     * Test
     */
    @Before
    public void setup()
    {
        persistenceService.createPersistence();

    }
    
    @After
    public void dropAll(){
        persistenceDAOHibernateImpl.delete("usersAuthorities");
        persistenceDAOHibernateImpl.delete("authorities");
        persistenceDAOHibernateImpl.delete("users");
        persistenceDAOHibernateImpl.delete("consolePointToPointFlow");
        persistenceDAOHibernateImpl.delete("consolePointToPointFlowProfile");
        persistenceDAOHibernateImpl.delete("consoleModule");
        persistenceDAOHibernateImpl.delete("moduleStartup");
        persistenceDAOHibernateImpl.delete("systemEvent");
        persistenceDAOHibernateImpl.delete("confParamString");
        persistenceDAOHibernateImpl.delete("confParamMapString");
        persistenceDAOHibernateImpl.delete("confParamMap");
        persistenceDAOHibernateImpl.delete("confParamLong");
        persistenceDAOHibernateImpl.delete("confParamListString");
        persistenceDAOHibernateImpl.delete("confParamList");
        persistenceDAOHibernateImpl.delete("confParamInteger");
        persistenceDAOHibernateImpl.delete("confParamBoolean");
        persistenceDAOHibernateImpl.delete("configurationParameter");
        persistenceDAOHibernateImpl.delete("configuration");
        persistenceDAOHibernateImpl.delete("flowEventTriggerParameters");
        persistenceDAOHibernateImpl.delete("flowEVentTrigger");
        persistenceDAOHibernateImpl.delete("version");
        persistenceDAOHibernateImpl.delete("exclusionEvent");
        persistenceDAOHibernateImpl.delete("filter");
        persistenceDAOHibernateImpl.delete("wiretap");
    }

    /**
     * Test
     */
    @Test
    public void test_persistenceService_getVersion()
    {
        String version = persistenceService.getVersion();
        Assert.assertEquals("1.0.0", version);
    }

}
