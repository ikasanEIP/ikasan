package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;

public class DefaultMapMessageEventSerialiser implements
		JmsMessageEventSerialiser<MapMessage> {

	static final String PAYLOAD_PREFIX = "PAYLOAD_";
	static final String ATTRIBUTE_PREFIX = "_ATTRIBUTE_";

	static final String PAYLOAD_CONTENT_SUFFIX = "_CONTENT";
	static final String PAYLOAD_SRC_SYSTEM_SUFFIX = "_SRC_SYSTEM";

	static final String PAYLOAD_ID_SUFFIX = "_ID";
	static final String PAYLOAD_SPEC_SUFFIX = "_SPEC";
	
	static final String EVENT_FIELD_ID = "EVENT_ID";
	static final String EVENT_FIELD_PRIORITY = "EVENT_PRIORITY";
	static final String EVENT_FIELD_TIMESTAMP = "EVENT_TIMESTAMP";
	static final String EVENT_FIELD_SRC_SYSTEM = "EVENT_FIELD_SRC_SYSTEM";
	
	private PayloadFactory payloadFactory;

	@SuppressWarnings("unchecked")
	public Event fromMessage(MapMessage mapMessage, String moduleName,
			String componentName) throws JMSException {
		Event result = null;
		
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

	private Event demapEvent(MapMessage mapMessage, String moduleName,
			String componentName, List<Payload> payloads, List<String> eventFieldNames) throws JMSException {
		String eventId = null;
		int priority = -1;
		long timestamp = -1;
		String srcSystem = null;
		
		for(String fieldName : eventFieldNames){
			if (fieldName.equals(EVENT_FIELD_ID)){
				eventId=mapMessage.getString(EVENT_FIELD_ID);
			}
			else if (fieldName.equals(EVENT_FIELD_PRIORITY)){
				priority=mapMessage.getInt(EVENT_FIELD_PRIORITY);
			}
			else if (fieldName.equals(EVENT_FIELD_TIMESTAMP)){
				timestamp=mapMessage.getLong(EVENT_FIELD_TIMESTAMP);
			}
			else if (fieldName.equals(EVENT_FIELD_SRC_SYSTEM)){
				srcSystem=mapMessage.getString(EVENT_FIELD_SRC_SYSTEM);
			}
			else{
				throw new IllegalArgumentException("Unknown map entry ["+fieldName+"]");
			}
		}
		
		return new Event(eventId, priority, timestamp, srcSystem, payloads);
	}

	private Payload demapPayload(int payloadOrdinal, MapMessage mapMessage,
			List<String> payloadFieldNames) throws JMSException {
		String fullPayloadPrefix = PAYLOAD_PREFIX+payloadOrdinal;
		
		String payloadId = null;
		Spec payloadSpec = null;
		String payloadSrcSystem = null;
		byte[] payloadContent = null;
		
		Map<String, String> payloadAttributes = new HashMap<String, String>();
		
		for(String fieldName : payloadFieldNames){
			
			//payload content
			if (fieldName.equals(fullPayloadPrefix+PAYLOAD_CONTENT_SUFFIX)){
				payloadContent=mapMessage.getBytes(fullPayloadPrefix+PAYLOAD_CONTENT_SUFFIX);
			}
			//payload srcSystem
			else if (fieldName.equals(fullPayloadPrefix+PAYLOAD_SRC_SYSTEM_SUFFIX)){
				payloadSrcSystem=mapMessage.getString(fullPayloadPrefix+PAYLOAD_SRC_SYSTEM_SUFFIX);
			}
			//payload id
			else if (fieldName.equals(fullPayloadPrefix+PAYLOAD_ID_SUFFIX)){
				payloadId=mapMessage.getString(fullPayloadPrefix+PAYLOAD_ID_SUFFIX);
			}
			//payload spec
			else if (fieldName.equals(fullPayloadPrefix+PAYLOAD_SPEC_SUFFIX)){
				payloadSpec=Spec.valueOf(mapMessage.getString(fullPayloadPrefix+PAYLOAD_SPEC_SUFFIX));
			}else if(fieldName.startsWith(fullPayloadPrefix+ATTRIBUTE_PREFIX)){
				//its a payload attribute
				payloadAttributes.put(fieldName.substring((fullPayloadPrefix+ATTRIBUTE_PREFIX).length()), mapMessage.getString(fieldName));
			}
			else{
				throw new IllegalArgumentException("Unknown map entry ["+fieldName+"]");
			}
		}
		
		
		Payload payload = payloadFactory.newPayload(payloadId, payloadSpec, payloadSrcSystem, payloadContent);
		
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
	 * 	entry(0) -> List<String> of all Event level field names
	 *  entry(1) -> List<String> of all field names beginning with PAYLOAD_0
	 *  entry(2) -> List<String> of all field names beginning with PAYLOAD_1
	 *  
	 *  Note: this method will barf if there are non continuous PAYLOAD_X values
	 * 
	 * @param mapNames
	 * @return complex List of Lists as described above
	 */
	private List<List<String>> groupMapNames(Enumeration<String> mapNames) {
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

	public MapMessage toMessage(Event event, Session session)
			throws JMSException {
		MapMessage mapMessage = session.createMapMessage();

		List<Payload> payloads = event.getPayloads();
		for (int i = 0; i < payloads.size(); i++) {
			Payload payload = payloads.get(i);
			mapMessage.setBytes(PAYLOAD_PREFIX + i + PAYLOAD_CONTENT_SUFFIX,
					payload.getContent());
			mapMessage.setString(PAYLOAD_PREFIX + i + PAYLOAD_SRC_SYSTEM_SUFFIX, payload.getSrcSystem());
			mapMessage.setString(PAYLOAD_PREFIX + i + PAYLOAD_ID_SUFFIX, payload.getId());
			mapMessage.setString(PAYLOAD_PREFIX + i + PAYLOAD_SPEC_SUFFIX, payload.getSpec().name());
			
			//map any payload attributes
			for (String attributeName : payload.getAttributeNames()){
				mapMessage.setString(PAYLOAD_PREFIX + i +ATTRIBUTE_PREFIX+attributeName, payload.getAttribute(attributeName));
			}
		}
		mapMessage.setString(EVENT_FIELD_ID, event.getId());
		mapMessage.setInt(EVENT_FIELD_PRIORITY, event.getPriority());
		mapMessage.setLong(EVENT_FIELD_TIMESTAMP, event.getTimestamp());
		mapMessage.setString(EVENT_FIELD_SRC_SYSTEM, event.getSrcSystem());

		return mapMessage;
	}

	public void setPayloadFactory(PayloadFactory payloadFactory) {
		this.payloadFactory = payloadFactory;
		
	}

}
