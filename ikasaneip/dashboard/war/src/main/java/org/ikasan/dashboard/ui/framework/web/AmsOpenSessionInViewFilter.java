package org.ikasan.dashboard.ui.framework.web;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

/**
 * 
 * @author CMI2 Development Team
 *
 */
public class AmsOpenSessionInViewFilter extends OpenSessionInViewFilter {
    private Logger logger = Logger.getLogger(AmsOpenSessionInViewFilter.class);

	@Override
	public void closeSession(Session session, SessionFactory sessionFactory)
	{
	    try
	    {
	        session.flush();
	    }
	    catch(Exception e)
	    {
	        logger.info("An exception occurred trying to flush the hibernate session: " + e.getMessage());
	    }

		super.closeSession(session, sessionFactory);
	}
}
