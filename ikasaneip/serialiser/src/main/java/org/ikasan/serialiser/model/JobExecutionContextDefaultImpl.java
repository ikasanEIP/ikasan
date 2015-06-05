/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.serialiser.model;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class JobExecutionContextDefaultImpl implements JobExecutionContext
{
	private JobDataMap jobDataMap;
	private Calendar calendar;
	private Date fireTime;
	private JobDetail jobDetail;
	private Job job;
	private long jobRunTime;
	private Date nextFireTime;
	private Date previousFireTime;
	private int refireCount;
	private Object result;
	private Date scheduledFireTime;
	private Scheduler scheduler;
	private Trigger trigger;
	private boolean isRecovering;
	
	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key)
	{
		return null;//this.jobDataMap.get(key);
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getCalendar()
	 */
	@Override
	public Calendar getCalendar()
	{
		return this.calendar;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getFireTime()
	 */
	@Override
	public Date getFireTime()
	{
		return this.fireTime;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getJobDetail()
	 */
	@Override
	public JobDetail getJobDetail()
	{
		return this.jobDetail;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getJobInstance()
	 */
	@Override
	public Job getJobInstance()
	{
		return this.job;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getJobRunTime()
	 */
	@Override
	public long getJobRunTime()
	{
		return this.jobRunTime;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getMergedJobDataMap()
	 */
	@Override
	public JobDataMap getMergedJobDataMap()
	{
		return this.jobDataMap;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getNextFireTime()
	 */
	@Override
	public Date getNextFireTime()
	{
		return this.nextFireTime;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getPreviousFireTime()
	 */
	@Override
	public Date getPreviousFireTime()
	{
		return this.previousFireTime;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getRefireCount()
	 */
	@Override
	public int getRefireCount()
	{
		return this.refireCount;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getResult()
	 */
	@Override
	public Object getResult()
	{
		return this.result;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getScheduledFireTime()
	 */
	@Override
	public Date getScheduledFireTime()
	{
		return this.scheduledFireTime;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getScheduler()
	 */
	@Override
	public Scheduler getScheduler()
	{
		return this.scheduler;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#getTrigger()
	 */
	@Override
	public Trigger getTrigger()
	{
		return this.trigger;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#isRecovering()
	 */
	@Override
	public boolean isRecovering()
	{
		return this.isRecovering;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(Object key, Object value)
	{
//		this.jobDataMap.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobExecutionContext#setResult(java.lang.Object)
	 */
	@Override
	public void setResult(Object result)
	{
		this.result = result;
	}


	/**
	 * @param jobDataMap the jobDataMap to set
	 */
	public void setJobDataMap(JobDataMap jobDataMap)
	{
		this.jobDataMap = jobDataMap;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(Job job)
	{
		this.job = job;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar)
	{
		this.calendar = calendar;
	}

	/**
	 * @param fireTime the fireTime to set
	 */
	public void setFireTime(Date fireTime)
	{
		this.fireTime = fireTime;
	}

	/**
	 * @param jobDetail the jobDetail to set
	 */
	public void setJobDetail(JobDetail jobDetail)
	{
		this.jobDetail = jobDetail;
	}

	/**
	 * @param jobRunTime the jobRunTime to set
	 */
	public void setJobRunTime(long jobRunTime)
	{
		this.jobRunTime = jobRunTime;
	}

	/**
	 * @param nextFireTime the nextFireTime to set
	 */
	public void setNextFireTime(Date nextFireTime)
	{
		this.nextFireTime = nextFireTime;
	}

	/**
	 * @param previousFireTime the previousFireTime to set
	 */
	public void setPreviousFireTime(Date previousFireTime)
	{
		this.previousFireTime = previousFireTime;
	}

	/**
	 * @param refireCount the refireCount to set
	 */
	public void setRefireCount(int refireCount)
	{
		this.refireCount = refireCount;
	}

	/**
	 * @param scheduledFireTime the scheduledFireTime to set
	 */
	public void setScheduledFireTime(Date scheduledFireTime)
	{
		this.scheduledFireTime = scheduledFireTime;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(Trigger trigger)
	{
		this.trigger = trigger;
	}

	/**
	 * @param isRecovering the isRecovering to set
	 */
	public void setRecovering(boolean isRecovering)
	{
		this.isRecovering = isRecovering;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result
				+ ((fireTime == null) ? 0 : fireTime.hashCode());
		result = prime * result + (isRecovering ? 1231 : 1237);
		result = prime * result + ((job == null) ? 0 : job.hashCode());
		result = prime * result
				+ ((jobDataMap == null) ? 0 : jobDataMap.hashCode());
		result = prime * result
				+ ((jobDetail == null) ? 0 : jobDetail.hashCode());
		result = prime * result + (int) (jobRunTime ^ (jobRunTime >>> 32));
		result = prime * result
				+ ((nextFireTime == null) ? 0 : nextFireTime.hashCode());
		result = prime
				* result
				+ ((previousFireTime == null) ? 0 : previousFireTime.hashCode());
		result = prime * result + refireCount;
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime
				* result
				+ ((scheduledFireTime == null) ? 0 : scheduledFireTime
						.hashCode());
		result = prime * result
				+ ((scheduler == null) ? 0 : scheduler.hashCode());
		result = prime * result + ((trigger == null) ? 0 : trigger.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobExecutionContextDefaultImpl other = (JobExecutionContextDefaultImpl) obj;
		if (calendar == null)
		{
			if (other.calendar != null)
				return false;
		} else if (!calendar.equals(other.calendar))
			return false;
		if (fireTime == null)
		{
			if (other.fireTime != null)
				return false;
		} else if (!fireTime.equals(other.fireTime))
			return false;
		if (isRecovering != other.isRecovering)
			return false;
		if (job == null)
		{
			if (other.job != null)
				return false;
		} else if (!job.equals(other.job))
			return false;
		if (jobDataMap == null)
		{
			if (other.jobDataMap != null)
				return false;
		} else if (!jobDataMap.equals(other.jobDataMap))
			return false;
		if (jobDetail == null)
		{
			if (other.jobDetail != null)
				return false;
		} else if (!jobDetail.equals(other.jobDetail))
			return false;
		if (jobRunTime != other.jobRunTime)
			return false;
		if (nextFireTime == null)
		{
			if (other.nextFireTime != null)
				return false;
		} else if (!nextFireTime.equals(other.nextFireTime))
			return false;
		if (previousFireTime == null)
		{
			if (other.previousFireTime != null)
				return false;
		} else if (!previousFireTime.equals(other.previousFireTime))
			return false;
		if (refireCount != other.refireCount)
			return false;
		if (result == null)
		{
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (scheduledFireTime == null)
		{
			if (other.scheduledFireTime != null)
				return false;
		} else if (!scheduledFireTime.equals(other.scheduledFireTime))
			return false;
		if (scheduler == null)
		{
			if (other.scheduler != null)
				return false;
		} else if (!scheduler.equals(other.scheduler))
			return false;
		if (trigger == null)
		{
			if (other.trigger != null)
				return false;
		} else if (!trigger.equals(other.trigger))
			return false;
		return true;
	}

}
