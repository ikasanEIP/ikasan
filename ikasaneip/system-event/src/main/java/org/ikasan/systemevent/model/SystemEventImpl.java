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
package org.ikasan.systemevent.model;

import jakarta.persistence.*;
import org.ikasan.spec.systemevent.SystemEvent;

import javax.naming.OperationNotSupportedException;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Simple JavaBean encapsulating system event information
 * 
 * Simply binds information about an action (can be anything) taken on a subject (again anything)
 * at a given time, by a given actor
 * 
 * @author Ikasan Development Team
 *
 */
@Entity
@Table(name="SystemEvent")
public class SystemEventImpl implements SystemEvent
{
    /**
     * Unique id required for persistence
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Transient
	private String moduleName;

	/**
	 * What happened?
	 */
    @Column(name="Action")
    private String action;
	
	/**
	 * Who/what was causing/driving this?
	 */
    @Column(name="Actor")
    private String actor;
	
	/**
	 * To what? ie subject of the action
	 */
    @Column(name="Subject")
    private String subject;

	/**
	 * When did the action take place?
	 */
    @Column(name="Timestamp")
    private Date timestamp;
	
	/**
	 * Min time to keep this event if any
	 */
    @Column(name="Expiry")
    private Date expiry;

    /**
     * Flag to indicate if the entity has been harvested.
     */
    @Column(name="Harvested")
    private boolean harvested;

    /** the time the record was harvested */
    @Column(name="HarvestedDateTime")
    private long harvestedDateTime;
	
	/**
	 * no args constructor required for ORM
	 */
	@SuppressWarnings("unused")
	protected SystemEventImpl(){}

	/**
	 * Constructor
	 * 
	 * @param subject
	 * @param action
	 * @param timestamp
	 * @param actor
	 * @param expiry 
	 */
	public SystemEventImpl(String subject, String action, Date timestamp,
                           String actor, Date expiry) {
		super();
		this.subject = subject;
		this.action = action;
		this.timestamp = timestamp;
		this.actor = actor;
		this.expiry = expiry;
	}

    @Override
    public String getModuleName() {
	    return this.moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
	    this.moduleName = moduleName;
    }

    /**
	 * Accessor for action
	 * 
	 * @return action
	 */
	@Override
    public String getAction() {
		return action;
	}

	/**
	 * Accessor for actor 
	 * 
	 * @return actor
	 */
	@Override
    public String getActor() {
		return actor;
	}

	/**
	 * Accessor for id
	 * 
	 * @return id
	 */
	@Override
    public Long getId() {
		return id;
	}

	/**
	 * Accessor for subject
	 * 
	 * @return subject
	 */
	@Override
    public String getSubject() {
		return subject;
	}

	/**
	 * Accessor for timestamp
	 * 
	 * @return timestamp
	 */
	@Override
    public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Accessor for expiry
	 * 
	 * @return expiry
	 */
	@Override
    public Date getExpiry() {
		return expiry;
	}

    /**
     * Accessor for harvested
     *
     * @return harvested
     */
    public boolean isHarvested()
    {
        return harvested;
    }

    /**
     * Accessor for harvestedDateTime
     *
     * @return
     */
    public long getHarvestedDateTime()
    {
        return harvestedDateTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        { return true; }
        if ( o == null || getClass() != o.getClass() )
        { return false; }
        SystemEventImpl that = (SystemEventImpl) o;
        return harvested == that.harvested && harvestedDateTime == that.harvestedDateTime && Objects
            .equals(action, that.action) && Objects.equals(actor, that.actor) && Objects.equals(id, that.id) && Objects
            .equals(subject, that.subject) && Objects.equals(timestamp, that.timestamp) && Objects
            .equals(expiry, that.expiry);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(action, actor, id, subject, timestamp, expiry, harvested, harvestedDateTime);
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

    public void setHarvested(boolean harvested)
    {
        this.harvested = harvested;
    }

    public void setHarvestedDateTime(long harvestedDateTime)
    {
        this.harvestedDateTime = harvestedDateTime;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", SystemEventImpl.class.getSimpleName() + "[", "]").add("action='" + action + "'")
                                                                                       .add("actor='" + actor + "'")
                                                                                       .add("id=" + id)
                                                                                       .add("subject='" + subject + "'")
                                                                                       .add("timestamp=" + timestamp)
                                                                                       .add("expiry=" + expiry)
                                                                                       .add("harvested=" + harvested)
                                                                                       .add("harvestedDateTime="
                                                                                           + harvestedDateTime)
                                                                                       .toString();
    }
}
