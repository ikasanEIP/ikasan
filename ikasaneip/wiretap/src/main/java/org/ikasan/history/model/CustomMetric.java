package org.ikasan.history.model;

public class CustomMetric 
{
	private Long id;
	private Long messageHistoryFlowEventId;
	private String name;
	private String value;
	

	protected CustomMetric() 
	{
		super();
	}
	
	/**
	 * Constructor 
	 * 
	 * @param name
	 * @param value
	 */
	public CustomMetric(String name, String value) 
	{
		super();
		this.name = name;
		this.value = value;
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
	 * @return the messageHistoryFlowEventId
	 */
	public Long getMessageHistoryFlowEventId()
	{
		return messageHistoryFlowEventId;
	}
	
	/**
	 * @param messageHistoryFlowEventId the messageHistoryFlowEventId to set
	 */
	public void setMessageHistoryFlowEventId(Long messageHistoryFlowEventId) 
	{
		this.messageHistoryFlowEventId = messageHistoryFlowEventId;
	}
	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	
	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) 
	{
		this.value = value;
	}
		
}
