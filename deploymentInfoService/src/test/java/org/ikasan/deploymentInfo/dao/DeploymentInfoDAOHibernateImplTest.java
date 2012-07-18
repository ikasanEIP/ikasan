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
package org.ikasan.deploymentInfo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.deploymentInfo.model.DeploymentInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for {@link DeploymentInfoDAOHibernateImpl}.
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
public class DeploymentInfoDAOHibernateImplTest
{
    @Resource
    DeploymentInfoDAO deploymentInfoDAO;
    
    /**
     * Test save followed by a findDeploymentInfo
     */
    @Test public void test_save_followed_by_findDeploymentInfo_followed_by_remove()
    {
        // first time has no entry
        Assert.assertNull("deploymentInfo should not exist", deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName"));

        // insert a deploymentInfo and try retrieving it
        DeploymentInfo deploymentInfo = new DeploymentInfo("clusterName", "moduleName", "serverName");
        deploymentInfoDAO.save(deploymentInfo);
        DeploymentInfo retrievedDeploymentInfo = deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName");
        Assert.assertTrue("The retrieved deploymentInfo is not the instance that was saved", deploymentInfo.equals(retrievedDeploymentInfo) );

        // remove the deploymentInfo entry and check removed
        deploymentInfoDAO.remove(deploymentInfo);
        Assert.assertNull("deploymentInfo should be null", deploymentInfoDAO.findDeploymentInfo("clusterName", "moduleName", "serverName") );
    }

    /**
     * Test saving a collection of deploymentInfos and removing them one by one
     */
    @Test public void test_saveDeploymentInfos()
    {
        // ensure no entries
        Assert.assertTrue("No entries should be persisted", deploymentInfoDAO.findDeploymentInfos("clusterName", "moduleName").size() == 0);

        // insert a collection of deploymentInfos
        DeploymentInfo deploymentInfo1 = new DeploymentInfo("clusterName", "moduleName", "serverName1");
        DeploymentInfo deploymentInfo2 = new DeploymentInfo("clusterName", "moduleName", "serverName2");
        DeploymentInfo deploymentInfo3 = new DeploymentInfo("clusterName", "moduleName", "serverName3");
        List<DeploymentInfo> deploymentInfos = new ArrayList<DeploymentInfo>();
        deploymentInfos.add(deploymentInfo1);
        deploymentInfos.add(deploymentInfo2);
        deploymentInfos.add(deploymentInfo3);
        deploymentInfoDAO.saveAll(deploymentInfos);

        // check they were saved
        List<DeploymentInfo> retrievedDeploymentInfos = deploymentInfoDAO.findDeploymentInfos("clusterName", "moduleName");
        Assert.assertTrue("Should have three entries", retrievedDeploymentInfos.size() == 3);
        for(DeploymentInfo deploymentInfo:deploymentInfos)
        {
            deploymentInfoDAO.remove(deploymentInfo);
        }
        
        // ensure no entries
        Assert.assertTrue("No entries should be persisted", deploymentInfoDAO.findDeploymentInfos("clusterName", "moduleName").size() == 0);
    }

}
