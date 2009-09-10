package org.ikasan.demo.businesserror.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.demo.businesserror.dao.BusinessErrorDao;
import org.ikasan.demo.businesserror.eai.EaiAdapter;
import org.ikasan.demo.businesserror.eai.BusinessErrorListener;
import org.ikasan.demo.businesserror.model.BusinessError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="errorService")
public class BusinessErrorService implements BusinessErrorListener{

	private Logger logger = Logger.getLogger(BusinessErrorService.class);
	
	private BusinessErrorDao businessErrorDao;
	
	private Map<String, EaiAdapter> eaiAdapters = new HashMap<String, EaiAdapter>();
	
	
	@Autowired
	public BusinessErrorService(BusinessErrorDao businessErrorDao,
			List<EaiAdapter> eaiAdapters) {
		super();
		this.businessErrorDao = businessErrorDao;
		for (EaiAdapter eaiAdapter : eaiAdapters){
			this.eaiAdapters.put(eaiAdapter.getOriginatingSystem(), eaiAdapter);
			eaiAdapter.setBusinessErrorListener(this);
		}
	}


	public List<BusinessError> getBusinessErrors() {
		return businessErrorDao.list();
	}
	
	public BusinessError getBusinessError(Long businessErrorId) {
		return businessErrorDao.load(businessErrorId);
	}
	
	public void requestResubmission(Long businessErrorId){
		logger.info("called with errorId ["+businessErrorId+"]");
		final BusinessError businessError = businessErrorDao.load(businessErrorId);
		
		EaiAdapter eaiAdapter = eaiAdapters.get(businessError.getOriginatingSystem());
		
		eaiAdapter.postBusinessError(businessError);
		
		//delete the businessError locally
		businessErrorDao.delete(businessError);
		
	}


	public void onBusinessError(BusinessError businessError) {
		businessErrorDao.save(businessError);
	}

	
	
	
}
