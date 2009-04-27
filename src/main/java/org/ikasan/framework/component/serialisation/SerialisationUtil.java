/*
 * $Id: SerialisationUtil.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/serialisation/SerialisationUtil.java $
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