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
package org.ikasan.framework.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.systemevent.model.SystemEvent;
import org.ikasan.framework.systemevent.service.SystemEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for the administrator screen
 * 
 * @author Ikasan Development Team
 */
@Controller
@RequestMapping("/admin/systemEvents/*.htm")
public class SystemEventLogController
{
	private SystemEventService systemEventService;
	
    /** Simple date format definition for days months and years */
    private static SimpleDateFormat ddMMyyyyFormat;

    /** Simple date format definition for hours minutes and seconds */
    private static SimpleDateFormat HHmmss;
    
    static {
    	ddMMyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        ddMMyyyyFormat.setLenient(false);
        HHmmss = new SimpleDateFormat("HH:mm:ss");
        HHmmss.setLenient(false);
    }
	
    /**
     * Constructor
     * 
     */
    @Autowired
    public SystemEventLogController(SystemEventService systemEventService)
    {
        this.systemEventService = systemEventService;
    }
	
	
	
    /**
     * search for system events
     * 
     * @return - key to next view
     */
    @RequestMapping(value="search.htm", method = RequestMethod.GET)
    public String search(HttpServletRequest request, @RequestParam(required = false) Boolean newSearch, 
            @RequestParam(required = false) Integer page, @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Boolean orderAsc, 
    		@RequestParam(required = false)String subject,
    		@RequestParam(required = false)String action,
    		@RequestParam(required = false)String actor,
    		@RequestParam(required = false)String timestampFromDate,
    		@RequestParam(required = false)String timestampFromTime,
    		@RequestParam(required = false)String timestampToDate,
    		@RequestParam(required = false)String timestampToTime,

    		ModelMap model)
    {
    	Date timestampFrom = createDateTime(timestampFromDate, timestampFromTime);
    	Date timestampTo = createDateTime(timestampToDate, timestampToTime);
    	
    	
    	
        // Setup the generic search criteria
        int pageNo = page!=null?page:0;
        int pageSize = 25;
        String orderByField = orderBy==null?"timestamp":orderBy;
        boolean orderAscending = orderAsc!=null?orderAsc:false;
    	
        
     // Perform the paged search
        PagedSearchResult<SystemEvent> pagedResult = null;
        //if (noErrors)
        //{
            pagedResult = systemEventService.listSystemEvents(pageNo, pageSize, orderBy, orderAscending, subject, action, timestampFrom, timestampTo, actor);
       // }

        // Store the search parameters used
        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("subject", subject);
        searchParams.put("action", action);
        searchParams.put("actor", actor);
        searchParams.put("timestampFromDate", timestampFromDate);
        searchParams.put("timestampFromTime", timestampFromTime);
        searchParams.put("timestampToDate", timestampToDate);
        searchParams.put("timestampToTime", timestampToTime);

        model.addAttribute("searchParams", searchParams);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderAsc", orderAsc);
        model.addAttribute("page", pageNo);
        model.addAttribute("results", pagedResult);
        // pagedResult can be null if we're returning errors to the user
        if (pagedResult != null)
        {
            model.addAttribute("firstResultIndex", pagedResult.getFirstResultIndex());
            model.addAttribute("lastPage", pagedResult.isLastPage());
            model.addAttribute("resultSize", pagedResult.getResultSize());
            model.addAttribute("size", pagedResult.size());
        }
        // Set a default value for the JSP to cleanly deal with errors coming back
        else
        {
            model.addAttribute("resultSize", 0);
        }

        model.addAttribute("searchResultsUrl", request.getRequestURL());
        
    	
        return "admin/systemEventLog";
    }
    
    
    /**
     * Helper method to create date and time from user supplied strings
     * 
     * @param dateString - The date component
     * @param timeString - the time component
     * @return A Java representation of the Date
     */
    private Date createDateTime(String dateString, String timeString)
    {
        if (dateString ==null || "".equals(dateString)){
            return null;
        }
        
        
        Calendar calendar = new GregorianCalendar();
        try
        {
 
            Date time = HHmmss.parse(timeString);
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(time);
            
            
            calendar.setTime(ddMMyyyyFormat.parse(dateString));
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        }
        catch (ParseException e)
        {
           throw new RuntimeException(e);
        }
        return calendar.getTime();
    }
    
    /**
     * Invoke housekeeping on system event log
     * 
     * @return - key to next view
     */
    @RequestMapping(value="housekeeping.htm", method = RequestMethod.POST)
    public String housekeepnig(HttpServletRequest request, HttpServletResponse response, ModelMap model)
    {
    	systemEventService.housekeep();
        return "redirect:search.htm";
    }
}
