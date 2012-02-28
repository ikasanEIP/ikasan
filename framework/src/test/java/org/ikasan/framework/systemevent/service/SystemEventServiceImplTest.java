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
