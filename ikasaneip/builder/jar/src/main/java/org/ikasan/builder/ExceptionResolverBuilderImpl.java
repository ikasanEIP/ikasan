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
package org.ikasan.builder;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.MatchingExceptionResolver;
import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.exceptionResolver.matcher.MatcherBasedExceptionGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Exception Resolver builder.
 * 
 * @author Ikasan Development Team
 */
public class ExceptionResolverBuilderImpl implements ExceptionResolverBuilder
{
	/**
	 * List of matcher based exception groups to be used by the exception resolver.
	 */
	List matcherBasedExceptionGroups = new ArrayList();

	/**
	 * Build and return an instance of the exception resolver
	 * @return
     */
	public ExceptionResolver build()
	{
		return new MatchingExceptionResolver(this.matcherBasedExceptionGroups);
	}

	/**
	 * Add a matcher based exception based on the specified class and action to take for that exception class.
	 * @param exceptionClass
	 * @param exceptionAction
	 */
	@Override
	public ExceptionResolverBuilder addExceptionToAction(Class exceptionClass, ExceptionAction exceptionAction)
	{
		this.matcherBasedExceptionGroups.add( new MatcherBasedExceptionGroup( new IsInstanceOf(exceptionClass), exceptionAction) );
		return this;
	}
}

