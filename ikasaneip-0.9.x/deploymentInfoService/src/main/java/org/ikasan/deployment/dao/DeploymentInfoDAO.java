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
package org.ikasan.deployment.dao;

import java.util.List;

import org.ikasan.deployment.model.DeploymentInfo;

/**
 * DAO contract for the deployment info persitence which is used to record Ikasan 
 * module deployments in a highly available runtime environment. 
 * 
 * This most usually consists of a number of deployments which are loaded, 
 * but not actively started - the deploymentInfo details this.
 * 
 * @author Ikasan Development Team
 */
public interface DeploymentInfoDAO
{
    /**
     * Find an existing deployment info based on the given cluster, module and server name.
     * If none is found then 'null' will be returned.
     * @param clusterName
     * @param moduleName
     * @param serverName
     * @return DeploymentInfo
     */
    public DeploymentInfo findDeploymentInfo(String clusterName, String moduleName, String serverName);
    
    /**
     * Find all deployment infos for a given cluster and module name. If none are
     * found then an empty list is returned.
     * @param clusterName
     * @param moduleName
     * @return List<DeploymentInfo>
     */
    public List<DeploymentInfo> findDeploymentInfos(String clusterName, String moduleName);
    
    /**
     * Save a new or updated deployment info.
     * @param deploymentInfo
     */
    public void save(DeploymentInfo deploymentInfo);
    
    /**
     * Save a collection of new or updated deployment infos.
     * @param deploymentInfos
     */
    public void saveAll(List<DeploymentInfo> deploymentInfos);

    /**
     * Remove a deployment info.
     * @param deploymentInfo
     */
    public void remove(DeploymentInfo deploymentInfo);
}
