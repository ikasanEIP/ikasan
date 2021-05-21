package com.ikasan.sample.spring.boot.component.factory;

import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.awaitility.core.ThrowingRunnable;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.*;
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
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = { IncreaseBookPricesJmsToSftpFlowTest.PropertiesInitializer.class })
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

    private DateTimeFormatter dateTimeFormatter =  DateTimeFormat.forPattern("yyyyMMdHHmmss");

    private DateTime now;

    @Resource
    private JmsTemplate jmsTemplate;

    @ClassRule
    public static TemporaryFolder sftpBaseFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception
    {
        flowTestRule.withFlow(flow);
        flowTestRule.consumer("Increase Book Prices Jms Consumer")
            .converter("Increase Book Prices Xslt Converter")
            .converter("Increase Book Prices Xml Validator")
            .converter("Increase Book Prices Xml to Sftp Payload Converter")
            .producer("Increase Book Prices Sftp Producer");
        flowTestRule.startFlow();
        sendMessage(consumerQueue,
            new File("src/test/resources/increaseBookPricesJmsToSftpFlow/test-books.xml"));

        assertFileOnSftpServer(Paths.get("increasedBookPrices",expectedFileName()).toString());
        assertEquals(expectedFileContents, IOUtils.toString(sftpRule.getFile(Paths.get("increasedBookPrices",
            expectedFileName()).toString()),
            StandardCharsets.UTF_8));
        assertWithPolling(()-> flowTestRule.assertIsSatisfied());
        flowTestRule.stopFlow();
    }

    private String expectedFileContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AdjustedBooks xmlns:x=\"http://www.bookprices.com/xsd/book-prices.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.bookprices.com/xsd/adjusted-book-prices.xsd\"><AdjustedBook id=\"bk001\"><Author>Writer</Author><Title>The First Book</Title><Genre>Fiction</Genre><Price>49.45</Price><PubDate>2000-10-01</PubDate><Review>An amazing story of nothing.</Review></AdjustedBook><AdjustedBook id=\"bk002\"><Author>Poet</Author><Title>The Poet's First Poem</Title><Genre>Poem</Genre><Price>27.44</Price><PubDate>2000-10-01</PubDate><Review>Least poetic poems.</Review></AdjustedBook></AdjustedBooks>";

    private void assertFileOnSftpServer(String expectedFileName) {
        assertWithPolling(() -> {
            try {
                sftpRule.getFile(expectedFileName);
            } catch (SftpException sfe) {
                Assert.fail(sfe.toString());
            }
        });
    }

    private String expectedFileName() {
        return "price-increased-books-" + dateTimeFormatter.print(now) + ".xml";
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
    public static void beforeClass() throws IOException {
        File root = sftpBaseFolder.getRoot();
        FileUtils.forceMkdir(Paths.get(root.getAbsolutePath(),"increasedBookPrices").toFile());
        sftpRule = new SftpRule("test", "test", sftpBaseFolder.getRoot().getAbsolutePath(),
            SocketUtils.findAvailableTcpPort(10000, 20000));
        sftpRule.start();
    }

    @Before
    public void setup()
    {
        now = new DateTime();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        flow = moduleUnderTest.getFlow(flowUnderTestName);
        flowTestRule.withFlow(flow);
    }

    @AfterClass
    public static void tearDown()
    {
        sftpRule.stop();
    }

    @After
    public void after(){
        new ActiveMqHelper().removeAllMessages();
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