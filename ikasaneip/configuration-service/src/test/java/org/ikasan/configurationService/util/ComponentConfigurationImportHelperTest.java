package org.ikasan.configurationService.util;

import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.service.ConfiguredResourceConfigurationService;
import org.ikasan.configurationService.service.SampleConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ikasan Development Team on 25/12/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/configuration-service-conf.xml",
        "/hsqldb-datasource-conf.xml",
        "/substitute-components.xml"
})
public class ComponentConfigurationImportHelperTest
{
    private String configXml = "<?xml version=\"1.0\"?><componentConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"schemaLocation\">" +
            "<id>configureResourceId</id><description></description><parameters><booleanParameter>" +
            "<name>booleanParam</name><value>true</value><description></description></booleanParameter>" +
            "<integerParameter><name>intParam</name><value>1</value><description></description>" +
            "</integerParameter><listParameter><name>list</name><description></description>" +
            "<value>value</value></listParameter><longParameter><name>longParam</name><value>1</value>" +
            "<description></description></longParameter><mapParameter><name>map</name><description>" +
            "</description><item><name>key</name><value>updated value</value></item></mapParameter>" +
            "<maskedStringParameter><name>maskedString</name><value>masked</value><description>" +
            "</description></maskedStringParameter><stringParameter><name>string</name><value>string</value>" +
            "<description></description></stringParameter></parameters></componentConfiguration>";

    private String configXmlNoConfiguredResourceId = "<?xml version=\"1.0\"?><componentConfiguration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"schemaLocation\">" +
            "<id></id><description></description><parameters><booleanParameter>" +
            "<name>booleanParam</name><value>true</value><description></description></booleanParameter>" +
            "<integerParameter><name>intParam</name><value>1</value><description></description>" +
            "</integerParameter><listParameter><name>list</name><description></description>" +
            "<value>value</value></listParameter><longParameter><name>longParam</name><value>1</value>" +
            "<description></description></longParameter><mapParameter><name>map</name><description>" +
            "</description><item><name>key</name><value>updated value</value></item></mapParameter>" +
            "<maskedStringParameter><name>maskedString</name><value>masked</value><description>" +
            "</description></maskedStringParameter><stringParameter><name>string</name><value>string</value>" +
            "<description></description></stringParameter></parameters></componentConfiguration>";
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    SampleConfiguredResource configuredResource = new SampleConfiguredResource();

    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfiguredResourceConfigurationService configurationService;

    @Resource
    ComponentConfigurationImportHelper helper;

    @Test
    public void test() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        List<String> list = new ArrayList();
        list.add("value1");
        list.add("value2");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);
        sampleConfiguration.setList(list);
        sampleConfiguration.setBooleanParam(false);
        sampleConfiguration.setLongParam(1000L);
        sampleConfiguration.setIntParam(100);
        sampleConfiguration.setMaskedString("some string!");

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);

        this.configurationService.saveConfiguration(configuration);

        this.helper.updateComponentConfiguration(configuration, configXml.getBytes());

        this.configurationService.saveConfiguration(configuration);

        this.configurationService.configure(this.configuredResource);

        Assert.assertEquals("Map item must be updated", this.configuredResource.getConfiguration().getMap().get("key"), "updated value");
        Assert.assertEquals("List size must equal!", this.configuredResource.getConfiguration().getList().size(), 1);
        Assert.assertEquals("List.get(0) value must equal!", this.configuredResource.getConfiguration().getList().get(0), "value");
        Assert.assertEquals("Boolean value must equal", this.configuredResource.getConfiguration().getBooleanParam(), true);
        Assert.assertEquals("Long value must equal", this.configuredResource.getConfiguration().getLongParam(), new Long(1L));
        Assert.assertEquals("Bollean value must equal", this.configuredResource.getConfiguration().getIntParam(), new Integer(1));
        Assert.assertEquals("Bollean value must equal", this.configuredResource.getConfiguration().getMaskedString(), "masked");
    }

    @Test (expected = RuntimeException.class)
    public void test_exception_no_configured_resource_id() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);

        this.configurationService.saveConfiguration(configuration);

        this.helper.updateComponentConfiguration(configuration, this.configXmlNoConfiguredResourceId.getBytes());
    }

    private class SampleConfiguredResource implements ConfiguredResource<ImportExportConfiguration>
    {
        private ImportExportConfiguration configuration;
        private String configuredResourceId;

        @Override
        public ImportExportConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        public void setConfiguration(ImportExportConfiguration configuration) {
            this.configuration = configuration;
        }

        @Override
        public String getConfiguredResourceId() {
            return this.configuredResourceId;
        }

        @Override
        public void setConfiguredResourceId(String id) {
            this.configuredResourceId = id;
        }
    }
}
