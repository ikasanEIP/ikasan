package org.ikasan.spec.replay;

public interface ReplayListener<EVENT> 
{
	/**
	 * Event that gets fired when a replay event occurs.
	 * 
	 */
	public void onReplay(EVENT event);
}
