package org.ikasan.demo.businesserror.eai;

import org.ikasan.demo.businesserror.model.BusinessError;

public interface EaiAdapter {

	public void postBusinessError(BusinessError businessError);
	
	public void setBusinessErrorListener(BusinessErrorListener businessErrorListener);

	public String getOriginatingSystem();
}
