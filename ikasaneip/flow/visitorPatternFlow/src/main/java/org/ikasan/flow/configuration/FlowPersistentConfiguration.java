package org.ikasan.flow.configuration;


public class FlowPersistentConfiguration 
{
	private Boolean isRecording = false;	
	private Integer recordedEventTimeToLiveDays = 30;

	/**
	 * 
	 * @param isRecording
	 */
	public void setIsRecording(Boolean isRecording) 
	{
		this.isRecording = isRecording;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getIsRecording() 
	{
		return this.isRecording ;
	}

	/**
	 * 
	 * @param timeToLive
	 */
	public void setRecordedEventTimeToLive(Integer timeToLive) 
	{
		this.recordedEventTimeToLiveDays = timeToLive;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getRecordedEventTimeToLive() 
	{
		return this.recordedEventTimeToLiveDays;
	}

}
