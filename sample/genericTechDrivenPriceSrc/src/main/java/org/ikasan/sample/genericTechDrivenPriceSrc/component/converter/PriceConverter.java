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
package org.ikasan.sample.genericTechDrivenPriceSrc.component.converter;

import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechImpl;
import org.ikasan.sample.genericTechDrivenPriceSrc.tech.PriceTechMessage;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;

/**
 * This test class supports the <code>Translator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class PriceConverter implements Converter<PriceTechMessage,StringBuilder>
{
    public StringBuilder convert(PriceTechMessage price) throws TransformationException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("identifier = ");
        sb.append(price.getIdentifier());
        sb.append(" bid = ");
        sb.append(price.getBid());
        sb.append(" spread = ");
        sb.append(price.getSpread());
        sb.append(" at = ");
        sb.append(price.getTime());
        
        return sb;
    }
}
