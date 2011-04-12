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
package org.ikasan.sample.scheduleDrivenPriceSrc.component.converter;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.quartz.JobExecutionContext;

/**
 * This test class supports the <code>Translator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduleEventConverter implements Converter<JobExecutionContext,StringBuilder>
{
    public StringBuilder convert(JobExecutionContext context) throws TransformationException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("schedule executed at = ");
        sb.append(context.getFireTime());
        sb.append(" name = ");
        sb.append(context.getJobDetail().getName());
        return sb;
    }
}
