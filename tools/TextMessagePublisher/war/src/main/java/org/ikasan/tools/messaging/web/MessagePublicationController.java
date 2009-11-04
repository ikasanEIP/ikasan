package org.ikasan.tools.messaging.web;

import org.ikasan.tools.messaging.destination.discovery.DestinationDiscoverer;
import org.ikasan.tools.messaging.publisher.TextMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessagePublicationController {

	private TextMessagePublisher textMessagePublisher;
	
	private DestinationDiscoverer destinationDiscoverer;
	
	public static final String DESTINATION_PATH_PARAMETER_NAME = "destinationPath";
	
	public static final String MESSAGE_TEXT_PARAMETER_NAME = "messageText";

	@Autowired
	public MessagePublicationController(
			TextMessagePublisher textMessagePublisher,
			DestinationDiscoverer destinationDiscoverer) {
		super();
		this.textMessagePublisher = textMessagePublisher;
		this.destinationDiscoverer = destinationDiscoverer;
	}
	

    @RequestMapping(value="/publishTextMessage.htm", method = RequestMethod.POST)
    public String viewFlow(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
            @RequestParam(MESSAGE_TEXT_PARAMETER_NAME) String messageText, ModelMap model)
    {	
    	textMessagePublisher.publishTextMessage(destinationPath, messageText);
        return "success";
    }
    
    @RequestMapping("/home.htm")
    public String setupForm(ModelMap model) 
    {	

    	model.addAttribute("destinations", destinationDiscoverer.findDestinations());
    	
        return "form";
    }
}
