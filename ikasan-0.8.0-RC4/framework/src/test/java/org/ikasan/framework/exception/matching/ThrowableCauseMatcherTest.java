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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;




/**
 * Test class for ThrowableCauseMatcher
 * 
 * @author Ikasan Development Team
 *
 */
public class ThrowableCauseMatcherTest {

	final Throwable matchingThrowable = new Throwable("match");
	final Throwable nonMatchingThrowable = new Throwable("nonMatch");
	final Throwable directlyCausedByMatchingThrowable = new Throwable("directlyCausedByMatch", matchingThrowable);
	final Throwable directlyCausedByNonMatchingThrowable = new Throwable("directlyCausedByNonMatch",nonMatchingThrowable);
	final Throwable inDirectlyCausedByMatchingThrowable = new Throwable("indirectlyCausedByMatch",directlyCausedByMatchingThrowable);
	final Throwable inDirectlyCausedByNonMatchingThrowable = new Throwable("indirectlyCausedByNonMatch",directlyCausedByNonMatchingThrowable);

	final TypeSafeMatcher<Throwable> throwableMatcher = new MockThrowableMatcher(matchingThrowable);
	
	
	@Test
	public void testMatchesSafely_Directly() {
		final ThrowableCauseMatcher directCausedByMatcher = new ThrowableCauseMatcher(throwableMatcher) ;
		
		Assert.assertTrue(directCausedByMatcher.matchesSafely(matchingThrowable));
		Assert.assertFalse(directCausedByMatcher.matchesSafely(nonMatchingThrowable));
		Assert.assertTrue(directCausedByMatcher.matchesSafely(directlyCausedByMatchingThrowable));
		Assert.assertFalse(directCausedByMatcher.matchesSafely(directlyCausedByNonMatchingThrowable));
		Assert.assertFalse(directCausedByMatcher.matchesSafely(inDirectlyCausedByMatchingThrowable));
		Assert.assertFalse(directCausedByMatcher.matchesSafely(inDirectlyCausedByNonMatchingThrowable));
	}
	
	@Test
	public void testMatchesSafely_Indirectly() {
		final ThrowableCauseMatcher indirectCausedByMatcher = new ThrowableCauseMatcher(throwableMatcher,false) ;
		
		Assert.assertTrue(indirectCausedByMatcher.matchesSafely(matchingThrowable));
		Assert.assertFalse(indirectCausedByMatcher.matchesSafely(nonMatchingThrowable));
		Assert.assertTrue(indirectCausedByMatcher.matchesSafely(directlyCausedByMatchingThrowable));
		Assert.assertFalse(indirectCausedByMatcher.matchesSafely(directlyCausedByNonMatchingThrowable));
		Assert.assertTrue(indirectCausedByMatcher.matchesSafely(inDirectlyCausedByMatchingThrowable));
		Assert.assertFalse(indirectCausedByMatcher.matchesSafely(inDirectlyCausedByNonMatchingThrowable));
	}
	
	

}

class MockThrowableMatcher extends TypeSafeMatcher<Throwable>{

	private Throwable match;
	
	
	public MockThrowableMatcher(Throwable match) {
		super();
		this.match = match;
	}

	@Override
	public boolean matchesSafely(Throwable throwable) {
		return throwable.equals(match);
	}

	public void describeTo(Description arg0) {
		// TODO Auto-generated method stub
		
	}
}
