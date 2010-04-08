/* 
 * $Id$
 * $URL$
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
package org.ikasan.framework.initiator;


/**
 * JavaBean encapsulating startup control information for Initiators
 * 
 * The following start types are defined:
 * 
 * 		AUTOMATIC: Initiator will be started by its container when the container is initialised
 * 		MANUAL: Initiator will not be started by its container when container is started/initialised, but will be manually startable at a later stage
 * 		DISABLED: Initiator will not be started by its container when container is started/initialised, and will not be manually startable at a later stage	
 * 
 * @author The Ikasan Development Team
 *
 */
public class InitiatorStartupControl
{
	
	public enum StartupType { 
		DISABLED, MANUAL, AUTOMATIC;
	}

    /**
     * Name of the module with which the target Initiator is associated
     */
    private String moduleName;
    
    /**
     * Name of the target Initiator
     */
    private String initiatorName;
    
    /**
     * Startup type
     */
    private StartupType startupType;
    
    /**
     * Comment
     */
    private String comment;
    
   

	/**
     * Identity key
     */
    private Long id;
    
    
    /**
     * No args constructor, as required by ceratin ORM tools
     */
    @SuppressWarnings("unused")
    private InitiatorStartupControl(){}
    


 
    /**
     * Constructor
     * 
     * @param moduleName - Name of the module with which the target Initiator is associated
     * @param initiatorName - Name of the target Initiator
     */
    public InitiatorStartupControl(String moduleName, String initiatorName){
        this.moduleName = moduleName;
        this.initiatorName = initiatorName;
        this.startupType = StartupType.MANUAL;
    }
    

    /**
     * Accessor for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }


    /**
     * Setter for moduleName
     * 
     * @param moduleName
     */
    @SuppressWarnings("unused")
    private void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Accessor for initiatorName
     * 
     * @return initiatorName
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }



    /**
     * Setter for initiatorName
     * 
     * @param initiatorName
     */
    @SuppressWarnings("unused")
    private void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * Accessor for startupType
     * 
     * @return startupType
     */
    public StartupType getStartupType()
    {
        return startupType;
    }


    /**
     * Setter for startupType
     * 
     * @param action
     */
    public void setStartupType(StartupType startupType)
    {
        this.startupType = startupType;
    }

    @SuppressWarnings("unused")
	private String getStartupTypeString(){
    	return startupType.toString();
    }
    
    @SuppressWarnings("unused")
	private void setStartupTypeString(String startupTypeString){
    	this.startupType = StartupType.valueOf(startupTypeString);
    }
    /**
     * Accessor for id
     * 
     * @return id
     */
    public Long getId(){
        return id;
    }
    
    /**
     * Setter for id
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id){
        this.id = id;
    }
    
    public boolean isAutomatic(){
    	return startupType!=null && startupType.equals(StartupType.AUTOMATIC);
    }
    
    public boolean isManual(){
    	return startupType!=null && startupType.equals(StartupType.MANUAL);
    }
    
    public boolean isDisabled(){
    	return startupType!=null && startupType.equals(StartupType.DISABLED);
    }
    
    /**
     * Accessor for comment
     * 
     * @return comment
     */
    public String getComment() {
		return comment;
	}


	/**
	 * Mutator for comment
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
    
}
