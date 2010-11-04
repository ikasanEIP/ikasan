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

import org.ikasan.console.module.Module;

/**
 * Object that represents a PointToPointFlow in Ikasan.  A PointToPointFlow 
 * consists of 2 linked modules, e.g. src --> target
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlow
{
    /** Unique Id */
    private long id;
   
    /** The profile for this PointToPointFlow */
    private PointToPointFlowProfile pointToPointFlowProfile;

    /** The from Module */
    Module fromModule = null;
    
    /** The to Module */
    Module toModule = null;

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
     * Get the Profile for this PointToPointFlow
     *  
     * @return The Profile for this PointToPointFlow
     */
    public PointToPointFlowProfile getPointToPointFlowProfile()
    {
        return pointToPointFlowProfile;
    }

    /**
     * Set the Profile for this PointToPointFlow
     *  
     * @param pointToPointFlowProfile - The Profile for this PointToPointFlow
     */
    public void setPointToPointFlowProfile(PointToPointFlowProfile pointToPointFlowProfile)
    {
        this.pointToPointFlowProfile = pointToPointFlowProfile;
    }
    
    /**
     * Set the to Module
     * 
     * @param toModule - toModule for this PointToPointFlow
     */
    public void setToModule(Module toModule)
    {
        this.toModule = toModule;
    }

    /**
     * Get the toModule for this PointToPointFlow
     * 
     * @return toModule for this PointToPointFlow
     */
    public Module getToModule()
    {
        return toModule;
    }

    /**
     * Set the from Module
     * 
     * @param fromModule - fromModule for this PointToPointFlow
     */
    public void setFromModule(Module fromModule)
    {
        this.fromModule = fromModule;
    }

    /**
     * Get the fromModule for this PointToPointFlow
     * 
     * @return fromModule for this PointToPointFlow
     */
    public Module getFromModule()
    {
        return fromModule;
    }
    
}
