package org.ikasan.demo.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.demo.jms.JmsTextMessagePublisher;
import org.ikasan.demo.jms.JndiDestinationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the publication of messages.
 * 
 * @author Ikasan Development Team
 */
@Controller
public class PublicationController
{
  
	
	private JndiDestinationProvider destinationProvider;
	private JmsTextMessagePublisher jmsTextMessagePublisher;


	@Autowired
    public PublicationController(JndiDestinationProvider destinationProvider, JmsTextMessagePublisher jmsTextMessagePublisher) {
		super();
		this.destinationProvider = destinationProvider;
		this.jmsTextMessagePublisher=jmsTextMessagePublisher;
	}




    @RequestMapping(method=RequestMethod.GET,  value="/publicationForm.htm")
    public String setupForm(ModelMap model)
    {
    	List<String> channelNames = new ArrayList<String>();
    	

    	for (String destinationName : destinationProvider.getDestinationNames()){
    		channelNames.add(destinationName.toString());
    	}
    	
       
        model.addAttribute("channelNames", channelNames);
        
        return "publicationForm";
    }
    
    @RequestMapping(method=RequestMethod.POST,  value="/publicationForm.htm")
    public String submitForm(@RequestParam("destination")String destination,
    						@RequestParam("messageText")String messageText, 
    						@RequestParam("priority")Integer priority, 
    						ModelMap model)
    {
    	
    	
    	
    	
    	jmsTextMessagePublisher.publishTextMessage(destinationProvider.getDestination(destination), messageText, priority);
    	
       
        model.addAttribute("destination", destination);
        model.addAttribute("messageText", messageText);
        model.addAttribute("priority", priority);
        
        return "messageSent";
    }

    
}
