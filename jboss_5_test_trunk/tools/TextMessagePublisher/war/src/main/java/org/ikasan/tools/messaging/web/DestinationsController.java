package org.ikasan.tools.messaging.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.destination.DestinationHandle;
import org.ikasan.tools.messaging.DestinationTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/destinations")
public class DestinationsController {

	private DestinationTool destinationServer;
		
	public static final String MESSAGE_TEXT_PARAMETER_NAME = "messageText";

    public static final String MESSAGE_PRIORITY_PARAMETER_NAME = "priority";
    
    public static final String REPOSITORY_NAME_PARAMETER_NAME = "repositoryName";
    
    public static final String SUBSCRIPTION_NAME_PARAMETER_NAME = "subscriptionName";
   
    
    private Logger logger = Logger.getLogger(DestinationsController.class);
    
    private ServletFileUpload upload;

    @Autowired
	public DestinationsController(
			DestinationTool destinationServer) {
		super();
		this.destinationServer = destinationServer;
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		upload = new ServletFileUpload(factory);

	}
    
    
    @RequestMapping(method = RequestMethod.GET)
    public String list(ModelMap model) 
    {	
    	List<DestinationHandle> destinationHandles = destinationServer.getDestinations();	
    	model.addAttribute("destinations", destinationHandles);
        return "destinations/list";
    }
    
    
    @RequestMapping(value="/{destination}", method = RequestMethod.GET)
    public String getDestination(@PathVariable String destination,ModelMap model)
    {	
    	logger.info("called with destination:"+destination);
    	
    	DestinationHandle   destinationHandle = destinationServer.getDestination(destination);
    	model.addAttribute("destination", destinationHandle);
    	model.addAttribute("repositoryNames", order(destinationServer.getRepositories().keySet()));
    	model.addAttribute("subscriptionNames", order(destinationHandle.getSubscriptions().keySet()));
    	return "destinations/view";
    }
    
    
    
    
	

    @RequestMapping(value="/{destination}/publishTextMessage", method = RequestMethod.POST)
    public String publishTextMessage(@PathVariable String destination,
            @RequestParam(MESSAGE_TEXT_PARAMETER_NAME) String messageText, 
            @RequestParam(MESSAGE_PRIORITY_PARAMETER_NAME) int priority, ModelMap model)
    {	
    	logger.info("called with destination=["+destination+"], messageText=["+messageText+"]");
    	destinationServer.publishTextMessage(destination, messageText, priority);
    	return redirectToDestination(destination);
    }


	private String redirectToDestination(String destination) {
		String unique = ""+System.currentTimeMillis();
		
		return "redirect:/destinations/"+destination+"/";//+
				//"?"+unique;
	}
    
    @RequestMapping(value="/{destination}/publishMapMessage", method = RequestMethod.POST)
    public String publishMapMessage(@PathVariable String destination,
    		 ModelMap model, HttpServletRequest request) throws JMSException, IOException, FileUploadException
    {	
    	logger.info("called");
    	
    	
    	int priority=4;
    	
    	
    	
    	
    	List<FileItem> fileItems = upload.parseRequest(request);
    	FileItem fileItem = fileItems.get(0);
    	
    	byte[] content = fileItem.get();
        
        String xml = new String(content, "UTF-8");
        

        
        destinationServer.publishXmlMessage(destination, xml, priority);
        return redirectToDestination(destination);
    }
    
    
    
    

   
    

    
    
    private List<String> order(Set<String> keySet) {
		List<String> orderedList = new ArrayList<String>(keySet);
		Collections.sort(orderedList);
		return orderedList;
	}


   
    
    
    @RequestMapping(value="/{destination}/subscriptions/startSubscription", method = RequestMethod.POST)
    public String startSubscriber(
    		@PathVariable String destination,
    		@RequestParam(value=REPOSITORY_NAME_PARAMETER_NAME,required=false) String repositoryName,
    		@RequestParam(SUBSCRIPTION_NAME_PARAMETER_NAME) String subscriptionName
    		)
    {	

    	destinationServer.createSubscription(subscriptionName, destination, repositoryName);
    	
    	return redirectToDestination(destination);
    }
    
    @RequestMapping(value="/{destination}/subscriptions/{subscriptionName}/stopSubscription", method = RequestMethod.POST)
    public String stopSubscriber(
    		@PathVariable String destination,
    		@PathVariable String subscriptionName
    		)
    {	
    	destinationServer.destroyPersistingSubscription(destination,subscriptionName);
    	
    	return redirectToDestination(destination);
    }
    

    

   
    

    
}
