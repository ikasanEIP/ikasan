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
package org.ikasan.framework.exception;

/**
 * This class represents a part (<code>ResubmissionInfo</code>) of the
 * default exception message structure. <p/>
 * 
 * @author Ikasan Development Team
 */
public class ResubmissionInfo
{

    /** channelName can be removed once BW CMI has been deprecated */
    private String channelName = null;

    /** modelName can be removed once BW CMI has been deprecated */
    private String modelName = null;

    /** essentially the interface name */
    private String componentGroupName = null;

    /** bean component in the interface */
    private String componentName = null;

    /**
     * Creates a new <code>ResubmissionInfo</code> instance.
     * 
     * @param channelName 
     * @param modelName 
     * @param componentGroupName 
     * @param componentName 
     */
    public ResubmissionInfo(final String channelName,
                            final String modelName,
                            final String componentGroupName,
                            String componentName)
    {
        this.channelName = channelName;
        this.modelName = modelName;
        this.componentGroupName = componentGroupName;
        this.componentName = componentName;
    }

    /**
     * Creates a new <code>ResubmissionInfo</code> instance.
     * 
     */
    public ResubmissionInfo()
    {
        // Do Nothing
    }

    /**
     * Sets the channel name.
     * 
     * @param channelName 
     */
    public void setChannelName(final String channelName)
    {
        this.channelName = channelName;
    }

    /**
     * Returns the channel name.
     * 
     * @return the channel name.
     */
    public String getChannelName()
    {
        return this.channelName;
    }

    /**
     * Sets the component model name.
     * 
     * @param modelName 
     */
    public void setModelName(final String modelName)
    {
        this.modelName = modelName;
    }

    /**
     * Returns the component model name.
     * 
     * @return the component model name.
     */
    public String getModelName()
    {
        return this.modelName;
    }

    /**
     * Sets the component group name.
     * 
     * @param componentGroupName 
     */
    public void setComponentGroupName(final String componentGroupName)
    {
        this.componentGroupName = componentGroupName;
    }

    /**
     * Returns the component group name.
     * 
     * @return the component group name.
     */
    public String getComponentGroupName()
    {
        return this.componentGroupName;
    }

    /**
     * Sets the component name.
     * 
     * @param componentName 
     */
    public void setComponentName(final String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * Returns the component name.
     * 
     * @return the component name.
     */
    public String getComponentName()
    {
        return this.componentName;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        return "channelName: [" + this.channelName + "], " + "modelName: ["
                + this.modelName + "]" + "componentGroupName: ["
                + this.componentGroupName + "]" + "componentName: ["
                + this.componentName + "]";
    }

}
