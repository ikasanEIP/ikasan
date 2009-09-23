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
