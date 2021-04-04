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
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = { IncreasedBookPricesSftpToJmsFlowTest.PropertiesInitializer.class })
@SpringBootTest(classes = {
    Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IncreasedBookPricesSftpToJmsFlowTest
{
    @Resource private Module<Flow> moduleUnderTest;

    private String flowUnderTestName = "Increased Book Prices Sftp to Jms Flow";

    private String producerQueue = "book.prices.internal.outbound";


    @Value("${jms.esb.broker.shared.provider.url}")
    private String brokerUrl;

    private static SftpRule sftpRule;

    private BrowseMessagesOnQueueVerifier browseMessagesOnQueueVerifier;

    private IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    private Flow flow;

    @ClassRule
    public static TemporaryFolder sftpBaseFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception
    {
        flowTestRule.withFlow(flow);
        String xmlContents = IOUtils.toString(new FileInputStream(
            "src/test/resources/increasedBookPricesSftpToJmsFlow/adjusted-books.xml"),
            StandardCharsets.UTF_8);
        addFileToSftpServer("increasedBookPrices" + File.separator + "adjusted-books.xml", xmlContents);
        flowTestRule.consumer("Increased Book Prices Sftp Consumer")
            .converter("Increased Book Prices Payload to String Converter")
            .producer("Increased Book Prices Jms Producer");
        assertTrue(new File(
            sftpBaseFolder.getRoot() + File.separator + "increasedBookPrices"
                + File.separator + "adjusted-books.xml").exists());
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));
        flowTestRule.fireScheduledConsumer();

        assertWithPolling(()-> assertEquals(1, browseMessagesOnQueueVerifier.getCaptureResults().size()));
        assertWithPolling(()-> flowTestRule.assertIsSatisfied());
        assertEquals(xmlContents, ((ActiveMQTextMessage)browseMessagesOnQueueVerifier.getCaptureResults().get(0))
            .getText());
        flowTestRule.stopFlow();
    }

    private void addFileToSftpServer(String fileToAdd, String contents) {
        try {
            sftpRule.putFile(fileToAdd, contents);
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }


    private void assertWithPolling(ThrowingRunnable assertion){
        with().pollInterval(200, TimeUnit.MILLISECONDS).and().with().pollDelay(1,TimeUnit.SECONDS)
            .await().atMost(1000, TimeUnit.SECONDS).untilAsserted(assertion);
    }


    @BeforeClass
    public static void beforeClass() throws IOException {
        File root = sftpBaseFolder.getRoot();
        FileUtils.forceMkdir(Paths.get(root.getAbsolutePath(),"increasedBookPrices").toFile());
        sftpRule = new SftpRule("test", "test", sftpBaseFolder.getRoot().getAbsolutePath(),
            SocketUtils.findAvailableTcpPort(10000, 20000));
        sftpRule.start();
    }

    @Before
    public void setup() throws JMSException {
        flow = moduleUnderTest.getFlow(flowUnderTestName);
        flowTestRule.withFlow(flow);
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, producerQueue);
        browseMessagesOnQueueVerifier.start();
    }

    @AfterClass
    public static void tearDown()
    {
        sftpRule.stop();
    }

    @After
    public void after(){
        browseMessagesOnQueueVerifier.stop();
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