/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.sample.priceSrc.component.endpoint;

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.sample.priceSrc.tech.PriceTechImpl;
import org.ikasan.sample.priceSrc.tech.PriceTechListener;
import org.ikasan.sample.priceSrc.tech.PriceTechMessage;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;

/**
 * This test class supports the <code>Translator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class PriceConsumer implements Consumer<EventListener>, PriceTechListener
{
    /** consumer managed stubbed tech */
    private PriceTechImpl priceTechImpl;

    /** consumer event factory */
    private FlowEventFactory flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** simulate tech on a separate thread */
    private Thread techThread;

    /**
     * Constructor
     * @param stubbedTechImpl
     * @param flowEventFactory
     */
    public PriceConsumer(PriceTechImpl priceTechImpl, FlowEventFactory flowEventFactory)
    {
        this.priceTechImpl = priceTechImpl;
        this.flowEventFactory = flowEventFactory;
        this.priceTechImpl.setListener(this);
    }
    
    /**
     * Start the underlying tech
     */
    public void start()
    {
        if(techThread == null)
        {
            techThread = new Thread(this.priceTechImpl);
            techThread.start();
        }

        priceTechImpl.startDelivery();
    }

    /**
     * Stop the underlying tech
     */
    public void stop()
    {
        priceTechImpl.stopDelivery();
    }

    /**
     * Is the underlying tech actively running
     * @return isRunning
     */
    public boolean isRunning()
    {
        return priceTechImpl.isRunning();
    }

    /**
     * Set the consumer event listener
     * @param eventListener
     */
    public void setListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    /**
     * Callback method from the underlying tech.
     * On invocation this method creates a flowEvent from the tech specific
     * message and invokes the event listener.
     */
    public void onPrice(PriceTechMessage message)
    {
        String uniqueId = message.getIdentifier() + "_" + message.getDate().getTime();
        FlowEvent<?> flowEvent = flowEventFactory.newEvent(uniqueId, message);
        this.eventListener.invoke(flowEvent);
    }

}
