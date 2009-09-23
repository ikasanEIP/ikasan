/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        UserExceptionDefinition ued = new UserExceptionDefinition(new Integer(999), new Integer(0), "TestExternalExceptionDefRef");
        ued.setPublishable(new Boolean(true));
        ued.setDropDuplicate(true);
        ued.setDropDuplicatePeriod(dropDuplicatePeriod);
        Map<String, UserExceptionDefinition> userExceptionDefsMap = new HashMap<String, UserExceptionDefinition>();
        userExceptionDefsMap.put(testResolutionId, ued);
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
    	final Integer payloadPriority = 0;
    	final String exceptionPayloadId = "exceptionPayloadId";
    	
        this.classMockery.checking( new Expectations()
        {
            {
                one(payloadFactory).newPayload("emrException",Spec.TEXT_XML, null, transformedException.getBytes());will(returnValue(exceptionPayload));
            	allowing(exceptionPayload).getPriority();
				will(returnValue(payloadPriority));
				
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
