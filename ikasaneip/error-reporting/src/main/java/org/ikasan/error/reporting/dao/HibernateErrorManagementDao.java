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
package org.ikasan.error.reporting.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.dao.constants.ErrorManagementDaoConstants;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceAction;
import org.ikasan.error.reporting.model.ErrorOccurrenceLink;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.Link;
import org.ikasan.error.reporting.model.Note;
import org.ikasan.error.reporting.service.ErrorReportingManagementServiceImpl;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateErrorManagementDao  extends HibernateDaoSupport implements ErrorManagementDao
{
	private static Logger logger = Logger.getLogger(HibernateErrorManagementDao.class);

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceAction(org.ikasan.error.reporting.model.ErrorOccurrenceAction)
	 */
	@Override
	public void saveErrorOccurrenceAction(
			ErrorOccurrenceAction errorOccurrenceAction)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceAction);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveNote(org.ikasan.error.reporting.model.Note)
	 */
	@Override
	public void saveNote(Note note)
	{
		this.getHibernateTemplate().saveOrUpdate(note);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#deleteNote(org.ikasan.error.reporting.model.Note)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteNote(final Note note)
	{
		this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
   
                Query query = session.createQuery(ErrorManagementDaoConstants.DELETE_NOTE);
                
                query.setParameter(ErrorManagementDaoConstants.NOTE_ID, note.getId());

                query.executeUpdate();
                
                return null;
            }
        });
		
		this.getHibernateTemplate().delete(note);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceLink(org.ikasan.error.reporting.model.ErrorOccurrenceLink)
	 */
	@Override
	public void saveErrorOccurrenceLink(ErrorOccurrenceLink errorOccurrenceLink)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceNote(org.ikasan.error.reporting.model.ErrorOccurrenceNote)
	 */
	@Override
	public void saveErrorOccurrenceNote(ErrorOccurrenceNote errorOccurrenceNote)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceNote);	
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#deleteErrorOccurence(org.ikasan.error.reporting.model.ErrorOccurrence)
	 */
	@Override
	public void deleteErrorOccurence(ErrorOccurrence errorOccurrence)
	{
		this.getHibernateTemplate().delete(errorOccurrence);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#findErrorOccurrences(java.util.List)
	 */
	@Override
	public List<ErrorOccurrence> findErrorOccurrences(List<String> errorUris)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
		
		if(errorUris != null && errorUris.size() > 0)
		{
			criteria.add(Restrictions.in("uri", errorUris));
		}
		
		criteria.addOrder(Order.desc("timestamp"));

        return (List<ErrorOccurrence>)this.getHibernateTemplate().findByCriteria(criteria, 0, 2000);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getNotesByErrorUri(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Note> getNotesByErrorUri(final String errorUri)
	{
		return (List<Note>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
   
                Query query = session.createQuery(ErrorManagementDaoConstants.GET_NOTE_BY_ERROR_URI);
                
                query.setParameter(ErrorManagementDaoConstants.ERROR_URI, errorUri);

                return (List<Note>)query.list();
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#findErrorOccurrenceActions(java.util.List, java.util.List, java.util.List, java.util.Date, java.util.Date)
	 */
	@Override
	public List<ErrorOccurrence> findActionErrorOccurrences(
			List<String> moduleName, List<String> flowName,
			List<String> flowElementname, Date startDate, Date endDate)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
		
		if(moduleName != null && moduleName.size() > 0)
		{
			criteria.add(Restrictions.in("moduleName", moduleName));
		}
		
		if(flowName != null && flowName.size() > 0)
		{
			criteria.add(Restrictions.in("flowName", flowName));
		}
		
		if(flowElementname != null && flowElementname.size() > 0)
		{
			criteria.add(Restrictions.in("flowElementName", flowElementname));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("userActionTimestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("userActionTimestamp", endDate.getTime()));
		}
		
		criteria.add(Restrictions.isNotNull("userAction"));
		criteria.addOrder(Order.desc("userActionTimestamp"));

        return (List<ErrorOccurrence>)this.getHibernateTemplate().findByCriteria(criteria, 0, 2000);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#houseKeepErrorOccurrenceActions()
	 */
	@Override
	public List<ErrorOccurrenceAction> houseKeepErrorOccurrenceActions()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getAllErrorUrisWithNote()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllErrorUrisWithNote()
	{
		return (List<String>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
   
                Query query = session.createQuery("select ecn.id.errorUri from ErrorOccurrenceNote ecn");

                return (List<String>)query.list();
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getErrorOccurrenceNotesByErrorUri(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ErrorOccurrenceNote> getErrorOccurrenceNotesByErrorUri(
			final String errorUri)
	{
		return (List<ErrorOccurrenceNote>)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
   
                Query query = session.createQuery(ErrorManagementDaoConstants.GET_ERROR_OCCURRENCE_NOTE_BY_ERROR_URI);
                
                query.setParameter(ErrorManagementDaoConstants.ERROR_URI, errorUri);

                return (List<ErrorOccurrenceNote>)query.list();
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#close(java.util.List)
	 */
	@Override
	public void close(final List<String> uris, final String user)
	{
		this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
   
                Query query = session.createQuery(ErrorManagementDaoConstants.CLOSE_ERROR_OCCURRENCE);
                
                query.setParameterList(ErrorManagementDaoConstants.ERROR_URIS, uris);
                query.setParameter(ErrorManagementDaoConstants.USER, user);
                query.setParameter(ErrorManagementDaoConstants.TIMESTAMP, System.currentTimeMillis());
                
                logger.info("Query: " + query);
                System.out.println("Query: " + query);
                
                query.executeUpdate();
                
                return null;
            }
        });
	}

}
