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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>Router</code> that routes <code>Event<code>s to a single configured path based on the value of a named attribute
 * on the <code>Event</code>s first <code>Payload</code>
 * 
 * If there is no configured path for the attribute value (or this is null), then a single default result is returned
 * 
 * @author Ikasan Development Team
 */
public class FirstPayloadAttributeRouter extends SingleResultRouter
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(FirstPayloadAttributeRouter.class);

    /** Mapping of expected attribute value regular expression, to the result that should be returned if that attribute value is matched */
    private Map<String, String> attributeValuesRegexToResults = new HashMap<String, String>();

    /** Flag, if set to true, will allow default result to be returned on non matches, otherwise exception will be thrown */
    private boolean returnsDefaultForNonMatches = false;
    
    /** Name of the <code>Payload</code> attribute upon whose value we wish to switch     */
    private String attributeName;

    /**
     * Constructor
     * 
     * @param attributeName - name of Payload attribute upon whose value we wish to switch
     * @param attributeValuesToResults -Mapping of expected attribute values, to the result that should be returned if that attribute value
     *            is matched
     * @param returnsDefaultForNonMatches - flag, if set to true, will allow default result to be returned on non
     *            matches, otherwise exception will be thrown
     */
    public FirstPayloadAttributeRouter(String attributeName, Map<String, String> attributeValuesToResults, boolean returnsDefaultForNonMatches)
    {
        super();
        if (attributeName==null||"".equals(attributeName)){
        	throw new IllegalArgumentException("non empty attributeName must be supplied on construction");
        }
        if (attributeValuesToResults != null)
        {
            this.attributeValuesRegexToResults.putAll(attributeValuesToResults);
        }
        this.attributeName=attributeName;
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
        
        Payload firstPayload = event.getPayloads().get(0);
        String payloadAttribute = firstPayload.getAttribute(attributeName);
        
        if (payloadAttribute!=null){
        	Iterator<Entry<String, String>> iterator = this.attributeValuesRegexToResults.entrySet().iterator();
	        while (iterator.hasNext())
	        {
	            Entry<String, String> attributeValueRegexToResultEntry = iterator.next();
	            String eventNameRegex = attributeValueRegexToResultEntry.getKey();
	            Pattern pattern = Pattern.compile(eventNameRegex);
	            Matcher matcher = pattern.matcher(payloadAttribute);
	            if (matcher.matches())
	            {
	                result = attributeValueRegexToResultEntry.getValue();
	            }
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
                throw new UnroutableEventException("Could not route Event based on first payload's ["+attributeName+"] attribute, which was ["+payloadAttribute+"]");
            }
        }
        logger.info("evaluated Event [" + event.idToString() + "], obtained result [" + result + "]");
        return result;
    }
}
