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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.UserExceptionHandler;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.plugins.JMSEventPublisherPlugin;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Plugin that currently wraps all the default user exception handling.
 * 
 * @author Ikasan Development Team
 */
public class UserExceptionHandlerImpl implements UserExceptionHandler, UserExceptionHandlerConstants
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(UserExceptionHandlerImpl.class);

    /** User defined exceptions */
    private Map<String, UserExceptionDefinition> userExceptionDefs;

    /** External defined exceptions */
    private Map<String, ExternalExceptionDefinition> externalExceptionDefs;

    /** Exception Transformer */
    private ExceptionTransformer exceptionTransformer;

    /** JMS Publisher */
    private JMSEventPublisherPlugin jmsEventPublisher;

    /** Cache for managing duplicate exceptions */
    private ExceptionCache exceptionCache;

    /** Factory for instantiating Payloads */
    private PayloadFactory payloadFactory;

    /**
     * TODO Constructor has too many dependents. This must be rationalised when we move towards the plugin approach.
     * 
     * @param userExceptionDefs Map of user exception definitions
     * @param externalExceptionDefs Map of external exception definitions
     * @param exceptionTransformer Transformer to transform exceptions to external standard
     * @param exceptionCache Exception Cache (so we don';t send duplicates)
     * @param jmsEventPublisher JMS Event Publisher Plugin
     * @param payloadFactory Payload factory
     */
    public UserExceptionHandlerImpl(Map<String, UserExceptionDefinition> userExceptionDefs,
            Map<String, ExternalExceptionDefinition> externalExceptionDefs, ExceptionTransformer exceptionTransformer,
            ExceptionCache exceptionCache, JMSEventPublisherPlugin jmsEventPublisher, PayloadFactory payloadFactory)
    {
        this.userExceptionDefs = userExceptionDefs;
        this.externalExceptionDefs = externalExceptionDefs;
        this.exceptionTransformer = exceptionTransformer;
        this.exceptionCache = exceptionCache;
        this.jmsEventPublisher = jmsEventPublisher;
        this.payloadFactory = payloadFactory;
    }

    /**
     * Get the user exception
     * 
     * @param id The id of the user exception
     * @return UserExceptionDefinition
     * @throws Exception Exception if we fail to retrieve
     */
    private UserExceptionDefinition getUserException(final String id) throws Exception
    {
        UserExceptionDefinition def = this.userExceptionDefs.get(id);
        if (def == null)
        {
            throw new Exception("Undefined userException for id [" + id + "].");
        }
        return def;
    }

    /**
     * Implementation of the UserExceptionHandler invocation method. This updates the exceptionContext for use by later
     * flows in the user exception handling.
     * 
     * @param exceptionContext The context of the exception
     * @throws Exception Exception if invocation fails
     */
    public void invoke(ExceptionContext exceptionContext) throws Exception
    {
        // Lookup all required exception definitions
        lookupExceptionDefs(exceptionContext);
        // filter duplicates
        if (!isPublicationSuppressable(exceptionContext))
        {
            transform(exceptionContext);
            publish(exceptionContext);
        }
    }

    /**
     * Lookup the Exception definitions based on context
     * 
     * @param exceptionContext Exception context
     */
    private void lookupExceptionDefs(ExceptionContext exceptionContext)
    {
        // Log the exception
        if (logger.isInfoEnabled())
        {
            logger.info("User Exception Handler received ExceptionContext for " + "component ["
                    + exceptionContext.getComponentName() + "] " + "resolution [" + exceptionContext.getResolutionId()
                    + "] " + "detail [" + exceptionContext.getThrowable().getMessage() + "]");
        }
        UserExceptionDefinition userExceptionDef;
        // Get the user exception configuration entry
        try
        {
            userExceptionDef = getUserException(exceptionContext.getResolutionId());
        }
        // Any exceptions then use default
        catch (Exception e)
        {
            if (logger.isInfoEnabled())
            {
                logger.info(e + " Resorting to default user exception definition.");
            }
            userExceptionDef = UserExceptionDefinition.getDefaultUserExceptionDefinition();
        }
        // Always populate the exception context regardless
        exceptionContext.put(USER_EXCEPTION_DEF, userExceptionDef);
    }

    /**
     * Returns true if publication of this exception should be skipped because either
     * 
     * a) it is configured not to be published<br>
     * b) we detect it to be a duplicate
     * 
     * @param exceptionContext Exception context
     * @return true if we should be skipping publication of this
     */
    private boolean isPublicationSuppressable(ExceptionContext exceptionContext)
    {
        boolean result = false;
        UserExceptionDefinition userExceptionDef = (UserExceptionDefinition) exceptionContext.get(USER_EXCEPTION_DEF);
        if (userExceptionDef != null)
        {
            Boolean configuredPublishable = userExceptionDef.getPublishable();
            if ((configuredPublishable != null) && (configuredPublishable.booleanValue() == false))
            {
                // We can stop right here as publishable is configured to false
                if (logger.isDebugEnabled())
                {
                    logger.debug("About to suppress publication of this exception because exceptions resolving to ["
                            + exceptionContext.getResolutionId() + "] are not configured to be publishable");
                }
                return true;
            }
            // Are we even filtering duplicates?
            if (userExceptionDef.getDropDuplicate().booleanValue())
            {
                // Check the cache for any notifications within the notification period
                if (this.exceptionCache.notifiedSince(exceptionContext.getResolutionId(), userExceptionDef
                    .getDropDuplicatePeriod()))
                {
                    // We are going to filter this because it is a duplicate
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("About to filter out publication of this exception because it is a duplicate of ["
                                + exceptionContext.getResolutionId() + "] within the time period of ["
                                + userExceptionDef.getDropDuplicatePeriod() + "]");
                    }
                    result = true;
                }
                // Notify the cache of this occurrence
                exceptionCache.notify(exceptionContext.getResolutionId());
            }
        }
        return result;
    }

    /**
     * Transform the exception
     * 
     * @param exceptionContext Exception context to help us
     * @throws TransformerException Exception if we could not transform the exception
     */
    private void transform(ExceptionContext exceptionContext) throws TransformerException
    {
        // Retrieve the user exception definition
        UserExceptionDefinition userExceptionDef = (UserExceptionDefinition) exceptionContext.get(USER_EXCEPTION_DEF);
        if (userExceptionDef == null)
        {
            logger.warn("User exception definition is 'null'. " + "Setting default user exception definition.");
            userExceptionDef = UserExceptionDefinition.getDefaultUserExceptionDefinition();
        }
        // Now get and set the external exception definition
        ExternalExceptionDefinition externalExceptionDef = this.externalExceptionDefs.get(userExceptionDef
            .getExternalExceptionRef());
        if (externalExceptionDef == null)
        {
            externalExceptionDef = ExternalExceptionDefinition.getDefaultExternalExceptionDefinition();
        }
        // Invoke the transformer
        String externalException = this.exceptionTransformer.transform(exceptionContext, externalExceptionDef);
        // Pop results back into the exception context
        exceptionContext.put(UserExceptionHandlerConstants.EXTERNAL_EXCEPTION_XML, externalException);
    }

    /**
     * Publish the exception
     * 
     * @param exceptionContext The context of the exception
     * @throws PluginInvocationException Exception if the Plugin fails
     */
    private void publish(ExceptionContext exceptionContext) throws PluginInvocationException
    {
        String externalExceptionXml = (String) exceptionContext
            .get(UserExceptionHandlerConstants.EXTERNAL_EXCEPTION_XML);
        if (externalExceptionXml == null)
        {
            // It is a mandatory precondition for this field to exist in the exceptionContext, if publishable is set to
            // true, so may as well blow up if this is missing
            throw new CommonRuntimeException("Mandatory field EXTERNAL_EXCEPTION_XML missing from exceptionContext");
        }
        /*
         * Using the jmsEventPublisherPlugin which can only take an Event. Therefore with current implementation we need
         * to create an Event simply to be able to publish TODO remove this unnecessary Event creation by making a
         * publisher capable of publishing directly
         */
//        String componentGroupName = null; // really doesn't matter
//        String componentName = null; // really doesn't matter
        Payload payload = payloadFactory.newPayload("userExceptionPayloadId", externalExceptionXml.getBytes());
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        Event event = new Event("userExceptionHandler", "userExceptionHandler", "userExceptionEventId",payloads);
        //event.setName("emrException");
        // Publish away!
        jmsEventPublisher.invoke(event);
    }
}
