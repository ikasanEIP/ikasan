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
package org.ikasan.framework.initiator.messagedriven.jca.jboss;

import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.resource.spi.ResourceAdapter;

/**
 * Utility class for obtaining the JBoss JCA Resource Adapter for use 
 * with JCA JMS Endpoint implementations
 **/
public abstract class JBossResourceAdapterUtils
{
    private static final String MBEAN_SERVER_LOCATOR_CLASS_NAME = "org.jboss.mx.util.MBeanServerLocator";
    private static final String MBEAN_SERVER_LOCATOR_METHOD_NAME = "locateJBoss";
    private static final String RESOURCE_ADAPTER_SERVICE_NAME = "jboss.jca:name='jms-ra.rar',service=RARDeployment";
    private static final String JBOSS_RESOURCE_ADAPTER_ATTRIBUTE_NAME = "ResourceAdapter";

    /**
     * Obtain the default JBoss Resource Adapter through a JMX invocation
     * for the JBossWorkManagerMBean.ResourceAdapter
     * @return ResourceAdapter
     * @see org.jboss.resource.work.JBossWorkManagerMBean
     */
    public static ResourceAdapter getResourceAdapter()
    {
        try
        {
            Method locaJBoss = Class.forName(MBEAN_SERVER_LOCATOR_CLASS_NAME).getMethod(MBEAN_SERVER_LOCATOR_METHOD_NAME, (Class[]) null);
            //The underlying method is static, so we can pass null for obj parameter
            MBeanServer server = (MBeanServer)locaJBoss.invoke(null, new Object[0]);
            ObjectName objName = new ObjectName(RESOURCE_ADAPTER_SERVICE_NAME);
            Object ra = server.getAttribute(objName, JBOSS_RESOURCE_ADAPTER_ATTRIBUTE_NAME);
            return (ResourceAdapter)ra;
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Cannot get JBoss Resource Adapter:", e);
        }
    }

}
