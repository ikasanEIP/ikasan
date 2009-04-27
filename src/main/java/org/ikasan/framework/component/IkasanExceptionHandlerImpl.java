/*
 * $Id: IkasanExceptionHandlerImpl.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/IkasanExceptionHandlerImpl.java $
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
package org.ikasan.framework.component;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.component.UserExceptionHandler;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionResolution;
import org.ikasan.framework.exception.IkasanExceptionResolutionImpl;
import org.ikasan.framework.exception.IkasanExceptionResolutionNotFoundException;
import org.ikasan.framework.exception.IkasanExceptionResolver;

/**
 * Ikasan Exception Handler concrete implementation class.
 * 
 * Default is to defer to the PluginInvoker.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionHandlerImpl implements IkasanExceptionHandler
{
    /** Logger */
    private static Logger logger = Logger.getLogger(IkasanExceptionHandlerImpl.class);

    /** Module name on behalf of which this handler is operating */
    private String moduleName;

    /** Exception resolver configuration for this module */
    private IkasanExceptionResolver exceptionResolver;

    /** Configuration for the user steps for exception handling */
    private UserExceptionHandler userExceptionHandler;

    /** Listener object for handling Exceptions thrown during UserExceptionHandling */
    private UserExceptionHandlingExceptionListener userExceptionHandlingExceptionListener;

    /**
     * Constructor
     * 
     * @param moduleName - must be a value other than 'empty' or 'null'
     * @param exceptionResolver - must not be 'null'
     */
    public IkasanExceptionHandlerImpl(final String moduleName, final IkasanExceptionResolver exceptionResolver)
    {
        this(moduleName, exceptionResolver, (UserExceptionHandler) null);
    }

    /**
     * Constructor
     * 
     * @param moduleName - must be a value other than 'empty' or 'null'
     * @param exceptionResolver - must not be 'null'
     * @param userExceptionHandler - may be null
     */
    public IkasanExceptionHandlerImpl(final String moduleName, final IkasanExceptionResolver exceptionResolver,
            UserExceptionHandler userExceptionHandler)
    {
        this.moduleName = moduleName;
        if (this.moduleName == null || this.moduleName.length() == 0)
        {
            throw new IllegalArgumentException("Cannot instantiate IkasanExceptionResolver with moduleName ["
                    + this.moduleName + "]!");
        }
        this.exceptionResolver = exceptionResolver;
        if (this.exceptionResolver == null)
        {
            throw new IllegalArgumentException("Cannot instantiate IkasanExceptionResolver with moduleName ["
                    + this.exceptionResolver + "]!");
        }
        this.userExceptionHandler = userExceptionHandler;
    }

    /**
     * Invoke the exception handling
     * 
     * @param componentName The name of the component
     * @param thrown The exception that was thrown
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Throwable thrown)
    {
        return this.invoke(componentName, (Event) null, thrown);
    }

    /**
     * Invoke the exception handling
     * 
     * @param componentName The name of the component
     * @param event The event
     * @param thrown The exception that was thrown
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, Event event, Throwable thrown)
    {
        IkasanExceptionResolution frameworkResolution = null;
        try
        {
            // look up the exception to an Ikasan definition
            frameworkResolution = exceptionResolver.resolve(componentName, thrown);
        }
        catch (IkasanExceptionResolutionNotFoundException e)
        {
            logger.fatal(e);
            frameworkResolution = IkasanExceptionResolutionImpl.getEmergencyResolution();
        }
        // invoke any user defined exception handling
        invokeUserExceptionHandling(componentName, frameworkResolution, thrown, event);
        logger.info("handled throwable [" + thrown + "] componentName [" + componentName + "] returning action ["
                + frameworkResolution.getAction().getType().toString() + "]");
        return frameworkResolution.getAction();
    }

    /**
     * Invoke any user defined exception handling
     * 
     * @param componentName The name of the component
     * @param resolution The resolution for the exception
     * @param thrown The exception that was thrown
     * @param event The event
     */
    private void invokeUserExceptionHandling(String componentName, IkasanExceptionResolution resolution,
            Throwable thrown, Event event)
    {
        if (userExceptionHandler != null)
        {
            try
            {
                ExceptionContext exceptionContext = new ExceptionContext(thrown, event, componentName);
                exceptionContext.setResolutionId(resolution.getId());
                userExceptionHandler.invoke(exceptionContext);
            }
            catch (Throwable t)
            {
                logger.warn("Exception encountered on user defined actions. "
                        + "This will just be logged as only the original exception will be acted upon.", t);
                if (userExceptionHandlingExceptionListener != null)
                {
                    userExceptionHandlingExceptionListener.notify(t);
                }
            }
        }
    }

    /**
     * Accessor for userExceptionHandlingExceptionListener
     * 
     * @return userExceptionHandlingExceptionListener
     */
    public UserExceptionHandlingExceptionListener getUserExceptionHandlingExceptionListener()
    {
        return userExceptionHandlingExceptionListener;
    }

    /**
     * Mutator for userExceptionHandlingExceptionListener
     * 
     * @param userExceptionHandlingExceptionListener Exception if user exception handlnig fails
     */
    public void setUserExceptionHandlingExceptionListener(
            UserExceptionHandlingExceptionListener userExceptionHandlingExceptionListener)
    {
        this.userExceptionHandlingExceptionListener = userExceptionHandlingExceptionListener;
    }
}
