/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.exception.user;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.UserExceptionHandler;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.plugins.JMSEventPublisherPlugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for <code>UserExceptionHandlerImpl</code>
 * @author Ikasan Development Team
 */
public class UserExceptionHandlerImplTest
{ 

	/**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
        setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Thrown Exception */
    private Exception thrown = new Exception("test");
    
    /** Mocked test Exception Cache */
    private ExceptionCache cache = classMockery.mock(ExceptionCache.class, "exceptionCache");
    
    /** Mocked test Payload factory */
    private PayloadFactory payloadFactory = classMockery.mock(PayloadFactory.class);
    
    /** Mocked test JMSEventPublisherPlugin factory */
    private JMSEventPublisherPlugin publisher = classMockery.mock(JMSEventPublisherPlugin.class, "jmsPublisher");
    
    /** Mocked test ExceptionTransformer */
    private ExceptionTransformer exceptionTransformer = classMockery.mock(ExceptionTransformer.class);
    
    /** Mocked test exception payload */ 
    private Payload exceptionPayload = classMockery.mock(Payload.class);
    
    /** transformedException message */
    private String transformedException ="transformedException";
    
    /** Drop duplicate period, defaults to 4000ms */
    private Long dropDuplicatePeriod = new Long(4000);
    
    /** Mocked test event */
    private Event event = classMockery.mock(Event.class, "originalEvent");
    
    /** resolution Id string */
    private String testResolutionId = "resolutionId";
    
    /** Exception Context */
    private ExceptionContext exceptionContext;

    
    /**
     * Constructor
     */
    public UserExceptionHandlerImplTest(){
    	exceptionContext = new ExceptionContext(thrown, event, "testComponent");
        exceptionContext.setResolutionId(testResolutionId);
    }

    /**
     * UserExceptionDefinition sets publishable flag to false, and therefore ignored.
     * 
     * Test that when handled, an exception is not transformed nor published if the configuration
     * suggests that it should not be published
     * 
     * @throws Exception
     */
    @Test
    public void testInvokeWithConfiguredNonPublishableDoesNotTransformNorPublish() throws Exception
    {
    	//create and configure the ExceptionHandler
        UserExceptionDefinition ued = new UserExceptionDefinition(new Integer(999), new Integer(0), "TestExternalExceptionDef");
        ued.setPublishable(new Boolean(false));
        Map<String, UserExceptionDefinition> userExceptionDefsMap = new HashMap<String, UserExceptionDefinition>();
        userExceptionDefsMap.put(testResolutionId, ued);
        UserExceptionHandler handler = new UserExceptionHandlerImpl(userExceptionDefsMap, null, null, null, null, null);

        //invoke the handler
        handler.invoke(exceptionContext);
        
        //check that all expectations on mocks were satisfied
        classMockery.assertIsSatisfied();
    }

    
    /**
     * Test that when handled, an exception is transformed and published if the configuration
     * suggests that it should not be published, and duplicate filtered, but this is not a
     * duplicate handling
     * 
     * @throws Exception
     */
    @Test
    public void testInvokeWithConfiguredPublishableAndDuplicateFilteringButIsNotDuplicateTransformsAndPublishes() throws Exception
    {
    	//create and configure the ExceptionHandler
        UserExceptionDefinition userExceptionDefinition = new UserExceptionDefinition(new Integer(999), new Integer(0), "TestExternalExceptionDefRef");
        userExceptionDefinition.setPublishable(new Boolean(true));
        userExceptionDefinition.setDropDuplicate(true);
        userExceptionDefinition.setDropDuplicatePeriod(dropDuplicatePeriod);
        
        Map<String, UserExceptionDefinition> userExceptionDefsMap = new HashMap<String, UserExceptionDefinition>();
        userExceptionDefsMap.put(testResolutionId, userExceptionDefinition);
        
        Map<String, ExternalExceptionDefinition> externalExceptionDefsMap = new HashMap<String, ExternalExceptionDefinition>();
        ExternalExceptionDefinition externalExceptionDefinition = ExternalExceptionDefinition.getDefaultExternalExceptionDefinition();
        externalExceptionDefsMap.put("TestExternalExceptionDefRef", externalExceptionDefinition);
        UserExceptionHandler handler = new UserExceptionHandlerImpl(userExceptionDefsMap, externalExceptionDefsMap, exceptionTransformer, cache, publisher, payloadFactory);

        
        //mock the duplicate filter to perform a cache miss
        mockDuplicateFilter(false);
       
        //expect that we will transform and publish
        expectTransformerToBeCalled(externalExceptionDefinition);
        expectPublisherToBeCalled();
        
        //invoke the handler
        handler.invoke(exceptionContext);

        //check that all expectations on mocks were satisfied
        classMockery.assertIsSatisfied();
    }




    /**
     * Test that when handled, an exception is transformed and published if the configuration
     * suggests that it should not be published, and duplicate filtering is not configured
     * 
     * @throws Exception
     */
    @Test
    public void testInvokeWithConfiguredPublishableAndNonDuplicateFilteringTransformsAndPublishes() throws Exception
    {
    	//create and configure the ExceptionHandler
        UserExceptionDefinition ued = new UserExceptionDefinition(new Integer(999), new Integer(0), "TestExternalExceptionDefRef");
        ued.setPublishable(new Boolean(true));
        ued.setDropDuplicate(false);
        Map<String, UserExceptionDefinition> userExceptionDefsMap = new HashMap<String, UserExceptionDefinition>();
        userExceptionDefsMap.put(testResolutionId, ued);
        ExternalExceptionDefinition externalExceptionDefinition = ExternalExceptionDefinition.getDefaultExternalExceptionDefinition();
        Map<String, ExternalExceptionDefinition> externalExceptionDefsMap = new HashMap<String, ExternalExceptionDefinition>();
        externalExceptionDefsMap.put("TestExternalExceptionDefRef", externalExceptionDefinition);
        UserExceptionHandler handler = new UserExceptionHandlerImpl(userExceptionDefsMap, externalExceptionDefsMap, exceptionTransformer, cache, publisher, payloadFactory);

        //expect that we will transform and publish
        expectTransformerToBeCalled(externalExceptionDefinition);
        expectPublisherToBeCalled();
        
        //invoke the handler
        handler.invoke(exceptionContext);

        //check that all expectations on mocks were satisfied
        classMockery.assertIsSatisfied();
       
    }

    /**
     * 
     * Test that when handled, an exception is not transformed nor published if the configuration
     * suggests that it should not be published, and is duplicate filtering, but this is a duplicate
     * exception
     * 
     * @throws Exception
     */
    public void testInvokeWithConfiguredPublishableAndDuplicateFilteringAndExceptionIsDuplicateDoesNotTransformNorPublish() throws Exception
    {
    	//create and configure the ExceptionHandler
        UserExceptionDefinition ued = new UserExceptionDefinition(new Integer(999), new Integer(0), "TestExternalExceptionDefRef");
        ued.setPublishable(new Boolean(true));
        ued.setDropDuplicate(true);
        ued.setDropDuplicatePeriod(new Long(4000));
        Map<String, UserExceptionDefinition> userExceptionDefsMap = new HashMap<String, UserExceptionDefinition>();
        userExceptionDefsMap.put(testResolutionId, ued);
        ExternalExceptionDefinition externalExceptionDefinition = ExternalExceptionDefinition.getDefaultExternalExceptionDefinition();
        Map<String, ExternalExceptionDefinition> externalExceptionDefsMap = new HashMap<String, ExternalExceptionDefinition>();
        externalExceptionDefsMap.put("TestExternalExceptionDefRef", externalExceptionDefinition);
        UserExceptionHandler handler = new UserExceptionHandlerImpl(userExceptionDefsMap, externalExceptionDefsMap, exceptionTransformer, cache, publisher, payloadFactory);

        //mock the duplicate filter to perform a cache miss
        mockDuplicateFilter(true);
       
        //invoke the handler
        handler.invoke(exceptionContext);

        //check that all expectations on mocks were satisfied
        classMockery.assertIsSatisfied();
     }

    /**
     * @param externalExceptionDefinition
     * @throws TransformerException
     */
    @SuppressWarnings("synthetic-access")
	private void expectTransformerToBeCalled(final ExternalExceptionDefinition externalExceptionDefinition) throws TransformerException 
    {
        this.classMockery.checking(new Expectations()
        {
            {
                one(exceptionTransformer).transform(exceptionContext, externalExceptionDefinition);
                will(returnValue(transformedException));
            }
        });
    }

    /**
     * @throws PluginInvocationException
     */
    @SuppressWarnings("synthetic-access")
	private void expectPublisherToBeCalled() throws PluginInvocationException
    {
    	final String exceptionPayloadId = "exceptionPayloadId";
    	
        this.classMockery.checking( new Expectations()
        {
            {
                one(payloadFactory).newPayload("userExceptionPayloadId",Spec.TEXT_XML, transformedException.getBytes());will(returnValue(exceptionPayload));
				
            	allowing(exceptionPayload).idToString();
            	will(returnValue(exceptionPayloadId));
                
            	one(publisher).invoke(with(any(Event.class)));
            }
        });
    }

	/**
	 * @param isDuplicate
	 */
	@SuppressWarnings("synthetic-access")
	private void mockDuplicateFilter(final boolean isDuplicate) {
		classMockery.checking( new Expectations()
        {
            {
                one(cache).notifiedSince(testResolutionId, dropDuplicatePeriod); will(returnValue(isDuplicate));
                one(cache).notify(testResolutionId);
                
                
            }
        });
	}
 
}
