/* 
 * $Id$
 * $URL$ 
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.tools.errormanager.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.tools.errormanager.model.BusinessError;
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
