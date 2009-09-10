package org.ikasan.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.payload.service.PayloadProvider;

import org.apache.log4j.Logger;

public class RandomWordPayloadProvider implements PayloadProvider{

	private List<String> words;
	
	private PayloadFactory payloadFactory;
	
	
	/** Logger */
    private static Logger logger = Logger.getLogger(RandomWordPayloadProvider.class);
	
	

    public RandomWordPayloadProvider(List<String> words, PayloadFactory payloadFactory) {
		super();
		this.words = words;
		this.payloadFactory = payloadFactory;
	}

	public List<Payload> getNextRelatedPayloads() throws ResourceException {
	    List<Payload> result = new ArrayList<Payload>();
		
		Random random = new Random();
		int nextInt = random.nextInt(words.size()-1);
		String randomWord = words.get(nextInt);
		
		
		int payloadId = randomWord.hashCode();
		payloadId = (int) ((37*payloadId)+ System.currentTimeMillis());
		
		Payload newPayload = payloadFactory.newPayload(""+payloadId,"meaninglessPayloadName",  Spec.TEXT_PLAIN,"meaninglessSrcSystem", randomWord.getBytes());

		result.add(newPayload);
		return result;
	}

}
