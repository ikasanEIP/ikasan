package org.ikasan.replay.model;

import java.util.Set;

public class ReplayAudit 
{
	private Long id;
	private String user;
	private String replayReason;
	private String targetServer;
	private Set<ReplayAuditEvent> replayAuditEvents;
	private long timestamp;

	/**
	 * Default constructor for Hibernate
	 */
	@SuppressWarnings("unused")
	private ReplayAudit()
	{
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param user
	 * @param replayReason
	 * @param targetServer
	 * @param moduleName
	 * @param flowName
	 */
	public ReplayAudit(String user, String replayReason, String targetServer) 
	{
		super();
		this.user = user;
		this.replayReason = replayReason;
		this.targetServer = targetServer;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * @return the id
	 */
	public Long getId() 
	{
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) 
	{
		this.id = id;
	}
	
	/**
	 * @return the user
	 */
	public String getUser() 
	{
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) 
	{
		this.user = user;
	}
	
	/**
	 * @return the replayReason
	 */
	public String getReplayReason() 
	{
		return replayReason;
	}
	
	/**
	 * @param replayReason the replayReason to set
	 */
	public void setReplayReason(String replayReason) 
	{
		this.replayReason = replayReason;
	}
	
	/**
	 * @return the targetServer
	 */
	public String getTargetServer() 
	{
		return targetServer;
	}


	/**
	 * @param targetServer the targetServer to set
	 */
	public void setTargetServer(String targetServer) 
	{
		this.targetServer = targetServer;
	}

	/**
	 * @return the replayAuditEvents
	 */
	public Set<ReplayAuditEvent> getReplayAuditEvents() 
	{
		return replayAuditEvents;
	}

	/**
	 * @param replayAuditEvents the replayAuditEvents to set
	 */
	public void setReplayAuditEvents(Set<ReplayAuditEvent> replayAuditEvents) 
	{
		this.replayAuditEvents = replayAuditEvents;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() 
	{
		return timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) 
	{
		this.timestamp = timestamp;
	}	
	
}
