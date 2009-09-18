package org.ikasan.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.routing.RouterException;

public class WordDictionaryRouter implements Router {

	private Map<String, String> dictionary;
	

	
	
	public WordDictionaryRouter(Map<String, String> dictionary) {
		super();
		this.dictionary = dictionary;
	}


	public List<String> onEvent(Event event) throws RouterException {
		
		Payload payload = event.getPayloads().get(0);
		
		String originalWord = new String (payload.getContent());
		String type = dictionary.get(originalWord);
		List<String> result = new ArrayList<String>();
		result.add(type);
		return result;
	}

}
