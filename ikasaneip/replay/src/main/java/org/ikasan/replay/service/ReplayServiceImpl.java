package org.ikasan.replay.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.ikasan.replay.dao.ReplayDao;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.replay.ReplayListener;
import org.ikasan.spec.replay.ReplayService;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Replay service implementation.
 *
 * @author Ikasan Development Team
 *
 */
public class ReplayServiceImpl implements ReplayService<ReplayEvent, ReplayAuditEvent> 
{
	private static Logger logger = LoggerFactory.getLogger(ReplayService.class);
	
	private ReplayDao replayDao;
	
	private boolean cancel = false;
    
    private List<ReplayListener<ReplayAuditEvent>> replayListeners 
    	= new ArrayList<ReplayListener<ReplayAuditEvent>>();

    private RestTemplate restTemplate;
	/**
	 * Constructor
	 * 
	 * @param replayDao
	 */
	public ReplayServiceImpl(ReplayDao replayDao) 
	{
		super();
		this.replayDao = replayDao;
		restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(
				Arrays.asList(
						new ByteArrayHttpMessageConverter()
						,new StringHttpMessageConverter()));
	}



	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#replay(java.util.List)
	 */
	@Override
	public void replay(String targetServer, List<ReplayEvent> events,
			String authUser, String authPassword, String user, String replayReason) 
	{
		cancel = false;

    	ReplayAudit replayAudit = new ReplayAudit(user, replayReason, targetServer);
    	logger.debug("Saving replayAudit: " + replayAudit);
    	
    	this.replayDao.saveOrUpdate(replayAudit);
    	
    	for(ReplayEvent event: events)
    	{
    		if(cancel == true)
    		{
    			// stop processing if cancel is true
    			return;
    		}

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

			ResponseEntity<String> response = null;
			try {
				HttpEntity request = initRequest(event.getEvent(),event.getModuleName(),authUser,authPassword
						);
				response = restTemplate.exchange(new URI(url), HttpMethod.PUT,request,String.class);

				boolean success = true;

				if(!response.getStatusCode().is2xxSuccessful())
				{
					success = false;
				}

				ReplayAuditEvent replayAuditEvent = new ReplayAuditEvent(replayAudit, event, success,
						response.getBody(), System.currentTimeMillis());

				logger.debug("Saving replayAuditEvent: " + replayAuditEvent);

				this.replayDao.saveOrUpdate(replayAuditEvent);

				replayAuditEvent.setReplayEvent(event);

				for(ReplayListener<ReplayAuditEvent> listener: this.replayListeners)
				{
					listener.onReplay(replayAuditEvent);
				}
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(),e);
			}
    	}
	}


	private HttpEntity initRequest(byte[] event,String module ,String user, String password){
		HttpHeaders headers = new HttpHeaders();
		if(user!=null && password !=null){
			String credentials = user + ":" +password;
			String encodedCridentials =  new String(Base64.encodeBase64(credentials.getBytes()));
			headers.set(HttpHeaders.AUTHORIZATION, "Basic "+encodedCridentials);
		}
		headers.set(HttpHeaders.USER_AGENT,module);
		return new HttpEntity(event,headers);

	}
	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#addReplayListener(org.ikasan.spec.replay.ReplayListener)
	 */
	@Override
	public void addReplayListener(ReplayListener<ReplayAuditEvent> listener) 
	{
		this.replayListeners.add(listener);
	}



	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#cancel()
	 */
	@Override
	public void cancel() 
	{
		cancel = true;
	}



	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#isCancelled()
	 */
	@Override
	public boolean isCancelled() 
	{
		return cancel;
	}

}
