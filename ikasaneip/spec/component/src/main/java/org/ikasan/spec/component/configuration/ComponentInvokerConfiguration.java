package org.ikasan.spec.component.configuration;

public class ComponentInvokerConfiguration
{
	private Boolean captureMetrics = false;
	private Boolean snapEvent = false;
	
	/**
     * Retrieves the current status of capturing metrics.
     *
     * @return Boolean value indicating whether metrics are being captured or not.
     */
	public Boolean getCaptureMetrics() 
	{
		return captureMetrics;
	}
	
	/**
     * Sets the flag indicating whether metrics should be captured.
     *
     * @param captureMetrics true to capture metrics, false otherwise
     */
	public void setCaptureMetrics(Boolean captureMetrics) 
	{
		this.captureMetrics = captureMetrics;
	}
	
	/**
     * Retrieves the current value of the snapEvent property.
     *
     * @return the current value of the snapEvent property
     */
	public Boolean getSnapEvent() 
	{
		return snapEvent;
	}
	
	/**
     * Sets the flag indicating whether the snap event should occur.
     *
     * @param snapEvent true to enable snap event, false to disable it
     */
	public void setSnapEvent(Boolean snapEvent) 
	{
		this.snapEvent = snapEvent;
	}

}
