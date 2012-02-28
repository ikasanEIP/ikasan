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

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultAttributeResolverTest {

	
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    final Map<String, Object> localAttributes = new HashMap<String, Object>();
    final Map<String, Object> firstSubresolverAttributes = new HashMap<String, Object>();
    final Map<String, Object> secondSubresolverAttributes = new HashMap<String, Object>();
    
    private String ATTRIBUTE_1_NAME = "attribute1Name";
    private String ATTRIBUTE_2_NAME = "attribute2Name";
    private String ATTRIBUTE_3_NAME = "attribute3Name";
    private String ATTRIBUTE_4_NAME = "attribute4Name";
    
    private String ATTRIBUTE_VALUE_1="attributeValue1";
    private String ATTRIBUTE_VALUE_2="attributeValue2";
    private String ATTRIBUTE_VALUE_3="attributeValue3";
    private String ATTRIBUTE_VALUE_4="attributeValue4";
    private String ATTRIBUTE_VALUE_5="attributeValue5";
    private String ATTRIBUTE_VALUE_6="attributeValue6";
    
    
    
    
    public DefaultAttributeResolverTest(){
    	localAttributes.put(ATTRIBUTE_1_NAME, ATTRIBUTE_VALUE_1);
    	localAttributes.put(ATTRIBUTE_2_NAME, ATTRIBUTE_VALUE_2);
    	firstSubresolverAttributes.put(ATTRIBUTE_2_NAME, ATTRIBUTE_VALUE_3);
    	firstSubresolverAttributes.put(ATTRIBUTE_3_NAME, ATTRIBUTE_VALUE_4);
    	secondSubresolverAttributes.put(ATTRIBUTE_3_NAME, ATTRIBUTE_VALUE_5);
    	secondSubresolverAttributes.put(ATTRIBUTE_4_NAME, ATTRIBUTE_VALUE_6);
   }
    
	/**
	 * Tests that the result of resolveAttributes will be an empty map if the underlying matcher fails to match
	 */
	@Test
	public void testResolveAttributes_withNonMatchingMatcher_willReturnEmptyMap() {
	    final Matcher<?> nonMatcher = mockery.mock(Matcher.class);
	    final Object objectToMatch = new Object();
	    
	    mockery.checking(new Expectations()
        {
            {
                one(nonMatcher).matches(objectToMatch);will(returnValue(false));
             
            }
        });
	    
	    DefaultAttributeResolver resolver = new DefaultAttributeResolver(nonMatcher, localAttributes);
	    Map<String, Object> result = resolver.resolveAttributes(objectToMatch);
	    Assert.assertTrue("result should be empty when underlying matcher fails to match",result.isEmpty());
	    
	    mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests that the result of a resolveAttributes where the underlying matcher matches, and there are no subresolvers
	 * will be the attributes configured directly on that resolver
	 */
	@Test
	public void testResolveAttributes_withMatchingMatcherAndNoSubresolvers_willReturnLocalAttributes() {
	    final Matcher<?> matcher = mockery.mock(Matcher.class);
	    final Object objectToMatch = new Object();
	    
	    mockery.checking(new Expectations()
        {
            {
                one(matcher).matches(objectToMatch);will(returnValue(true));
             
            }
        });
	    
	    DefaultAttributeResolver resolver = new DefaultAttributeResolver(matcher, localAttributes);
	    Map<String, Object> result = resolver.resolveAttributes(objectToMatch);
	    Assert.assertEquals("DefaultAttributeResolver.resolveAttributes should return map containing all local attributes when underlying matcher matches, and there are no subresolvers", localAttributes, result);
	    
	    mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests that when the underlying matcher matches, and there are subresolvers present, the result of resolveAttributes will be 
	 * a merge of the locally configured attributes, and the result of each of the subresolvers invoked sequentially.
	 * 
	 * Any attributes already resolved will be overridden by subsequent resolver result
	 */
	@Test
	public void testResolveAttributes_withMatchingMatcherAndSubresolvers_willReturnLocalAttributesSequentiallyOverriddenBySubresolverAttributes() {
	    final Matcher<?> matcher = mockery.mock(Matcher.class);
	    final Object objectToMatch = new Object();
	    
	    final AttributeResolver firstSubresolver = mockery.mock(AttributeResolver.class);
	    final AttributeResolver secondSubresolver = mockery.mock(AttributeResolver.class);
	    
	    mockery.checking(new Expectations()
        {
            {
                one(matcher).matches(objectToMatch);will(returnValue(true));
                one(firstSubresolver).resolveAttributes(objectToMatch); will(returnValue(firstSubresolverAttributes));
                one(secondSubresolver).resolveAttributes(objectToMatch); will(returnValue(secondSubresolverAttributes));
             
            }
        });
	    
	    List<AttributeResolver> subresolvers = new ArrayList<AttributeResolver>();
	    subresolvers.add(firstSubresolver);
	    subresolvers.add(secondSubresolver);
	    
	    DefaultAttributeResolver resolver = new DefaultAttributeResolver(matcher, localAttributes);
	    resolver.setSubresolvers(subresolvers);
	    Map<String, Object> result = resolver.resolveAttributes(objectToMatch);
	    
	    Map<String, Object> expectedResults = new HashMap<String, Object>();
	    expectedResults.put(ATTRIBUTE_1_NAME, ATTRIBUTE_VALUE_1);
	    expectedResults.put(ATTRIBUTE_2_NAME, ATTRIBUTE_VALUE_3);
	    expectedResults.put(ATTRIBUTE_3_NAME, ATTRIBUTE_VALUE_5);
	    expectedResults.put(ATTRIBUTE_4_NAME, ATTRIBUTE_VALUE_6);

	    Assert.assertEquals(expectedResults, result);
	    
	    mockery.assertIsSatisfied();

	}

}
