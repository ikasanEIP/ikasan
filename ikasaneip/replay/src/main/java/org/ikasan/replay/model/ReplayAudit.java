package org.ikasan.replay.model;

import java.util.Set;

public class ReplayAudit 
{
	private Long id;
	private String user;
	private String replayReason;
	private String moduleName;
	private String flowName;
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
	 * @param timestamp
	 */
	public ReplayAudit(String user, String replayReason, long timestamp) 
	{
		super();
		this.user = user;
		this.replayReason = replayReason;
		this.timestamp = timestamp;
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
	 * @return the moduleName
	 */
	public String getModuleName() 
	{
		return moduleName;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName() 
	{
		return flowName;
	}

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName) 
	{
		this.flowName = flowName;
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
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}	
	
}
