package org.ikasan.dashboard.ui.general.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import org.ikasan.dashboard.Application;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.error.reporting.service.SolrErrorReportingManagementServiceImpl;
import org.ikasan.replay.service.SolrReplayAuditServiceImpl;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SearchResultTest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private SolrErrorReportingManagementServiceImpl solrErrorReportingService;

    @MockBean
    private HospitalAuditService hospitalAuditService;

    @MockBean
    private ResubmissionRestServiceImpl resubmissionRestService;

    @MockBean
    private ReplayRestServiceImpl replayRestService;

    @MockBean
    private ModuleMetaDataService moduleMetaDataService;

    @MockBean
    private SolrReplayAuditServiceImpl replayAuditService;

    @MockBean
    private IkasanAuthentication ikasanAuthentication;

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
    public void test_no_results_found() {
        SearchResults searchResults = new SearchResults(this.solrSearchService, this.hospitalAuditService
            , resubmissionRestService, replayRestService, moduleMetaDataService, replayAuditService);

        Assertions.assertNotNull(searchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        searchResults.search(0, System.currentTimeMillis() + 100000L, "", List.of("error", "exclusion", "wiretap"),
            false, new ArrayList<>(), new ArrayList<>());

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        Assertions.assertEquals(0, solrSearchFilteringGrid.getResultSize(), "Search results size equals 0!");

    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

}
