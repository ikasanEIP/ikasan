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

package org.ikasan.framework.error.filtering;

import java.util.List;

import org.hamcrest.TypeSafeMatcher;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;
import org.ikasan.framework.component.routing.SingleResultRouter;
import org.ikasan.framework.error.grouping.ErrorOccurrenceGroup;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.serialisation.ErrorOccurrenceXmlSerialiser;
import org.ikasan.framework.exception.user.ExceptionCache;

/**
 * Specialist router for filtering ErrorOccurrence Events based on a predefined set of rules
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceFilteringRouter extends SingleResultRouter implements Router {
	
	public static final String DUPLICATE_PERIOD_ATTRIBUTE_NAME = "duplicatePeriod";

	private ErrorOccurrenceXmlSerialiser errorOccurrenceXmlSerialiser;
	


	private List<TypeSafeMatcher<ErrorOccurrence>> suppressableMatchers = null;
	
	private List<ErrorOccurrenceGroup> duplicateGroupings; 
	
	private ExceptionCache exceptionCache;

	public final static String EXCLUSION_TRANSITION = "exclude";
	
	public final static String INCLUSION_TRANSITION = "include";

	/**
	 * @param errorOccurrenceXmlSerialiser
	 * @param exceptionCache
	 */
	public ErrorOccurrenceFilteringRouter(
			ErrorOccurrenceXmlSerialiser errorOccurrenceXmlSerialiser,
			ExceptionCache exceptionCache) {
		super();
		this.errorOccurrenceXmlSerialiser = errorOccurrenceXmlSerialiser;
		this.exceptionCache = exceptionCache;
	}
	
	
	public String evaluate(Event event) throws RouterException {

		List<Payload> payloads = event.getPayloads();
		Payload solePayload = payloads.get(0);
		
		String xmlString = new String(solePayload.getContent());
		
		ErrorOccurrence errorOccurrence = errorOccurrenceXmlSerialiser.toObject(xmlString);
		
		
		//explicit suppression
		if (suppressableMatchers!=null){
			for (TypeSafeMatcher<ErrorOccurrence> suppressableMatcher : suppressableMatchers){
				if (suppressableMatcher.matchesSafely(errorOccurrence)){
					return EXCLUSION_TRANSITION;
				}
			}
		}
		
		//duplicate suppression
		if (duplicateGroupings!=null){
			for (ErrorOccurrenceGroup errorOccurrenceGroup : duplicateGroupings){
				if (errorOccurrenceGroup.hasAsMember(errorOccurrence)){
					String groupName = errorOccurrenceGroup.getGroupName();
					Long duplicatePeriod = (Long) errorOccurrenceGroup.getAttribute(DUPLICATE_PERIOD_ATTRIBUTE_NAME);
					if (duplicatePeriod!=null){
						if (exceptionCache.notifiedSince(groupName, duplicatePeriod)){
							return EXCLUSION_TRANSITION;
						}
						else{
							exceptionCache.notify(groupName);
						}
					}
				}
			}
		}
		
		return INCLUSION_TRANSITION;
	}

	public void setSupressableMatchers(
			List<TypeSafeMatcher<ErrorOccurrence>> suppressableMatchers) {
		this.suppressableMatchers = suppressableMatchers;
	}
	
	public void setDuplicateGroupings(List<ErrorOccurrenceGroup> duplicateGroupings) {
		this.duplicateGroupings = duplicateGroupings;
	}
	
}
