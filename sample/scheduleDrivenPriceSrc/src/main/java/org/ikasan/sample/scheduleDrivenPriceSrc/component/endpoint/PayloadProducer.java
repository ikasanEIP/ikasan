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
package org.ikasan.sample.scheduleDrivenPriceSrc.component.endpoint;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;

/**
 * This class provides a stubbed example of a producer component.
 * 
 * @author Ikasan Development Team
 */
public class PayloadProducer implements Producer<StringBuilder>
{
    /** Logger instance */
    private Logger logger = Logger.getLogger(PayloadProducer.class);

    public void invoke(StringBuilder payload) throws EndpointException 
    {
        logger.info("Producer invoked with [" + payload + "]");
        System.out.println("Producer invoked with [" + payload + "]");
    }
}
