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
package org.ikasan.framework.component.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>Router</code> that routes <code>Event<code>s to a single configured path based on Event.getSrcSystem()
 * 
 * If there is no configured path for the Event name, then a single default result is returned
 * 
 * @author Ikasan Development Team
 */
public class EventSourceSystemRouter implements Router
{
    /** result to return if event name is unmatched */
    public static final String DEFAULT_RESULT = "default";

    /** Logger instance */
    private Logger logger = Logger.getLogger(EventSourceSystemRouter.class);

    /** Mapping of expected source systems, to the result that should be returned if that sourceSystem is matched */
    private Map<String, List<String>> targetsToSrcSystems = new HashMap<String, List<String>>();

    /** Lag, if set to true, will allow default result to be returned on non matches, otherwise exception will be thrown */
    private boolean returnsDefaultForNonMatches = false;

    /**
     * Constructor
     * 
     * @param targetsToSrcSystems -Mapping of targets to a List of all Src Systems that should be router
     * @param returnsDefaultForNonMatches - flag, if set to true, will allow default result to be returned on non
     *            matches, otherwise exception will be thrown
     */
    public EventSourceSystemRouter(Map<String, List<String>> targetsToSrcSystems, boolean returnsDefaultForNonMatches)
    {
        super();
        if (targetsToSrcSystems != null)
        {
            this.targetsToSrcSystems.putAll(targetsToSrcSystems);
        }
        this.returnsDefaultForNonMatches = returnsDefaultForNonMatches;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.routing.SingleResultRouter#onEvent(org.ikasan.framework.component.Event)
     */
    public List<String> onEvent(Event event) throws RouterException
    {
        List<String> result = new ArrayList<String>();
        String eventSourceSystem = event.getSrcSystem();
        logger.debug("got eventSourceSystem [" + eventSourceSystem + "] targetsToSrcSystems [" + targetsToSrcSystems
                + "]");
        for (String target : targetsToSrcSystems.keySet())
        {
            if (targetsToSrcSystems.get(target).contains(eventSourceSystem))
            {
                result.add(target);
            }
        }
        if (result.size() == 0)
        {
            if (returnsDefaultForNonMatches)
            {
                result.add(DEFAULT_RESULT);
            }
            else
            {
                throw new UnroutableEventException("Event with srcSystem [" + eventSourceSystem + "] is not supported");
            }
        }
        logger.info("evaluated Event [" + event.idToString() + "], obtained result [" + result + "]");
        return result;
    }
}
