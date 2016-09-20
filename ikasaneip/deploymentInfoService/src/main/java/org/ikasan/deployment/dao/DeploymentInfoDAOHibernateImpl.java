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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.deployment.model.DeploymentInfo;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the DeploymentInfoDAO contract.
 * @author jeffmitchell
 */
public class DeploymentInfoDAOHibernateImpl extends HibernateDaoSupport implements DeploymentInfoDAO
{
    /* (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.dao.DeploymentInfoDAO#findDeploymentInfo(java.lang.String, java.lang.String, java.lang.String)
     */
    public DeploymentInfo findDeploymentInfo(String clusterName, String moduleName, String serverName)
    {
        DetachedCriteria deploymentInfoCriteria = DetachedCriteria.forClass(DeploymentInfo.class);
        deploymentInfoCriteria.add(Restrictions.eq("clusterName", clusterName));
        deploymentInfoCriteria.add(Restrictions.eq("moduleName", moduleName));
        deploymentInfoCriteria.add(Restrictions.eq("serverName", serverName));
        List<DeploymentInfo> deploymentInfos = this.getHibernateTemplate().findByCriteria(deploymentInfoCriteria);
        if(deploymentInfos == null || deploymentInfos.size() == 0)
        {
            return null;
        }
        
        return deploymentInfos.get(0);
    }

    /* (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.dao.DeploymentInfoDAO#findDeploymentInfos(java.lang.String, java.lang.String)
     */
    public List<DeploymentInfo> findDeploymentInfos(String clusterName, String moduleName)
    {
        DetachedCriteria deploymentInfoCriteria = DetachedCriteria.forClass(DeploymentInfo.class);
        deploymentInfoCriteria.add(Restrictions.eq("clusterName", clusterName));
        deploymentInfoCriteria.add(Restrictions.eq("moduleName", moduleName));
        return this.getHibernateTemplate().findByCriteria(deploymentInfoCriteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.dao.DeploymentInfoDAO#save(com.mizuho.middleware.ha.deployment.window.DeploymentInfo)
     */
    public void save(DeploymentInfo deploymentInfo)
    {
        this.getHibernateTemplate().saveOrUpdate(deploymentInfo);
    }

    /*
     * (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.dao.DeploymentInfoDAO#saveAll(java.util.List)
     */
    public void saveAll(List<DeploymentInfo> deploymentInfos)
    {
        this.getHibernateTemplate().saveOrUpdateAll(deploymentInfos);
    }

    /* (non-Javadoc)
     * @see com.mizuho.middleware.ha.deployment.dao.DeploymentInfoDAO#remove(com.mizuho.middleware.ha.deployment.window.DeploymentInfo)
     */
    public void remove(DeploymentInfo deploymentInfo)
    {
        this.getHibernateTemplate().delete(deploymentInfo);
    }
}
