package org.ikasan.tools.messaging.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.ikasan.tools.messaging.server.DestinationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/messages")
public class MessagesController {

	private DestinationServer destinationServer;
	
	
    
    private Logger logger = Logger.getLogger(MessagesController.class);
    


    @Autowired
	public MessagesController(
			DestinationServer destinationServer) {
		super();
		this.destinationServer = destinationServer;
	}
    
    

    
    
    @RequestMapping(value="/subscription/{destination}/{subscriptionName}/{messageId}", method = RequestMethod.GET)
    public String viewMessageBySubscription(@PathVariable String destination,
    		@PathVariable String subscriptionName,
    		@PathVariable String messageId,
    		@RequestParam(required=false,value="download") Boolean download,
    		ModelMap model,
    		HttpServletResponse response
    		) throws IOException{
    	MessageWrapper message = destinationServer.getMessage(destination,subscriptionName, messageId);
    	model.addAttribute("message", message);
    	
    	Map<String, String> messageProperties = new HashMap<String, String>();
    	
    	if (download!=null && download.booleanValue()){
        	String filename = message.getMessageId()+".xml";
        	
        	String  xmlString = destinationServer.getMessageAsXml(destination,subscriptionName, messageId);
        	//response.setContentType("text/xml");
        	response.setContentType ("application/download");
    		response.setHeader ("Content-Disposition", "attachment; filename=\""+filename+"\"");
            response.getOutputStream().write(xmlString.getBytes());
            
            return null;
    	}

    	
    	if (message instanceof TextMessageWrapper){
    		return "messages/textMessage";
    	} else if (message instanceof MapMessageWrapper){
    		Map<String, Object> map = ((MapMessageWrapper)message).getMap();
    		
    		Map<String, Object> messageContent = new HashMap<String, Object>();
    		for (String mapKey : map.keySet()){
    			Object mapValue = map.get(mapKey);
    			String renderableValue=mapValue.toString();
    			if (mapValue instanceof byte[]){
    				renderableValue = "byte array comprising ["+new String((byte[])mapValue)+"]";
    			}
    			
				messageContent.put(mapKey, renderableValue);
    		}
    		model.addAttribute("messageContent", messageContent);
    		
			return "messages/mapMessage";
		}
    	return "messages/unsupportedMessage";
    }
    
    
    
    
}
