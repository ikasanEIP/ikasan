/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.systemevent.service;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.ikasan.framework.systemevent.dao.SystemEventDao;
import org.ikasan.framework.systemevent.model.SystemEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test class for <code>SystemEventServiceImpl</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class SystemEventServiceImplTest {

	private Mockery mockery = new Mockery();
	
	private SystemEventDao systemEventDao = mockery.mock(SystemEventDao.class);
	
	private SystemEventServiceImpl nonExpiringSystemEventServiceImpl = new SystemEventServiceImpl(systemEventDao,null);

	private static final Long expiry = 60l;
	
	private SystemEventServiceImpl expiringSystemEventServiceImpl = new SystemEventServiceImpl(systemEventDao,expiry);

	@Test
	public void testLogSystemEvent_nonExpiring() {
		final String subject = "subject";
		final String action = "action";
		final String actor = "actor";
		
		mockery.checking(new Expectations()
        {
            {
                one(systemEventDao).save(with(new SystemEventMatcher(subject, action, actor,null)));
            }
        });
		
		nonExpiringSystemEventServiceImpl.logSystemEvent(subject, action,  actor);
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testLogSystemEvent_expiring() {
		final String subject = "subject";
		final String action = "action";
		final String actor = "actor";
		
		mockery.checking(new Expectations()
        {
            {
            	one(systemEventDao).save(with(new SystemEventMatcher(subject, action, actor,expiry)));
            }
        });
		
		expiringSystemEventServiceImpl.logSystemEvent(subject, action, actor);
		mockery.assertIsSatisfied();
	}
	
	class SystemEventMatcher extends TypeSafeMatcher<SystemEvent>{

		private String subject, action, actor;
		
		private Long expiryMinutes;
		
		public SystemEventMatcher(String subject, String action, String actor,
				Long expiryMinutes) {
			super();
			this.subject = subject;
			this.action = action;
			this.actor = actor;
			this.expiryMinutes = expiryMinutes;
		}

		

		
		
		
		@Override
		public boolean matchesSafely(SystemEvent item) {
			if (!this.subject.equals(item.getSubject())){
				return false;
			}
			if (!this.action.equals(item.getAction())){
				return false;
			}
			if (!this.actor.equals(item.getActor())){
				return false;
			}
			if (this.expiryMinutes!=null){
				if (!item.getExpiry().equals(new Date(item.getTimestamp().getTime()+(60000*this.expiryMinutes.longValue())))){
					return false;
				}
			}else{
				if (item.getExpiry()!=null){
					return false;
				}
			}
			return true;
		}

		public void describeTo(Description arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
