/* 
 * $Id: HibernateInitiatorStartupControlDao.java 2821 2010-02-12 14:01:56Z magicduncan $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/framework/src/main/java/org/ikasan/framework/flow/initiator/dao/HibernateInitiatorStartupControlDao.java $
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
package org.ikasan.flow.dao;

import java.util.List;

import org.ikasan.flow.FlowStartupControl;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>FlowStartupControlDao</code>
 * 
 * 
 * @author Ikasan Development Team
 * 
 */
public class HibernateFlowStartupControlDao extends HibernateDaoSupport implements FlowStartupControlDao
{
    /**
     * General query for finding existing InitiatorCommands for a given
     * Initiator
     */
    private static final String flowStartupControlQuery = "from FlowStartupControl i where i.moduleName = ? and i.flowName = ?";

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.initiator.dao.FlowStartupControlDao#
     * getFlowStartupControl(java.lang.String, java.lang.String)
     */
    public FlowStartupControl getFlowStartupControl(String moduleName, String flowName)
    {
        List results = getHibernateTemplate().find(flowStartupControlQuery, new Object[] { moduleName, flowName });
        if (!results.isEmpty())
        {
            return (FlowStartupControl) results.get(0);
        }
        return new FlowStartupControl(moduleName, flowName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.initiator.dao.FlowStartupControlDao#save
     * (org.ikasan.framework.initiator.FlowStartupControl)
     */
    public void save(FlowStartupControl flowStartupControl)
    {
        getHibernateTemplate().saveOrUpdate(flowStartupControl);
    }
}
