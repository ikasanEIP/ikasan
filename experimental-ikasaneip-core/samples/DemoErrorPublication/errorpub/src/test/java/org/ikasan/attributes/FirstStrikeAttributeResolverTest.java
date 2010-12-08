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
package org.ikasan.attributes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class FirstStrikeAttributeResolverTest {

    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
    private AttributeResolver attributeResolver1 = mockery.mock(AttributeResolver.class);
    
    private AttributeResolver attributeResolver2 = mockery.mock(AttributeResolver.class);
    
    private List<AttributeResolver> attributeResolvers = new ArrayList<AttributeResolver>();
    
    public FirstStrikeAttributeResolverTest(){
    	attributeResolvers.add(attributeResolver1);
    	attributeResolvers.add(attributeResolver2);
    }
    
    
	/**
	 * Tests that if none of the underlying resolvers resolve any attributes for the specified
	 * object, then an empty map will be returned
	 */
	@Test
	public void testResolveAttributes_withNonResolvingResolvers_willReturnEmptyMap() {
		final Object objectToMatch = new Object();
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
            	one(attributeResolver1).resolveAttributes(objectToMatch);
            	inSequence(sequence);
            	will(returnValue(new HashMap<String, Object>()));
            	one(attributeResolver2).resolveAttributes(objectToMatch);
            	inSequence(sequence);
            	will(returnValue(new HashMap<String, Object>()));
             
            }
        });
		
		FirstStrikeAttributeResolver firstStrikeAttributeResolver = new FirstStrikeAttributeResolver(attributeResolvers);
		Map<String, Object> result = firstStrikeAttributeResolver.resolveAttributes(objectToMatch);
	
		Assert.assertEquals("Result of FirstStrikeAttributeResolver where none of the underlying resolvers resolves anything, should be an empty map", new HashMap<String, Object>(), result);
		
	    mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests that the result of the first underlying resolver to resolve any attributes will
	 * be immediately returned, without consulting subsequent underlying resolvers
	 */
	@Test
	public void testResolveAttributes_withResolvingResolver_willReturnAfterFirstResolver() {
		final Object objectToMatch = new Object();
		
		final Map<String, Object> resolverResult = new HashMap<String, Object>();
		resolverResult.put("someName", "someValue");
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
            	one(attributeResolver1).resolveAttributes(objectToMatch);
            	inSequence(sequence);
            	will(returnValue(resolverResult));
            	
            }
        });
		
		FirstStrikeAttributeResolver firstStrikeAttributeResolver = new FirstStrikeAttributeResolver(attributeResolvers);
		Map<String, Object> result = firstStrikeAttributeResolver.resolveAttributes(objectToMatch);
	
		Assert.assertEquals("FirstStrikeAttributeResolver should return the result of first underlying resolver that resolves any result",resolverResult, result);
		
	    mockery.assertIsSatisfied();
	}

}
