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
package org.ikasan.consumer.jms;

import javax.jms.Destination;

import org.ikasan.consumer.jms.DestinationResolver;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

/**
 * Specific implementation of a destination resolver which unwraps a 
 * destination from a proxied object.
 * 
 * @author JeffMitchell
 */
public class DestinationResolverSpringProxyImpl implements DestinationResolver
{
    /** destination which could be a vanilla destination or a proxied destination */
    private Destination destination;

    /**
     * Constructor
     * @param destination
     */
    public DestinationResolverSpringProxyImpl(Destination destination)
    {
        this.destination = destination;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.consumer.jms.DestinationResolver#getDestination()
     */
    public Destination getDestination()
    {
        try
        {
            if(AopUtils.isAopProxy(destination) && destination instanceof Advised)
            {
                Advised advised = (Advised) destination;
                Object unwrappedObject = advised.getTargetSource().getTarget();
                if(unwrappedObject instanceof Destination)
                {
                    return (Destination)unwrappedObject;
                }
                
                throw new RuntimeException("Expected proxied object of Destination, but returned object [" 
                    + unwrappedObject.getClass().getName() + "]");
            }
            else
            {
                return destination;
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to resolve destination from object of type " 
                + destination.getClass().getName() + "]");
        }
    }
}
