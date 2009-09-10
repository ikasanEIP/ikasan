/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.component;

import java.io.Serializable;

/**
 * ScheduleInfo class.
 * @deprecated - This has been deprecated TODO replace with?
 * 
 * @author Ikasan Development Team
 */
@Deprecated
public class ScheduleInfo
    implements Serializable
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** scheduled by a rollback/retry operation */
    public static final ScheduleInfo UNDEFINED_TASK = 
        new ScheduleInfo(new Integer(0), "Undefined schedule info.");
    
    /** scheduled by a rollback/retry operation */
    public static final ScheduleInfo ROLLBACK_RETRY_TASK =
        new ScheduleInfo(new Integer(1), "Scheduled within a rollback/retry.");
    
    /** scheduled by normal onTimeout operation */
    public static final ScheduleInfo ONTIMEOUT_TASK =
        new ScheduleInfo(new Integer(2), "Scheduled within an onTimeout.");
    
    /** schedule cause code */
    private Integer id;
    
    /** schedule description */
    private String description;
    
    /** count */
    private int count;

    /**
     * Default constructor
     * Creates a new instance of <code>ScheduleInfo</code>.
     * 
     * @param id 
     * @param description 
     */
    public ScheduleInfo(final Integer id, final String description)
    {
        this.id = id;
        this.description = description;
    }
}
