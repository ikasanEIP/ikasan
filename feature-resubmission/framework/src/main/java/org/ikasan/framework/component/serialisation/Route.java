/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.framework.component.serialisation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ikasan Development Team
 * 
 */
public class Route
{
    /**Source system name */
    private String sourceSystem;
    /** Target systems list */
    private List<TargetSystem> targetSystemsList = new ArrayList<TargetSystem>();

    /**
     * Default constructor.
     */
    protected Route()
    {
        // Does nothing.
    }

    /**
     * Constructor.
     * @param sourceSystem 
     * @param targetSystems
     */
    public Route(String sourceSystem, List<TargetSystem> targetSystems)
    {
        this.sourceSystem = sourceSystem;
        this.targetSystemsList = targetSystems;
    }
    
    // Setters
    /**
     * @param targetSystems
     */
    public void setTargetSystemsList(List<TargetSystem> targetSystems)
    {
        this.targetSystemsList = targetSystems;
    }

    /**
     * @param sourceSystem
     */
    public void setSourceSystem(String sourceSystem)
    {
        this.sourceSystem = sourceSystem;
    }
    
    // Getters
    /**
     * @return targetSystems
     */
    public List<TargetSystem> getTargetSystems()
    {
        return this.targetSystemsList;
    }
    
    /**
     * @return The source system
     */
    public String getSourceSystem()
    {
        return this.sourceSystem;
    }
    
    /**
     * Add a target system to the existing list.
     * @param targetSystem
     */
    public void addTargetSystem(TargetSystem targetSystem)
    {
        this.targetSystemsList.add(targetSystem);
    }
    
    /**
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Source System: ");
        sb.append(this.sourceSystem);
        sb.append("\nTargetSystems: ");
        sb.append(this.targetSystemsList);
        sb.append('\n');
        return sb.toString();
    }
}
