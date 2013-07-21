package org.ikasan.demo.businesserror.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.demo.businesserror.model.BusinessError;
import org.springframework.stereotype.Component;

@Component
public class InMemoryBusinessErrorDao  implements BusinessErrorDao {

	private Logger logger = Logger.getLogger(InMemoryBusinessErrorDao.class);
	
	private Long id = 0l;
	
	private Map<Long, BusinessError> businessErrors = new HashMap<Long, BusinessError>();
	
	public void delete(BusinessError businessError) {
		Long key = businessError.getId();
		logger.info("called with key ["+key+"], contains?"+businessErrors.containsKey(key));
		businessErrors.remove(key);	
		
		
		
	}

	public List<BusinessError> list() {
		List<BusinessError> result = new ArrayList<BusinessError>();
		result.addAll(businessErrors.values());
		return result;
	}

	public BusinessError load(Long businessErrorId) {
		
		BusinessError businessError = businessErrors.get(businessErrorId);
		logger.info("load called with id ["+businessErrorId+"] returnnig businessError with id ["+businessError.getId()+"]");
		return businessError;
	}

	public void save(BusinessError businessError) {
		Long thisId = id++;
		businessError.setId(thisId);
		
		businessErrors.put(thisId, businessError);
		
		logger.info(businessErrors);
	}



}
