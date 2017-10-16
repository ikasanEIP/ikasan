package org.ikasan.replay.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.ikasan.spec.replay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.ikasan.replay.model.ReplayAudit;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
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
	
	private ReplayAuditDao<ReplayAudit,ReplayAuditEvent> replayAuditDao;
	
	private boolean cancel = false;
    
    private List<ReplayListener<ReplayAuditEvent>> replayListeners 
    	= new ArrayList<ReplayListener<ReplayAuditEvent>>();

    private RestTemplate restTemplate;
	/**
	 * Constructor
	 * 
	 * @param replayAuditDao
	 */
	public ReplayServiceImpl(ReplayAuditDao replayAuditDao)
	{
		super();
		this.replayAuditDao = replayAuditDao;
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
    	
    	this.replayAuditDao.saveOrUpdateAudit(replayAudit);
    	
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

            logger.info("Event: " + event.getEvent());

            ResponseEntity<String> response = null;

				String url = targetServer
						+ event.getModuleName().replace(" ", "%20")
						+ "/rest/replay/eventReplay/"
						+ event.getModuleName().replace(" ", "%20")
						+ "/"
						+ event.getFlowName().replace(" ", "%20");

				HttpEntity request = initRequest(event.getEvent(), event.getModuleName(), authUser, authPassword);

				logger.info("Attempting to call URL: " + url);

				String responseBody = null;

				try
				{
					response = restTemplate.exchange(new URI(url), HttpMethod.PUT, request, String.class);
					responseBody = response.getBody();
				}
				catch(final HttpClientErrorException e)
				{
					logger.error("An error has occurred attempting to relay event: " + e.getResponseBodyAsString(), e);
					responseBody = e.getResponseBodyAsString();
				}
				catch (Exception e)
				{
					logger.error("An error has occurred attempting to relay event: " + e.getMessage(),e);
				}

				boolean success = true;

				if(response == null || !response.getStatusCode().is2xxSuccessful())
				{
					success = false;
				}

				ReplayAuditEvent replayAuditEvent = new ReplayAuditEvent(replayAudit, event, success,
						responseBody, System.currentTimeMillis());

				logger.debug("Saving replayAuditEvent: " + replayAuditEvent);

				this.replayAuditDao.saveOrUpdate(replayAuditEvent);


				for(ReplayListener<ReplayAuditEvent> listener: this.replayListeners)
				{
					listener.onReplay(replayAuditEvent);
				}
    	}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#replay(java.util.List)
	 */
	@Override
	public void replay(String targetServer, ReplayEvent event,
					   String authUser, String authPassword, String user, String replayReason)
	{
		cancel = false;

		ReplayAudit replayAudit = new ReplayAudit(user, replayReason, targetServer);
		logger.debug("Saving replayAudit: " + replayAudit);

		this.replayAuditDao.saveOrUpdateAudit(replayAudit);


		if(!targetServer.endsWith("/"))
		{
			targetServer += "/";
		}

		logger.info("Event: " + event.getEvent());

		ResponseEntity<String> response = null;

		String url = targetServer
				+ event.getModuleName().replace(" ", "%20")
				+ "/rest/replay/eventReplay/"
				+ event.getModuleName().replace(" ", "%20")
				+ "/"
				+ event.getFlowName().replace(" ", "%20");

		HttpEntity request = initRequest(event.getEvent(), event.getModuleName(), authUser, authPassword);

		logger.info("Attempting to call URL: " + url);

		String responseBody = null;

		RuntimeException exception = null;

		try
		{
			response = restTemplate.exchange(new URI(url), HttpMethod.PUT, request, String.class);
			responseBody = response.getBody();
		}
		catch(final HttpClientErrorException e)
		{
			logger.error("An error has occurred attempting to replay event: " + e.getResponseBodyAsString(), e);
			responseBody = e.getResponseBodyAsString();
			exception = new RuntimeException(e.getResponseBodyAsString(), e);
		}
		catch (Exception e)
		{
			logger.error("An error has occurred attempting to replay event: " + e.getMessage(),e);
			exception = new RuntimeException(e.getMessage(), e);
		}

		boolean success = true;

		if(response == null || !response.getStatusCode().is2xxSuccessful())
		{
			success = false;
		}

		ReplayAuditEvent replayAuditEvent = new ReplayAuditEvent(replayAudit, event, success,
				responseBody, System.currentTimeMillis());

		logger.debug("Saving replayAuditEvent: " + replayAuditEvent);

		this.replayAuditDao.saveOrUpdate(replayAuditEvent);

		if(exception != null)
		{
			throw exception;
		}
	}


	private HttpEntity initRequest(byte[] event, String module, String user, String password)
	{
		HttpHeaders headers = new HttpHeaders();

		if(user!=null && password !=null)
		{
			String credentials = user + ":" +password;
			String encodedCridentials =  new String(Base64.encodeBase64(credentials.getBytes()));
			headers.set(HttpHeaders.AUTHORIZATION, "Basic "+encodedCridentials);
		}
		headers.set(HttpHeaders.USER_AGENT, module);

		return new HttpEntity(event, headers);
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
