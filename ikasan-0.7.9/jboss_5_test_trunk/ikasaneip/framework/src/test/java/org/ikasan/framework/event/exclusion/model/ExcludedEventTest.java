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
package org.ikasan.framework.event.exclusion.model;



import org.junit.Assert;
import org.junit.Test;


/**
 * @author Ikasan Development Team
 *
 */
public class ExcludedEventTest {
	
	private static final String RESOLVER = "resolver";

	
	@Test
	public void testResolutionFieldsWillBeUnsetByDefault(){
		ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		Assert.assertNull("lastUpdatedBy should be null by default",  excludedEvent.getLastUpdatedBy());
		Assert.assertNull("lastUpdatedTime should be null by default",  excludedEvent.getLastUpdatedTime());
		Assert.assertNull("resolution should be null by default", excludedEvent.getResolution());
		Assert.assertFalse("isResolved should be false by default",  excludedEvent.isResolved());
	}
	
	@Test
	public void testResolveAsCancelled_willSetResolutionFields(){
		ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		excludedEvent.resolveAsCancelled(RESOLVER);
		Assert.assertEquals("lastUpdatedBy should be set with the resolver following resolveAsCancelled",  RESOLVER, excludedEvent.getLastUpdatedBy());
		Assert.assertNotNull("lastUpdatedTime should be set following resolveAsCancelled",  excludedEvent.getLastUpdatedTime());
		Assert.assertTrue("isResolved should be true following resolveAsCancelled",  excludedEvent.isResolved());
		Assert.assertEquals("resolution field should be cancelled following resolveAsCancelled",ExcludedEvent.RESOLUTION_CANCELLED, excludedEvent.getResolution());
	}
	
	@Test
	public void testResolveAsResubmitted_willSetResolutionFields(){
		ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		excludedEvent.resolveAsResubmitted(RESOLVER);
		Assert.assertEquals("lastUpdatedBy should be set with the resolver following resolveAsResubmitted",  RESOLVER, excludedEvent.getLastUpdatedBy());
		Assert.assertNotNull("lastUpdatedTime should be set following resolveAsResubmitted",  excludedEvent.getLastUpdatedTime());
		Assert.assertTrue("isResolved should be true following resolveAsResubmitted",  excludedEvent.isResolved());
		Assert.assertEquals("resolution field should be resubmitted following resolveAsCancelled",ExcludedEvent.RESOLUTION_RESUBMITTED, excludedEvent.getResolution());

	}
	
	@Test(expected=IllegalStateException.class)
	public void testResolveAsResubmitted_willThrowIllegalStateExceptionIfAlreadyResolved(){
		ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		excludedEvent.resolveAsResubmitted(RESOLVER);
		Assert.assertTrue("isResolved should be true following resolveAsResubmitted",  excludedEvent.isResolved());
		excludedEvent.resolveAsResubmitted(RESOLVER);
		Assert.fail("Should have thrown IllegalStateException when attempting to resolveAsResubmitted an ExcludedEvent which was already resolved");
	}
	
	@Test(expected=IllegalStateException.class)
	public void testResolveAsCancelled_willThrowIllegalStateExceptionIfAlreadyResolved(){
		ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		excludedEvent.resolveAsResubmitted(RESOLVER);
		Assert.assertTrue("isResolved should be true following resolveAsResubmitted",  excludedEvent.isResolved());
		excludedEvent.resolveAsCancelled(RESOLVER);
		Assert.fail("Should have thrown IllegalStateException when attempting to resolveAsCancelled an ExcludedEvent which was already resolved");
	}
	

}
