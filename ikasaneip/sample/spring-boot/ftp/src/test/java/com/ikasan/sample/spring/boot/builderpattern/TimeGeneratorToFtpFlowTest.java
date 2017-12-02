package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.testharness.flow.rule.IkasanStandaloneFlowTestRule;
import org.ikasan.testharness.flow.ftp.FtpRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * This test Sftp To Log Flow.
 *
 * @author Ikasan Development Team
 */
public class TimeGeneratorToFtpFlowTest
{
    private static String SAMPLE_MESSAGE = "Hello world!";

    public IkasanStandaloneFlowTestRule flowTestRule = new IkasanStandaloneFlowTestRule("TimeGenerator To Ftp Flow",
        Application.class);

    public FtpRule sftp = new FtpRule("test", "test", null, 22999);

    @Rule public TestRule chain = RuleChain.outerRule(sftp).around(flowTestRule);

    @Test public void test_file_upload() throws Exception
    {
        //Update Sftp Consumer config
        FtpProducerConfiguration consumerConfiguration = flowTestRule
            .getComponentConfig("Ftp Producer", FtpProducerConfiguration.class);
        consumerConfiguration.setOutputDirectory(sftp.getBaseDir());

        //Setup component expectations
        flowTestRule.consumer("Scheduled Consumer").converter("Random String Generator").producer("Ftp Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        flowTestRule.fireScheduledConsumer();
        flowTestRule.sleep(5000L);
    }
}
