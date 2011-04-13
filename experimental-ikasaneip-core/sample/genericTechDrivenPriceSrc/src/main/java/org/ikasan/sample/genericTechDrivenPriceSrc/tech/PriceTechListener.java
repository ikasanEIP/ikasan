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
 * This demonstrates a stubbed tech listener as
 * such would be utilised to listen to any system/application API.
 * 
 * @author Ikasan Development Team
 */
public interface PriceTechListener
{
    public void onPrice(PriceTechMessage message);    
}
