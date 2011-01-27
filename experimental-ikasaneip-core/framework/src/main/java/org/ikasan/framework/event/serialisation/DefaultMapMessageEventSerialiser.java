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
package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.spec.flow.event.EventFactory;
import org.ikasan.spec.flow.event.FlowEvent;

/**
 * Default implementation for converting an FlowEvent to and from a MapMessage
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultMapMessageEventSerialiser implements
		JmsMessageEventSerialiser<MapMessage> {

	static final protected String PAYLOAD_PREFIX = "PAYLOAD_";
	static final protected String ATTRIBUTE_PREFIX = "_ATTRIBUTE_";

	static final protected String PAYLOAD_CONTENT_SUFFIX = "_CONTENT";
	static final protected String PAYLOAD_SRC_SYSTEM_SUFFIX = "_SRC_SYSTEM";

	static final protected String PAYLOAD_ID_SUFFIX = "_ID";
	static final protected String PAYLOAD_SPEC_SUFFIX = "_SPEC";
	
	static final protected String EVENT_FIELD_ID = "EVENT_ID";
	static final protected String EVENT_FIELD_PRIORITY = "EVENT_PRIORITY";
	static final protected String EVENT_FIELD_TIMESTAMP = "EVENT_TIMESTAMP";
	static final protected String EVENT_FIELD_SRC_SYSTEM = "EVENT_FIELD_SRC_SYSTEM";
	
	/**
	 * Payload factory, only needed for deserialisation
	 */
	protected PayloadFactory payloadFactory;

    /** TODO pass eventFactory */
    private EventFactory<String,FlowEvent> eventFactory;
	
	@SuppressWarnings("unchecked")
	public FlowEvent fromMessage(MapMessage mapMessage, String moduleName,
			String componentName) throws JMSException, EventDeserialisationException {
		FlowEvent result = null;
		
		Enumeration<String> mapNames = mapMessage.getMapNames();
		
		List<List<String>> groupedKeys = groupMapNames(mapNames);
		List<String> eventFieldNames = groupedKeys.get(0);
		
		List<List<String>> payloadFieldNameLists = groupedKeys.subList(1, groupedKeys.size());
		
		List<Payload> payloads = new ArrayList<Payload>();
		for (int payloadOrdinal=0;payloadOrdinal<payloadFieldNameLists.size();payloadOrdinal++){
			List<String> payloadFieldNames = payloadFieldNameLists.get(payloadOrdinal);
		
			payloads.add(demapPayload(payloadOrdinal, mapMessage, payloadFieldNames));
		}
		
		result = demapEvent(mapMessage, moduleName, componentName, payloads, eventFieldNames);
		
		
		return result;
	}

	protected FlowEvent demapEvent(MapMessage mapMessage, String moduleName,
			String componentName, List<Payload> payloads, List<String> eventFieldNames) throws JMSException, EventDeserialisationException {
		String eventId = null;
		int priority = -1;
		Date timestamp = null;
		
		for(String fieldName : eventFieldNames){
			if (fieldName.equals(EVENT_FIELD_ID)){
				eventId=mapMessage.getString(EVENT_FIELD_ID);
			}
			else if (fieldName.equals(EVENT_FIELD_PRIORITY)){
				priority=mapMessage.getInt(EVENT_FIELD_PRIORITY);
			}
			else if (fieldName.equals(EVENT_FIELD_TIMESTAMP)){
				timestamp=new Date(mapMessage.getLong(EVENT_FIELD_TIMESTAMP));
			}
			
			else{
				throw new EventDeserialisationException("Unknown map entry ["+fieldName+"]");
			}
		}
		
		return eventFactory.newEvent(eventId, payloads);
	}

	protected Payload demapPayload(int payloadOrdinal, MapMessage mapMessage,
			List<String> payloadFieldNames) throws JMSException, EventDeserialisationException {
		String fullPayloadPrefix = PAYLOAD_PREFIX+payloadOrdinal;
		
		String payloadId = null;
		byte[] payloadContent = null;
		
		Map<String, String> payloadAttributes = new HashMap<String, String>();
		
		for(String fieldName : payloadFieldNames){
			
			//payload content
			if (fieldName.equals(fullPayloadPrefix+PAYLOAD_CONTENT_SUFFIX)){
				payloadContent=mapMessage.getBytes(fullPayloadPrefix+PAYLOAD_CONTENT_SUFFIX);
			}
			//payload id
			else if (fieldName.equals(fullPayloadPrefix+PAYLOAD_ID_SUFFIX)){
				payloadId=mapMessage.getString(fullPayloadPrefix+PAYLOAD_ID_SUFFIX);
			}
			else if(fieldName.startsWith(fullPayloadPrefix+ATTRIBUTE_PREFIX)){
				//its a payload attribute
				payloadAttributes.put(fieldName.substring((fullPayloadPrefix+ATTRIBUTE_PREFIX).length()), mapMessage.getString(fieldName));
			}
			else{
				throw new EventDeserialisationException("Unknown map entry ["+fieldName+"]");
			}
		}
		
		
		Payload payload = payloadFactory.newPayload(payloadId, payloadContent);
		
		//set any payload attributs
		for (String attributeName : payloadAttributes.keySet()){
			payload.setAttribute(attributeName, payloadAttributes.get(attributeName));
		}
		
		
		return payload;
	}

	/**
	 * Helper method that groups the keys to all map entries into Lists by thier payload, and lists these in payload order, 
	 * starting with a List of non payload (event level) fields.
	 * 
	 * Resultant list should be:
	 * 	entry(0) -> List<String> of all FlowEvent level field names
	 *  entry(1) -> List<String> of all field names beginning with PAYLOAD_0
	 *  entry(2) -> List<String> of all field names beginning with PAYLOAD_1
	 *  
	 *  Note: this method will barf if there are non continuous PAYLOAD_X values
	 * 
	 * @param mapNames
	 * @return complex List of Lists as described above
	 */
	protected List<List<String>> groupMapNames(Enumeration<String> mapNames) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		List<String> eventFields = new ArrayList<String>();
		result.add(eventFields);
		
		Map<String, List<String>> payloadFieldNameLists = new HashMap<String, List<String>>();
		
		while (mapNames.hasMoreElements()){
			String mapName = (String)mapNames.nextElement();
			
			if (mapName.startsWith(PAYLOAD_PREFIX)){
				//its some sort of payload field
				String fullPayloadPrefix = mapName.substring(0, mapName.indexOf('_', PAYLOAD_PREFIX.length()));
				if (payloadFieldNameLists.get(fullPayloadPrefix)==null){
					payloadFieldNameLists.put(fullPayloadPrefix, new ArrayList<String>());
				}
				
				List<String> payloadFields = payloadFieldNameLists.get(fullPayloadPrefix);
				payloadFields.add(mapName);
			} else{
				//its some sort of event level field
				eventFields.add(mapName);
			}
		}
		
		List<String> fullPayloadPrefixes = new ArrayList<String>(payloadFieldNameLists.keySet());
		Collections.sort(fullPayloadPrefixes);
		

		
		
		for (int i=0;i<fullPayloadPrefixes.size();i++){
			String fullPayloadPrefix = fullPayloadPrefixes.get(i);
			if (!fullPayloadPrefix.equals(PAYLOAD_PREFIX+i)){
				throw new RuntimeException("Non-continuous payload sequence! was expecting ["+PAYLOAD_PREFIX+i+"] but got ["+fullPayloadPrefix+"]");
			}
			result.add(payloadFieldNameLists.get(fullPayloadPrefix));
		}
		return result;
	}

	/**
	 * @see org.ikasan.framework.event.serialisation.JmsMessageFlowEventSerialiser#toMessage(org.ikasan.spec.flow.event.FlowEvent, javax.jms.Session)
	 */
	public MapMessage toMessage(FlowEvent event, Session session)
			throws JMSException {
		MapMessage mapMessage = session.createMapMessage();

		// TODO commented out for Generics
//		List<Payload> payloads = event.getPayloads();
//		for (int i = 0; i < payloads.size(); i++) {
//			Payload payload = payloads.get(i);
//			mapMessage.setBytes(PAYLOAD_PREFIX + i + PAYLOAD_CONTENT_SUFFIX,
//					payload.getContent());
//			mapMessage.setString(PAYLOAD_PREFIX + i + PAYLOAD_ID_SUFFIX, payload.getId());
//			
//			//map any payload attributes
//			for (String attributeName : payload.getAttributeNames()){
//				mapMessage.setString(PAYLOAD_PREFIX + i +ATTRIBUTE_PREFIX+attributeName, payload.getAttribute(attributeName));
//			}
//		}
//		mapMessage.setString(EVENT_FIELD_ID, event.getId());
//		mapMessage.setInt(EVENT_FIELD_PRIORITY, event.getPriority());
//		mapMessage.setLong(EVENT_FIELD_TIMESTAMP, event.getTimestamp().getTime());

		return mapMessage;
	}

	/**
	 * Allows the payloadFactory to be supplied. Only necessary for deserialisation
	 * 
	 * @param payloadFactory
	 */
	public void setPayloadFactory(PayloadFactory payloadFactory) {
		this.payloadFactory = payloadFactory;
		
	}

}
