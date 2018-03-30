package org.ikasan.replay.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.ikasan.replay.model.BulkReplayResponse;
import org.ikasan.replay.model.ReplayResponse;
import org.ikasan.spec.replay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.ikasan.replay.model.HibernateReplayAudit;
import org.ikasan.replay.model.HibernateReplayAuditEvent;
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
public class ReplayServiceImpl implements ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse>
{
	private static Logger logger = LoggerFactory.getLogger(ReplayService.class);
	
	private ReplayAuditDao<HibernateReplayAudit,HibernateReplayAuditEvent> replayAuditDao;
	
	private boolean cancel = false;
    
    private List<ReplayListener<HibernateReplayAuditEvent>> replayListeners
    	= new ArrayList<ReplayListener<HibernateReplayAuditEvent>>();

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
	public BulkReplayResponse replay(String targetServer, List<ReplayEvent> events,
                                     String authUser, String authPassword, String user, String replayReason,
                                     Map<String, String> moduleContextMappings)
	{
		cancel = false;

    	HibernateReplayAudit replayAudit = new HibernateReplayAudit(user, replayReason, targetServer);
    	logger.info("Saving replayAudit: " + replayAudit);
    	
    	this.replayAuditDao.saveOrUpdateAudit(replayAudit);

		BulkReplayResponse bulkReplayResponse = new BulkReplayResponse();
    	
    	for(ReplayEvent event: events)
    	{
    		if(cancel == true)
    		{
    			// stop processing if cancel is true
    			return bulkReplayResponse;
    		}

    		String contextPath = moduleContextMappings.get(event.getModuleName());

			ReplayResponse replayResponse = this.replay(targetServer, event, authUser, authPassword, user, replayReason, replayAudit,contextPath);

			bulkReplayResponse.addReplayResponse(replayResponse);

    	}

    	return bulkReplayResponse;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#replay(java.util.List)
	 */
	@Override
	public ReplayResponse replay(String targetServer, ReplayEvent event,
                                 String authUser, String authPassword, String user, String replayReason, String contextPath)
	{
		return this.replay(targetServer, event, authUser, authPassword, user, replayReason, null, contextPath);
	}


	private ReplayResponse replay(String targetServer, ReplayEvent event,
                                  String authUser, String authPassword, String user,
                                  String replayReason, HibernateReplayAudit replayAudit, String contextPath)
	{
		cancel = false;

		if(replayAudit == null)
		{
			replayAudit = new HibernateReplayAudit(user, replayReason, targetServer);
			logger.info("Saving replayAudit: " + replayAudit);

			this.replayAuditDao.saveOrUpdateAudit(replayAudit);
		}

		logger.info("Event: " + event.getEvent());

		ReplayResponse replayResponse = this.replayMessage(targetServer, event, authUser, authPassword, contextPath);

		HibernateReplayAuditEvent replayAuditEvent = new HibernateReplayAuditEvent(replayAudit, event, replayResponse.isSuccess(),
				replayResponse.getResponseBody(), System.currentTimeMillis());

		logger.info("Saving replayAuditEvent: " + replayAuditEvent);

		this.replayAuditDao.saveOrUpdate(replayAuditEvent);

		for(ReplayListener<HibernateReplayAuditEvent> listener: this.replayListeners)
		{
			listener.onReplay(replayAuditEvent);
		}

		return replayResponse;
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

	private ReplayResponse replayMessage(String targetServer, ReplayEvent event,
										 String authUser, String authPassword, String contextPath)
	{
		ResponseEntity<String> response = null;

		HttpEntity request = initRequest(event.getEvent(), event.getModuleName(), authUser, authPassword);


		String responseBody = null;
		RuntimeException exception = null;

		if(!targetServer.endsWith("/"))
		{
			targetServer += "/";
		}

		if(targetServer.endsWith("/") && contextPath.startsWith("/"))
        {
            targetServer = targetServer.substring(0, targetServer.length()-1);
        }

		String url = targetServer
				+ contextPath.replace(" ", "%20")
				+ "/rest/replay/eventReplay/"
				+ event.getModuleName().replace(" ", "%20")
				+ "/"
				+ event.getFlowName().replace(" ", "%20");

		logger.info("Attempting to call URL: " + url);

		try
		{
			response = restTemplate.exchange(new URI(url), HttpMethod.PUT, request, String.class);
			responseBody = response.getBody();
		}
		catch(final HttpClientErrorException e)
		{
			responseBody = String.format("An error has occurred attempting to replay event: HTTP Status Code[%s], Response[%s]", e.getRawStatusCode(),
					e.getResponseBodyAsString());

			exception = new RuntimeException(responseBody, e);
		}
		catch (Exception e)
		{
			responseBody = String.format("An error has occurred attempting to replay event: Error Message[%s]", e.getMessage());
			exception = new RuntimeException(responseBody, e);
		}

		boolean success = true;

		if(response == null || !response.getStatusCode().is2xxSuccessful())
		{
			success = false;
		}

		return new ReplayResponse(success, responseBody, exception);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.replay.ReplayService#addReplayListener(org.ikasan.spec.replay.ReplayListener)
	 */
	@Override
	public void addReplayListener(ReplayListener<HibernateReplayAuditEvent> listener)
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
