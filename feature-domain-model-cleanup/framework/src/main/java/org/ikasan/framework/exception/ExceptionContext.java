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

import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.ikasan.framework.component.Event;

/**
 * Mutable context for handling of exceptional events
 * 
 * @author Ikasan Development Team
 *
 */
public class ExceptionContext 
    extends HashMap<String, Object> 
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5529902530602114502L;

    /**
     * Original Throwable
     */
    private Throwable throwable;
    
    /**
     * Event with which a problem has been experienced
     */
    private Event event;
    
    /**
     * Name of component within which problem was experienced
     */
    private String componentName;
    
    /**
     * Key to the resolution of this exception within some domain
     */
    private String resolutionId;
    

    /**
     * Constructor
     * 
     * @param throwable
     * @param event
     * @param componentName
     */
    public ExceptionContext(Throwable throwable, Event event,
            String componentName)
    {
        super();
        this.throwable = throwable;
        this.event = event;
        this.componentName = componentName;
    }

    /**
     * Accessor for the throwable
     * 
     * @return throwable
     */
    public Throwable getThrowable()
    {
        return throwable;
    }

    /**
     * Accessor for the Event
     * 
     * @return Event
     */
    public Event getEvent()
    {
        return event;
    }

    /**
     * Accessor for the componentName
     * 
     * @return componentName
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Accessor for resolutionId
     * 
     * @return String resolutionId
     */
    public String getResolutionId()
    {
        return resolutionId;
    }

    /**
     * Mutator for resolutionId
     * 
     * @param resolutionId
     */
    public void setResolutionId(String resolutionId)
    {
        this.resolutionId = resolutionId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(-854945079, 459763127).appendSuper(
            super.hashCode()).append(this.throwable).append(
            this.componentName).append(
            this.resolutionId).append(this.event).toHashCode()
            ;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof ExceptionContext))
        {
            return false;
        }
        ExceptionContext rhs = (ExceptionContext) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(this.throwable, rhs.throwable)
            .append(this.componentName, rhs.componentName).append(this.resolutionId,
                rhs.resolutionId).append(
                this.event, rhs.event).isEquals();
    }
    
}
