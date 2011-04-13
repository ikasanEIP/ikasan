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