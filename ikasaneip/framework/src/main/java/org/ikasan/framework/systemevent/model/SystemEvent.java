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
package org.ikasan.framework.systemevent.model;

import java.util.Date;

/**
 * Simple JavaBean encapsulating system event information
 * 
 * Simply binds information about an action (can be anything) taken on a subject (again anything)
 * at a given time, by a given actor
 * 
 * @author Ikasan Development Team
 *
 */
public class SystemEvent {
	
	/**
	 * What happened?
	 */
	private String action;
	
	/**
	 * Who/what was causing/driving this?
	 */
	private String actor;
	
	/**
	 * Unique id required for persistence
	 */
	private Long id;
	
	/**
	 * To what? ie subject of the action
	 */
	private String subject;

	/**
	 * When did the action take place?
	 */
	private Date timestamp;
	
	/**
	 * Min time to keep this event if any
	 */
	private Date expiry;

	
	/**
	 * no args constructor required for ORM
	 */
	@SuppressWarnings("unused")
	private SystemEvent(){}

	/**
	 * Constructor
	 * 
	 * @param subject
	 * @param action
	 * @param timestamp
	 * @param actor
	 * @param expiry 
	 */
	public SystemEvent(String subject, String action, Date timestamp,
			String actor, Date expiry) {
		super();
		this.subject = subject;
		this.action = action;
		this.timestamp = timestamp;
		this.actor = actor;
		this.expiry = expiry;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemEvent other = (SystemEvent) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actor == null) {
			if (other.actor != null)
				return false;
		} else if (!actor.equals(other.actor))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	/**
	 * Accessor for action
	 * 
	 * @return action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Accessor for actor 
	 * 
	 * @return actor
	 */
	public String getActor() {
		return actor;
	}

	/**
	 * Accessor for id
	 * 
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Accessor for subject
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Accessor for timestamp
	 * 
	 * @return timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Accessor for expiry
	 * 
	 * @return expiry
	 */
	public Date getExpiry() {
		return expiry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actor == null) ? 0 : actor.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@SuppressWarnings("unused")
	private void setAction(String action) {
		this.action = action;
	}

	@SuppressWarnings("unused")
	private void setActor(String actor) {
		this.actor = actor;
	}
	
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}
	
	@SuppressWarnings("unused")
	private void setSubject(String subject) {
		this.subject = subject;
	}
	
	@SuppressWarnings("unused")
	private void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@SuppressWarnings("unused")
	private void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

}
