package org.ikasan.spec.replay;


public interface ReplayAudit
{
	/**
	 * @return the id
	 */
	public Long getId();
	
	/**
	 * @param id the id to set
	 */
	@SuppressWarnings("unused")
	public void setId(Long id);
	
	/**
	 * @return the user
	 */
	public String getUser();
	
	/**
	 * @param user the user to set
	 */
	public void setUser(String user);
	
	/**
	 * @return the replayReason
	 */
	public String getReplayReason();
	
	/**
	 * @param replayReason the replayReason to set
	 */
	public void setReplayReason(String replayReason);
	
	/**
	 * @return the targetServer
	 */
	public String getTargetServer();


	/**
	 * @param targetServer the targetServer to set
	 */
	public void setTargetServer(String targetServer);

	/**
	 * @return the timestamp
	 */
	public long getTimestamp();
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp);

}
