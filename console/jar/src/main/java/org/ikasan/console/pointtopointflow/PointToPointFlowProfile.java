/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */
package org.ikasan.console.pointtopointflow;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Object that represents a PointToPointFlowProfile in Ikasan.
 * 
 * A PointToPointFlowProfile can be see as owning a list of PointToPointFlows.
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowProfile
{
    
    /** Unique Id */
    private long id;
    
    /** The name for this PointToPointFlow */
    private String name;

    /** The set of PointToPointFlow objects that make up this profile */
    private Set<PointToPointFlow> pointToPointFlows = new LinkedHashSet<PointToPointFlow>();
    
    /**
     * Get the unique id
     * 
     * @return id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the unique id, private as it is only referenced by Hibernate and 
     * not actually used.
     * 
     * @param id - id to set
     */
    @SuppressWarnings("unused")
    private void setId(long id)
    {
        this.id = id;
    }
        
    /**
     * Get the set of PointToPointFlows
     * 
     * @return set of PointToPointFlows for this Profile
     */
    public Set<PointToPointFlow> getPointToPointFlows()
    {
        return pointToPointFlows;
    }

    /**
     * Set the PointToPointFlows for this profile
     * 
     * @param pointToPointFlows - PointToPointFlows for this profile
     */
    public void setPointToPointFlows(Set<PointToPointFlow> pointToPointFlows)
    {
        this.pointToPointFlows = pointToPointFlows;
    }

    /**
     * Get the name for this PointToPointFlow
     * 
     * @return The name for this PointToPointFlow
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name for this PointToPointFlow
     * 
     * @param name - The name for this PointToPointFlow
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
