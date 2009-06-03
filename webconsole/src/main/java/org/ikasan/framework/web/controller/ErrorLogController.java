/**
 * 
 */
package org.ikasan.framework.web.controller;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the error log 
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/errors/*.htm")
public class ErrorLogController {

	/**
	 * Error logging service
	 */
	private ErrorLoggingService errorLoggingService;
    /**
	 * @param errorLoggingService
	 */
	@Autowired
	public ErrorLogController(ErrorLoggingService errorLoggingService) {
		super();
		this.errorLoggingService = errorLoggingService;
	}
	/**
     * List all errorOccurrences
     * 
     * @param model - The model (map)
     * @return errors view
     */
    @RequestMapping("list.htm")
    public String listErrors(@RequestParam(required=false) Integer page, ModelMap model)
    {
    	int pageNo = page!=null?page:0;
    	
    	model.addAttribute("page", pageNo);
        PagedSearchResult<ErrorOccurrence> errors = errorLoggingService.getErrors(pageNo, 25);
		model.addAttribute("loggedErrors", errors);
		model.addAttribute("firstResultIndex", errors.getFirstResultIndex());
		model.addAttribute("lastPage", errors.isLastPage());
		model.addAttribute("resultSize", errors.getResultSize());
		model.addAttribute("size", errors.size());
        return "admin/errors/errors";
    }
    
	/**
     * Display ErrorOccurrence
     * 
     * @param model - The model (map)
     * @return errors view
     */
    @RequestMapping("viewError.htm")
    public String view(@RequestParam long errorId, ModelMap model)
    {
        model.addAttribute("error", errorLoggingService.getErrorOccurrence(errorId));
        return "admin/errors/viewError";
    }
}
