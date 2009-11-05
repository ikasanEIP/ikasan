package org.ikasan.tools.messaging.web;

import org.ikasan.tools.messaging.destination.DestinationHandle;
import org.ikasan.tools.messaging.server.DestinationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessagePublicationController {

	private DestinationServer destinationServer;
	
	public static final String DESTINATION_PATH_PARAMETER_NAME = "destinationPath";
	
	public static final String MESSAGE_TEXT_PARAMETER_NAME = "messageText";

    public static final String MESSAGE_PRIORITY_PARAMETER_NAME = "priority";

    @Autowired
	public MessagePublicationController(
			DestinationServer destinationServer) {
		super();
		this.destinationServer = destinationServer;
	}
	

    @RequestMapping(value="/publishTextMessage.htm", method = RequestMethod.POST)
    public String viewFlow(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
            @RequestParam(MESSAGE_TEXT_PARAMETER_NAME) String messageText, 
            @RequestParam(MESSAGE_PRIORITY_PARAMETER_NAME) int priority, ModelMap model)
    {	
    	destinationServer.publishTextMessage(destinationPath, messageText, priority);
        return "success";
    }

    
    @RequestMapping("/destinations.htm")
    public String showDestinations(ModelMap model) 
    {	

    	model.addAttribute("destinations", destinationServer.getDestinations());
    	
        return "destinations";
    }
    
    @RequestMapping(value="/destination.htm", method = RequestMethod.GET)
    public String viewDestination(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
             ModelMap model)
    {	
    	DestinationHandle   destination = destinationServer.getDestination(destinationPath);
    	model.addAttribute("destination", destination);
    	return "destination";
    }
}
