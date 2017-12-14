package org.ikasan.replay.model;


import org.ikasan.spec.replay.ReplayAudit;

public class HibernateReplayAudit implements ReplayAudit
{
	private Long id;
	private String moduleName;
	private String flowName;
	private String eventId;
	private String user;
	private String replayReason;
	private String targetServer;
	private long timestamp;

	/**
	 * Default constructor for Hibernate
	 */
	@SuppressWarnings("unused")
	private HibernateReplayAudit()
	{
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param user
	 * @param replayReason
	 * @param targetServer
	 */
	public HibernateReplayAudit(String user, String replayReason, String targetServer)
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
	public void setId(Long id)
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((replayReason == null) ? 0 : replayReason.hashCode());
		result = prime * result
				+ ((targetServer == null) ? 0 : targetServer.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HibernateReplayAudit other = (HibernateReplayAudit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (replayReason == null) {
			if (other.replayReason != null)
				return false;
		} else if (!replayReason.equals(other.replayReason))
			return false;
		if (targetServer == null) {
			if (other.targetServer != null)
				return false;
		} else if (!targetServer.equals(other.targetServer))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ReplayAudit [id=" + id + ", user=" + user + ", replayReason="
				+ replayReason + ", targetServer=" + targetServer
				 + ", timestamp="
				+ timestamp + "]";
	}	
	
	
	
}
