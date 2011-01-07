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
package org.ikasan.connector.base;

import java.util.Timer;
import java.util.TimerTask;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.XATerminator;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;

/**
 * This is the abstract implementation of ResourceAdapter interface. All the
 * transport adapter classes must extend this class. The default implementation
 * does not provides any functionality except capturing the application server's
 * facilities provided via BootStrapContext. Especially, the WorkManager,
 * XATerminator and Timer references are obtained from this BootStrapContext
 * that should be assigned to local variables.
 * 
 * It is a standard not to create any threads in adapters. Instead it is
 * recommended to use the Work Management contract to create Work instances
 * (which are java threads in its own right) and submit to application server
 * for execution. Application server will create/obtain a new or free thread to
 * execute the Work instance. As these Work instances are managed by the
 * application server, greater control is exhibited.
 * 
 * The Timer facility is used to execute the Work instances in a scheduled
 * manner. If your application doens't have a consumer that sits and waits for
 * the incoming messages, but you do have a requirement of looping through
 * indefinitely, take use of Timer facility.
 * 
 * The XATerminator is used by the EIS to complete/recover the transactions that
 * are initiated by the EIS. Usually the XATerminator instance could be same as
 * the TransactionManager.
 * 
 * All the extending classes such as FileResourceAdapter, JMSResourceAdapter etc
 * must implement endpointActivation, endDeactivation and getXAResources
 * methods.
 * 
 * @author Ikasan Development Team
 */
public abstract class AbstractResourceAdapter implements ResourceAdapter
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(AbstractResourceAdapter.class);

    /**
     * The boot strap context reference obtained from ApplicationServer to get
     * its facilities
     */
    private BootstrapContext bootCtx = null;

    /**
     * The work manager is used for submitting worker threads. This way,
     * adapters are restricted to create their own threads
     */
    private WorkManager workManager = null;

    /** Use this reference to schedule Work */
    private Timer timer = null;

    /**
     * XATerminator is used for completing the transaction calls originated from
     * EIS.
     * 
     * TODO Not read, but somehow used by inbound connectors?
     */
    private XATerminator xaTerminator = null;

    /**
     * The start method is called by the Application Server to pass the
     * bootstrap context to the resource adapter.
     * 
     * @param ctx -The bootstrap context
     * @throws ResourceAdapterInternalException - Exception if we fail to
     *             bootstrap this resource
     */
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException
    {
        logger.info("Starting the resource adapter..."); //$NON-NLS-1$
        setBootstrapContext(ctx);
        setXATerminator(bootCtx.getXATerminator());
        setWorkManager(bootCtx.getWorkManager());
        try
        {
            this.timer = bootCtx.createTimer();
        }
        catch (UnavailableException e)
        {
            String err = "Exception while creating the timer."; //$NON-NLS-1$
            logger.fatal(err, e);
            throw new ResourceAdapterInternalException(err, e);
        }
    }

    /**
     * Set the XATerminator
     * 
     * @param terminator - XAterminator to set
     */
    private void setXATerminator(final XATerminator terminator)
    {
        this.xaTerminator = terminator;
        logger.debug("XATerminator set."); //$NON-NLS-1$
    }

    /**
     * Set the BoostrapContext
     * 
     * @param ctx - Bootstrap Context to set
     */
    private void setBootstrapContext(final BootstrapContext ctx)
    {
        this.bootCtx = ctx;
        logger.debug("BootStrapContext set."); //$NON-NLS-1$
    }

    /**
     * This method is called by the application server when a server goes down
     * or an adapter is undeployed. Release the resources during this method
     * call.
     */
    public void stop()
    {
        logger.info("Stopping the resource adapter " //$NON-NLS-1$
                + "and releasing the resources..."); //$NON-NLS-1$
        this.bootCtx = null;
        this.workManager = null;
        this.timer.cancel();
        this.timer = null;
        logger.info("Resource adapter is stopped and resources are released."); //$NON-NLS-1$
    }

    /**
     * Method to get a reference to WorkManager
     * 
     * @return WorkManager
     */
    public WorkManager getWorkManager()
    {
        logger.debug("Returning WorkManager..."); //$NON-NLS-1$
        return workManager;
    }

    /**
     * Set the WorkManager instance
     * 
     * @param workManager - Work manager to set 
     */
    public void setWorkManager(final WorkManager workManager)
    {
        this.workManager = workManager;
        logger.debug("WorkManager set."); //$NON-NLS-1$
    }

    /**
     * Method to retrieve a Timer reference
     * 
     * @return Timer
     */
    public Timer getTimer()
    {
        logger.debug("Returning Timer..."); //$NON-NLS-1$
        return timer;
    }

    /**
     * This method must be implemented by the subclasses. Application server
     * calls this method when an endpoint (probably MessageDrivenBean) is
     * deployed
     */
    public abstract void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException;

    /**
     * This method is called by the application server when a MDB is
     * undeployed/removed. The subclasses must implement this method
     */
    public abstract void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec);

    /**
     * When an Application server comes back after a crash, it calls this method
     * by giving the ActivationSpecs so that the adapter can find the
     * XAResources that has uncommitted/withhold transactions
     */
    public abstract XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException;

    /**
     * This method schedules the Timer task with a default initial delay (0
     * seconds). The method kicks off immediately
     * 
     * @param task The task that is to be set off
     */
    public void schedule(TimerTask task)
    {
        logger.debug("Scheduling timer task..."); //$NON-NLS-1$
        getTimer().schedule(task, 0);
    }
}
