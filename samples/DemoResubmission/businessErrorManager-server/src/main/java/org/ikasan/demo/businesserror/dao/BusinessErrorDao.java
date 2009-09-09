package org.ikasan.demo.businesserror.dao;

import java.util.List;

import org.ikasan.demo.businesserror.model.BusinessError;

public interface BusinessErrorDao {

	public void save(BusinessError businessError);
	
	public BusinessError load(Long businessErrorId);
	
	public List<BusinessError> list();

	public void delete(BusinessError businessError);
}
