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
