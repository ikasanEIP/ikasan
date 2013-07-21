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
package org.ikasan.errorpub.filter;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ikasan.attributes.AttributeResolver;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.AbstractFilteringRouter;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;
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
public class ErrorOccurrenceDuplicateFilteringRouter extends AbstractFilteringRouter implements Router {
	
	public static final String DUPLICATE_PERIOD_ATTRIBUTE_NAME = "duplicatePeriod";
	public static final String DUPLICATE_GROUPING_ID_ATTRIBUTE_NAME = "duplicateGroupingId";
	
	private AttributeResolver attributeResolver; 
	
	private NotificationCache notificationCache;
	
	private DocumentBuilder documentBuilder;
	



	/**
	 * Constructor
	 * 
	 * @param attributeResolver
	 * @param notificationCache
	 * @param documentBuilderFactory
	 * @throws ParserConfigurationException
	 */
	public ErrorOccurrenceDuplicateFilteringRouter(
			AttributeResolver attributeResolver,
			NotificationCache notificationCache, 
			DocumentBuilderFactory documentBuilderFactory) throws ParserConfigurationException {
		super();
		this.attributeResolver = attributeResolver;
		this.notificationCache = notificationCache;
		

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}
	
	
	/* (non-Javadoc)
	 * @see org.ikasan.framework.component.routing.AbstractFilteringRouter#filter(org.ikasan.framework.component.Event)
	 */
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
		

		
		//duplicate suppression
		
		Map<String, Object> attributes = attributeResolver.resolveAttributes(errorOccurrenceDocument);
		
		String duplicateGroupingId = (String)attributes.get(DUPLICATE_GROUPING_ID_ATTRIBUTE_NAME);
		Long duplicatePeriod = (Long) attributes.get(DUPLICATE_PERIOD_ATTRIBUTE_NAME);
		if (duplicateGroupingId!=null){
			//check that there is a non-negative duplicate period also
			if (duplicatePeriod ==null || duplicatePeriod<0){
				throw new RouterException("invalid duplicatePeriod for duplicateGrouping ["+duplicateGroupingId+"]");
			}
			
			if (notificationCache.notifiedSince(duplicateGroupingId, duplicatePeriod)){
				return true;
			}
			else{
				notificationCache.notify(duplicateGroupingId);
			}
		}
		
		return false;
	}


	
}

