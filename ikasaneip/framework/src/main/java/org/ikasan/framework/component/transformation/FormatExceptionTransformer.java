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
package org.ikasan.framework.component.transformation;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Encoding;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.TransformationException;
import org.ikasan.framework.component.transformation.Transformer;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.user.ExceptionTransformer;
import org.ikasan.framework.exception.user.ExternalExceptionDefinition;

/**
 * Implementation of <code>Transformer</code> which provides support for 
 * transforming any provided exception in the business flow into a payload of an
 * event.
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class FormatExceptionTransformer implements Transformer
{
    /** transformer for exception creation */
    private ExceptionTransformer exceptionTransformer;

    /** external exception definition */
    private ExternalExceptionDefinition externalExceptionDef;

    /** new payload name */
    private String payloadName;

    /** flow component name */
    private String componentName;

    /**
     * Constructor
     * 
     * @param exceptionTransformer 
     * @param externalExceptionDef 
     * @param payloadName 
     */
    public FormatExceptionTransformer(ExceptionTransformer exceptionTransformer,
            ExternalExceptionDefinition externalExceptionDef, String payloadName)
    {
        this.exceptionTransformer = exceptionTransformer;
        if(this.exceptionTransformer == null)
        {
            throw new IllegalArgumentException("exceptionTransformer cannot be 'null'");
        }

        this.externalExceptionDef = externalExceptionDef;
        if(this.externalExceptionDef == null)
        {
            throw new IllegalArgumentException("externalExceptionDef cannot be 'null'");
        }
        
        this.payloadName = payloadName;
    }

    /**
     * Accessor for componentName
     * @param componentName
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws TransformationException
    {
        List<Payload> payloads = event.getPayloads();

        try
        {
            Throwable throwable = getException(event);
            ExceptionContext exceptionContext = new ExceptionContext(throwable, event, componentName);

            // transform 
            String formattedException = exceptionTransformer.transform(exceptionContext, externalExceptionDef);
            
            // update payload attributes
            Payload payload = payloads.get(0);
            payload.setContent(formattedException.getBytes());
            payload.setName(payloadName);
            payload.setSpec(Spec.TEXT_XML.toString());
            payload.setEncoding(Encoding.NOENC.toString());

            // replace event payloads with the new payload
            payloads.clear();
            event.setPayload(payload);
        }
        catch (TransformerException e)
        {
            throw new TransformationException(e);
        }
    }

    /**
     * Extending classes must return the exception to be reported.
     * @param event
     * @return throwable
     * @throws TransformationException 
     */
    abstract public Throwable getException(Event event) throws TransformationException;
}
