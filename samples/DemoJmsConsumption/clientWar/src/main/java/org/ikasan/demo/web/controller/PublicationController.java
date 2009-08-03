package org.ikasan.demo.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for the publication of messages.
 * 
 * @author Ikasan Development Team
 */
@Controller
public class PublicationController
{
  



    /**
     * View the initiator
     * 
     * @param moduleName - The name of the module
     * @param initiatorName - The name of the initiator
     * @param model - The model
     * @return "modules/viewInitiator"
     * @throws SchedulerException - Exception if there was a scheduler problem
     */
    @RequestMapping("/publicationForm.htm")
    public String viewInitiator(ModelMap model)
    {
    	List<String> channelNames = new ArrayList<String>();
    		
    	channelNames.add("thisChannel");
    	channelNames.add("thatChannel");
       
        model.addAttribute("channelNames", channelNames);
        
        return "publicationForm";
    }

    
}
