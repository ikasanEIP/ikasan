package org.ikasan.tools.messaging.web;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
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
	
	public static final String FILE_SYSTEM_PATH_PARAMETER_NAME = "fileSystemPath";
	
	public static final String MESSAGE_TEXT_PARAMETER_NAME = "messageText";
	
	public static final String MESSAGE_ID_PARAMETER_NAME = "messageId";

    public static final String MESSAGE_PRIORITY_PARAMETER_NAME = "priority";
    
    private Logger logger = Logger.getLogger(MessagePublicationController.class);
    
    private ServletFileUpload upload;

    @Autowired
	public MessagePublicationController(
			DestinationServer destinationServer) {
		super();
		this.destinationServer = destinationServer;
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		upload = new ServletFileUpload(factory);

	}
	

    @RequestMapping(value="/publishTextMessage.htm", method = RequestMethod.POST)
    public String viewFlow(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
            @RequestParam(MESSAGE_TEXT_PARAMETER_NAME) String messageText, 
            @RequestParam(MESSAGE_PRIORITY_PARAMETER_NAME) int priority, ModelMap model)
    {	
    	destinationServer.publishTextMessage(destinationPath, messageText, priority);
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
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
    
    
    @RequestMapping(value="/startSimpleSubscription.htm", method = RequestMethod.POST)
    public String startSimpleSubscriber(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath)
    {	
    	destinationServer.createSimpleSubscription(destinationPath);
    	
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
    }
    
    @RequestMapping(value="/stopSimpleSubscription.htm", method = RequestMethod.POST)
    public String stopSimpleSubscriber(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath)
    {	
    	destinationServer.destroySimpleSubscription(destinationPath);
    	
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
    }
    
    
    
    
    @RequestMapping(value="/startPersistingSubscription.htm", method = RequestMethod.POST)
    public String startPersistingSubscriber(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
    		@RequestParam(FILE_SYSTEM_PATH_PARAMETER_NAME) String fileSystemPath)
    {	
    	destinationServer.createPersistingSubscription(destinationPath, new File(fileSystemPath));
    	
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
    }
    
    @RequestMapping(value="/stopPersistingSubscription.htm", method = RequestMethod.POST)
    public String stopPersistingSubscriber(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath)
    {	
    	destinationServer.destroyPersistingSubscription(destinationPath);
    	
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
    }
    
    @RequestMapping(value="/message.htm", method = RequestMethod.GET)
    public String viewMessage(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
    		@RequestParam(MESSAGE_ID_PARAMETER_NAME) String messageId,
             ModelMap model) throws JMSException
    {	
    	Message message = destinationServer.getMessage(destinationPath, messageId);
    	model.addAttribute("message", message);
    	
    	Map<String, String> messageProperties = new HashMap<String, String>();
    	Enumeration propertyNames = message.getPropertyNames();
    	while (propertyNames.hasMoreElements()){
    		String propertyName = (String)propertyNames.nextElement();
    		messageProperties.put(propertyName, message.getStringProperty(propertyName));
    	}
    	model.addAttribute("messageProperties", messageProperties);
    	
    	
    	
    	if (message instanceof TextMessage){
    		return "textMessage";
    	} else if (message instanceof MapMessage){
    		Map<String, Object> messageContent = new HashMap<String, Object>();
    		Enumeration mapNames = ((MapMessage)message).getMapNames();
    		while(mapNames.hasMoreElements()){
    			String mapName = (String) mapNames.nextElement();
    			Object mapValue = ((MapMessage)message).getObject(mapName);
    			String renderableValue=mapValue.toString();
    			if (mapValue instanceof byte[]){
    				renderableValue = "byte array comprising ["+new String((byte[])mapValue)+"]";
    			}
    			
				messageContent.put(mapName, renderableValue);
    		}
    		model.addAttribute("messageContent", messageContent);
    		
			return "mapMessage";
		}
    	return "unsupportedMessage";
    	
    }
    
    
    @RequestMapping(value="/export.htm", method = RequestMethod.GET)
    public String downloadMessage(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
    		@RequestParam(MESSAGE_ID_PARAMETER_NAME) String messageId,
             ModelMap model, HttpServletResponse response) throws JMSException, IOException
    {	
    	Message message = destinationServer.getMessage(destinationPath, messageId);
    	String filename = message.getJMSMessageID()+".xml";
    	
    	String  xmlString = destinationServer.getMessageAsXml(destinationPath, messageId);
    	//response.setContentType("text/xml");
    	response.setContentType ("application/download");
		response.setHeader ("Content-Disposition", "attachment; filename=\""+filename+"\"");
        response.getOutputStream().write(xmlString.getBytes());
        
        return null;
    }
    
    @RequestMapping(value="/publishMapMessage.htm", method = RequestMethod.POST)
    public String publishMapMessage(@RequestParam(DESTINATION_PATH_PARAMETER_NAME) String destinationPath,
    		 //@RequestParam(MESSAGE_PRIORITY_PARAMETER_NAME) int priority,
    		 ModelMap model, HttpServletRequest request) throws JMSException, IOException, FileUploadException
    {	
    	logger.info("called");
    	
    	
    	int priority=4;
    	
    	
    	
    	
    	List<FileItem> fileItems = upload.parseRequest(request);
    	FileItem fileItem = fileItems.get(0);
    	
    	byte[] content = fileItem.get();
    	
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        int read = 0;
//        while (read>-1){
//            read = fileItem.getInputStream().read();
//            if (read!=-1){
//                byteArrayOutputStream.write(read);
//            }
//            logger.info("reading...");
//        }
//        logger.info("done reading");
//        byte[] content = byteArrayOutputStream.toByteArray();
        
        String xml = new String(content, "UTF-8");
        

        
        destinationServer.publishXmlMessage(destinationPath, xml, priority);
    	return "redirect:/destination.htm?"+DESTINATION_PATH_PARAMETER_NAME+"="+destinationPath;
    }
    
    
}
