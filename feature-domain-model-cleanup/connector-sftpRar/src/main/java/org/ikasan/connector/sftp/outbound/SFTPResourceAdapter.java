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
package org.ikasan.connector.sftp.outbound;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

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
