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
import org.ikasan.spec.serialiser.SerialiserFactory;



public class ReplayServiceImpl implements ReplayService<ReplayEvent> 
{
	private Logger logger = Logger.getLogger(ReplayService.class);
	
	private ReplayDao replayDao;
	
	/** need a serialiser to serialise the incoming event payload of T */
    private SerialiserFactory serialiserFactory;
    
    private List<ReplayListener<ReplayEvent>> replayListeners 
    	= new ArrayList<ReplayListener<ReplayEvent>>();
    
    
	/**
	 * Constructor
	 * 
	 * @param replayDao
	 * @param serialiserFactory
	 */
	public ReplayServiceImpl(ReplayDao replayDao, SerialiserFactory serialiserFactory) 
	{
		super();
		this.replayDao = replayDao;
		this.serialiserFactory = serialiserFactory;
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
    	logger.info("Saving replayAudit: " + replayAudit);
    	
    	this.replayDao.saveOrUpdate(replayAudit);
    	
    	for(ReplayEvent event: events)
    	{
	    	String url = targetServer 
	    			+ "/"
					+ event.getModuleName() 
					+ "/rest/replay/"
					+ event.getModuleName() 
		    		+ "/"
		    		+ event.getFlowName();
			
			logger.info("Replay Url: " + url);
			
		    WebTarget webTarget = client.target(url);
		    Response response = webTarget.request().put(Entity.entity(this.serialiserFactory.getDefaultSerialiser().deserialise(event.getEvent())
		    		, MediaType.APPLICATION_OCTET_STREAM));
		    
		    ReplayAuditEvent replayAuditEvent = new ReplayAuditEvent(replayAudit, event, 
		    		response.getStatus() + " " +  response.readEntity(String.class));
		    
		    logger.info("Saving replayAuditEvent: " + replayAuditEvent);
		    
		    this.replayDao.saveOrUpdate(replayAuditEvent);
		    
		    for(ReplayListener<ReplayEvent> listener: this.replayListeners)
		    {
		    	listener.onReplay(event);
		    }
    	}
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#addReplayListener(org.ikasan.spec.replay.ReplayListener)
	 */
	@Override
	public void addReplayListener(ReplayListener<ReplayEvent> listener) 
	{
		this.replayListeners.add(listener);
	}

}
