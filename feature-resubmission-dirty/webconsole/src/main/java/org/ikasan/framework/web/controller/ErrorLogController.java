/**
 * 
 */
package org.ikasan.framework.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
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
    public String listErrors(
    		@RequestParam(required=false) String moduleName, 
    		@RequestParam(required=false) String flowName,
    		HttpServletRequest request,
    		@RequestParam(required=false) Integer page, 
    		@RequestParam(required=false) String orderBy,
    		@RequestParam(required=false) Boolean orderAsc,
    		ModelMap model)
    {


    	//perform the paged search
         PagedSearchResult<ErrorOccurrence> pagedResult = errorLoggingService.getErrors(MasterDetailControllerUtil.defaultZero(page), 25,MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), MasterDetailControllerUtil.nullForEmpty(moduleName), MasterDetailControllerUtil.nullForEmpty(flowName));

    	//search restriction params
    	Map<String, Object> searchParams = new HashMap<String, Object>();
    	MasterDetailControllerUtil.addParam(searchParams,"moduleName", moduleName);
    	MasterDetailControllerUtil.addParam(searchParams,"flowName", flowName);
    	
    	MasterDetailControllerUtil.addPagedModelAttributes(MasterDetailControllerUtil.resolveOrderBy(orderBy), MasterDetailControllerUtil.defaultFalse(orderAsc), model, MasterDetailControllerUtil.defaultZero(page), pagedResult,
				request, searchParams);
        return "admin/errors/errors";
    }
    
	/**
     * Display ErrorOccurrence
     * 
     * @param model - The model (map)
     * @return errors view
     */
    @RequestMapping("viewError.htm")
    public String view(@RequestParam long errorId, 
    		@RequestParam(required=false) String searchResultsUrl, 
    		ModelMap model)
    {
        model.addAttribute("error", errorLoggingService.getErrorOccurrence(errorId));
        model.addAttribute("searchResultsUrl", searchResultsUrl);
        return "admin/errors/viewError";
    }
    
    
    
}
