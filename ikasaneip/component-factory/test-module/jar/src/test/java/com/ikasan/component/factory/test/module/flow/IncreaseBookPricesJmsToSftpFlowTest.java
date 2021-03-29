package com.ikasan.component.factory.test.module.flow;

import com.ikasan.component.factory.test.module.boot.Application;
import org.apache.commons.io.IOUtils;
import org.awaitility.core.ThrowingRunnable;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IncreaseBookPricesJmsToSftpFlowTest
{
    @Resource private Module<Flow> moduleUnderTest;

    private String flowUnderTestName = "Increase Book Prices Jms to Sftp Flow";

    private String consumerQueue = "book.prices.inbound";

    private static SftpRule sftpRule;

    private IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    private Flow flow;

    @Resource
    private JmsTemplate jmsTemplate;

    @ClassRule
    public static TemporaryFolder sftpBaseFolder = new TemporaryFolder();

    @Test
    public void test() throws IOException
    {
        flowTestRule.consumer("Increase Book Prices Jms Consumer")
            .converter("Increase Book Prices Xslt Converter")
            .converter("Increase Book Prices Xml Validator")
            .producer("Increase Book Prices Sftp Producer");
        flowTestRule.startFlow();
        sendMessage(consumerQueue,
            new File("src/test/resources/increaseBookPricesJmsToSftpFlow/test-books.xml"));

        assertWithPolling(()-> flowTestRule.assertIsSatisfied());
        flowTestRule.stopFlow();
    }

    private void assertWithPolling(ThrowingRunnable assertion){
        with().pollInterval(200, TimeUnit.MILLISECONDS).and().with().pollDelay(1,TimeUnit.SECONDS)
            .await().atMost(10, TimeUnit.SECONDS).untilAsserted(assertion);
    }
    private void sendMessage(String destination, File file) throws IOException
    {
            String contentToSend = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
            jmsTemplate.convertAndSend(destination, contentToSend, new MessagePostProcessor()
            {
                @Override public Message postProcessMessage(Message message) throws JMSException
                {
                    message.setStringProperty("IkasanEventLifeIdentifier", "eventId");
                    return message;
                }
            });
    }

    @BeforeClass
    public static void beforeClass(){
        sftpRule = new SftpRule("test", "test", sftpBaseFolder.getRoot().getAbsolutePath(),
            SocketUtils.findAvailableTcpPort(10000, 20000));
        sftpRule.start();
    }

    public void setup()
    {
        flow = moduleUnderTest.getFlow(flowUnderTestName);
        flowTestRule.withFlow(flow);
    }

    @AfterClass
    public static void tearDown()
    {
        sftpRule.stop();
    }

    static class PropertiesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
    {
        /**
         * This overrides properties in the application.properties file so that dynamic port allocation in tests
         * can be handled
         */
        @Override public void initialize(ConfigurableApplicationContext configurableApplicationContext)
        {
            TestPropertyValues.of("test.sftp.port=" + sftpRule.getPort())
                .applyTo(configurableApplicationContext.getEnvironment());
            TestPropertyValues.of("test.sftp.baseDirectory=" + sftpRule.getBaseDir())
                .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}