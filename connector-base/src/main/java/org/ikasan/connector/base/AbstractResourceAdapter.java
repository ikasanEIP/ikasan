/*
 * $Id: AbstractResourceAdapter.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/AbstractResourceAdapter.java $
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

// Imported log4j classes
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
public abstract class AbstractResourceAdapter
    implements ResourceAdapter
{
    /**
     * The logger instance.
     */
    private static Logger logger =
        Logger.getLogger(AbstractResourceAdapter.class);

    /**
     * The boot strap context reference obtained from ApplicationServer to get
     * its facilities
     */
    private BootstrapContext bootCtx = null;

    /**
     * The work manager is used for submitting worker threads. This way, adapters
     * are restricted to create their own threads
     */
    private WorkManager workManager = null;

    /**
     * Use this reference to schedule Work
     */
    private Timer timer = null;

    /**
     * XATerminator is used for completing the transaction calls originated
     * from EIS.
     * 
     * TODO Not read, but somehow used by inbound connectors?
     */
    private XATerminator xaTerminator = null;

    /**
     * The start method is called by the Application Server to pass the
     * bootstrap context to the resource adapter.
     * 
     * @param ctx
     * @throws ResourceAdapterInternalException
     */
    public void start(BootstrapContext ctx)
        throws ResourceAdapterInternalException
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
     * @param terminator
     */
    private void setXATerminator(final XATerminator terminator)
    {
        this.xaTerminator = terminator;
        logger.debug("XATerminator set."); //$NON-NLS-1$
    }

    /**
     * Set the BoostrapContext
     * 
     * @param ctx
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
     * set the WorkManager instance
     * 
     * @param workManager
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
    public abstract void
        endpointActivation(MessageEndpointFactory endpointFactory,
                           ActivationSpec spec)
        throws ResourceException;

    /**
     * This method is called by the application server when a MDB is
     * undeployed/removed. The subclasses must implement this method
     */
    public abstract void
        endpointDeactivation(MessageEndpointFactory endpointFactory,
                             ActivationSpec spec);

    /**
     * When an Application server comes back after a crash, it calls this method
     * by giving the ActivationSpecs so that the adapter can find the XAResources
     * that has uncommitted/withhold transactions
     */
    public abstract XAResource[] getXAResources(ActivationSpec[] specs)
        throws ResourceException;

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

    /**
     * This method schedules the timer task with an initial delay and interval
     * 
     * @param task The task that is to be executed
     * @param worker The worker instance to be fed to TimerTask.
     * @param initDelay The initial delay before the timer sets off. 0 seconds
     *            indicates straight away.
     * @param timerInterval The interval at which the time should execute (in
     *            seconds
     */
    //
    // Not sure about this - this could cause a number of problems.
    //
    // public void schedule(TimerTask task, int initDelay, int timerInterval)
    // {
    //     logger.debug("schedule get timer");
    //     getTimer().schedule(task, initDelay, timerInterval * 60 * 1000);
    // }
    //

}
