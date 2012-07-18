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
package org.ikasan.deploymentInfo.model;

import org.ikasan.deploymentInfo.model.DeploymentInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link DeploymentInfo} focusing on 
 * testing constructor, mutators, and equals-hashCode contract
 * 
 * @author Ikasan Development Team
 *
 */
public class DeploymentInfoTest
{
    private DeploymentInfo first = new DeploymentInfo("clusterName1", "moduleName1", "serverName1");
    private DeploymentInfo second = new DeploymentInfo("clusterName2", "moduleName2", "serverName2");
    private DeploymentInfo third = new DeploymentInfo("clusterName1", "moduleName1", "serverName1");

    private DeploymentInfo first_withDiffServerName = new DeploymentInfo("clusterName1", "moduleName1", "serverNameDifference");
    private DeploymentInfo first_withDiffModuleName = new DeploymentInfo("clusterName1", "moduleName1", "serverNameDifference");
    private DeploymentInfo first_withDiffClusterName = new DeploymentInfo("clusterName1", "moduleName1", "serverNameDifference");

    /**
     * Test failed constructor due to null cluster name.
     */
    @Test(expected=IllegalArgumentException.class) public void test_failed_constructor_due_null_clusterName()
    {
        new DeploymentInfo(null, null, null);
    }
    
    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected=IllegalArgumentException.class) public void test_failed_constructor_due_null_moduleName()
    {
        new DeploymentInfo("clusterName", null, null);
    }
    
    /**
     * Test failed constructor due to null server name.
     */
    @Test(expected=IllegalArgumentException.class) public void test_failed_constructor_due_null_serverName()
    {
        new DeploymentInfo("clustername", "moduleName", null);
    }
    
    /**
     * Test default getters
     */
    @Test public void test_default_getters()
    {
        DeploymentInfo deploymentInfo = new DeploymentInfo("clusterName", "moduleName", "serverName");
        Assert.assertTrue("clusterName should be equal", deploymentInfo.getClusterName().equals("clusterName"));
        Assert.assertTrue("moduleName should be equal", deploymentInfo.getModuleName().equals("moduleName"));
        Assert.assertTrue("serverName should be equal", deploymentInfo.getServerName().equals("serverName"));
        Assert.assertFalse("Active should be false", deploymentInfo.isActive());
        Assert.assertNull("Url should be null", deploymentInfo.getUrl());
        Assert.assertTrue("CreateDateTime should be > 0", deploymentInfo.getCreateDateTime() > 0);
        Assert.assertTrue("UpdateDateTime should be == 0", deploymentInfo.getUpdateDateTime() == 0);
    }

    /**
     * Test mutators
     */
    @Test public void test_mutators()
    {
        DeploymentInfo deploymentInfo = new DeploymentInfo("clusterName", "moduleName", "serverName");
        deploymentInfo.setActive(Boolean.TRUE);
        deploymentInfo.setUrl("url");
        deploymentInfo.setUpdateDateTime(100L);
        
        Assert.assertTrue("clusterName should be equal", deploymentInfo.getClusterName().equals("clusterName"));
        Assert.assertTrue("moduleName should be equal", deploymentInfo.getModuleName().equals("moduleName"));
        Assert.assertTrue("serverName should be equal", deploymentInfo.getServerName().equals("serverName"));
        Assert.assertTrue("Active should be true", deploymentInfo.isActive());
        Assert.assertTrue("Url should be url", deploymentInfo.getUrl().equals("url"));
        Assert.assertTrue("CreateDateTime should be > 0", deploymentInfo.getCreateDateTime() > 0);
        Assert.assertTrue("UpdateDateTime should be 100L", deploymentInfo.getUpdateDateTime() == 100L);
    }

    /**
     * Test equality on equals
     */
    @Test public void test_equals()
    {
        Assert.assertFalse("First and second should not be equal", first.equals(second));
        Assert.assertTrue("First and third should be equal", first.equals(third));
        Assert.assertFalse("First should not be equal if cluster name is different", first.equals(first_withDiffClusterName));
        Assert.assertFalse("First should not be equal if module name is different", first.equals(first_withDiffModuleName));
        Assert.assertFalse("First should not be equal if server name is different", first.equals(first_withDiffServerName));
    }

    /**
     * Test equality on hashcode
     */
    @Test public void test_hashcode()
    {
        Assert.assertFalse("First and second should have different hashcodes", first.hashCode() == second.hashCode());
        Assert.assertTrue("First and third should have the same hashcodes", first.hashCode() == third.hashCode());
    }

    /**
     * Test equality based on different class types
     */
    @Test public void test_equals_different_classes()
    {
        Assert.assertFalse("First and String are not the same class type hence not equal", first.equals("any string"));
    }

}
