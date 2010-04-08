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
package org.ikasan.framework.initiator.messagedriven.spring;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener;
import org.ikasan.framework.initiator.messagedriven.MessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Extension of Spring's DefaultMessageListenerContainer to expose listener setup failures to a registered Listener
 * 
 * @author Ikasan Development Team
 *
 */
public class SpringMessageListenerContainer extends DefaultMessageListenerContainer implements MessageListenerContainer
{
 private Logger logger = Logger.getLogger(SpringMessageListenerContainer.class);
    
    /**
     * Flag indicating last attempt to connect was a failure
     */
    private boolean listenerSetupFailure = false;
    
    /**
     * Registered failure listener
     */
    private ListenerSetupFailureListener listenerSetupExceptionListener;
    
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.messagedriven.MessageListenerContainer#setListenerSetupExceptionListener(org.ikasan.framework.initiator.messagedriven.ListenerSetupFailureListener)
     */
    public void setListenerSetupExceptionListener(ListenerSetupFailureListener listenerSetupExceptionListener)
    {
        this.listenerSetupExceptionListener = listenerSetupExceptionListener;
    }


    /**
     * Accessor for listenerSetupFailure flag
     * 
     * @return listenerSetupFailure
     */
    public boolean isListenerSetupFailure()
    {
        return listenerSetupFailure;
    }


    @Override
    protected void handleListenerSetupFailure(Throwable ex, boolean alreadyRecovered)
    {
        super.handleListenerSetupFailure(ex, alreadyRecovered);
        logger.info("already recovered:"+alreadyRecovered+", ex:"+ex);
        this.listenerSetupFailure = true;
        if (listenerSetupExceptionListener!=null){
            listenerSetupExceptionListener.notifyListenerSetupFailure(ex);
        }
    }
    
    @Override
    protected boolean receiveAndExecute(Object invoker, Session session, MessageConsumer consumer) throws JMSException
    {
        boolean result = super.receiveAndExecute(invoker, session, consumer);
        //if the preceding method call did not throw anything then we can reset the listenerSetupFailed flag
        listenerSetupFailure = false;
        return result;
    }
}
