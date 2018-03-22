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
package org.ikasan.component.endpoint.jms.spring.listener;

import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.jms.listener.IkasanMessageListenerContainer;
import org.ikasan.component.endpoint.jms.consumer.MessageProvider;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.configuration.Configured;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.transaction.TransactionManager;

/**
 * Extend IkasanMessageListenerContainer to ensure standard defaults are set on the container being instantiated.
 * And uses Arjuna transaction Manager to ensure JMS enlisting to XA transaction by usage of ConnectionFactoryProxy
 * provided by Arjuna.
 *
 * @author Ikasan Development Team
 */
public class ArjunaIkasanMessageListenerContainer extends IkasanMessageListenerContainer
        implements MessageProvider, Configured<SpringMessageConsumerConfiguration>, IsExclusionServiceAware
{
    private TransactionManager localTransactionManager;

    /**
     * Constructor with preferred defaults.
     */
    public ArjunaIkasanMessageListenerContainer()
    {
        super();
    }

    /**
     * If CF not already provided then look it up from JNDI
     */
    protected void establishConnectionFactory()
    {
        super.establishConnectionFactory();

        // proxy an XA CF, but only if its not already been proxied
        if(getConnectionFactory() instanceof XAConnectionFactory && !(getConnectionFactory() instanceof ConnectionFactoryProxy) )
        {
            ConnectionFactory connectionFactoryProxy = new ConnectionFactoryProxy(
                    (XAConnectionFactory) getConnectionFactory(),
                    new TransactionHelperImpl(localTransactionManager)
            );

            setConnectionFactory(connectionFactoryProxy);
        }
    }

    public TransactionManager getLocalTransactionManager()
    {
        return localTransactionManager;
    }

    public void setLocalTransactionManager(TransactionManager localTransactionManager)
    {
        this.localTransactionManager = localTransactionManager;
    }
}

