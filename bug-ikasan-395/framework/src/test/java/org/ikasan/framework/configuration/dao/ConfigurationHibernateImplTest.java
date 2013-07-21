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
package org.ikasan.framework.configuration.dao;

import java.util.ArrayList;

import junit.framework.Assert;

import org.ikasan.framework.configuration.model.Configuration;
import org.ikasan.framework.configuration.model.ConfigurationParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for {@link ConfigurationHibernateImpl}.
 * 
 * @author Ikasan Development Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/org/ikasan/framework/configuration/dao/ConfigurationImplTest.xml",
        "/configuration-hsqldb-config.xml"})
public class ConfigurationHibernateImplTest
{
    @Autowired
    private ConfigurationDao configurationDao;

    @Before
    public void setup()
    {
        Configuration configuration = new Configuration("configurationId", new ArrayList<ConfigurationParameter>());
        configuration.getConfigurationParameters().add(new ConfigurationParameter("name","value","description"));
        configurationDao.save(configuration);
    }
    
    /**
     * Test finding a configuration,
     * where configurationId cannot be matched; and where configurationId can be matched.
     */
    @Test
    @DirtiesContext
    public void configuration_find_configuration()
    {
        // test execution
        Assert.assertNull("Searching for config where clientId doesn't exist", configurationDao.findById("configurationId does not exist"));
        Assert.assertNotNull("Searching for config where it does exist", configurationDao.findById("configurationId"));
    }

    /**
     * Test saving an updated configuration parameter
     */
    @Test
    @DirtiesContext
    public void configuration_save_updated_configurationParameter_in_configuration()
    {
        Configuration configuration = this.configurationDao.findById("configurationId");
        configuration.getConfigurationParameters().get(0).setValue("new value");
        configurationDao.save(configuration);
        
        // test execution
        Assert.assertEquals("Compare updated configuraionParameter with saved", "new value", 
                (configurationDao.findById("configurationId")).getConfigurationParameters().get(0).getValue());
    }

    /**
     * Test deleting a configuration
     */
    @Test
    @DirtiesContext
    public void configuration_delete_configuration()
    {
        Configuration configuration = configurationDao.findById("configurationId");
        Assert.assertNotNull("Check entry exists pre delete", configuration);

        configurationDao.delete(configuration);
        
        // test execution
        Assert.assertNull("Check entry has been removed post delete", 
            configurationDao.findById("configurationId"));
    }

}
