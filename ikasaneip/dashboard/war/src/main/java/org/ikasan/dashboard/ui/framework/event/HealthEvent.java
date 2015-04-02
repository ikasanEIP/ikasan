package org.ikasan.dashboard.ui.framework.event;

public class HealthEvent 
{
	private String alert;
	private String module;
	
	public HealthEvent(String alert, String module) 
	{
		super();
		this.alert = alert;
		this.module = module;
	}

	public String getAlert() 
	{
		return alert;
	}

	public String getModule() 
	{
		return module;
	}
}
