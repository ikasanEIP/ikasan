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
package org.ikasan.framework;

/**
 * This interface defines the framework context constants
 * the values of which are loaded from the frameworkResource properties.
 *
 * @author Ikasan Development Team
 */  
public interface FrameworkConst
{
    /** JMS XA Connection Factory for deployed (private) applications only */
    public String JMS_XA_CONNECTION_FACTORY = "jms.xa.connectionFactory"; //$NON-NLS-1$

    /** JMS Provider */
    public String JMS_PROVIDER = "jms.provider"; //$NON-NLS-1$

    /** JMS secured */
    public String JMS_SECURED = "jms.secured"; //$NON-NLS-1$

    /** JMS username */
    public String JMS_USERNAME = "jms.username"; //$NON-NLS-1$

    /** JMS password */
    public String JMS_PASSWORD = "jms.password"; //$NON-NLS-1$

    /** JMS encryptionPolicy */
    public String JMS_ENCRYPTION_POLICY = "jms.encryption.policy"; //$NON-NLS-1$

    /** connector non transactional persistence factory */
    public String DS_SESSION_FACTORY = "ds.session.factory"; //$NON-NLS-1$

    /** connector local transactional persistence factory */
    public String LOCALDS_SESSION_FACTORY = "localds.session.factory"; //$NON-NLS-1$

    /** connector XA transactional persistence factory */
    public String XADS_SESSION_FACTORY = "xads.session.factory"; //$NON-NLS-1$

    /** component environment context */
    public String COMPONENT_ENV = "component.env"; //$NON-NLS-1$

    /** component group name  */
    public String COMPONENT_GROUP_NAME = "component.group.name"; //$NON-NLS-1$

    /** component name  */
    public String COMPONENT_NAME = "component.name"; //$NON-NLS-1$

    /** Component Security Configuration resource */
    public String COMPONENT_SECURITY_CONF_RESOURCE = "component.security.conf.resource"; //$NON-NLS-1$

    /** component status URL */
    public String COMPONENT_STATUS_URL = "component.status.url"; //$NON-NLS-1$

    /** flow destination name  */
    public String FLOW_DESTINATION_NAME = "flow.destination.name"; //$NON-NLS-1$

    /** component exception resolver */
    public String COMPONENT_EXCEPTION_RESOLVER = "component.exception.resolver"; //$NON-NLS-1$

    /** name of the next flow */
    public String FLOW_NAME = "flow.name"; //$NON-NLS-1$

    /** name of the method of the next flow */
    public String FLOW_METHOD = "flow.method"; //$NON-NLS-1$

    /** name of the exception handler */
    public String FLOW_EXCEPTION_NAME = "flow.exception.name"; //$NON-NLS-1$

    /** name of the exception handler method */
    public String FLOW_EXCEPTION_METHOD = "flow.exception.method"; //$NON-NLS-1$
    
    /** General Constant for values we are unable to populate */
    public String UNDEFINED = "undefined.value"; //$NON-NLS-1$
} 
 

