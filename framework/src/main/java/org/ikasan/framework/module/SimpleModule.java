/* 
 * $Id: SimpleModule.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/module/SimpleModule.java $
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
package org.ikasan.framework.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.Initiator;

/**
 * A simple representation of a Module
 * @author Ikasan Development Team
 */
public class SimpleModule implements Module
{
    /** Initiators of flows within this module */
    private List<Initiator> initiators;

    /** Module name */
    protected String name;
    
    /** Human readable description of this module */
    private String description;

    /**
     * Constructor
     * 
     * @param name The name of the module
     * @param initiators A list of Initiators for the module
     */
    public SimpleModule(String name, List<Initiator> initiators)
    {
        this(name);
        this.initiators = new ArrayList<Initiator>(initiators);
    }

    /**
     * Constructor
     * 
     * @param name Name of the module
     */
    public SimpleModule(String name)
    {
        this.name = name;
    }

    /**
     * Accessor for name
     * 
     * @return module name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Resolve the initiator
     * 
     * @param initiatorName The name of the initiator
     * @return The resolve initiator
     */
    public Initiator getInitiator(String initiatorName)
    {

        Initiator initiator = null;
        for (Initiator thisInitiator : initiators)
        {
            if (thisInitiator.getName().equals(initiatorName))
            {
                initiator = thisInitiator;
                break;
            }
        }
        return initiator;
    }
    /**
     * @return the initiators
     */
    public List<Initiator> getInitiators()
    {
        return new ArrayList<Initiator>(initiators);
    }

    public Map<String, Flow> getFlows()
    {
        Map<String, Flow> result = new HashMap<String, Flow>();
        for (Initiator initiator : initiators)
        {
            Flow flow = initiator.getFlow();
            result.put(flow.getName(), flow);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.module.Module#getDescription()
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Setter for description
     * 
     * @param description
     */
    public void setDescription(String description){
        this.description = description;
    }
    
    /**
     * Required for ACL security
     * 
     * @return
     */
    public Long getId(){
        Long id = new Long(name.hashCode());
        return id;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer(getClass().getName()+" [");
        
        sb.append("name=");sb.append(name);sb.append(",");
        sb.append("description=");sb.append(description);sb.append(",");
        sb.append("initiators=");sb.append(initiators);

        sb.append("]");
        return sb.toString();
    
    }
}
