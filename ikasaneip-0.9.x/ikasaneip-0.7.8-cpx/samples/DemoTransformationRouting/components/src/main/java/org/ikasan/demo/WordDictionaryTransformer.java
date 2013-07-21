package org.ikasan.demo;

import java.util.HashMap;
import java.util.Map;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.TransformationException;
import org.ikasan.framework.component.transformation.Transformer;

public class WordDictionaryTransformer implements Transformer {

	private Map<String, String> wordDictionary = new HashMap<String, String>();
	
	public WordDictionaryTransformer(Map<String, String> wordDictionary) {
		super();
		this.wordDictionary = wordDictionary;
	}

	public void onEvent(Event event) throws TransformationException {
		for (Payload payload : event.getPayloads()){
			String originalWord = new String (payload.getContent());
			String translatedWord = wordDictionary.get(originalWord);
			if (translatedWord==null){
				throw new TransformationException("No word in dictionary for ["+originalWord+"]");
			}
			payload.setContent(translatedWord.getBytes());
		}

	}

}
