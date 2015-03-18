/*
 * $Id: XmlFormatterTest.java 40647 2014-11-07 11:05:05Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/test/java/com/mapping/configuration/ui/util/XmlFormatterTest.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package com.mapping.configuration.ui.util;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.ikasan.dashboard.ui.framework.util.XmlFormatter;
import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration;

/**
 * @author CMI2 Development Team
 *
 */
public class XmlFormatterTest
{
    private static final String MAPPING_CONFIGURATION_XML_1 = "<?xml version=\"1.0\"?><mappingConfiguration><client>CMI2</client>" +
            "<type>R2R Context to UUID Mapping</type><sourceContext>r2r-mhsa2Mhi-voiceTrade</sourceContext><targetContext>blbgToms-mhiCmfTgt</targetContext>" +
            "<description>This configuration manages the mapping between the R2R context and the Bloomberg TOMS UUID for MHSA to MHI Voice Trades." +
            "</description><numberOfSourceParams>1</numberOfSourceParams><sourceConfigurationValueQueries><sourceConfigurationValueQuery>" +
            "/cmfTrade/additionalData[@NAME='context']</sourceConfigurationValueQuery></sourceConfigurationValueQueries><mappingConfigurationValues>" +
            "<mappingConfigurationValue><sourceConfigurationValues><sourceConfigurationValue>V1</sourceConfigurationValue></sourceConfigurationValues>" +
            "<targetConfigurationValue>0</targetConfigurationValue></mappingConfigurationValue><mappingConfigurationValue><sourceConfigurationValues>" +
            "<sourceConfigurationValue>V2</sourceConfigurationValue></sourceConfigurationValues><targetConfigurationValue>0</targetConfigurationValue>" +
            "</mappingConfigurationValue><mappingConfigurationValue><sourceConfigurationValues><sourceConfigurationValue>V3</sourceConfigurationValue>" +
            "</sourceConfigurationValues><targetConfigurationValue>0</targetConfigurationValue></mappingConfigurationValue><mappingConfigurationValue>" +
            "<sourceConfigurationValues><sourceConfigurationValue>V4</sourceConfigurationValue></sourceConfigurationValues><targetConfigurationValue>" +
            "0</targetConfigurationValue></mappingConfigurationValue></mappingConfigurationValues></mappingConfiguration>";

    @Test
    public void test_format_success() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        String result = XmlFormatter.format(MAPPING_CONFIGURATION_XML_1);

        System.out.println(result);
    }

    
}
