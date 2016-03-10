package org.ikasan.replay.service;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.ikasan.replay.dao.ReplayDao;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.replay.ReplayService;



public class ReplayServiceImpl implements ReplayService<ReplayEvent> 
{
	private Logger logger = Logger.getLogger(ReplayService.class);
	
	private ReplayDao replayDao;
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#replay(java.util.List)
	 */
	@Override
	public void replay(String targetServer, List<ReplayEvent> events,
			String authUser, String authPassword) 
	{
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authUser, authPassword);
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    	
//    	Module module = topologyService.getModuleByName(exclusionEvent.getModuleName());
//    	
//    	if(module == null)
//    	{
//    		Notification.show("Unable to find server information for module we are attempting to re-submit to: " + exclusionEvent.getModuleName() 
//    				, Type.ERROR_MESSAGE);
//    		
//    		return;
//    	}
//    	
//    	Server server = module.getServer();
//		
    	for(ReplayEvent event: events)
    	{
	    	String url = targetServer 
	    			+ "/"
					+ event.getModuleName() 
					+ "/rest/replay/"
					+ event.getModuleName() 
		    		+ "/"
		    		+ event.getFlowName();
			
			logger.debug("Ignore Url: " + url);
			
		    WebTarget webTarget = client.target(url);
		    Response response = webTarget.request().put(Entity.entity("", MediaType.APPLICATION_OCTET_STREAM));
    	}
	}

}
