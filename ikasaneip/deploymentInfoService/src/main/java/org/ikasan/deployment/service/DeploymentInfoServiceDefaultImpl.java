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
package org.ikasan.deployment.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.deployment.dao.DeploymentInfoDAO;
import org.ikasan.deployment.model.DeploymentInfo;
import org.ikasan.spec.deployment.DeploymentInfoService;

/**
 * Implementation of the DeploymentInfoService. 
 * @author Ikasan Development Team
 */
public class DeploymentInfoServiceDefaultImpl implements DeploymentInfoService
{
    /** class logger */
    private static Logger logger = Logger.getLogger(DeploymentInfoServiceDefaultImpl.class);

    /** name of a logical cluster within which this application is deployed */
    private String clusterName;
    
    /** name of the module within the context of the clusterName and runtime server */
    private String moduleName;
    
    /** DAO for persistence */
    private DeploymentInfoDAO deploymentInfoDAO;

    /**
     * Constructor
     * @param deploymentInfoDAO
     */
    public DeploymentInfoServiceDefaultImpl(String clusterName, String moduleName, DeploymentInfoDAO deploymentInfoDAO)
    {
        this.clusterName = clusterName;
        if(clusterName == null)
        {
            throw new IllegalArgumentException("clusterName cannot be 'null'");
        }

        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        this.deploymentInfoDAO = deploymentInfoDAO;
        if(deploymentInfoDAO == null)
        {
            throw new IllegalArgumentException("DeploymentInfoDAO cannot be 'null'");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.service.DeploymentInfoService#registerInactive(java.lang.String, java.lang.String, java.lang.String)
     */
    public void registerInactiveServer(String serverName)
    {
        DeploymentInfo deploymentInfo = this.deploymentInfoDAO.findDeploymentInfo(clusterName, moduleName, serverName);
        if(deploymentInfo == null)
        {
            deploymentInfo = new DeploymentInfo(clusterName, moduleName, serverName);
        }

        deploymentInfo.setActive(Boolean.FALSE);
        deploymentInfo.setUpdateDateTime(System.currentTimeMillis());
        this.deploymentInfoDAO.save(deploymentInfo);
    }

    /*
     * (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.service.DeploymentInfoService#registerActive(java.lang.String, java.lang.String, java.lang.String)
     */
    public void registerActiveServer(String serverName)
    {
        // make sure only one deploymentInfo is active 
        List<DeploymentInfo> deploymentInfos = this.deploymentInfoDAO.findDeploymentInfos(clusterName, moduleName);
        for(DeploymentInfo deploymentInfo:deploymentInfos)
        {
            if(serverName.equals(deploymentInfo.getServerName()))
            {
                deploymentInfo.setActive(Boolean.TRUE);
            }
            else
            {
                deploymentInfo.setActive(Boolean.FALSE);
            }

            deploymentInfo.setUpdateDateTime(System.currentTimeMillis());
            this.deploymentInfoDAO.save(deploymentInfo);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.service.DeploymentInfoService#unregister(java.lang.String, java.lang.String, java.lang.String)
     */
    public void unregisterServer(String serverName)
    {
        DeploymentInfo deploymentInfo = this.deploymentInfoDAO.findDeploymentInfo(clusterName, moduleName, serverName);
        if(deploymentInfo == null)
        {
            logger.warn("DeploymentInfo not found for clusterName[" + clusterName + "] moduleName[" + moduleName + "] serverName[" + serverName + "]");
            return;
        }
        
        this.deploymentInfoDAO.remove(deploymentInfo);
    }

}
