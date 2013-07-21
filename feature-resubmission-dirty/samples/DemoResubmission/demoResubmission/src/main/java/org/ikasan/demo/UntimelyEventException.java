package org.ikasan.demo;

import org.ikasan.framework.component.endpoint.EndpointException;

public class UntimelyEventException extends EndpointException{


	
	public UntimelyEventException(String message){
		super(message);
	}
}
