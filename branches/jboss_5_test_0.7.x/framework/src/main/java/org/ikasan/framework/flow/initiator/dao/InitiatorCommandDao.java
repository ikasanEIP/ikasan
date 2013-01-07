package org.ikasan.framework.flow.initiator.dao;

import org.ikasan.framework.initiator.InitiatorCommand;

/**
 * Data Access interface for the persistence of <code>InitiatorCommand</code>s
 * 
 * @author Ikasan Development Team
 *
 */
public interface InitiatorCommandDao
{
    
    /**
     * Retrieves the latest <code>InitiatorCommand</code> for the specified <code>Initiator</code>
     * if any
     * 
     * @param moduleName
     * @param initiatorName
     * @return the most recent <code>InitiatorCommand</code>, or null if none exist for this <code>Initiator</code>
     */
    public InitiatorCommand getLatestInitiatorCommand(String moduleName, String initiatorName);
    
    /**
     * Persists the <code>InitiatorCommand</code>
     * 
     * @param initiatorCommand to persist
     * @param keepHistoricInitiatorCommands 
     */
    public void save(InitiatorCommand initiatorCommand, boolean keepHistoricInitiatorCommands);
}
