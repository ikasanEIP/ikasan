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
package org.ikasan.framework.module;

import java.util.List;
import java.util.Map;

import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.Initiator;

/**
 * The concept formerly known as a component group<br>
 * TODO Think of a better definition than this!!
 * 
 * @author Ikasan Development Team
 */
public interface Module
{

    /**
     * Returns the name of the module
     * 
     * @return name of the module
     */
    public String getName();

    /**
     * Resolves an <code>Initiator</code>
     * 
     * @param initiatorName name of <code>Initiator</code>
     * 
     * @return The Initiator
     */
    public Initiator getInitiator(String initiatorName);

    /**
     * Returns all of this module's <code>Initiator</code>s
     * 
     * @return List of Initiator
     */
    public List<Initiator> getInitiators();

    /**
     * Returns a Map of this module's <code>Flow</code>s
     * 
     * @return Map of Flows keyed by flowName
     */
    public Map<String, Flow> getFlows();

    /**
     * Returns a human readable description of this module
     * 
     * @return String description
     */
    public String getDescription();
}
