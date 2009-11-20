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

import java.util.Date;



import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.endpoint.Endpoint;
import org.ikasan.framework.component.endpoint.EndpointException;



/**
 * Endpoint that checks the time, then logs the event if it is an even minute past the hour
 * otherwise, on odd minutes past the hour throws an UntimelyEventException
 * 
 * @author The Ikasan Development Team
 *
 */
public class TimeDependentExceptionEndpoint implements Endpoint {

	
	private Logger logger = Logger.getLogger(TimeDependentExceptionEndpoint.class);
	

	public void onEvent(Event event) throws EndpointException {
		long currentTimeMillis = System.currentTimeMillis();
		long minsSince1970 = currentTimeMillis / 60000l;
		
		long modValue = minsSince1970 % 2;
		logger.info("currentTimeMillis:"+currentTimeMillis);
		logger.info("minsSince1970:"+minsSince1970);
		logger.info("modValue:"+modValue);
		
		if (modValue > 0){
			logger.info("its not a good time right now:"+event);
			throw new UntimelyEventException("Its not a good time right now, try again later...");
		} else{
			logger.info("dumped event:"+event);
		}

	}

}
