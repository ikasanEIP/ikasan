/*
 * $Id: TriggerRelationship.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/event/model/TriggerRelationship.java $
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
package org.ikasan.framework.flow.event.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An enum listing the possible trigger relationships
 * @author Ikasan Development Team
 */
public enum TriggerRelationship
{
    /** Before the trigger */
    BEFORE("before"), 
    /** After the trigger */
    AFTER("after");
    
    /** The trigger relationship description */
    private String description;

    /**
     * Get the trigger relationship description
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Relationship lookup map
     */
    private static final Map<String, TriggerRelationship> lookup = new HashMap<String, TriggerRelationship>();
    static
    {
        for (TriggerRelationship relationship : EnumSet.allOf(TriggerRelationship.class))
        {
            lookup.put(relationship.getDescription(), relationship);
        }
    }

    /**
     * Constructor
     * @param description
     */
    private TriggerRelationship(String description)
    {
        this.description = description;
    }

    /**
     * Get the trigger relationship based off a description
     * @param description
     * @return a trigger relationship
     */
    public static TriggerRelationship get(String description)
    {
        return lookup.get(description.toLowerCase());
    }
}
