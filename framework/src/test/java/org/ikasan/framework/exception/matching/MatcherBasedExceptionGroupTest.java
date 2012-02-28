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
package org.ikasan.framework.exception.matching;

import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 */
public class MatcherBasedExceptionGroupTest {

	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
	private Matcher<?> matcher = mockery.mock(Matcher.class);
	
	private IkasanExceptionAction ikasanExceptionAction = mockery.mock(IkasanExceptionAction.class);
	
	private Throwable throwable = mockery.mock(Throwable.class);
	/**
	 * Class Under Test
	 */
	private MatcherBasedExceptionGroup exceptionGroup = new MatcherBasedExceptionGroup(matcher, ikasanExceptionAction);
	
	@Test
	public void testGetAction() {
		Assert.assertEquals(ikasanExceptionAction, exceptionGroup.getAction());
	}

	@Test
	public void testIncludes_WillReturnResultOfUnderlyingMatcher() {
    	mockery.checking(new Expectations()
        {
            {
            	one(matcher).matches(throwable);will(returnValue(true));
            }
        });
    	Assert.assertTrue(exceptionGroup.includes(throwable));
    	mockery.assertIsSatisfied();
    	
    	mockery.checking(new Expectations()
        {
            {
            	one(matcher).matches(throwable);will(returnValue(false));
            }
        });
    	Assert.assertFalse(exceptionGroup.includes(throwable));
    	mockery.assertIsSatisfied();
	}

}
