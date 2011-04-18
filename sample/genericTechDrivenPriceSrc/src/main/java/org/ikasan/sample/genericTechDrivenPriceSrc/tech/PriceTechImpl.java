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
package org.ikasan.sample.genericTechDrivenPriceSrc.tech;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This tech demonstrates a stubbed price tech implementation as
 * such would be provided by any system/application API for the 
 * provision of messages from an inbound feed.
 * 
 * @author Ikasan Development Team
 */
public class PriceTechImpl implements Runnable
{
    /** registered listener on the tech */
    private PriceTechListener priceTechListener;

    /** price generator */
    private Random randomGenerator = new Random();
    
    /** time between checking to see if delivery is active */
    private long loop = 1000;
    
    /** pause time between price ticks */
    private long delay = 0;
    
    /** thread controller */
    boolean active = true;

    /** price delivery controller */
    boolean deliver = false;

    /** identifiers */
    private List<String> identifiers = new ArrayList<String>();
    {
        identifiers.add("abc");
        identifiers.add("def");
        identifiers.add("ghi");
        identifiers.add("jkl");
        identifiers.add("mno");
    }
    
    /**
     * Setter for the tech listener.
     * @param priceTechListener
     */
    public void setListener(PriceTechListener priceTechListener)
    {
        this.priceTechListener = priceTechListener;
    }

    public void setDelay(long delay)
    {
        this.delay = delay;
    }
    
    /**
     * Separate thread for invoking the registered listener with a 
     * tech data message.
     */
    public void run()
    {
        while(active)
        {
            try
            {
                while(deliver)
                {
                    int selectedIdentifier = randomGenerator.nextInt(5);
                    int bid = randomGenerator.nextInt();
                    int spread = randomGenerator.nextInt(10);
                    PriceTechMessage price = new PriceTechMessage(identifiers.get(selectedIdentifier), bid, spread);
                    this.priceTechListener.onPrice(price);
                    
                    pause(delay);
                }
    
                Thread.sleep(loop);
            }
            catch (InterruptedException e)
            {
                // dont care
            }
        }
    }
    
    public void startDelivery()
    {
        this.deliver = true;
    }

    public void stopDelivery()
    {
        this.deliver = false;
    }

    public boolean isRunning()
    {
        return this.deliver;
    }
    
    public void shutdown()
    {
        stopDelivery();
        this.active = false;
    }
    
    private void pause(long period)
    {
        try
        {
            Thread.sleep(period);
        }
        catch(InterruptedException e)
        {
            // dont care
        }
    }
}