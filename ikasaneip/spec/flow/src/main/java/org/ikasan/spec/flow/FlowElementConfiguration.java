package org.ikasan.spec.flow;

public interface FlowElementConfiguration 
{
	/**
	 * @return the captureMetrics
	 */
	public Boolean getCaptureMetrics();
	/**
	 * @param captureMetrics the captureMetrics to set
	 */
	public void setCaptureMetrics(Boolean captureMetrics);
	
	/**
	 * @return the snapEvent
	 */
	public Boolean getSnapEvent();
	
	/**
	 * @param snapEvent the snapEvent to set
	 */
	public void setSnapEvent(Boolean snapEvent);
}
