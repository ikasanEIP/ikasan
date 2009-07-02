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
package org.ikasan.framework.component.sequencing;

import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowComponent;

/**
 * Interface for all FlowComponents that perform a Sequencing function.
 * 
 * These are characterised by a variable number of <code>Event<code>s being routed downstream
 * based on the incoming <code>Event<code>.
 * 
 * @author Ikasan Development Team
 */
public interface Sequencer extends FlowComponent
{
    /**
     * Returns an ordered List<Event> for forwarding downstream
     * 
     * @param event The event to perform sequencing on
     * @param componentName 
     * @param moduleName 
     * @return List<Event> for forwarding downstream in order
     * @throws SequencerException Exception if we could not sequence
     */
    public List<Event> onEvent(Event event, String moduleName, String componentName) throws SequencerException;
}
