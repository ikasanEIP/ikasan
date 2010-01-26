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

package org.ikasan.framework.component;

import org.apache.log4j.Logger;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;

/**
 * Simple implementation of <code>IkasanExceptionHandler</code>, that simply logs the
 * exception and returns a predefined <code>IkasanExceptionAction</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class LoggingExceptionHandlerImpl implements IkasanExceptionHandler
{
    private Logger logger = Logger.getLogger(LoggingExceptionHandlerImpl.class);
    
    /**
     * Default error action
     */
    private IkasanExceptionAction rollbackAndStopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP,0l,0);

    /**
     * The action to return, note that this defaults to the rollbackAndStopAction
     */
    private IkasanExceptionAction errorAction = rollbackAndStopAction;
    

    /* (non-Javadoc)
     * @see org.ikasan.framework.component.IkasanExceptionHandler#invoke(java.lang.String, org.ikasan.framework.component.Event, java.lang.Throwable)
     */
    public IkasanExceptionAction invoke(String componentName, Event event, Throwable throwable)
    {
        logger.error("Throwable caught, componentName ["+componentName+"], event ["+event+"], throwable message["+throwable.getMessage()+"], stacktrace follows:");
        logErrorThrowable(throwable);
        
        logger.info(this+" about to return errorAction:"+errorAction);
        return errorAction;
    }


    /**
     * Log this exception at error level
     * 
     * @param throwable
     */
    private void logErrorThrowable(Throwable throwable)
    {
        Throwable thisThrowable = throwable;
        while (thisThrowable!=null){
            logger.error(throwable.getMessage());
            for (StackTraceElement stackTraceElement : thisThrowable.getStackTrace()){
                logger.error(stackTraceElement);
            }
            thisThrowable=thisThrowable.getCause();
            if(thisThrowable!=null){
                logger.error("...caused by...");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.component.IkasanExceptionHandler#invoke(java.lang.String, java.lang.Throwable)
     */
    public IkasanExceptionAction invoke(String componentName, Throwable throwable)
    {
        logger.error("Throwable caught, componentName ["+componentName+"], throwable message["+throwable.getMessage()+"], stacktrace follows:");
        logErrorThrowable(throwable);
        return errorAction;
    }
    
    /**
     * Setter for errorAction, overrides the default
     * 
     * @param errorAction
     */
    public void setErrorAction(IkasanExceptionAction errorAction)
    {
        this.errorAction = errorAction;
    }
}
