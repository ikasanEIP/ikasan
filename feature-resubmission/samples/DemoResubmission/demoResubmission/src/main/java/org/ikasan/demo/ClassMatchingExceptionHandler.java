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
package org.ikasan.demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.component.LoggingExceptionHandlerImpl;
import org.ikasan.framework.exception.IkasanExceptionAction;

public class ClassMatchingExceptionHandler extends LoggingExceptionHandlerImpl implements IkasanExceptionHandler{

	private Map<Class<? extends Throwable>, IkasanExceptionAction> errorMappings = new HashMap<Class<? extends Throwable>, IkasanExceptionAction>();

	private Logger logger = Logger.getLogger(ClassMatchingExceptionHandler.class);

	/**
	 * Constructor
	 * 
	 * @param errorMappings
	 */
	public ClassMatchingExceptionHandler(
			Map<Class<? extends Throwable>, IkasanExceptionAction> errorMappings) {
		super();
		this.errorMappings = errorMappings;
	}

	/**
     * Push an exception that occurred whilst handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param event The original event
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Event event, final Throwable throwable){
    	logger.info("about to check for configured action");
    	
    	IkasanExceptionAction mappedAction = resolveAction(throwable);
    	if (mappedAction!=null){
    		return mappedAction;
    	}
    	logger.info("did not find configured action");
    	
    	return super.invoke(componentName, event, throwable);
    }

    /**
     * Push an exception that occurred outside the scope of handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Throwable throwable){
    	IkasanExceptionAction mappedAction = resolveAction(throwable);
    	if (mappedAction!=null){
    		return mappedAction;
    	}
    	
    	return super.invoke(componentName, throwable);
    }

	private IkasanExceptionAction resolveAction(Throwable throwable) {
		IkasanExceptionAction result = null;
		
		Class <? extends Throwable> thisThrowableClass = throwable.getClass();
		

		for (Class <? extends Throwable> mappedThrowableClass :   errorMappings.keySet()){
			if (mappedThrowableClass==thisThrowableClass){
				result = errorMappings.get(mappedThrowableClass);
				break;
			}
			
			//TODO iterate up thisThroableClass's heirarchy trying to find a best match
		}
		logger.info("about to return resolved action:"+result);
		
		return result;
	}
	
    /**
     * @return
     */
    public Map<Class<? extends Throwable>, IkasanExceptionAction> getErrorMappings() {
		return errorMappings;
	}
}
