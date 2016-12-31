package org.ikasan.configurationService.util;

import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
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
public class ComponentConfigurationExportHelperTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Resource
    ComponentConfigurationExportHelper helper;

    ConfiguredResource configuredResource = new SampleConfiguredResource();

    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    @Test
    public void test()
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        List<String> list = new ArrayList<String>();
        list.add("value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);
        sampleConfiguration.setList(list);
        sampleConfiguration.setBooleanParam(true);
        sampleConfiguration.setLongParam(1L);
        sampleConfiguration.setIntParam(1);
        sampleConfiguration.setString("string");
        sampleConfiguration.setMaskedString("masked");

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);

        String xml = this.helper.getComponentConfigurationExportXml(configuration);

        System.out.println(xml);
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
