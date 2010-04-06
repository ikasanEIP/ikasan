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

import org.ikasan.framework.initiator.InitiatorCommand;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>InitiatorCommandDao</code>
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateInitiatorCommandDao extends HibernateDaoSupport implements InitiatorCommandDao
{
    /**
     * General query for finding existing InitiatorCommands for a given Initiator
     */
    private static final String moduleInitiatorCommandsQuery = "from InitiatorCommand i where i.moduleName = ? and i.initiatorName = ? order by i.submittedTime desc";
   
    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.initiator.dao.InitiatorCommandDao#getLatestInitiatorCommand(java.lang.String, java.lang.String)
     */
    public InitiatorCommand getLatestInitiatorCommand(String moduleName, String initiatorName)
    {
        List initaitorCommands = getInitiatorCommands(moduleName, initiatorName);
        InitiatorCommand latestInitatorCommand = null;
        
        if (!initaitorCommands.isEmpty()){
            latestInitatorCommand = (InitiatorCommand) initaitorCommands.get(0);
        }
        return latestInitatorCommand;
    }



    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.initiator.dao.InitiatorCommandDao#save(org.ikasan.framework.initiator.InitiatorCommand, boolean)
     */
    public void save(InitiatorCommand initiatorCommand, boolean keepHistoricInitiatorCommands)
    {
        List<InitiatorCommand> historicInitiatorCommands = getInitiatorCommands(initiatorCommand.getModuleName(), initiatorCommand.getInitiatorName());
        
        
        getHibernateTemplate().save(initiatorCommand);
        
        if (!keepHistoricInitiatorCommands){
            for (InitiatorCommand historicInitiatorCommand :historicInitiatorCommands){
                getHibernateTemplate().delete(historicInitiatorCommand);
            }
        }
        
    }
    
    /**
     * Finds all existing InitiatorCommands for a given Initiator
     * 
     * @param moduleName
     * @param initiatorName
     * @return List of Initiator commands in reverse chronological order
     */
    private List getInitiatorCommands(String moduleName, String initiatorName)
    {
        List initaitorCommands = getHibernateTemplate().find(moduleInitiatorCommandsQuery, new String[]{moduleName, initiatorName});
        return initaitorCommands;
    }
}
