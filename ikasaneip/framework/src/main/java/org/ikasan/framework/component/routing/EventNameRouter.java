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
