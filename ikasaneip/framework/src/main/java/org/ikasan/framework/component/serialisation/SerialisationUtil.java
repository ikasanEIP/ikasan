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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 * @author Ikasan Development Team
 */
public class SerialisationUtil {
    
    /** */
    private static Map<String, List<TargetSystem>> routingMap = new HashMap<String, List<TargetSystem>>();
    
    /** */
    private static XStream xstreamFromXml = new XStream(new DomDriver()); 
    
    /** */
    private static XStream xstreamToXml = new XStream(new XppDriver(new XmlFriendlyReplacer("$", "_")));
    
    /** */
    private static Logger logger = Logger.getLogger(SerialisationUtil.class);
    
    /**
     * 
     */
    static
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("source-target-event-routing.xml");
        setXsteamProps(xstreamFromXml);
        setXsteamProps(xstreamToXml);
        
        SourceTargetEventRouting ster = (SourceTargetEventRouting) xstreamFromXml.fromXML(is);
        for (Route route : ster.getRoutingList())
        {
            routingMap.put(route.getSourceSystem(), route.getTargetSystems());
        }
    }
    
    /**
     * Constructor
     */
    public SerialisationUtil ()
    {
        // Do Nothing
    }
    
    /**
     * @param sourceSystem
     * 
     * @return List of target systems
     */
    public List<TargetSystem> getTargetSystemsAsList (String sourceSystem)
    {
        logger.info("Source system = [" + sourceSystem + "]");
        List<TargetSystem> list = routingMap.get(sourceSystem);
        logger.info("The list = [" + list + "].");
        return list;
    }
    
    /**
     * @param sourceSystem
     * 
     * @return list of target systems as XML string
     */
    public String getTargetSystemsAsXmlString (String sourceSystem)
    {
        List<TargetSystem> targetSystemsAsList = this.getTargetSystemsAsList(sourceSystem);
        String xml = xstreamToXml.toXML(targetSystemsAsList);
        logger.info("Returning target systems as xml string = [" + xml + "].");
        return xml;
    }
    
    /**
     * @param targetSystem
     * 
     * @return single target as XML string
     */
    public String getSingleTargetSystemXmlString (TargetSystem targetSystem)
    {
        return xstreamToXml.toXML(targetSystem);
    }
    
    /**
     * @param targetSystemXmlString
     * 
     * @return A single TargetSystem
     */
    public TargetSystem getSingleTargetSystem(String targetSystemXmlString)
    {
        return (TargetSystem)xstreamFromXml.fromXML(targetSystemXmlString);
    }
    
    /**
     * @param xstream
     */
    private static void setXsteamProps(XStream xstream)
    {
        //Registering converters
        xstream.registerConverter(new SourceTargetEventRoutingConverter());
        xstream.registerConverter(new RouteConverter());
        xstream.registerConverter(new TargetSystemConverter());
        
        //Setting class aliases
        xstream.alias("source-target-event-routing", SourceTargetEventRouting.class);
        xstream.alias("route", Route.class);
        xstream.alias("TargetSystem", TargetSystem.class);
    }
    
    /**
     * Get the routing map
     * 
     * @return A map of Lists of Target Systems
     */
    protected Map<String, List<TargetSystem>> getRoutingMap()
    {
        return routingMap;
    }
}