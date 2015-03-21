/*
 * $Id: MappingConfigurationDocumentHelperTest.java 40647 2014-11-07 11:05:05Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/test/java/com/mapping/configuration/ui/util/MappingConfigurationDocumentHelperTest.java $
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

import org.ikasan.dashboard.ui.mappingconfiguration.model.MappingConfigurationValue;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationDocumentHelper;
import org.ikasan.mapping.model.MappingConfiguration;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationDocumentHelperTest
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
    public void test_get_mapping_configuration_success() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        MappingConfigurationDocumentHelper underTest = new MappingConfigurationDocumentHelper();
        MappingConfiguration mappingConfiguration = underTest.getMappingConfiguration(MAPPING_CONFIGURATION_XML_1.getBytes());

        Assert.assertNotNull(mappingConfiguration);

        Assert.assertEquals("This configuration manages the mapping between the R2R context and " +
        		"the Bloomberg TOMS UUID for MHSA to MHI Voice Trades.",mappingConfiguration.getDescription());
        Assert.assertEquals("r2r-mhsa2Mhi-voiceTrade", mappingConfiguration.getSourceContext().getName());
        Assert.assertEquals("blbgToms-mhiCmfTgt", mappingConfiguration.getTargetContext().getName());
    }

    @Test
    public void test_get_mapping_configuration_values_success() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        MappingConfigurationDocumentHelper underTest = new MappingConfigurationDocumentHelper();
        List<MappingConfigurationValue> mappingConfigurationValues = underTest.getMappingConfigurationValues(MAPPING_CONFIGURATION_XML_1.getBytes());

        Assert.assertNotNull(mappingConfigurationValues);

        Assert.assertEquals(mappingConfigurationValues.size(), 4);
    }
}
