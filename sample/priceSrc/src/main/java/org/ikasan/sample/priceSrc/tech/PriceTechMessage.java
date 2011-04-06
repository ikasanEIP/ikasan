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
package org.ikasan.sample.priceSrc.tech;

import java.util.Date;

/**
 * This test demonstrates a stubbed tech data model as
 * such would be utilised and transmitted by any system/application API.
 * 
 * @author Ikasan Development Team
 */
public class PriceTechMessage
{
    private Date date;
    private String identifier;
    private int bid;
    private int spread;
    
    public PriceTechMessage(String identifier, int bid, int spread)
    {
        this.identifier = identifier;
        this.bid = bid;
        this.spread = spread;
        this.date = new Date();
    }
    
    public Date getDate()
    {
        return date;
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