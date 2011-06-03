/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca.jboss;

import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;


/**
 * Extended version of the Spring JmsActivationSpecConfig which supports
 * additional properties that JBoss require in their JMS activation spec.
 * 
 * @author Ikasan Development Team
 */
public class JBossJmsActivationSpecConfig extends JmsActivationSpecConfig
{
    /* JBoss default JMS provider JNDI binding */
    private String providerAdapterJNDI;

    /* JBoss include JMS Username in the activationSpec */
    private String user;

    /* JBoss include JMS Password in the activationSpec */
    private String password;

    /* length of time (in millis) to keep inactive sessions */
    private int keepAlive;

    /* time (in seconds) between attempts to (re)-connect to JMS provider */
    private int reconnectInterval;

    /* whether to use the DLQ handler */
    private boolean useDLQ = false;

    /* actual DLQ handler */
    private String dlqHandler;

    /* DLQ user */
    private String dlqUser;

    /* DLQ password */
    private String dlqPassword;

    /* DLQ client id */
    private String dlqClientId;

    /* whether to force the use of transactions inside the adapter when using 
     * an XASession. Available from JBoss Application 5.2.0.GA 
     */
    private boolean forceTransacted = false;

    /**
     * @return the providerAdapterJNDI
     */
    public String getProviderAdapterJNDI()
    {
        return providerAdapterJNDI;
    }

    /**
     * @param providerAdapterJNDI the providerAdapterJNDI to set
     */
    public void setProviderAdapterJNDI(String providerAdapterJNDI)
    {
        this.providerAdapterJNDI = providerAdapterJNDI;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the keepAlive
     */
    public int getKeepAlive()
    {
        return keepAlive;
    }

    /**
     * @param keepAlive the keepAlive to set
     */
    public void setKeepAlive(int keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    /**
     * @return the reconnectInterval
     */
    public int getReconnectInterval()
    {
        return reconnectInterval;
    }

    /**
     * @param reconnectInterval the reconnectInterval to set
     */
    public void setReconnectInterval(int reconnectInterval)
    {
        this.reconnectInterval = reconnectInterval;
    }

    /**
     * @return the useDLQ
     */
    public boolean isUseDLQ()
    {
        return useDLQ;
    }

    /**
     * @param useDLQ the useDLQ to set
     */
    public void setUseDLQ(boolean useDLQ)
    {
        this.useDLQ = useDLQ;
    }

    /**
     * @return the dlqHandler
     */
    public String getDlqHandler()
    {
        return dlqHandler;
    }

    /**
     * @param dlqHandler the dlqHandler to set
     */
    public void setDlqHandler(String dlqHandler)
    {
        this.dlqHandler = dlqHandler;
    }

    /**
     * @return the dlqUser
     */
    public String getDlqUser()
    {
        return dlqUser;
    }

    /**
     * @param dlqUser the dlqUser to set
     */
    public void setDlqUser(String dlqUser)
    {
        this.dlqUser = dlqUser;
    }

    /**
     * @return the dlqPassword
     */
    public String getDlqPassword()
    {
        return dlqPassword;
    }

    /**
     * @param dlqPassword the dlqPassword to set
     */
    public void setDlqPassword(String dlqPassword)
    {
        this.dlqPassword = dlqPassword;
    }

    /**
     * @return the dlqClientId
     */
    public String getDlqClientId()
    {
        return dlqClientId;
    }

    /**
     * @param dlqClientId the dlqClientId to set
     */
    public void setDlqClientId(String dlqClientId)
    {
        this.dlqClientId = dlqClientId;
    }

    /**
     * @return the forceTransacted
     */
    public boolean isForceTransacted()
    {
        return forceTransacted;
    }

    /**
     * @param forceTransacted the forceTransacted to set
     */
    public void setForceTransacted(boolean forceTransacted)
    {
        this.forceTransacted = forceTransacted;
    }

}
