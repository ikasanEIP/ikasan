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
package org.ikasan.connector.sftp.outbound;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.connector.base.outbound.EISResourceAdapter;

/**
 * This class extends the outbound EISResourceAdapter with SFTP specific 
 * implementation.  As this is an outbound ResourceAdapter, we don't actually 
 * need to provide any endpointActiviation or endpointDeactivation 
 * implementation here.
 * 
 * The Application server on startup actually calls the start(BootstrapContext) 
 * method (implementation is in the AbstractResourceAdapter class)
 * 
 * @author Ikasan Development Team
 */
public class SFTPResourceAdapter extends EISResourceAdapter
{
   
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(SFTPResourceAdapter.class);

    /**
     * This is a callback method invoked by the application server when an 
     * endpoint is activated (that is, when a MDB is deployed).
     * 
     * The activation spec defines where to connect to - remote or local. All
     * the configuration information that is needed to connect to the EIS are
     * provided by the spec object.
     * 
     * The spec is configured (created) by parsing the deployment descriptor of
     * the endpoint by the application server.
     */
    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory,
                                   ActivationSpec spec)
    {
        logger.debug("SFTP Endpoint activated."); //$NON-NLS-1$
    }

    /**
     * Release any SFTP resources.
     */
    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory,
                                     ActivationSpec spec)
    {
        logger.debug("SFTP Endpoint de-activated (stopped). " //$NON-NLS-1$
                  + "Re-deploy to activate once again!"); //$NON-NLS-1$
        logger.debug("Connector released."); //$NON-NLS-1$
    }

    /**
     * When an Application server comes back after a crash, it calls this method
     * by giving the ActivationSpecs so that the adatper can find the 
     * XAResources that has uncommitted/withhold transactions, for the 
     * SFTPesourceAdapter this is null
     */
    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs)
    {
        logger.debug("Returning XAResource [null]..."); //$NON-NLS-1$
        return null;
    }

}
