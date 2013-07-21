package org.ikasan.demo.businesserror.eai.converter;

import org.ikasan.demo.businesserror.model.BusinessError;

public interface BusinessErrorConverter<T> {
	
	public BusinessError convertFrom(T type, String originatingSystem);
	
	public T convertTo(BusinessError businessError);

}
