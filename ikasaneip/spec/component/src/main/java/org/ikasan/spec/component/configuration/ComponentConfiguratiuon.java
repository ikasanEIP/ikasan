package org.ikasan.spec.component.configuration;

public class ComponentConfiguratiuon 
{
	private Boolean captureMetrics = false;
	private Boolean snapEvent = false;
	
	/**
	 * @return the captureMetrics
	 */
	public Boolean getCaptureMetrics() 
	{
		return captureMetrics;
	}
	
	/**
	 * @param captureMetrics the captureMetrics to set
	 */
	public void setCaptureMetrics(Boolean captureMetrics) 
	{
		this.captureMetrics = captureMetrics;
	}
	
	/**
	 * @return the snapEvent
	 */
	public Boolean getSnapEvent() 
	{
		return snapEvent;
	}
	
	/**
	 * @param snapEvent the snapEvent to set
	 */
	public void setSnapEvent(Boolean snapEvent) 
	{
		this.snapEvent = snapEvent;
	}
	
	
}
