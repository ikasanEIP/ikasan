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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.error.reporting.dao.constants.ErrorManagementDaoConstants;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceAction;
import org.ikasan.error.reporting.model.ErrorOccurrenceLink;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.Note;
import org.springframework.dao.support.DataAccessUtils;
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

	public static final String EVENT_IDS = "eventIds";
	public static final String NOW = "now";

	public static final String ERROR_OCCURRENCES_TO_DELETE_QUERY = "select uri from ErrorOccurrence eo " +
			" where eo.expiry < :" + NOW;

	public static final String ERROR_OCCURRENCE_DELETE_QUERY = "delete ErrorOccurrence eo " +
			" where eo.uri in(:" + EVENT_IDS + ")";

	public static final String ERROR_OCCURENCE_NOTES_TO_DELETE_QUERY = "select id.noteId from ErrorOccurrenceNote where id.errorUri in (:" + EVENT_IDS + ")";

	public static final String NOTES_DELETE_QUERY = "delete Note n " +
			" where n.id in(:" + EVENT_IDS + ")";

	public static final String ERROR_OCCURRENCE_NOTE_DELETE_QUERY = "delete ErrorOccurrenceNote where id.errorUri in (:" + EVENT_IDS + ")";

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceAction(org.ikasan.error.reporting.window.ErrorOccurrenceAction)
	 */
	@Override
	public void saveErrorOccurrenceAction(
			ErrorOccurrenceAction errorOccurrenceAction)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceAction);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveNote(org.ikasan.error.reporting.window.Note)
	 */
	@Override
	public void saveNote(Note note)
	{
		this.getHibernateTemplate().saveOrUpdate(note);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#deleteNote(org.ikasan.error.reporting.window.Note)
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
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceLink(org.ikasan.error.reporting.window.ErrorOccurrenceLink)
	 */
	@Override
	public void saveErrorOccurrenceLink(ErrorOccurrenceLink errorOccurrenceLink)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#saveErrorOccurrenceNote(org.ikasan.error.reporting.window.ErrorOccurrenceNote)
	 */
	@Override
	public void saveErrorOccurrenceNote(ErrorOccurrenceNote errorOccurrenceNote)
	{
		this.getHibernateTemplate().saveOrUpdate(errorOccurrenceNote);	
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#deleteErrorOccurence(org.ikasan.error.reporting.window.ErrorOccurrence)
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
	@SuppressWarnings("unchecked")
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
                
                logger.debug("Query: " + query);
                
                query.executeUpdate();
                
                return null;
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.error.reporting.dao.ErrorManagementDao#getNumberOfModuleErrors(java.lang.String)
	 */
	@Override
	public Long getNumberOfModuleErrors(String moduleName, boolean excluded, boolean actioned, Date startDate, Date endDate)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(ErrorOccurrence.class);
		
		if(moduleName != null)
		{
			criteria.add(Restrictions.eq("moduleName", moduleName));
		}
		
		if(startDate != null)
		{
			criteria.add(Restrictions.gt("timestamp", startDate.getTime()));
		}
		
		if(endDate != null)
		{
			criteria.add(Restrictions.lt("timestamp", endDate.getTime()));
		}
		
		if(excluded)
		{
			criteria.add(Restrictions.eq("action", "ExcludeEvent"));
		}
		
		if(actioned)
		{
			criteria.add(Restrictions.isNotNull("userAction"));
		}
		else
		{
			criteria.add(Restrictions.isNull("userAction"));
		}
		
		criteria.setProjection(Projections.projectionList()
		                    .add(Projections.count("moduleName")));
		
		return (Long) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public void housekeep(final Integer numToHousekeep)
	{
		getHibernateTemplate().execute(new HibernateCallback<Object>()
		{
			public Object doInHibernate(Session session) throws HibernateException
			{
				Query query = session.createQuery(ERROR_OCCURRENCES_TO_DELETE_QUERY);
				query.setLong(NOW, System.currentTimeMillis());
				query.setMaxResults(numToHousekeep);

				List<Long> errorUris = (List<Long>)query.list();

				if(errorUris.size() > 0)
				{
					query = session.createQuery(ERROR_OCCURRENCE_NOTE_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, errorUris);
					query.executeUpdate();

					query = session.createQuery(ERROR_OCCURENCE_NOTES_TO_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, errorUris);

					List<Long> errorOccurenceNotesIds = (List<Long>)query.list();

					if(errorOccurenceNotesIds.size() > 0)
					{
						query = session.createQuery(NOTES_DELETE_QUERY);
						query.setParameterList(EVENT_IDS, errorOccurenceNotesIds);
						query.executeUpdate();
					}

					query = session.createQuery(ERROR_OCCURRENCE_DELETE_QUERY);
					query.setParameterList(EVENT_IDS, errorUris);
					query.executeUpdate();
				}

				return null;
			}
		});
	}

}
