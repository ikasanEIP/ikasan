package org.ikasan.dashboard.ui.general.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import org.ikasan.dashboard.Application;
import org.ikasan.error.reporting.model.SolrErrorOccurrence;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HospitalViewTest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private SolrErrorReportingManagementServiceImpl solrErrorReportingService;

    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setup() throws Exception
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        final SpringServlet servlet = new SpringServlet(ctx, true) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException
            {
                final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
                    @Override
                    protected boolean isAtmosphereAvailable() {
                        return false;
                    }

                    @Override
                    protected RouteRegistry getRouteRegistry() {
                        new Routes().autoDiscoverViews("org.ikasan.dashboard.ui").register(this.getServlet().getServletContext());
                        RouteRegistry registry =  ApplicationRouteRegistry.getInstance(this.getServlet().getServletContext());
                        return registry;
                    }

                    @Override
                    public String getMainDivId(VaadinSession session, VaadinRequest request) {
                        return "ROOT-1";
                    }
                };
                service.init();
                return service;
            }
        };
        MockVaadin.setup(MockedUI::new, servlet);
    }

    @Test
    public void test_hospital_view_success() throws IOException
    {
        IkasanSolrDocument document = new IkasanSolrDocument();
        document.setId("id");
        document.setComponentName("component");
        document.setErrorAction("exclusion");
        document.setType("exclusion");
        document.setErrorDetail("error");
        document.setErrorUri("uri");
        document.setErrorMessage("message");
        document.setFlowName("flow");
        document.setModuleName("module");
        document.setPayloadRaw("payload".getBytes());
        document.setExceptionClass("exception.class");
        document.setEvent("event payload");
        document.setEventId("eventId");

        Mockito.when(this.solrSearchService.findById("exclusion", "12345"))
            .thenReturn(document);

        SolrErrorOccurrence errorOccurrence = new SolrErrorOccurrence("moduleName", "flowName"
            , "flowElementName", "errorDetail"
            , "errorMessage", "exceptionClass"
            , 1L, "event".getBytes(), "eventAsString");
        errorOccurrence.setAction("exclusion");

        Mockito.when(this.solrSearchService.findByErrorUri("error", "id"))
            .thenReturn(document);

        UI.getCurrent().navigate("exclusion/12345");

        try
        {
            HospitalView hospitalView = _get(HospitalView.class);
            Assertions.assertNotNull(hospitalView);

            TextField errorUriTf = (TextField)ReflectionTestUtils.getField(hospitalView, "errorUriTf");
            Assertions.assertEquals("id", errorUriTf.getValue(), "Error URI text field equals");

            TextField errorActionTf = (TextField)ReflectionTestUtils.getField(hospitalView, "errorActionTf");
            Assertions.assertEquals("exclusion", errorActionTf.getValue(), "Error Action text field equals");

            TextField eventIdTf = (TextField)ReflectionTestUtils.getField(hospitalView, "eventIdTf");
            Assertions.assertEquals("eventId", eventIdTf.getValue(), "Event Id text field equals");

            TextField flowNameTf = (TextField)ReflectionTestUtils.getField(hospitalView, "flowNameTf");
            Assertions.assertEquals("flow", flowNameTf.getValue(), "Flow name text field equals");

            TextField moduleNameTf = (TextField)ReflectionTestUtils.getField(hospitalView, "moduleNameTf");
            Assertions.assertEquals("module", moduleNameTf.getValue(), "Module name text field equals");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_hospital_view_redirect_not_found() throws IOException
    {
        UI.getCurrent().navigate("exclusion/12345");

        try
        {
            PageNotFoundView pageNotFoundView = _get(PageNotFoundView.class);
            Assertions.assertNotNull(pageNotFoundView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }
}
