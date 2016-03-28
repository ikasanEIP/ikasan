package org.ikasan.replay.service;

import java.util.ArrayList;
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
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.replay.ReplayListener;
import org.ikasan.spec.replay.ReplayService;



public class ReplayServiceImpl implements ReplayService<ReplayEvent, ReplayAuditEvent> 
{
	private Logger logger = Logger.getLogger(ReplayService.class);
	
	private ReplayDao replayDao;
    
    private List<ReplayListener<ReplayAuditEvent>> replayListeners 
    	= new ArrayList<ReplayListener<ReplayAuditEvent>>();
    
	/**
	 * Constructor
	 * 
	 * @param replayDao
	 * @param serialiserFactory
	 */
	public ReplayServiceImpl(ReplayDao replayDao) 
	{
		super();
		this.replayDao = replayDao;
	}



	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#replay(java.util.List)
	 */
	@Override
	public void replay(String targetServer, List<ReplayEvent> events,
			String authUser, String authPassword, String user, String replayReason) 
	{
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(authUser, authPassword);
    	
    	ClientConfig clientConfig = new ClientConfig();
    	clientConfig.register(feature) ;
    	
    	Client client = ClientBuilder.newClient(clientConfig);
    
    	ReplayAudit replayAudit = new ReplayAudit(user, replayReason, targetServer);
    	logger.debug("Saving replayAudit: " + replayAudit);
    	
    	this.replayDao.saveOrUpdate(replayAudit);
    	
    	for(ReplayEvent event: events)
    	{
    		if(!targetServer.endsWith("/"))
    		{
    			targetServer += "/";
    		}
    		
	    	String url = targetServer 
					+ event.getModuleName() 
					+ "/rest/replay/eventReplay/"
					+ event.getModuleName() 
		    		+ "/"
		    		+ event.getFlowName();
			
			logger.debug("Replay Url: " + url);
			
		    WebTarget webTarget = client.target(url);
		    Response response = webTarget.request().put(Entity.entity(event.getEvent()
		    		, MediaType.APPLICATION_OCTET_STREAM));
		    
		    boolean success = true;
		    
		    if(response.getStatus()  != 200)
    	    {
    	    	success = false;
    	    }
		    
		    ReplayAuditEvent replayAuditEvent = new ReplayAuditEvent(replayAudit, event, success,
		    		response.getStatus() + " " +  response.readEntity(String.class), System.currentTimeMillis());
		    
		    logger.debug("Saving replayAuditEvent: " + replayAuditEvent);
		    
		    this.replayDao.saveOrUpdate(replayAuditEvent);
		    
		    replayAuditEvent.setReplayEvent(event);
		    
		    for(ReplayListener<ReplayAuditEvent> listener: this.replayListeners)
		    {
		    	listener.onReplay(replayAuditEvent);
		    }
    	}
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#addReplayListener(org.ikasan.spec.replay.ReplayListener)
	 */
	@Override
	public void addReplayListener(ReplayListener<ReplayAuditEvent> listener) 
	{
		this.replayListeners.add(listener);
	}

}
