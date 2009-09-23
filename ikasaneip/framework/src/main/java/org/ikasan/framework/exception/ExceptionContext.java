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
package org.ikasan.framework.exception;

import java.util.HashMap;

import org.ikasan.framework.component.Event;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

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
