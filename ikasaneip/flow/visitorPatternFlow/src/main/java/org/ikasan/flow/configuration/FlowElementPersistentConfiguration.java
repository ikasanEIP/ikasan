package org.ikasan.flow.configuration;

import org.ikasan.spec.flow.FlowElementConfiguration;

public class FlowElementPersistentConfiguration implements FlowElementConfiguration 
{

	private Boolean captureMetrics = false;
	private Boolean snapEvent = false;
	
	@Override
	public Boolean getCaptureMetrics() 
	{
		return captureMetrics;
	}
	
	@Override
	public void setCaptureMetrics(Boolean captureMetrics) 
	{
		this.captureMetrics = captureMetrics;
	}
	
	@Override
	public Boolean getSnapEvent() 
	{
		return snapEvent;
	}
	
	@Override
	public void setSnapEvent(Boolean snapEvent) 
	{
		this.snapEvent = snapEvent;
	}
}
