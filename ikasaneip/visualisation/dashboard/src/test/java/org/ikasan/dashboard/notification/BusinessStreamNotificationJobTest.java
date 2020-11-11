package org.ikasan.dashboard.notification;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.business.stream.metadata.dao.SolrBusinessStreamMetadataDao;
import org.ikasan.business.stream.metadata.service.SolrBusinessStreamMetaDataServiceImpl;
import org.ikasan.dashboard.notification.email.EmailNotifier;
import org.ikasan.dashboard.notification.model.BusinessStreamExclusions;
import org.ikasan.dashboard.notification.model.BusinessStreamNotification;
import org.ikasan.dashboard.notification.model.EmailNotification;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationService;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.solr.dao.SolrGeneralDaoImpl;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;


public class BusinessStreamNotificationJobTest extends SolrTestCaseJ4 {

    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    public static final String BUSINESS_STREAM_PAYLOAD = "/data/graph/wriggle3.json";

    private SolrGeneralDaoImpl dao;

    private NodeConfig config;

    private TemplateEngine templateEngine = mockery.mock(TemplateEngine.class);
    private BusinessStreamNotification businessStreamNotification = mockery.mock(BusinessStreamNotification.class);
    private BusinessStreamNotificationService businessStreamNotificationService = mockery.mock(BusinessStreamNotificationService.class);
    private PlatformConfigurationService platformConfigurationService = mockery.mock(PlatformConfigurationService.class);
    private EmailNotifier emailNotifier = mockery.mock(EmailNotifier.class);
    private JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    @Test
    public void test_job_success_no_exclusions_found() throws JobExecutionException {
        mockery.checking(new Expectations(){{
            oneOf(businessStreamNotification).isNewExclusionsOnlyNotification();
            will(returnValue(true));
            oneOf(businessStreamNotification).getBusinessStreamName();
            will(returnValue("businessStreamName"));
            oneOf(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).getConfigurationValue(with(any(String.class)));
            will(returnValue("0"));
            exactly(1).of(businessStreamNotification).getLastRunTimestamp();
            will(returnValue(0L));
            oneOf(businessStreamNotification).getResultSize();
            will(returnValue(1000));
            oneOf(businessStreamNotificationService).getBusinessStreamExclusions("businessStreamName", 0L, 1000);
            will(returnValue(Optional.empty()));
            exactly(2).of(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).saveConfigurationValue(with(any(String.class)), with(any(String.class)));
        }});

        BusinessStreamNotificationJob notification = new BusinessStreamNotificationJob(emailTemplateEngine(), businessStreamNotification
            , businessStreamNotificationService, platformConfigurationService, emailNotifier);

        notification.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_job_success_exclusions_found() throws JobExecutionException {
        mockery.checking(new Expectations(){{
            ignoring(templateEngine);
            oneOf(businessStreamNotification).isNewExclusionsOnlyNotification();
            will(returnValue(true));
            oneOf(businessStreamNotification).getBusinessStreamName();
            will(returnValue("wriggle"));
            oneOf(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).getConfigurationValue(with(any(String.class)));
            will(returnValue("0"));
            exactly(1).of(businessStreamNotification).getLastRunTimestamp();
            will(returnValue(0L));
            oneOf(businessStreamNotification).getResultSize();
            will(returnValue(1000));
            oneOf(businessStreamNotification).getEmailBodyTemplate();
            will(returnValue("./src/test/resources/email/notification-email-jp.html"));
            oneOf(businessStreamNotification).getEmailSubjectTemplate();
            will(returnValue("./src/test/resources/email/notification-email-subject-jp.txt"));
            oneOf(businessStreamNotification).getRecipientList();
            will(returnValue(Arrays.asList("ikasan@ikasan.com")));
            oneOf(businessStreamNotification).isHtml();
            will(returnValue(true));
            oneOf(emailNotifier).sendNotification(with(any(EmailNotification.class)));
            oneOf(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).saveConfigurationValue(with(any(String.class)), with(any(String.class)));
        }});

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);
            this.initialiseDataExclusionsAndErrors(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);

            BusinessStreamNotificationJob notification = new BusinessStreamNotificationJob(emailTemplateEngine(), businessStreamNotification
                , businessStreamNotificationService, platformConfigurationService, emailNotifier);

            notification.execute(jobExecutionContext);
        }
        catch (SolrServerException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_job_success_no_exclusions_found_due_to_last_run_timestamp() throws JobExecutionException {
        mockery.checking(new Expectations(){{
            ignoring(templateEngine);
            oneOf(businessStreamNotification).isNewExclusionsOnlyNotification();
            will(returnValue(true));
            oneOf(businessStreamNotification).getBusinessStreamName();
            will(returnValue("wriggle"));
            oneOf(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).getConfigurationValue(with(any(String.class)));
            will(returnValue("0"));
            exactly(2).of(businessStreamNotification).getLastRunTimestamp();
            will(returnValue(System.currentTimeMillis() + 1000000L));
            oneOf(businessStreamNotification).getResultSize();
            will(returnValue(1000));
            exactly(2).of(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).saveConfigurationValue(with(any(String.class)), with(any(String.class)));
        }});

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);
            this.initialiseDataExclusionsAndErrors(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);

            BusinessStreamNotificationJob notification = new BusinessStreamNotificationJob(emailTemplateEngine(), businessStreamNotification
                , businessStreamNotificationService, platformConfigurationService, emailNotifier);

            notification.execute(jobExecutionContext);
        }
        catch (SolrServerException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        mockery.assertIsSatisfied();
    }

    @Test(expected = JobExecutionException.class)
    public void test_job_success_exclusions_found_exception_sending_email() throws JobExecutionException {
        mockery.checking(new Expectations(){{
            ignoring(templateEngine);
            oneOf(businessStreamNotification).isNewExclusionsOnlyNotification();
            will(returnValue(true));
            oneOf(businessStreamNotification).getBusinessStreamName();
            will(returnValue("wriggle"));
            oneOf(businessStreamNotification).getJobName();
            will(returnValue("jobName"));
            oneOf(platformConfigurationService).getConfigurationValue(with(any(String.class)));
            will(returnValue("0"));
            exactly(1).of(businessStreamNotification).getLastRunTimestamp();
            will(returnValue(0L));
            oneOf(businessStreamNotification).getResultSize();
            will(returnValue(1000));
            oneOf(businessStreamNotification).getEmailBodyTemplate();
            will(returnValue("./src/test/resources/email/notification-email-jp.html"));
            oneOf(businessStreamNotification).getEmailSubjectTemplate();
            will(returnValue("./src/test/resources/email/notification-email-subject-jp.txt"));
            oneOf(businessStreamNotification).getRecipientList();
            will(returnValue(Arrays.asList("ikasan@ikasan.com")));
            oneOf(businessStreamNotification).isHtml();
            will(returnValue(true));
            oneOf(emailNotifier).sendNotification(with(any(EmailNotification.class)));
            will(throwException(new RuntimeException("error!")));
        }});

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            init(server);

            this.initialiseDataBusinessStream(server);
            this.initialiseDataExclusionsAndErrors(server);

            BusinessStreamNotificationService businessStreamNotificationService = this.initialiseService(server);

            BusinessStreamNotificationJob notification = new BusinessStreamNotificationJob(emailTemplateEngine(), businessStreamNotification
                , businessStreamNotificationService, platformConfigurationService, emailNotifier);

            notification.execute(jobExecutionContext);
        }
        catch (SolrServerException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(textTemplateResolver());
        templateEngine.addTemplateResolver(htmlTemplateResolver());

        return templateEngine;
    }

    private ITemplateResolver textTemplateResolver() {
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString()).build();


    }

    private void init(EmbeddedSolrServer server) throws IOException, SolrServerException
    {
        CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
        createRequest.setCoreName("ikasan");
        createRequest.setConfigSet("minimal");
        server.request(createRequest);

        dao = new SolrGeneralDaoImpl();
        dao.setSolrClient(server);
    }

    private BusinessStreamNotificationService initialiseService(EmbeddedSolrServer server) {
        SolrGeneralDaoImpl solrGeneralDao = new SolrGeneralDaoImpl();
        solrGeneralDao.setSolrClient(server);

        SolrBusinessStreamMetadataDao solrBusinessStreamMetadataDao = new SolrBusinessStreamMetadataDao();
        solrBusinessStreamMetadataDao.setSolrClient(server);

        SolrErrorReportingServiceDao solrErrorReportingServiceDao = new SolrErrorReportingServiceDao();
        solrErrorReportingServiceDao.setSolrClient(server);

        SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(solrGeneralDao);
        SolrBusinessStreamMetaDataServiceImpl solrBusinessStreamMetaDataService
            = new SolrBusinessStreamMetaDataServiceImpl(solrBusinessStreamMetadataDao);

        return new BusinessStreamNotificationService(solrBusinessStreamMetaDataService, solrGeneralService);
    }

    private void initialiseDataBusinessStream(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "businessStream-wriggle");
        doc.addField("type", "businessStreamMetaData");
        doc.addField("moduleName", "wriggle");
        doc.addField("payload", this.loadDataFile(BUSINESS_STREAM_PAYLOAD));
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        server.add("ikasan", doc);
        server.commit();
    }

    private void initialiseDataExclusionsAndErrors(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "error-1");
        doc.addField("type", "error");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("errorUri", "1234");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the error payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        doc = new SolrInputDocument();
        doc.addField("id", "1234");
        doc.addField("type", "exclusion");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the exclusion payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        server.commit();
    }

    private void initialiseDataExclusionNoError(EmbeddedSolrServer server) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "exclusion-1");
        doc.addField("type", "exclusion");
        doc.addField("moduleName", "wriggle-im");
        doc.addField("flowName", "Wriggle Customer HTTP Request Flow");
        doc.addField("payload", "this is the exclusion payload");
        doc.addField("expiry", System.currentTimeMillis() + 10000000l);
        doc.addField("timestamp", System.currentTimeMillis() - 10000000l);
        server.add("ikasan", doc);

        server.commit();
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

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
