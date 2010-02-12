package org.ikasan.framework.flow.initiator.dao;

import org.ikasan.framework.initiator.InitiatorStartupControl;

/**
 * Data Access interface for the persistence of <code>InitiatorStartupControl</code>s
 * 
 * @author Ikasan Development Team
 *
 */
public interface InitiatorStartupControlDao
{
    
    /**
     * Retrieves the <code>InitiatorStartupControl</code> for the specified <code>Initiator</code>
     * 
     * @param moduleName
     * @param initiatorName
     * 
     * @return the <code>InitiatorStartupControl</code> for the specified <code>Initiator</code>
     */
    public InitiatorStartupControl getInitiatorStartupControl(String moduleName, String initiatorName);
    
    /**
     * Persists the <code>InitiatorStartupControl</code>
     * 
     * @param initiatorStartupControl to persist
     */
    public void save(InitiatorStartupControl initiatorStartupControl);
}
