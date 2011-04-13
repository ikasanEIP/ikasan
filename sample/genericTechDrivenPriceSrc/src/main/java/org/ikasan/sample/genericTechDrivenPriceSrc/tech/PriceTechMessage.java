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

/**
 * This demonstrates a stubbed tech data model as
 * such would be utilised and transmitted by any system/application API.
 * 
 * @author Ikasan Development Team
 */
public class PriceTechMessage
{
    private long time;
    private String identifier;
    private int bid;
    private int spread;
    
    public PriceTechMessage(String identifier, int bid, int spread)
    {
        this.identifier = identifier;
        this.bid = bid;
        this.spread = spread;
        this.time = System.currentTimeMillis();
    }
    
    public long getTime()
    {
        return time;
    }
    public String getIdentifier()
    {
        return identifier;
    }
    public int getBid()
    {
        return bid;
    }
    public int getSpread()
    {
        return spread;
    }

}