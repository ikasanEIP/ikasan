/*
 * $Id
 * $URL
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.error.model;

import java.util.Date;

import org.ikasan.framework.component.Event;

/**
 * This class represents an occurrence of an error in the system encapsulating as much as 
 * possible about the occurrence of that error
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrence {

	/**
	 * Unique identifier, populated by persistence mechanism
	 */
	private Long id;


	/**
	 * raw dump of the event associated with this error, if it was event/flow related
	 */
	private String currentEvent;
	
	/**
	 * raw dump of the error as it occurred
	 */
	private String errorDetail;

	/**
	 * Id of the event associated with this error, if it was event/flow related
	 */
	private String eventId;

	/**
	 * name of the flow element where this error occurred, if it was event/flow related
	 */
	private String flowElementName;

	/**
	 * name of the flow where this error occurred, if it was event/flow related
	 */
	private String flowName;

	/**
	 * name of the initiator where this flow occurred, if it took place during Event origination
	 */
	private String initiatorName;

	/**
	 * Time that this error was logged
	 */
	private Date logTime;

	/**
	 * name of the module where this error occurred
	 */
	private String moduleName;
	
	/**
	 * useby date for the errorOccurrence, after which the system may delete it
	 */
	private Date expiry;

	/**
	 * No args constructor, required by certain ORM technologies that shall remain nameless
	 */
	@SuppressWarnings("unused")
	private ErrorOccurrence(){}
	
	/**
	 * Constructor, to be used when the error relates to an Event
	 * 
	 * @param flowElementName
	 * @param flowName
	 * @param initiatorName
	 * @param moduleName
	 * @param expiry
	 */
	public ErrorOccurrence(Throwable throwable, Event event, String moduleName, String flowName, String flowElementName, Date expiry) {
		this(throwable, moduleName, expiry);
		this.currentEvent = flattenEvent(event);
		this.flowElementName = flowElementName;
		this.flowName = flowName;
		this.eventId = event.getId();
	}
	

	
	/**
	 * Constructor to be used when there is no Event involved
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param initiatorName
	 * @param expiry
	 */
	public ErrorOccurrence(Throwable throwable, String moduleName,
			String initiatorName, Date expiry) {
		this(throwable, moduleName, expiry);
		this.initiatorName = initiatorName;
	}
	
	/**
	 * private constructor, ensures logTime is set on creation
	 * 
	 * @param throwable
	 * @param moduleName
	 * @param expiry
	 */
	private ErrorOccurrence(Throwable throwable, String moduleName, Date expiry){
		this.errorDetail = flattenThrowable(throwable);
		this.moduleName = moduleName;
		this.logTime = new Date();
		this.expiry = expiry;
	}




	/**
	 * Flattens the Event into a human readable String value
	 * 
	 * @param event
	 * @return String representation of the event
	 */
	private String flattenEvent(Event event) {
		return event.toString();
	}




	/**
	 * @param throwable
	 * @return
	 */
	private String flattenThrowable(Throwable throwable) {
		StringBuffer flattenedBuffer = new StringBuffer();
		
		Throwable cause = throwable;
		while (cause!=null){
			flattenedBuffer.append(throwable.toString());
			flattenedBuffer.append("\n");
			for (StackTraceElement stackTraceElement : cause.getStackTrace()){
				flattenedBuffer.append(stackTraceElement.toString());
				flattenedBuffer.append("\n");
			}
			if (cause.getCause()!=null){
				flattenedBuffer.append("caused by ...\n");
			}
			cause = cause.getCause();
		}
		return flattenedBuffer.toString();
	}




	/**
	 * @return the currentEvent
	 */
	public String getCurrentEvent() {
		return currentEvent;
	}

	/**
	 * @return the errorDetail
	 */
	public String getErrorDetail() {
		return errorDetail;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @return the flowElementName
	 */
	public String getFlowElementName() {
		return flowElementName;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * @return the initiatorName
	 */
	public String getInitiatorName() {
		return initiatorName;
	}

	/**
	 * @return the logTime
	 */
	public Date getLogTime() {
		return logTime;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param currentEvent the currentEvent to set
	 */
	public void setCurrentEvent(String currentEvent) {
		this.currentEvent = currentEvent;
	}

	/**
	 * @param errorDetail the errorDetail to set
	 */
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	/**
	 * @param flowElementName the flowElementName to set
	 */
	public void setFlowElementName(String flowElementName) {
		this.flowElementName = flowElementName;
	}
	
	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	
	/**
	 * @param initiatorName the initiatorName to set
	 */
	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}
	
	/**
	 * @param logTime the logTime to set
	 */
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @return maximum of first 100 characters of errorDetail
	 */
	public String getErrorSummary(){
		if (errorDetail.length()<100){
			return errorDetail;
		} else{
			return errorDetail.substring(0, 100);
		}
	}


	/**
	 * @param id the id to set
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}
	

	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	/**
	 * @return the expiry
	 */
	public Date getExpiry() {
		return expiry;
	}




	
	public String toString(){
		StringBuffer sb = new StringBuffer(getClass().getName()+" [");
		sb.append("id=");sb.append(id);sb.append(",");
		sb.append("moduleName=");sb.append(moduleName);sb.append(",");
		sb.append("flowName=");sb.append(flowName);sb.append(",");
		sb.append("initiatorName=");sb.append(initiatorName);sb.append(",");
		sb.append("flowElementName=");sb.append(flowElementName);sb.append(",");
		sb.append("errorDetail=");sb.append(errorDetail);sb.append(",");
		sb.append("currentEvent=");sb.append(currentEvent);sb.append(",");
		sb.append("eventId=");sb.append(eventId);sb.append(",");
		sb.append("logTime=");sb.append(logTime);sb.append(",");
		sb.append("expiry=");sb.append(expiry);
		sb.append("]");
		return sb.toString();
	}



}
