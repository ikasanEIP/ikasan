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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ikasan Development Team
 */
public class SourceTargetEventRouting
{
    /**Route list*/
    private List<Route> routingList = new ArrayList<Route>();
    /**
     * 
     */
    private Map<String, List<TargetSystem>> router = new HashMap<String, List<TargetSystem>>();
    
    /**
     * Default constructor.
     */
    protected SourceTargetEventRouting()
    {
        // Does nothing.
    }

    /**
     * Constructor.
     * @param routingList
     */
    public SourceTargetEventRouting(List<Route> routingList)
    {
        this.routingList = routingList;
    }
    
    // Setters
    /**
     * @param routingList
     */
    public void setTargetSystemsList(List<Route> routingList)
    {
        this.routingList = routingList;
    }

    // Getters
    /**
     * @return targetSystems
     */
    public List<Route> getRoutingList()
    {
        return this.routingList;
    }
    
    /**
     * Add a target system to the existing list.
     * @param route
     */
    public void addRoute(Route route)
    {
        this.routingList.add(route);
    }
    
    /**
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Routes:\n");
        sb.append(this.routingList);
        sb.append('\n');
        return sb.toString();
    }
    
    /**
     * @param source
     * @param list
     */
    public void addRouteMap(String source, List<TargetSystem> list)
    {
        this.router.put(source, list);
    }
}
