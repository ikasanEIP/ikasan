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
