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
package org.ikasan.sample.genericTechDrivenPriceSrc.component.endpoint;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;

/**
 * Implementation of a producer which simply logs the incoming
 * StribngBuilder payload content.
 * 
 * @author Ikasan Development Team
 */
public class PriceProducer implements Producer<StringBuilder>
{
    /** Logger instance */
    private Logger logger = Logger.getLogger(PriceProducer.class);

    /** invocation count */
    private int invocationCount = 0;
    
    public void invoke(StringBuilder payload) throws EndpointException 
    {
        logger.info("Producer invoked with [" + payload + "]");
        
        // TODO - remove me
        System.out.println("[" + invocationCount++ + "] Producer invoked with [" + payload + "]");
    }
}
