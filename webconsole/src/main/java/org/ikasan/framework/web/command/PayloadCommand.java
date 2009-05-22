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
package org.ikasan.framework.web.command;

/**
 * A Wrapper class that can contain a payload along with the module name and initiator 
 * name to deliver that payload to.
 *  
 * @author Ikasan Development Team
 */
public class PayloadCommand
{
    /** The content of the payload */
    private String payloadContent;
    
    /** The name of the module */
    private String moduleName;
    
    /** The name of the initiator */
    private String initiatorName;

    /**
     * Constructor
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     */
    public PayloadCommand(String moduleName, String initiatorName)
    {
        super();
        this.moduleName = moduleName;
        this.initiatorName = initiatorName;
    }

    /**
     * Get the initiator name
     * @return the initiator name
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }

    /**
     * Get the module name
     * @return the module name
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Get the payload content
     * @return the payload content
     */
    public String getPayloadContent()
    {
        return payloadContent;
    }

    /**
     * Set the initiator name
     * @param initiatorName - the initiator name
     */
    public void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * Set the module name
     * @param moduleName - the module name
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Set the payload content name
     * @param payloadContent - the payload content
     */
    public void setPayloadContent(String payloadContent)
    {
        this.payloadContent = payloadContent;
    }
}
