package org.ikasan.demo.businesserror.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.demo.businesserror.model.BusinessError;
import org.ikasan.demo.businesserror.service.BusinessErrorService;
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
		modelMap.addAttribute("error", businessError);
    	return "error";
    }
	
	@RequestMapping("/requestResubmission.htm")
    public String requestResubmission(@RequestParam(ERROR_ID_PARAMETER) Long errorId){
    	errorManagerService.requestResubmission(errorId);
    	return "redirect:errors.htm";
    }
}
