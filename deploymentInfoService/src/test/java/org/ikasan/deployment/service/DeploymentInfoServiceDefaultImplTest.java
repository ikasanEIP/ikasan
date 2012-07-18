/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.deployment.service;

import javax.annotation.Resource;

import org.ikasan.deployment.dao.DeploymentInfoDAO;
import org.ikasan.deployment.model.DeploymentInfo;
import org.ikasan.deployment.service.DeploymentInfoServiceDefaultImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for {@link DeploymentInfoServiceDefaultImpl}.
 * 
 * @author Ikasan Development Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
      "/deploymentInfoService-conf.xml",
      "/hsqldb-datasource-conf.xml"
      })
public class DeploymentInfoServiceDefaultImplTest
{
    /** class on test */
    @Resource
    DeploymentInfoService deploymentInfoService;
    
    /** dao for checking results */
    @Resource
    DeploymentInfoDAO deploymentInfoDAO;
    
    /**
     * Test standard registration lifecycle of
     * 
     *  - registerInactiveServer (first time its deployed, but not active)
     *  - registerInactiveServer (subsequent deployments, but not active)
     *  - registerActiveServer   (its now active)
     *  - unregisterServer       (its now undeployed)
     *  
     */
    @Test public void test_registration_lifecycle()
    {
        // check no entries via the DAO
        Assert.assertNull("deploymentInfo should be null", deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName") );

        // register first time inactive entry and check it worked
        deploymentInfoService.registerInactiveServer("serverName");
        DeploymentInfo retrievedDeploymentInfo = deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName");
        Assert.assertNotNull("deploymentInfo should be present", retrievedDeploymentInfo);
        Assert.assertFalse("deploymentInfo should be inactive", retrievedDeploymentInfo.isActive());

        // register second time inactive entry and check it still worked
        deploymentInfoService.registerInactiveServer("serverName");
        retrievedDeploymentInfo = deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName");
        Assert.assertNotNull("deploymentInfo should be present", retrievedDeploymentInfo);
        Assert.assertFalse("deploymentInfo should be inactive", retrievedDeploymentInfo.isActive());

        // update register to active entry and check
        deploymentInfoService.registerActiveServer("serverName");
        retrievedDeploymentInfo = deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName");
        Assert.assertNotNull("deploymentInfo should be present", retrievedDeploymentInfo);
        Assert.assertTrue("deploymentInfo should be active", retrievedDeploymentInfo.isActive());

        // unregisterServer on undeployment and check
        deploymentInfoService.unregisterServer("serverName");
        Assert.assertNull("deploymentInfo should be null", deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName") );
    }

}
