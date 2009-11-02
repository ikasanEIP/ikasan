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
package org.ikasan.tools.errormanager.web;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.ikasan.tools.errormanager.model.BusinessError;
import org.ikasan.tools.errormanager.service.BusinessErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BusinessErrorController {
	
	private BusinessErrorService errorManagerService;
	
	private static final String ERROR_ID_PARAMETER = "errorId";
	
	private Logger logger = Logger.getLogger(BusinessErrorController.class);

	@Autowired
    public BusinessErrorController(BusinessErrorService errorManagerService) {
		super();
		this.errorManagerService = errorManagerService;
	}

	@RequestMapping("/errors.htm")
    public String viewErrors(ModelMap modelMap){

    	List<BusinessError> businessErrors = errorManagerService.getBusinessErrors();
		modelMap.addAttribute("errors", businessErrors);
    	logger.info("called, currently there are "+businessErrors.size()+" errors");
    	return "errors";
    }

	@RequestMapping("/error.htm")
    public String viewError(@RequestParam(ERROR_ID_PARAMETER) Long errorId, ModelMap modelMap){

    	BusinessError businessError = errorManagerService.getBusinessError(errorId);
    	modelMap.addAttribute("errorDetailLines", getLines(businessError.getErrorMessage()));
		modelMap.addAttribute("error", businessError);
    	return "error";
    }
	
	private List<String> getLines(String message) {
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(message, "\n");
		while (st.hasMoreTokens()){
			result.add(st.nextToken());
		}
		
		return result;
	}

}
