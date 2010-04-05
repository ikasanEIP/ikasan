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
 

