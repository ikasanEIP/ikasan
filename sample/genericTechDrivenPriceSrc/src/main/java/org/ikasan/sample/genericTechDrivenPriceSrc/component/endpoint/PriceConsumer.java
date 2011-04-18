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
package org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint;

import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechListener;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechMessage;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Implementation of a consumer which manages the tech and 
 * receives messages via the tech listener.
 *
 * @author Ikasan Development Team
 */
public class PriceConsumer implements Consumer<EventListener>, PriceTechListener
{
    /** consumer managed stubbed tech */
    private PriceTechImpl priceTechImpl;

    /** consumer event factory */
    private EventFactory<FlowEvent<?,?>> flowEventFactory;

    /** consumer event listener */
    private EventListener eventListener;

    /** simulate tech on a separate thread */
    private Thread techThread;

    /**
     * Constructor
     * @param stubbedTechImpl
     * @param flowEventFactory
     */
    public PriceConsumer(PriceTechImpl priceTechImpl, EventFactory<FlowEvent<?,?>> flowEventFactory)
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
        String uniqueId = message.getIdentifier() + "_" + message.getTime();
        FlowEvent<?,?> flowEvent = flowEventFactory.newEvent(uniqueId, message);
        this.eventListener.invoke(flowEvent);
    }

}
