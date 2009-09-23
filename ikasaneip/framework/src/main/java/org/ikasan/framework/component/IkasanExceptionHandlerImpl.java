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
