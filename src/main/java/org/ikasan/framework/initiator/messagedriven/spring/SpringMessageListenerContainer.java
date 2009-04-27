/*
 * $Id: SpringMessageListenerContainer.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/messagedriven/spring/SpringMessageListenerContainer.java $
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
package org.ikasan.framework.initiator.messagedriven.spring;

import org.ikasan.framework.initiator.messagedriven.MessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Wrapper for Spring's DefaultMessageListenerContainer
 * 
 * @author Ikasan Development Team
 *
 */
public class SpringMessageListenerContainer implements MessageListenerContainer
{
    /**
     * Wrapped instance
     */
    private DefaultMessageListenerContainer defaultMessageListenerContainer;
    
    /**
     * Constructor
     * 
     * @param defaultMessageListenerContainer
     */
    public SpringMessageListenerContainer(DefaultMessageListenerContainer defaultMessageListenerContainer)
    {
        super();
        this.defaultMessageListenerContainer = defaultMessageListenerContainer;
    }



    /**
     * Accessor for wrapped instance
     * 
     * @return
     */
    public DefaultMessageListenerContainer getDefaultMessageListenerContainer()
    {
        return defaultMessageListenerContainer;
    }



    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.MessageListenerContainer#isRunning()
     */
    public boolean isRunning()
    {
        return defaultMessageListenerContainer.isRunning();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.MessageListenerContainer#start()
     */
    public void start()
    {
        defaultMessageListenerContainer.start();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.MessageListenerContainer#stop()
     */
    public void stop()
    {
        defaultMessageListenerContainer.stop();
    }
}
