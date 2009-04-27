/*
 * $Id: Route.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/serialisation/Route.java $
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
