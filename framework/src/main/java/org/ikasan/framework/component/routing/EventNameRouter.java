/* 
 * $Id: EventNameRouter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/routing/EventNameRouter.java $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>Router</code> that routes <code>Event<code>s to a single configured path based on Event.getName()
 * 
 * If there is no configured path for the Event name, then a single default result is returned
 * 
 * @author Ikasan Development Team
 */
public class EventNameRouter extends SingleResultRouter
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(EventNameRouter.class);

    /** Mapping of expected event names regular expression, to the result that should be returned if that name is matched */
    private Map<String, String> eventNamesRegexToResults = new HashMap<String, String>();

    /** Flag, if set to true, will allow default result to be returned on non matches, otherwise exception will be thrown */
    private boolean returnsDefaultForNonMatches = false;

    /**
     * Constructor
     * 
     * @param eventNamesToResults -Mapping of expected event names, to the result that should be returned if that name
     *            is matched
     * @param returnsDefaultForNonMatches - flag, if set to true, will allow default result to be returned on non
     *            matches, otherwise exception will be thrown
     */
    public EventNameRouter(Map<String, String> eventNamesToResults, boolean returnsDefaultForNonMatches)
    {
        super();
        if (eventNamesToResults != null)
        {
            this.eventNamesRegexToResults.putAll(eventNamesToResults);
        }
        this.returnsDefaultForNonMatches = returnsDefaultForNonMatches;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.routing.SingleResultRouter#evaluate(org .ikasan.framework.component.Event)
     */
    @Override
    protected String evaluate(Event event) throws RouterException
    {
        String result = null;
        String eventName = event.getName();
        Iterator<Entry<String, String>> iterator = this.eventNamesRegexToResults.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<String, String> eventNameRegexToResultEntry = iterator.next();
            String eventNameRegex = eventNameRegexToResultEntry.getKey();
            Pattern pattern = Pattern.compile(eventNameRegex);
            Matcher matcher = pattern.matcher(eventName);
            if (matcher.matches())
            {
                result = eventNameRegexToResultEntry.getValue();
            }
        }
        if (result == null)
        {
            if (this.returnsDefaultForNonMatches)
            {
                result = Router.DEFAULT_RESULT;
            }
            else
            {
                throw new UnroutableEventException("Event with name [" + eventName + "] is not supported");
            }
        }
        logger.info("evaluated Event [" + event.idToString() + "], obtained result [" + result + "]");
        return result;
    }
}
