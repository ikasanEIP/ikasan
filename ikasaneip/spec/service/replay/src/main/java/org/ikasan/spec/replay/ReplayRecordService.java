package org.ikasan.spec.replay;

public interface ReplayRecordService<EVENT> 
{
	/**
	 * Record an EVENT so that it can be replayed.
	 * 
	 * @param event
	 * @param moduleName
	 * @param flowname
	 * @param timeToLiveDays
	 */
	public void record(EVENT event, String moduleName, String flowname, int timeToLiveDays);
}
