package org.ikasan.demo.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.servlet.http.HttpServletRequest;

import org.ikasan.demo.jms.JmsTextMessagePublisher;
import org.ikasan.demo.jms.JndiDestinationProvider;
import org.ikasan.demo.jms.MessageMemeneto;
import org.ikasan.demo.jms.SimpleSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the subscription to channels.
 * 
 * @author Ikasan Development Team
 */
@Controller
public class SubscriptionsController
{
  
	
	private JndiDestinationProvider destinationProvider;
	private Map<String, SimpleSubscriber> subscriptions = new HashMap<String, SimpleSubscriber>();
	private ConnectionFactory connectionFactory;


	@Autowired
    public SubscriptionsController(JndiDestinationProvider destinationProvider, ConnectionFactory connectionFactory) {
		super();
		this.destinationProvider = destinationProvider;
		this.connectionFactory = connectionFactory;
	}




    @RequestMapping(method=RequestMethod.GET,  value="/subscriptions.htm")
    public String setupForm(ModelMap model)
    {
    	List<String> channelNames = new ArrayList<String>();
    	

    	for (String destinationName : destinationProvider.getDestinationNames()){
    		channelNames.add(destinationName.toString());
    	}
    	
    	
        model.addAttribute("channelNames", channelNames);
        model.addAttribute("subscriptions", subscriptions);
        
        
        return "subscriptions";
    }
    
    @RequestMapping(method=RequestMethod.POST,  value="/subscriptions.htm")
    public String submitForm(HttpServletRequest request, 
    		@RequestParam(value="subscribe", required=false)String subscribe, 
    		@RequestParam(value="unsubscribe", required=false)String unsubscribe, 
    						ModelMap model)
    {
    	
    	Map parameterMap = request.getParameterMap();
    	
    	
    	if (subscribe!=null){
    		subscriptions.put(subscribe, new SimpleSubscriber(connectionFactory, destinationProvider.getDestination(subscribe)));
    	}
    	
    	if (unsubscribe!=null){
    		subscriptions.remove(unsubscribe);
    	}

        return "redirect:subscriptions.htm";
    }

    @RequestMapping(method=RequestMethod.GET,  value="/subscription.htm")
    public String viewSubscription(@RequestParam(value="channel", required=false)String channelName, ModelMap model)
    {
    	
        model.addAttribute("channelName", channelName);
        model.addAttribute("subscription", subscriptions.get(channelName));
        
        
        return "subscription";
    }
    
    @RequestMapping(method=RequestMethod.GET,  value="/message.htm")
    public String viewMessage(@RequestParam(value="channelName")String channelName, 
    						  @RequestParam(value="messageID")String messageID,
    						  ModelMap model)
    {
        SimpleSubscriber subscriber  = subscriptions.get(channelName);
        
        for (MessageMemeneto memeneto : subscriber.getMessagesReceived()){
        	if (memeneto.getMessageID().equals(messageID)){
        		model.addAttribute("message", memeneto);
        		break;
        	}
        }
    	
        model.addAttribute("channelName", channelName);
		model.addAttribute("subscription", subscriber);
        
        
        return "message";
    }
    
}
