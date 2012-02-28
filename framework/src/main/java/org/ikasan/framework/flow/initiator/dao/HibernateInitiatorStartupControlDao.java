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
package org.ikasan.framework.flow.initiator.dao;

import java.util.List;

import org.ikasan.framework.initiator.InitiatorStartupControl;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>InitiatorStartupControlDao</code>
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateInitiatorStartupControlDao extends HibernateDaoSupport implements InitiatorStartupControlDao
{
    /**
     * General query for finding existing InitiatorCommands for a given Initiator
     */
    private static final String initiatorStartupControlQuery = "from InitiatorStartupControl i where i.moduleName = ? and i.initiatorName = ?";

	/* (non-Javadoc)
	 * @see org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao#getInitiatorStartupControl(java.lang.String, java.lang.String)
	 */
	public InitiatorStartupControl getInitiatorStartupControl(String moduleName,
			String initiatorName) {
		List results = getHibernateTemplate().find(initiatorStartupControlQuery, new Object[]{moduleName,initiatorName});
		if (!results.isEmpty()){
			return (InitiatorStartupControl)results.get(0);
		}
		return new InitiatorStartupControl(moduleName, initiatorName);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.flow.initiator.dao.InitiatorStartupControlDao#save(org.ikasan.framework.initiator.InitiatorStartupControl)
	 */
	public void save(InitiatorStartupControl initiatorStartupControl) {
		getHibernateTemplate().saveOrUpdate(initiatorStartupControl);
		
	}
   
    
}
