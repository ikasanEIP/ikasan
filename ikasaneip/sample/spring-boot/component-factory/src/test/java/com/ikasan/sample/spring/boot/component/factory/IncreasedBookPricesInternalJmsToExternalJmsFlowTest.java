package com.ikasan.sample.spring.boot.component.factory;

import com.jcraft.jsch.SftpException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.awaitility.core.ThrowingRunnable;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IncreasedBookPricesInternalJmsToExternalJmsFlowTest
{
    @Resource private Module<Flow> moduleUnderTest;

    private String flowUnderTestName = "Increased Book Prices Internal Jms to External Jms Flow";

    private String consumerQueue = "book.prices.internal.outbound";

    private String producerQueue = "book.prices.external.outbound";

    private IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    private Flow flow;

    @Resource
    private JmsTemplate jmsTemplate;

    @ClassRule
    public static TemporaryFolder sftpBaseFolder = new TemporaryFolder();


    @Value("${jms.esb.broker.shared.provider.url}")
    private String brokerUrl;

    private BrowseMessagesOnQueueVerifier browseMessagesOnQueueVerifier;

    @Test
    public void test() throws Exception
    {
        File messageFile = new File(
            "src/test/resources/increasedBookPricesInternalJmsToExternalJmsFlow/adjusted-books.xml");
        String messageContents = IOUtils.toString(new FileInputStream(messageFile), StandardCharsets.UTF_8);
        flowTestRule.withFlow(flow);
        flowTestRule.consumer("Increased Book Prices Internal Jms Consumer")
            .producer("Increased Book Prices External Jms Producer");
        flowTestRule.startFlow();
        sendMessage(consumerQueue,messageFile);
        assertWithPolling(()->assertEquals(1,browseMessagesOnQueueVerifier.getCaptureResults().size()));
        assertEquals(messageContents,(((ActiveMQTextMessage)
            browseMessagesOnQueueVerifier.getCaptureResults().get(0)).getText()));
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


    @Before
    public void setup() throws JMSException {
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, producerQueue);
        browseMessagesOnQueueVerifier.start();
        flow = moduleUnderTest.getFlow(flowUnderTestName);
        flowTestRule.withFlow(flow);
    }
}