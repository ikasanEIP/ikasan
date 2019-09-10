package org.ikasan.dashboard.ui.visualisation.adapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.ikasan.dashboard.ui.visualisation.model.ConfigurationMetaDataImpl;
import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ModuleVisjsAdapterTest
{
    public static final String MODULE_JSON = "/data/graph/module.json";

    public static final String MODULE_ONE_JSON = "/data/graph/module-one.json";
    public static final String MODULE_TWO_JSON = "/data/graph/module-two.json";
    public static final String MODULE_THREE_JSON = "/data/graph/module-three.json";
    public static final String MODULE_FOUR_JSON = "/data/graph/module-four.json";
    public static final String MODULE_FIVE_JSON = "/data/graph/module-five.json";
    public static final String MODULE_SIX_JSON = "/data/graph/module-six.json";

    public static final String MODULE_SAMPLE_JMS_JSON = "/data/graph/sample-jms-module.json";
    public static final String JMS_PRODUCER_CONFIGURATION = "/data/graph/jmsProducerConfiguration.json";
    public static final String JMS_CONSUMER_CONFIGURATION = "/data/graph/jmsConsumerConfiguration.json";

    public static final String MODULE_SAMPLE_SFTP_JSON = "/data/graph/sample-sftp-module.json";
    public static final String SFTP_JMS_PRODUCER_CONFIGURATION = "/data/graph/sftpJmsProducerConfiguration.json";
    public static final String SFTP_JMS_CONSUMER_CONFIGURATION = "/data/graph/sftpJmsConsumerConfiguration.json";
    public static final String SFTP_PRODUCER_CONFIGURATION = "/data/graph/sftpProducerConfiguration.json";
    public static final String SFTP_CONSUMER_CONFIGURATION = "/data/graph/sftpConsumerConfiguration.json";
    public static final String SFTP_CONSUMER_CONFIGURATION_NO_REMOTE_HOST = "/data/graph/sftpConsumerConfigurationNoRemoteHost.json";
    public static final String SFTP_PRODUCER_CONFIGURATION_NO_REMOTE_HOST = "/data/graph/sftpProducerConfigurationNoRemoteHost.json";

    JsonModuleMetaDataProvider jsonModuleMetaDataProvider
        = new JsonModuleMetaDataProvider(new JsonFlowMetaDataProvider());


    @Test
    public void test_adapt_module_no_configurations() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_JSON));

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, new ArrayList<>());

        Assertions.assertEquals("module name", module.getName(), "module name equals");
        Assertions.assertEquals("module version", module.getVersion(), "module version equals");
        Assertions.assertEquals("module description", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(6, module.getFlows().size(), "number of flows equal");

        Flow flow = module.getFlows().get(0);

        Assertions.assertEquals("Simple Flow 1", flow.getName(), "flow names equals");
        Assertions.assertEquals("FLOW_CONFIGURATION_ID", flow.getConfigurationId(), "flow configuration id equals");

        Consumer consumer = flow.getConsumer();

        Assertions.assertEquals("Test Consumer", consumer.getLabel(), "consumer label equals");
        Assertions.assertEquals("frontend/images/event-driven-consumer.png", consumer.getImage(), "consumer image equals");
        Assertions.assertEquals("Test Consumer0", consumer.getId(), "consumer id equals");

        Filter filter = (Filter)consumer.getTransition();

        Assertions.assertEquals("Test Filter", filter.getLabel(), "filter label equals");
        Assertions.assertEquals("frontend/images/message-filter.png", filter.getImage(), "filter image equals");
        Assertions.assertEquals("Test Filter1", filter.getId(), "filter id equals");

        Splitter splitter = (Splitter)filter.getTransition();

        Assertions.assertEquals("Test Splitter", splitter.getLabel(), "splitter label equals");
        Assertions.assertEquals("frontend/images/splitter.png", splitter.getImage(), "splitter image equals");
        Assertions.assertEquals("Test Splitter2", splitter.getId(), "splitter id equals");

        Broker broker = (Broker)splitter.getTransition();

        Assertions.assertEquals("Test Broker", broker.getLabel(), "broker label equals");
        Assertions.assertEquals("frontend/images/broker.png", broker.getImage(), "broker image equals");
        Assertions.assertEquals("Test Broker3", broker.getId(), "broker id equals");

        MessageConverter messageConverter = (MessageConverter)broker.getTransition();

        Assertions.assertEquals("Test Converter", messageConverter.getLabel(), "broker label equals");
        Assertions.assertEquals("frontend/images/message-translator.png", messageConverter.getImage(), "broker image equals");
        Assertions.assertEquals("Test Converter4", messageConverter.getId(), "broker id equals");

        MessageEndPoint messageEndpoint = (MessageEndPoint)messageConverter.getTransition();

        Assertions.assertEquals("Test Producer", messageEndpoint.getLabel(), "broker label equals");
        Assertions.assertEquals("frontend/images/message-endpoint.png", messageEndpoint.getImage(), "broker image equals");
        Assertions.assertEquals("Test Producer5", messageEndpoint.getId(), "broker id equals");

    }

    @Test
    public void test_adapt_jms_module_with_configurations() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_SAMPLE_JMS_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ConfigurationMetaData configurationMetaData = this.getConfigurationMetadata(JMS_PRODUCER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(JMS_CONSUMER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("sample-boot-jms", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Sample Module", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(1, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_sftp_module_with_configurations() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_SAMPLE_SFTP_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ConfigurationMetaData configurationMetaData = this.getConfigurationMetadata(SFTP_JMS_PRODUCER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_JMS_CONSUMER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_CONSUMER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_PRODUCER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("sample-boot-sftp-jms", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Sftp Jms Sample Module", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(2, module.getFlows().size(), "number of flows equal");

        Flow flow = module.getFlows().get(0);

        Assertions.assertEquals("Sftp To Jms Flow", flow.getName(), "flow names equals");
        Assertions.assertEquals("sample-boot-sftp-jms-Sftp To Jms Flow", flow.getConfigurationId(), "flow configuration id equals");

        Consumer consumer = flow.getConsumer();

        Assertions.assertEquals("Sftp Consumer", consumer.getLabel(), "consumer label equals");
        Assertions.assertEquals("frontend/images/sftp-consumer.png", consumer.getImage(), "consumer image equals");
        Assertions.assertEquals("Sftp Consumer0", consumer.getId(), "consumer id equals");

        MessageConverter converter = (MessageConverter)consumer.getTransition();

        Assertions.assertEquals("Sftp Payload to Map\nConverter", converter.getLabel(), "converter label equals");
        Assertions.assertEquals("frontend/images/message-translator.png", converter.getImage(), "converter image equals");
        Assertions.assertEquals("Sftp Payload to Map Converter1", converter.getId(), "converter id equals");

        MessageProducer producer = (MessageProducer)converter.getTransition();

        Assertions.assertEquals("Sftp Jms Producer", producer.getLabel(), "producer label equals");
        Assertions.assertEquals("frontend/images/channel-adapter.png", producer.getImage(), "producer image equals");
        Assertions.assertEquals("Sftp Jms Producer2", producer.getId(), "producer id equals");

        MessageChannel messageChannel = (MessageChannel)producer.getTransition();

        Assertions.assertEquals("sftp.private.jms.queue", messageChannel.getLabel(), "messageChannel label equals");
        Assertions.assertEquals("frontend/images/message-channel.png", messageChannel.getImage(), "messageChannel image equals");
        Assertions.assertEquals("channel3", messageChannel.getId(), "messageChannel id equals");
    }

    @Test
    public void test_adapt_sftp_module_with_configurations_no_remote_host() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_SAMPLE_SFTP_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ConfigurationMetaData configurationMetaData = this.getConfigurationMetadata(SFTP_JMS_PRODUCER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_JMS_CONSUMER_CONFIGURATION);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_CONSUMER_CONFIGURATION_NO_REMOTE_HOST);
        configurationMetaDataList.add(configurationMetaData);

        configurationMetaData = this.getConfigurationMetadata(SFTP_PRODUCER_CONFIGURATION_NO_REMOTE_HOST);
        configurationMetaDataList.add(configurationMetaData);

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("sample-boot-sftp-jms", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Sftp Jms Sample Module", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(2, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_sftp_module_one() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_ONE_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("bloomberg-trade", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Bloomberg MHI TOMS Post Trade Feed and Trade Booking Application", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(18, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_module_two() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_TWO_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("espeed-trade", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Espeed Trade module publishing FIX trade confirmations to the BDM pre-book channel, after converting them to a cmfTrade message.", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(2, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_module_three() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_THREE_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("ion-jgbTrade", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("ION Debt Trade", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(6, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_module_four() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_FOUR_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("electronicTrade", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Region to Region Electronic Trading", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(12, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_module_five() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_FIVE_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("trading-place-trade-sa", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("Tradeweb JGB Trade", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(10, module.getFlows().size(), "number of flows equal");
    }

    @Test
    public void test_adapt_module_six() throws IOException
    {
        ModuleMetaData moduleMetaData = this.jsonModuleMetaDataProvider
            .deserialiseModule(loadDataFile(MODULE_SIX_JSON));

        ArrayList<ConfigurationMetaData> configurationMetaDataList = new ArrayList<>();

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaDataList);

        Assertions.assertEquals("tt-trade", module.getName(), "module name equals");
        Assertions.assertEquals(null, module.getVersion(), "module version equals");
        Assertions.assertEquals("TT Trade", module.getDescription(), "module descriptions equals");
        Assertions.assertEquals(8, module.getFlows().size(), "number of flows equal");
    }

    protected ConfigurationMetaData getConfigurationMetadata(String file) throws IOException
    {
        String configuration = loadDataFile(file);

        ObjectMapper objectMapper = new ObjectMapper();

        ConfigurationMetaDataImpl configurationMetaData
                = objectMapper.readValue(configuration, ConfigurationMetaDataImpl.class);

            return configurationMetaData;
    }

    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
