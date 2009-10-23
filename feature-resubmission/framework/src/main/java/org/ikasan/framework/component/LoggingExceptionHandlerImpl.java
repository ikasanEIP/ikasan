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
    public IkasanExceptionAction handleThrowable(String componentName, Throwable throwable)
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
