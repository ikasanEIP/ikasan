/*
 * $Id: Priority.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/component/Priority.java $
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

/**
 * DeliveryProperties allows indirect setting of properties on the JMS.
 * 
 * @author Jeff Mitchell
 */
public enum Priority
{
    /** Urgent Priority */
    URGENT(9, "Urgent - systems will die if this doesnt get there!"),
    /** High Priority */
    HIGH(7,   "High - really need to get this delivered"),
    /** Normal Priority */
    NORMAL(4, "Normal - default delivery priority"),
    /** Low Priority */
    LOW(2,    "Low - deliver when you have a moment"),
    /** Lowest Priority */
    LOWEST(0, "Lowest - whenever!");
    
    /** JMS mapped priority */
    private final int    level;
    
    /** priority description */
    private final String description;

    /**
     * Creates a new instance of <code>Priority</code>
     * with the specified level and description.
     * 
     * @param level 
     * @param description 
     */
    private Priority(final int level, String description)
    {
        this.level = level;
        this.description = description;
    }
    
    /**
     * Getter for Level
     * 
     * @return int
     */
    public int getLevel()
    {
        return this.level;
    }
    
    /**
     * Getter for Description
     * 
     * @return int
     */
    public String getDescription()
    {
        return this.description;
    }
    
}
