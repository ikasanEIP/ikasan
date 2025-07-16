package org.ikasan.manifest;

import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataExtractor;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class JsonModuleManifestMetaDataProviderTest {

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};
    ConfigurationManagement configurationManagement = mockery.mock(ConfigurationManagement.class);
    ApplicationContext applicationContext = mockery.mock(ApplicationContext.class);

    private JsonModuleManifestMetaDataProvider provider;
    private String moduleManifestJson;

    @Before
    public void setUp() throws IOException {
        JsonFlowMetaDataProvider jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        JsonModuleMetaDataProvider jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(jsonFlowMetaDataProvider);
        JsonConfigurationMetaDataExtractor jsonConfigurationMetaDataExtractor = new JsonConfigurationMetaDataExtractor(this.configurationManagement);
        this.provider = new JsonModuleManifestMetaDataProvider(jsonModuleMetaDataProvider, jsonConfigurationMetaDataExtractor);
        this.provider.setApplicationContext(this.applicationContext);
        File file = new File("src/test/resources/data/sample-module-manifest.json");
        this.moduleManifestJson = Files.readString(file.toPath());
    }

    @Test
    public void test_deserialiseModuleManifest_with_all_fields() {
        ModuleManifestMetaData moduleManifestMetaData = provider.deserialiseModuleManifest(moduleManifestJson);

        Assert.assertNotNull(moduleManifestMetaData);

        // Assert ModuleMetaData
        Assert.assertNotNull(moduleManifestMetaData.getModuleMetaData());
        Assert.assertEquals("my-first-agent-module", moduleManifestMetaData.getModuleMetaData().getName());
        Assert.assertEquals("This is my first attempt to build Ikasan using an agent", moduleManifestMetaData.getModuleMetaData().getDescription());
        Assert.assertEquals("1.0.0-SNAPSHOT", moduleManifestMetaData.getModuleMetaData().getVersion());
        Assert.assertEquals("SCHEDULER_AGENT", moduleManifestMetaData.getModuleMetaData().getType().name());
        Assert.assertEquals("http://localhost:8080", moduleManifestMetaData.getModuleMetaData().getUrl());

        // Assert Flows
        Assert.assertNotNull(moduleManifestMetaData.getModuleMetaData().getFlows());
        Assert.assertEquals(2, moduleManifestMetaData.getModuleMetaData().getFlows().size());

        // Assert first flow
        Assert.assertEquals("JMS to Database Flow", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getName());
        Assert.assertEquals("JMS Consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getComponentName());
        Assert.assertEquals("consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getComponentType());
        Assert.assertEquals("org.ikasan.component.jms.JmsConsumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getImplementingClass());
        Assert.assertTrue(moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().isConfigurable());
        Assert.assertEquals("jms-consumer-config", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getConfigurationId());

        Assert.assertEquals(1, moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().size());
        Assert.assertEquals("Database Producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getComponentName());
        Assert.assertEquals("producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getComponentType());
        Assert.assertEquals("org.ikasan.agent.sample.DbProducer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getImplementingClass());
        Assert.assertTrue(moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).isConfigurable());
        Assert.assertEquals("db-producer-config", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getConfigurationId());

        Assert.assertEquals(1, moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().size());
        Assert.assertEquals("JMS Consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getFrom());
        Assert.assertEquals("Database Producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getTo());
        Assert.assertEquals("default", moduleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getName());

        // Assert second flow
        Assert.assertEquals("DB to JMS Flow", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getName());
        Assert.assertEquals("Database Consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getComponentName());
        Assert.assertEquals("consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getComponentType());
        Assert.assertEquals("org.ikasan.agent.demo.DBConsumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getImplementingClass());
        Assert.assertTrue(moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().isConfigurable());
        Assert.assertEquals("db-consumer-config", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getConfigurationId());

        Assert.assertEquals(1, moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().size());
        Assert.assertEquals("JMS Producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getComponentName());
        Assert.assertEquals("producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getComponentType());
        Assert.assertEquals("org.ikasan.component.jms.JmsProducer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getImplementingClass());
        Assert.assertTrue(moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).isConfigurable());
        Assert.assertEquals("jms-producer-config-db-flow", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getConfigurationId());

        Assert.assertEquals(1, moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().size());
        Assert.assertEquals("Database Consumer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getFrom());
        Assert.assertEquals("JMS Producer", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getTo());
        Assert.assertEquals("default", moduleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getName());

        // Assert ConfigurationMetaData
        Assert.assertNotNull(moduleManifestMetaData.getConfigurationMetaData());
        Assert.assertEquals(4, moduleManifestMetaData.getConfigurationMetaData().size());

        // Assert first configuration
        Assert.assertEquals("jms-consumer-config", moduleManifestMetaData.getConfigurationMetaData().get(0).getConfigurationId());
        Assert.assertEquals("Configuration for the JMS Consumer", moduleManifestMetaData.getConfigurationMetaData().get(0).getDescription());
        Assert.assertEquals("org.ikasan.component.jms.JmsConsumerConfiguration", moduleManifestMetaData.getConfigurationMetaData().get(0).getImplementingClass());
        Assert.assertEquals(13, ((List)moduleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).size());
        Assert.assertEquals("connectionFactoryName", ((ConfigurationParameterMetaData)((List)moduleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getName());
        Assert.assertEquals("${jms.consumer.connectionFactoryName}", ((ConfigurationParameterMetaData)((List)moduleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getValue());
        Assert.assertEquals("The name of the JMS ConnectionFactory bean to use.", ((ConfigurationParameterMetaData)((List)moduleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getDescription());
        Assert.assertEquals("java.lang.String", ((ConfigurationParameterMetaData)((List)moduleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getImplementingClass());

        // Assert DependencyManagementMetaData
        Assert.assertNotNull(moduleManifestMetaData.getDependencyManagement());
        Assert.assertNotNull(moduleManifestMetaData.getDependencyManagement().getRepositories());
        Assert.assertEquals(5, moduleManifestMetaData.getDependencyManagement().getRepositories().size());

        Assert.assertEquals("ikasaneip-snapshots", moduleManifestMetaData.getDependencyManagement().getRepositories().get(0).getId());
        Assert.assertEquals("http://oss.sonatype.org/content/repositories/snapshots/", moduleManifestMetaData.getDependencyManagement().getRepositories().get(0).getUrl());
        Assert.assertEquals("ikasaneip-releases", moduleManifestMetaData.getDependencyManagement().getRepositories().get(1).getId());
        Assert.assertEquals("http://oss.sonatype.org/content/repositories/releases/", moduleManifestMetaData.getDependencyManagement().getRepositories().get(1).getUrl());
        Assert.assertEquals("central", moduleManifestMetaData.getDependencyManagement().getRepositories().get(2).getId());
        Assert.assertEquals("https://repo1.maven.org/maven2", moduleManifestMetaData.getDependencyManagement().getRepositories().get(2).getUrl());
        Assert.assertEquals("ikasaneip-releases", moduleManifestMetaData.getDependencyManagement().getRepositories().get(3).getId());
        Assert.assertEquals("https://maven.repository.redhat.com/earlyaccess/all", moduleManifestMetaData.getDependencyManagement().getRepositories().get(3).getUrl());
        Assert.assertEquals("local-repository", moduleManifestMetaData.getDependencyManagement().getRepositories().get(4).getId());
        Assert.assertEquals("file:///Users/mick/.m2/repository/", moduleManifestMetaData.getDependencyManagement().getRepositories().get(4).getUrl());


        Assert.assertNotNull(moduleManifestMetaData.getDependencyManagement().getDependencies());
        Assert.assertEquals(1, moduleManifestMetaData.getDependencyManagement().getDependencies().size());

        Assert.assertEquals("org.ikasan", moduleManifestMetaData.getDependencyManagement().getDependencies().get(0).getGroup());
        Assert.assertEquals("ikasan-configuration-service", moduleManifestMetaData.getDependencyManagement().getDependencies().get(0).getArtefact());
        Assert.assertEquals("4.1.0", moduleManifestMetaData.getDependencyManagement().getDependencies().get(0).getVersion());
    }

    @Test
    public void test_serialiseModuleManifest() {
        ModuleManifestMetaData moduleManifestMetaData = provider.deserialiseModuleManifest(moduleManifestJson);
        String serialisedJson = provider.serialiseModuleManifest(moduleManifestMetaData);
        ModuleManifestMetaData deserialisedModuleManifestMetaData = provider.deserialiseModuleManifest(serialisedJson);

        // Assert ModuleMetaData
        Assert.assertNotNull(deserialisedModuleManifestMetaData.getModuleMetaData());
        Assert.assertEquals("my-first-agent-module", deserialisedModuleManifestMetaData.getModuleMetaData().getName());
        Assert.assertEquals("This is my first attempt to build Ikasan using an agent", deserialisedModuleManifestMetaData.getModuleMetaData().getDescription());
        Assert.assertEquals("1.0.0-SNAPSHOT", deserialisedModuleManifestMetaData.getModuleMetaData().getVersion());
        Assert.assertEquals("SCHEDULER_AGENT", deserialisedModuleManifestMetaData.getModuleMetaData().getType().name());
        Assert.assertEquals("http://localhost:8080", deserialisedModuleManifestMetaData.getModuleMetaData().getUrl());

        // Assert Flows
        Assert.assertNotNull(deserialisedModuleManifestMetaData.getModuleMetaData().getFlows());
        Assert.assertEquals(2, deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().size());

        // Assert first flow
        Assert.assertEquals("JMS to Database Flow", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getName());
        Assert.assertEquals("JMS Consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getComponentName());
        Assert.assertEquals("consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getComponentType());
        Assert.assertEquals("org.ikasan.component.jms.JmsConsumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getImplementingClass());
        Assert.assertTrue(deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().isConfigurable());
        Assert.assertEquals("jms-consumer-config", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getConsumer().getConfigurationId());

        Assert.assertEquals(1, deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().size());
        Assert.assertEquals("Database Producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getComponentName());
        Assert.assertEquals("producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getComponentType());
        Assert.assertEquals("org.ikasan.agent.sample.DbProducer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getImplementingClass());
        Assert.assertTrue(deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).isConfigurable());
        Assert.assertEquals("db-producer-config", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getFlowElements().get(0).getConfigurationId());

        Assert.assertEquals(1, deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().size());
        Assert.assertEquals("JMS Consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getFrom());
        Assert.assertEquals("Database Producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getTo());
        Assert.assertEquals("default", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(0).getTransitions().get(0).getName());

        // Assert second flow
        Assert.assertEquals("DB to JMS Flow", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getName());
        Assert.assertEquals("Database Consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getComponentName());
        Assert.assertEquals("consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getComponentType());
        Assert.assertEquals("org.ikasan.agent.demo.DBConsumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getImplementingClass());
        Assert.assertTrue(deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().isConfigurable());
        Assert.assertEquals("db-consumer-config", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getConsumer().getConfigurationId());

        Assert.assertEquals(1, deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().size());
        Assert.assertEquals("JMS Producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getComponentName());
        Assert.assertEquals("producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getComponentType());
        Assert.assertEquals("org.ikasan.component.jms.JmsProducer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getImplementingClass());
        Assert.assertTrue(deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).isConfigurable());
        Assert.assertEquals("jms-producer-config-db-flow", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getFlowElements().get(0).getConfigurationId());

        Assert.assertEquals(1, deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().size());
        Assert.assertEquals("Database Consumer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getFrom());
        Assert.assertEquals("JMS Producer", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getTo());
        Assert.assertEquals("default", deserialisedModuleManifestMetaData.getModuleMetaData().getFlows().get(1).getTransitions().get(0).getName());

        // Assert ConfigurationMetaData
        Assert.assertNotNull(deserialisedModuleManifestMetaData.getConfigurationMetaData());
        Assert.assertEquals(4, deserialisedModuleManifestMetaData.getConfigurationMetaData().size());

        // Assert first configuration
        Assert.assertEquals("jms-consumer-config", deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getConfigurationId());
        Assert.assertEquals("Configuration for the JMS Consumer", deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getDescription());
        Assert.assertEquals("org.ikasan.component.jms.JmsConsumerConfiguration", deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getImplementingClass());
        Assert.assertEquals(13, ((List)deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).size());
        Assert.assertEquals("connectionFactoryName", ((ConfigurationParameterMetaData)((List)deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getName());
        Assert.assertEquals("${jms.consumer.connectionFactoryName}", ((ConfigurationParameterMetaData)((List)deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getValue());
        Assert.assertEquals("The name of the JMS ConnectionFactory bean to use.", ((ConfigurationParameterMetaData)((List)deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getDescription());
        Assert.assertEquals("java.lang.String", ((ConfigurationParameterMetaData)((List)deserialisedModuleManifestMetaData.getConfigurationMetaData().get(0).getParameters()).get(0)).getImplementingClass());

        // Assert DependencyManagementMetaData
        Assert.assertNotNull(deserialisedModuleManifestMetaData.getDependencyManagement());
        Assert.assertNotNull(deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories());
        Assert.assertEquals(5, deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().size());

        Assert.assertEquals("ikasaneip-snapshots", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(0).getId());
        Assert.assertEquals("http://oss.sonatype.org/content/repositories/snapshots/", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(0).getUrl());
        Assert.assertEquals("ikasaneip-releases", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(1).getId());
        Assert.assertEquals("http://oss.sonatype.org/content/repositories/releases/", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(1).getUrl());
        Assert.assertEquals("central", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(2).getId());
        Assert.assertEquals("https://repo1.maven.org/maven2", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(2).getUrl());
        Assert.assertEquals("ikasaneip-releases", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(3).getId());
        Assert.assertEquals("https://maven.repository.redhat.com/earlyaccess/all", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(3).getUrl());
        Assert.assertEquals("local-repository", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(4).getId());
        Assert.assertEquals("file:///Users/mick/.m2/repository/", deserialisedModuleManifestMetaData.getDependencyManagement().getRepositories().get(4).getUrl());


        Assert.assertNotNull(deserialisedModuleManifestMetaData.getDependencyManagement().getDependencies());
        Assert.assertEquals(1, deserialisedModuleManifestMetaData.getDependencyManagement().getDependencies().size());

        Assert.assertEquals("org.ikasan", deserialisedModuleManifestMetaData.getDependencyManagement().getDependencies().get(0).getGroup());
        Assert.assertEquals("ikasan-configuration-service", deserialisedModuleManifestMetaData.getDependencyManagement().getDependencies().get(0).getArtefact());
        Assert.assertEquals("4.1.0", deserialisedModuleManifestMetaData.getDependencyManagement().getDependencies().get(0).getVersion());
    }
}