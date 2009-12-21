package org.ikasan.tools.messaging.web;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.server.DestinationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/repositories")
public class RepositoriesController {

	public static final String FILE_SYSTEM_PATH_PARAMETER_NAME = "fileSystemPath";
	
    public static final String REPOSITORY_NAME_PARAMETER_NAME = "repositoryName";
   
    
	private DestinationServer destinationServer;
	
	
    
    private Logger logger = Logger.getLogger(RepositoriesController.class);
    


    @Autowired
	public RepositoriesController(
			DestinationServer destinationServer) {
		super();
		this.destinationServer = destinationServer;
	}
    
    

    
    @RequestMapping("")
    public String list(ModelMap model) 
    {	

    	model.addAttribute("repositories", destinationServer.getRepositories());
    	
        return "repositories/list";
    }
    
    @RequestMapping("/createFileSystemRepository")
    public String createFileSystemRepository(
    		@RequestParam(REPOSITORY_NAME_PARAMETER_NAME)String name,  @RequestParam(FILE_SYSTEM_PATH_PARAMETER_NAME)String fileSystemPath, ModelMap map){
    	logger.info("called");
    	destinationServer.createFileSystemRepository(name, fileSystemPath);
    	return "redirect:/repositories/";
    }
    
    
    
    
}
