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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.Matcher;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.AbstractFilteringRouter;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;
import org.ikasan.framework.exception.user.ExceptionCache;
import org.ikasan.framework.util.grouping.AttributedGroup;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Specialist router for filtering Events based on a predefined set of rules for suppression 
 * and duplicate filtering
 * 
 * Originally written for filtering Events containing ErrorOccurrence XML
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceFilteringRouter extends AbstractFilteringRouter implements Router {
	
	public static final String DUPLICATE_PERIOD_ATTRIBUTE_NAME = "duplicatePeriod";


	private List<Matcher<Document>> suppressableMatchers = null;
	
	private List<AttributedGroup> duplicateGroupings; 
	
	private ExceptionCache exceptionCache;


	
	private DocumentBuilder documentBuilder;
	


	/**
	 * @param exceptionCache
	 * @throws ParserConfigurationException 
	 */
	public ErrorOccurrenceFilteringRouter(
			ExceptionCache exceptionCache, DocumentBuilderFactory documentBuilderFactory) throws ParserConfigurationException {
		super();
		this.exceptionCache = exceptionCache;

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}
	
	
	public boolean filter(Event event) throws RouterException {

		List<Payload> payloads = event.getPayloads();
		Payload solePayload = payloads.get(0);
		
		String xmlString = new String(solePayload.getContent());
		
		

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        Document errorOccurrenceDocument;
		try {
			errorOccurrenceDocument = documentBuilder.parse(is);
		} catch (SAXException e) {
			throw new RouterException(e);
		} catch (IOException e) {
			throw new RouterException(e);
		}
		
		
		
		
		
		//explicit suppression
		if (suppressableMatchers!=null){
			for (Matcher<Document> suppressableMatcher : suppressableMatchers){
				if (suppressableMatcher.matches(errorOccurrenceDocument)){
					return true;
				}
			}
		}
		
		//duplicate suppression
		if (duplicateGroupings!=null){
			for (AttributedGroup errorOccurrenceGroup : duplicateGroupings){
				if (errorOccurrenceGroup.hasAsMember(errorOccurrenceDocument)){
					String groupName = errorOccurrenceGroup.getGroupName();
					Long duplicatePeriod = (Long) errorOccurrenceGroup.getAttribute(DUPLICATE_PERIOD_ATTRIBUTE_NAME);
					if (duplicatePeriod!=null){
						if (exceptionCache.notifiedSince(groupName, duplicatePeriod)){
							return true;
						}
						else{
							exceptionCache.notify(groupName);
						}
					}
				}
			}
		}
		
		return false;
	}

	public void setSupressableMatchers(
			List<Matcher<Document>> suppressableMatchers) {
		this.suppressableMatchers = suppressableMatchers;
	}
	
	public void setDuplicateGroupings(List<AttributedGroup> duplicateGroupings) {
		this.duplicateGroupings = duplicateGroupings;
	}
	
}
