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
package org.ikasan.framework.component.transformation;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.user.ExceptionTransformer;
import org.ikasan.framework.exception.user.ExternalExceptionDefinition;

/**
 * Implementation of <code>Transformer</code> which provides support for 
 * transforming any provided exception in the business flow into a payload of an
 * event.
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class FormatExceptionTransformer implements Transformer
{
    /** transformer for exception creation */
    private ExceptionTransformer exceptionTransformer;

    /** external exception definition */
    private ExternalExceptionDefinition externalExceptionDef;

    /** new payload name */
    private String payloadName;

    /** flow component name */
    private String componentName;

    /**
     * Constructor
     * 
     * @param exceptionTransformer 
     * @param externalExceptionDef 
     * @param payloadName 
     */
    public FormatExceptionTransformer(ExceptionTransformer exceptionTransformer,
            ExternalExceptionDefinition externalExceptionDef, String payloadName)
    {
        this.exceptionTransformer = exceptionTransformer;
        if(this.exceptionTransformer == null)
        {
            throw new IllegalArgumentException("exceptionTransformer cannot be 'null'");
        }

        this.externalExceptionDef = externalExceptionDef;
        if(this.externalExceptionDef == null)
        {
            throw new IllegalArgumentException("externalExceptionDef cannot be 'null'");
        }
        
        this.payloadName = payloadName;
    }

    /**
     * Accessor for componentName
     * @param componentName
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws TransformationException
    {
        List<Payload> payloads = event.getPayloads();

        try
        {
            Throwable throwable = getException(event);
            ExceptionContext exceptionContext = new ExceptionContext(throwable, event, componentName);

            // transform 
            String formattedException = exceptionTransformer.transform(exceptionContext, externalExceptionDef);
            
            // update payload attributes
            Payload payload = payloads.get(0);
            payload.setContent(formattedException.getBytes());

        }
        catch (TransformerException e)
        {
            throw new TransformationException(e);
        }
    }

    /**
     * Extending classes must return the exception to be reported.
     * @param event
     * @return throwable
     * @throws TransformationException 
     */
    abstract public Throwable getException(Event event) throws TransformationException;
}
