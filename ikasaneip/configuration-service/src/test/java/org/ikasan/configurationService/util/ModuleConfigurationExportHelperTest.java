package org.ikasan.configurationService.util;

import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.service.SampleConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
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
public class ModuleConfigurationExportHelperTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    ConfiguredResource configuredResource = new SampleConfiguredResource();

    @Resource
    ModuleConfigurationExportHelper helper;

    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    @Test
    public void test()
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);
        this.configurationService.saveConfiguration(configuration);

        Module module = new Module("name", "contextRoot", "description", "version", null, "diagramUrl");

        Flow flow = new Flow("Name", "Description", module);
        Component component = new Component();
        component.setConfigurable(true);
        component.setConfigurationId("configureResourceId");

        HashSet<Component> components = new HashSet<Component>();
        components.add(component);

        flow.setComponents(components);

        HashSet<Flow> flows = new HashSet<Flow>();
        flows.add(flow);

        module.setFlows(flows);

        String xml = this.helper.getModuleConfigurationExportXml(module);

        System.out.println(xml);
    }

    @Test
    public void test_with_null_flow()
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);
        this.configurationService.saveConfiguration(configuration);

        Module module = new Module("name", "contextRoot", "description", "version", null, "diagramUrl");

        Flow flow = new Flow("Name", "Description", module);
        Component component = new Component();
        component.setConfigurable(true);
        component.setConfigurationId("configureResourceId");

        HashSet<Component> components = new HashSet<Component>();
        components.add(component);

        flow.setComponents(components);

        HashSet<Flow> flows = new HashSet<Flow>();
        flows.add(flow);
        flows.add(null);

        module.setFlows(flows);

        String xml = this.helper.getModuleConfigurationExportXml(module);

        System.out.println(xml);
    }

    @Test
    public void test_with_empty_flows()
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);
        this.configurationService.saveConfiguration(configuration);

        Module module = new Module("name", "contextRoot", "description", "version", null, "diagramUrl");

        Flow flow = new Flow("Name", "Description", module);
        Component component = new Component();
        component.setConfigurable(true);
        component.setConfigurationId("configureResourceId");

        HashSet<Component> components = new HashSet<Component>();
        components.add(component);

        flow.setComponents(components);

        HashSet<Flow> flows = new HashSet<Flow>();

        module.setFlows(flows);

        String xml = this.helper.getModuleConfigurationExportXml(module);

        System.out.println(xml);
    }

    @Test
    public void test_with_null_flows()
    {
        Map<String, String> map = new HashMap();
        map.put("key", "value");

        ImportExportConfiguration sampleConfiguration = new ImportExportConfiguration();
        sampleConfiguration.setMap(map);

        this.configuredResource.setConfiguredResourceId("configureResourceId");
        this.configuredResource.setConfiguration(sampleConfiguration);

        Configuration configuration = this.configurationService.createConfiguration(this.configuredResource);
        this.configurationService.saveConfiguration(configuration);

        Module module = new Module("name", "contextRoot", "description", "version", null, "diagramUrl");

        Flow flow = new Flow("Name", "Description", module);
        Component component = new Component();
        component.setConfigurable(true);
        component.setConfigurationId("configureResourceId");

        HashSet<Component> components = new HashSet<Component>();
        components.add(component);

        flow.setComponents(components);

        module.setFlows(null);

        String xml = this.helper.getModuleConfigurationExportXml(module);

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
